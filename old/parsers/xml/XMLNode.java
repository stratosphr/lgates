package parsers.xml;

import formatters.xml.IXMLVisitable;
import formatters.xml.IXMLVisitor;
import formatters.xml.XMLFormatter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 18/09/17.
 * Time : 17:53
 */
public final class XMLNode implements IXMLVisitable {

    private final String name;
    private final HashMap<String, String> attributes;
    private final List<XMLNode> children;

    XMLNode(Element element) {
        this.name = element.getNodeName();
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
        for (int i = 0; i < element.getAttributes().getLength(); i++) {
            attributes.put(element.getAttributes().item(i).getNodeName(), element.getAttributes().item(i).getNodeValue());
        }
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            if (element.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                children.add(new XMLNode((Element) element.getChildNodes().item(i)));
            }
        }
    }

    @Override
    public String accept(IXMLVisitor visitor) {
        return visitor.visit(this);
    }

    public List<XMLNode> getChildren(String tag) {
        return getChildren().stream().filter(node -> node.getName().equals(tag)).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public List<XMLNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return XMLFormatter.format(this);
    }

}
