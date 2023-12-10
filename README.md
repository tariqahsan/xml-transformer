# After building the xml-transformer.jar by mvn clean install
# run the xml-transformer.jar with the arguments from the command line -
java -jar xml-transformer.jar <directory of the zip file> <base directory where the output xml and pdf files will be stored>

e.g.

java -jar C:\Dev\XMLTransformer\target\xml-transformer.jar 
C:\Dev\XMLTransformer\IAC2019-12-03.zip
C:\Dev\XMLTransformer\output

The generated XML files for each <Record> node of <Records> node in ecms.xml will have the name of <AccessionNumber> and will be placed in C:\Dev\XMLTransformer\output\xml_<timestamp> folder 
e.g 
C:\Dev\XMLTransformer\output\xml_20231210154514\AD1000282.xml
C:\Dev\XMLTransformer\output\xml_20231210154514\AD1000283.xml
C:\Dev\XMLTransformer\output\xml_20231210154514\AD1000285.xml
C:\Dev\XMLTransformer\output\xml_20231210154514\AD1000286.xml

The PDF files of zip file 
e.g. the above C:\Dev\XMLTransformer\IAC2019-12-03.zip containing the PDF files will be copied into -
C:\Dev\XMLTransformer\output\pdf_20231210154514\AD1000282.pdf
C:\Dev\XMLTransformer\output\pdf_20231210154514\AD1000283.pdf
C:\Dev\XMLTransformer\output\pdf_20231210154514\AD1000285.pdf
C:\Dev\XMLTransformer\output\pdf_20231210154514\AD1000286.pdf

