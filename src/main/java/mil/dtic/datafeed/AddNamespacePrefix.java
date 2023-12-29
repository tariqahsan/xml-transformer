package mil.dtic.datafeed;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
	
	public void display() {
		System.out.println("AddNamespacePrefix DatafeedProperties: " + datafeedProperties.getMdr().get("uri"));
		System.out.println("AddNamespacePrefix Value: " + uri);
	}
	
	public void addNamespaceAndNodePrefix(Document document) {
        try {
        	
            // Add a namespace declaration to the root element
            NodeList records = document.getElementsByTagName("Record");
    		Element record = null;

    		//Iterate
    		for(int i=0; i< records.getLength();i++){
    			record = (Element) records.item(i);
    			addNamespaceDeclaration(record, "IacEcms", "http://dtic.mil/IacEcms/record/IacEcms");
    		}
    		
    		// Get all node names to a List
            List<String> nodeNames = getAllNodeNamesToList(document);
            
            // Print the node names except for nodes - ECMS & Records
            for (String nodeName : nodeNames) {
            	if(nodeName != "ECMS" && nodeName != "Records") {
            	
            		NodeList nodes = document.getElementsByTagName(nodeName);
            		for(int i=0; i< nodes.getLength();i++){
            			Element element = (Element) nodes.item(i);
            			String newNodeName = "IacEcms:" + element.getNodeName();
            			renameElement(element, newNodeName);
            		}
            	}	
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void addNamespaceDeclaration(Element element, String prefix, String uri) {
        if (element != null) {
            element.setAttribute("xmlns:" + prefix, uri);
        }
    }

    public void addNamespaceAttributePrefix(Document document) {
    	
    	DatafeedProperties datafeedProperties = new DatafeedProperties();
    	logger.info("Getting the datafeedProperties info ...");
    	//NamespaceProperties nsp = datafeedProperties.getNamespace().get("mdr");
    	//logger.info("nsp.getUri " + nsp.getUri());
    	//logger.info(datafeedProperties.getUri());
    	
//    	uri = datafeedproperties.getMdr().get("uri");
    	//uri = datafeedProperties.getUri();
    	//logger.info("addNamespaceAttributePrefix : Environment URI: " + environment.getProperty("datafeed.namespace.mdr.uri"));
//    	
    	// Get only the first 'Record' node
    	Node oldNode = document.getElementsByTagName("Record").item(0);
		Element newElement = document.createElementNS("http://dtic.mil/mdr/record", "mdr:Record");
		newElement.setAttribute("Type", "IAC");
		
		Element iacEcmsRecord = document.createElementNS("http://dtic.mil/mdr/record/IacEcms", "IacEcms:Record");
		newElement.appendChild(iacEcmsRecord);
		
		// Had to do this way to prevent the error - 
		// HIERARCHY_REQUEST_ERR DOMException typically occurs when you 
		// try to insert a node into a location in the XML document where 
		// it is not allowed by the XML specification. 
		// This error is often related to restrictions on node placement within the XML hierarchy.
		NodeList children = oldNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node clonedChild = child.cloneNode(true);
			iacEcmsRecord.appendChild(clonedChild);
		}

		oldNode.getParentNode().replaceChild(newElement, oldNode);
		
		// Get all node names to a List
        List<String> nodeNames = getAllNodeNamesToList(document);
        
        // Print the node names except for nodes - ECMS & Records
        for (String nodeName : nodeNames) {
        	if(nodeName != "ECMS" && nodeName != "Records" && !nodeName.contains("Record")) {

        		NodeList nodes = document.getElementsByTagName(nodeName);
        		for(int i=0; i< nodes.getLength();i++){
        			Element element = (Element) nodes.item(i);
        			String newNodeName = "IacEcms:" + element.getNodeName();
        			renameElement(element, newNodeName);
        		}
        	}	
        }
        
        Element metaEcmsRecord = document.createElementNS("http://dtic.mil/mdr/record/meta", "meta:Metadata");
		newElement.appendChild(metaEcmsRecord);

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
    
    private  Element getFirstElement(Document document) {
        // Example: Retrieve the first element for demonstration purposes
        NodeList elements = document.getElementsByTagName("Record");
        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            if (element instanceof Element) {
                return (Element) element;
            }
        }
        return null;
    }

    private  void renameElement(Element element, String newElementName) {
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
            parent.replaceChild(newElement, element);
        }
    }
}

