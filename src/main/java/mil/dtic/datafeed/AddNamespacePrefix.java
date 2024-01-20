package mil.dtic.datafeed;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mil.dtic.datafeed.config.DatafeedProperties;

@Component
public class AddNamespacePrefix {

	private  final Logger logger = LoggerFactory.getLogger(AddNamespacePrefix.class);

	@Autowired
	DatafeedProperties datafeedProperties;

	@Value("${datafeed.namespace.mdr.uri}")
	private String uri;

	public void addNamespaceAttributePrefix(Document document, String prefix, String record) {

		logger.info("Getting the datafeedProperties info ...");

		// Get only the first 'Record' node
		Node oldNode = document.getElementsByTagName("Record").item(0);
		Element newElement = document.createElementNS(datafeedProperties.getMdr().get("uri"), datafeedProperties.getMdr().get("qualifiedname"));
		newElement.setAttribute(datafeedProperties.getAttribute().get("name"), datafeedProperties.getAttribute().get("value"));

		// e.g. document.createElementNS("http://dtic.mil/mdr/record/TrEcms", "tr:Record")
		Element feedRecord = document.createElementNS(datafeedProperties.getFeed().get("uri"), datafeedProperties.getFeed().get("qualifiedname"));
		newElement.appendChild(feedRecord);

		// Had to do this way to prevent the error - 
		// HIERARCHY_REQUEST_ERR DOMException typically occurs when you 
		// try to insert a node into a location in the XML document where 
		// it is not allowed by the XML specification. 
		// This error is often related to restrictions on node placement within the XML hierarchy.
		NodeList children = oldNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node clonedChild = child.cloneNode(true);
			feedRecord.appendChild(clonedChild);
		}

		oldNode.getParentNode().replaceChild(newElement, oldNode);

		// Get all node names to a List
		List<String> nodeNames = getAllNodeNamesToList(document);

		// Print the node names except for nodes - ECMS & Records
		for (String nodeName : nodeNames) {

			if(!nodeName.equals(datafeedProperties.getNodenamelist().get(0)) && !nodeName.equals(datafeedProperties.getNodenamelist().get(1)) && !nodeName.equals(datafeedProperties.getNodenamelist().get(2))) {

				NodeList nodes = document.getElementsByTagName(nodeName);
				for(int i=0; i< nodes.getLength();i++){
					Element element = (Element) nodes.item(i);
					// To make sure namespace elements are excluded before adding the new prefix
					if(element.getNamespaceURI() == null) {
						String newNodeName = datafeedProperties.getPrefix() + element.getNodeName();
						renameElement(element, newNodeName);
					}
				}
			}	
		}

	}

	public void addMetaElements(Document document) { 

		// Obtain the IacEcms:Record element
		Element iacEcmsRecord = (Element) document.getElementsByTagName(datafeedProperties.getFeed().get("qualifiedname")).item(0);
		Node dataFeedNode = document.getElementsByTagName(datafeedProperties.getFeed().get("qualifiedname")).item(0);
		Node metaDataNode = dataFeedNode.cloneNode(true);
		NodeList metaNodeList = metaDataNode.getChildNodes();
		for(int i=0; i < metaNodeList.getLength(); i++) {
		}

		// Create a new element with the meta:Metadata name and namespace
		Element metaMetadata = document.createElementNS("http://dtic.mil/mdr/record/meta", "meta:Metadata");

		// Copy the attributes and children of the IacEcms:Record element to the meta:Metadata element
		NamedNodeMap attrs = iacEcmsRecord.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) document.importNode(attrs.item(i), true);
			metaMetadata.getAttributes().setNamedItem(attr);
		}

		NodeList children = iacEcmsRecord.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = document.importNode(children.item(i), true);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				//Document document = node.getOwnerDocument();
			}
			metaMetadata.appendChild(child);
		}
		NodeList metaChilds = metaMetadata.getChildNodes();
		for (int i = 0; i < metaChilds.getLength(); i++) {
			Node metaChildNode = metaChilds.item(i);

			// Only Node Type - ELEMENT 
			if(metaChildNode.getNodeType() == Node.ELEMENT_NODE) {

				String newNodeName = null;

				if (metaChildNode.getNodeName().contains(Constants.SEPARATOR)) {

					// Contains the substrings separated by ':'
					newNodeName = metaChildNode.getNodeName().split(Constants.SEPARATOR)[1];
				} else {

					// Separator ':' is not present in the string
					newNodeName = metaChildNode.getNodeName();	
				}

				changePrefix(metaChildNode, "http://dtic.mil/mdr/record/meta", "meta");
			}

		}

		// Append the meta:Metadata element after the IacEcms:Record element
		iacEcmsRecord.getParentNode().insertBefore(metaMetadata, iacEcmsRecord.getNextSibling());
	}

	private List<String> getAllNodeNamesToList(Node node) {
		List<String> nodeNames = new ArrayList<>();

		if (node != null) {

			// Collect the node name if it's an element node
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				nodeNames.add(node.getNodeName());
			}

			// Recursively process child nodes
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				nodeNames.addAll(getAllNodeNamesToList(children.item(i)));
			}
		}

		return nodeNames;
	}

	private void renameElement(Element element, String newElementName) {
		if (element != null) {
			Document document = element.getOwnerDocument();
			Element newElement = document.createElement(newElementName);

			// Copy attributes to the new element
			NamedNodeMap attributes = element.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				newElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
			}

			// Copy child elements to the new element
			NodeList children = element.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				Node importedNode = document.importNode(child, true);
				newElement.appendChild(importedNode);
			}

			// Replace the old element with the new one
			Node parent = element.getParentNode();
			//
			parent.replaceChild(newElement, element);
		}
	}

	public void changePrefix(Node node, String namespace, String prefix) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Document doc = node.getOwnerDocument();
			Element oldElement = (Element) node;

			String newNodeName = null;
			//String newNodeNamePrefixed = null;

			if (node.getNodeName().contains(Constants.SEPARATOR)) {

				// Contains the substrings separated by ':'
				newNodeName = node.getNodeName().split(Constants.SEPARATOR)[1];

			} else {

				// Separator ':' is not present in the string
				newNodeName = node.getNodeName();

			}
			Element newElement = doc.createElementNS(namespace, prefix + ":" + newNodeName);

			// Copy attributes to the new element
			NamedNodeMap attributes = oldElement.getAttributes();
			for (int i = 0; i < attributes.getLength(); ++i) {
				Node attribute = attributes.item(i);
				newElement.setAttributeNS(attribute.getNamespaceURI(), attribute.getNodeName(), attribute.getNodeValue());
			}

			// Move children to the new element
			while (oldElement.hasChildNodes()) {
				newElement.appendChild(oldElement.getFirstChild());
			}

			// Replace old element with new element
			Node parent = oldElement.getParentNode();
			parent.replaceChild(newElement, oldElement);

			node = newElement;  // node now points to the new element
		}

		// Process the children
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i) {
			changePrefix(list.item(i), namespace, prefix);
		}
	}

	private String findAttrInChildren(Element element, String tag) {
		if (!element.getAttribute(tag).isEmpty()) {
			return element.getAttribute(tag);
		}

		NodeList children = element.getChildNodes();
		for (int i = 0, len = children.getLength(); i < len; i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) children.item(i);
				String attr = findAttrInChildren(childElement, tag);
				if (attr != null) {
					return attr;
				}
			}
		}

		// We didn't find it, return null
		return null;
	}

}