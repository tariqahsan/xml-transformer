package mil.dtic.datafeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
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
