package parsers.xml;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

/**
 * Created by gvoiron on 18/09/17.
 * Time : 09:49
 */
public final class XMLParser {

    private XMLParser() {
    }

    public static XMLDocument parse(File xmlFile, File xsdFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = sf.newSchema(xsdFile);
            factory.setSchema(schema);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler());
            return new XMLDocument(builder.parse(xmlFile));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new Error(e);
        }
    }

}
