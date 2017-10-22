package ll.gates;

import java.util.Map;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:04
 */
public final class True extends AGate {

    public True(String name) {
        super(name, true);
    }

    @Override
    boolean simulate(Map<AGate, Boolean> gatesValues) {
        return true;
    }

}
