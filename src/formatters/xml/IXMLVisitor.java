package formatters.xml;

import parsers.xml.XMLDocument;
import parsers.xml.XMLNode;

/**
 * Created by gvoiron on 19/09/17.
 * Time : 09:43
 */
public interface IXMLVisitor {

    String visit(XMLDocument xmlDocument);

    String visit(XMLNode xmlNode);

}
