package formatters;

import java.util.Collections;

import static utilities.Chars.NL;
import static utilities.Chars.TAB;

/**
 * Created by gvoiron on 14/09/17.
 * Time : 11:36
 */
public abstract class AFormatter {

    private int indentation;

    protected AFormatter() {
        this.indentation = 0;
    }

    protected String indentLeft() {
        if (indentation >= 1) {
            --indentation;
        }
        return "";
    }

    protected String indentRight() {
        ++indentation;
        return "";
    }

    private String indentation() {
        return String.join("", Collections.nCopies(indentation, TAB));
    }

    protected String indent(String text) {
        return indentation() + text;
    }

    protected String line() {
        return line("");
    }

    protected String line(String text) {
        return text + NL;
    }

    protected String indentLine(String text) {
        return indent(line(text));
    }

}
