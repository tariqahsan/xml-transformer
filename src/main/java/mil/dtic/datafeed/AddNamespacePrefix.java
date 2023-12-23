package mil.dtic.datafeed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AddNamespacePrefix {
	
	public void addNamespaceAndNodePrefix(Document document) {
        try {
        	
            // Add a namespace declaration to the root element
            NodeList records = document.getElementsByTagName("Record");
    		Element record = null;

    		//Iterate
    		for(int i=0; i< records.getLength();i++){
    			record = (Element) records.item(i);
    			System.out.println(record.getNodeName());
    			addNamespaceDeclaration(record, "IacEcms", "http://dtic.mil/IacEcms/record/IacEcms");
    	
    			// For only the first 'Record'
//    			if (record == records.item(0)) {
//    				addNamespaceDeclaration(record, "mdr", "http://dtic.mil/IacEcms/record/mdr");
//    			}
    		}
    		
    		// Get all node names to a List
            List<String> nodeNames = getAllNodeNamesToList(document);
            
            // Print the node names except for nodes - ECMS & Records
            for (String nodeName : nodeNames) {
            	if(nodeName != "ECMS" && nodeName != "Records") {
            		System.out.println("Node Name: " + nodeName);
            	
            		NodeList nodes = document.getElementsByTagName(nodeName);
            		for(int i=0; i< nodes.getLength();i++){
            			Element element = (Element) nodes.item(i);
            			System.out.println(element.getNodeName());
            			String newNodeName = "IacEcms:" + element.getNodeName();
            			System.out.println(newNodeName);
            			renameElement(element, newNodeName);
            		}
            	}	
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addNamespaceDeclaration(Element element, String prefix, String uri) {
        if (element != null) {
            element.setAttribute("xmlns:" + prefix, uri);
        }
    }
    
    static void addNamespaceAttributePrefix(Document document) {
    	
    	Node oldNode = document.getElementsByTagName("Record").item(0);
		Element newElement = document.createElementNS("http://dtic.mil/mdr/record", "mdr:Record");
		newElement.setAttribute("Type", "IAC");

		Element iacEcmsRecord = document.createElementNS("http://dtic.mil/mdr/record/IacEcms", "IacEcms:Record");
		newElement.appendChild(iacEcmsRecord);

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
        		System.out.println("Node Name: " + nodeName);
        	
        		NodeList nodes = document.getElementsByTagName(nodeName);
        		for(int i=0; i< nodes.getLength();i++){
        			Element element = (Element) nodes.item(i);
        			System.out.println(element.getNodeName());
        			String newNodeName = "IacEcms:" + element.getNodeName();
        			System.out.println(newNodeName);
        			renameElement(element, newNodeName);
        		}
        	}	
        }
    }
    
    private static List<String> getAllNodeNamesToList(Node node) {
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
    
    private static Element getFirstElement(Document document) {
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

    private static void renameElement(Element element, String newElementName) {
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

