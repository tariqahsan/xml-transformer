package mil.dtic.datafeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

//@SpringBootApplication
public class XmlTransformerApplication {

	public static void main(String[] args) {
		
		//SpringApplication.run(XmlTransformerApplication.class, args);
		
		String zipFilePath = "C:\\Users\\Tariq Ahsan\\Desktop\\Training\\Selenium\\Workspace\\XMLTransformer\\TR_2020-08-27.zip";
		String targetPDFDirectory = "C:\\Users\\Tariq Ahsan\\Desktop\\Training\\Selenium\\Workspace\\XMLTransformer\\pdf";
		String targetXMLDirectory = "C:\\Users\\Tariq Ahsan\\Desktop\\Training\\Selenium\\Workspace\\XMLTransformer\\xml";
		String targetBaseDirectory = "C:\\Users\\Tariq Ahsan\\Desktop\\Training\\Selenium\\Workspace\\XMLTransformer";
	

		try {
			// Unzip the folder
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String pdfTargetDirectory = targetBaseDirectory + File.separator + "pdf_" + timestamp;
			String xmlTargetDirectory = targetBaseDirectory + File.separator + "xml_" + timestamp;
			File xmldir = new File(xmlTargetDirectory);
	        if (!xmldir.exists()) xmldir.mkdirs();
	        File pdfdir = new File(pdfTargetDirectory);
	        if (!pdfdir.exists()) pdfdir.mkdirs();

			unzipAndCopyFiles(zipFilePath, pdfTargetDirectory, xmlTargetDirectory);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unzipAndCopyFiles(String zipFilePath, String pdfTargetDirectory, String xmlTargetDirectory) throws IOException {
		try (ZipFile zipFile = new ZipFile(zipFilePath)) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();
				int lastIndexOfSlash = entryName.lastIndexOf("\\");
				if (lastIndexOfSlash != -1) {
					entryName = entryName.substring(lastIndexOfSlash + 1);
				}

				// Check if the entry is a file
				if (!entryName.isEmpty()) {
					// Copy PDF files
					if (entryName.toLowerCase().endsWith(".pdf")) {
						copyFile(zipFile.getInputStream(entry), pdfTargetDirectory, entryName);
					}

					// Copy XML files
					if (entryName.toLowerCase().endsWith(".xml")) {
						copyFile(zipFile.getInputStream(entry), xmlTargetDirectory, entryName);
						processXMLFile(xmlTargetDirectory + File.separator + entryName, xmlTargetDirectory);
					}
				}
			}
		}

		}
			private static void copyFile(InputStream inputStream, String targetDirectory, String entryName) throws IOException {
				String targetPath = targetDirectory + File.separator + entryName;
				try (OutputStream outputStream = new FileOutputStream(targetPath)) {
					byte[] buffer = new byte[1024];
					int length;
					while ((length = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, length);
					}
				}
			}

			private static void processXMLFile(String xmlFilePath, String targetXMLDirectory) {
				try {
					// Load the XML document
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new File(xmlFilePath));

					// Process each 'Record' node under 'Records'
					processRecordNodes(document, targetXMLDirectory);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			private static void processRecordNodes(Document document, String targetXMLDirectory) throws Exception {
				// Get the root element
				Element rootElement = document.getDocumentElement();

				// Find 'Records' node
				NodeList recordsList = rootElement.getElementsByTagName("Records");
				if (recordsList.getLength() > 0) {
					Element recordsElement = (Element) recordsList.item(0);

					// Process each 'Record' node under 'Records'
					NodeList recordNodes = recordsElement.getElementsByTagName("Record");
					for (int i = 0; i < recordNodes.getLength(); i++) {
						Element recordElement = (Element) recordNodes.item(i);

						// Get the value of the 'AccessionNumber' node
						String accessionNumber = getAccessionNumber(recordElement);

						if (accessionNumber != null && !accessionNumber.isEmpty()) {
							// Create a new document containing only the current 'Record' node and its descendants
							Document newDocument = createNewDocument(recordElement);

							// Save the new document to a separate file named after the 'AccessionNumber' value
							String targetFilePath = targetXMLDirectory + File.separator + accessionNumber + ".xml";
							saveXmlDocument(newDocument, targetFilePath);
						}
					}
				}
			}

			private static String getAccessionNumber(Element recordElement) {
				// Find the 'AccessionNumber' node under the current 'Record' node
				NodeList accessionNumberList = recordElement.getElementsByTagName("AccessionNumber");
				if (accessionNumberList.getLength() > 0) {
					return accessionNumberList.item(0).getTextContent().trim();
				}
				return null;
			}

			private static Document createNewDocument(Element rootElement) throws Exception {
				// Create a new document
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document newDocument = builder.newDocument();

				// Import the current 'Record' node and its descendants into the new document
				Node importedNode = newDocument.importNode(rootElement, true);
				newDocument.appendChild(importedNode);

				return newDocument;
			}

			private static void saveXmlDocument(Document document, String filePath) throws Exception {
				// Use a Transformer to save the document to a new file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(filePath));
				transformer.transform(source, result);
			}
		}

