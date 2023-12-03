package mil.dtic.datafeed;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SpringBootApplication
public class XmlTransformerApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(XmlTransformerApplication.class, args);
		
		// Access command line arguments here
		//String filePath = "EF003062.xml";
		String filePath = args[0];
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			//update attribute value
			updateAttributeValue(doc); // executing this one only for the test

			//update Element value
			//updateElementValue(doc);

			//delete element
			//deleteElement(doc);

			//add new element
			//addElement(doc);

			//write the updated document to file or console
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			//                StreamResult result = new StreamResult(new File("Transformed-EF003062.xml"));
			StreamResult result = new StreamResult(new File(args[1]));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			System.out.println("XML file updated successfully");

		} catch (SAXException | ParserConfigurationException | IOException | TransformerException e1) {
			e1.printStackTrace();
		}

	}

	private static void deleteElement(Document doc) {

		NodeList ureds = doc.getElementsByTagName("meta:Metadata");
		Element ured = null;
		//Iterate
		for(int i=0; i<ureds.getLength();i++){
			ured = (Element) ureds.item(i);
			Node genderNode = ured.getElementsByTagName("meta:DODFundingSources").item(0);
			ured.removeChild(genderNode);
		}

	}

	private static void updateElementValue(Document doc) {

		NodeList ureds = doc.getElementsByTagName("meta:Metadata");
		Element ured = null;

		//Iterate
		for(int i=0; i<ureds.getLength();i++){
			ured = (Element) ureds.item(i);
			Node name = ured.getElementsByTagName("meta:DODFundingSources").item(0).getFirstChild();
			name.setNodeValue(name.getNodeValue().toUpperCase());
		}
	}

	private static void updateAttributeValue(Document doc) {

		NodeList ureds = doc.getElementsByTagName("meta:Metadata");
		Element ured = null;

		for(int i=0; i<ureds.getLength();i++){
			ured = (Element) ureds.item(i);

			//System.out.println(ured.getTextContent());
			Node ingestionDateNode = ured.getElementsByTagName("meta:IngestionDate").item(0).getFirstChild();
			String ingestionDate = ured.getElementsByTagName("meta:IngestionDate").item(0).getFirstChild().getNodeValue();
			System.out.println(ingestionDate);
			// Just a one liner regex. There are better way to check date validation
			boolean isValidFormat = ingestionDate.matches("([0-9]{4})-([0-9]{2})/([0-9]{2})");
			// Check if the date value is invalid then correct it
			if(!isValidFormat) {

				// Parse the original date string
				LocalDateTime dateTime = LocalDateTime.parse(ingestionDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

				// Format the modified date string
				String validIngestionDate = dateTime.format(DateTimeFormatter.ISO_DATE);

				// Print the modified date string
				System.out.println(validIngestionDate);
				ingestionDateNode.setNodeValue(validIngestionDate);
			}
			System.out.println(isValidFormat);
		}
	}

}
