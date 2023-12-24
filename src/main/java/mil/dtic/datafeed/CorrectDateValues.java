package mil.dtic.datafeed;

import org.w3c.dom.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class CorrectDateValues {

	public void dateCorrection(Document document) {
        try {
           
            // Update all date and string values in the XML file
            updateValues(document, "Date");
            updateValues(document, "DateTime");
            updateValues(document, "String");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateValues(Document document, String nodeName) {
        NodeList elements = document.getElementsByTagName(nodeName);
        for (int i = 0; i < elements.getLength(); i++) {
            Node node = elements.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String value = element.getTextContent();
                if(!value.isEmpty() && value != null) {
                	String updatedValue = updateDateFormat(value);
	                element.setTextContent(updatedValue);
                }
            }
        }
    }

    private static String updateDateFormat(String dateValue) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = inputFormat.parse(dateValue);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Handle parsing exceptions if needed
            e.printStackTrace();
            return dateValue; // Return original value on error
        }
    }
}
