import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

public class XmlTransformation {
    private static final Logger logger = LogManager.getLogger(XmlTransformation.class);
    private static final String INPUTFILE_PATH = "InputFile_Location";
    private static final String OUTPUTFILE_PATH = "OutputFile_Location";
    private static final String PROPERTY_FILE_LOCATION ="config.properties";

    public static void main(String[] args){
        Properties getProperties = new Properties();
        InputStream inputXml=null;
        System.out.println("Enter InputXMLFile Name");
        Scanner inputXMLFilename = new Scanner(System.in);
        String xmlFileName = inputXMLFilename.nextLine();

        try(InputStream inputStream = XmlTransformation.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_LOCATION);
            FileInputStream inputXmlFile = new FileInputStream(getFileLocation(getProperties,inputStream,INPUTFILE_PATH)+"\\"+xmlFileName);
            OutputStream outputXML = new FileOutputStream(getFileLocation(getProperties,inputStream,OUTPUTFILE_PATH)+"\\"+"Transformed.xml")){
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputXmlFile);
            XPathExpression expression = XPathFactory.newInstance().newXPath().compile("//code/content");
            NodeList list = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
            for(int i=0; i<list.getLength();i++) {
                list.item(i).getParentNode().removeChild(list.item(i));
            }
            logger.info("Transforming XML");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputXML);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            logger.info("Transformation Completed");
        } catch (ParserConfigurationException | IOException | TransformerException | XPathExpressionException | SAXException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getFileLocation(Properties properties,InputStream inPut,String location) throws IOException {
        String filePath;
        if(inPut !=null){
            properties.load(inPut);
            filePath = properties.getProperty(location);
        }else {
            throw new FileNotFoundException("Property File not Found");
        }
        return filePath;
    }
}
