package parsers.xml;

import formatters.xml.IXMLVisitable;
import formatters.xml.IXMLVisitor;
import formatters.xml.XMLFormatter;
import org.w3c.dom.Document;

/**
 * Created by gvoiron on 18/09/17.
 * Time : 10:12
 */
public final class XMLDocument implements IXMLVisitable {

    private final XMLNode root;

    XMLDocument(Document document) {
        document.getDocumentElement().normalize();
        this.root = new XMLNode(document.getDocumentElement());
    }

    @Override
    public String accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }

    public XMLNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return XMLFormatter.format(this);
    }

}
