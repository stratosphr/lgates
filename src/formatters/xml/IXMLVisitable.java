package formatters.xml;

/**
 * Created by gvoiron on 19/09/17.
 * Time : 09:42
 */
public interface IXMLVisitable {

    String accept(IXMLVisitor visitor);

}
