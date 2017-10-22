package ll.gates;

import java.util.Map;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class Not extends AGate {

    public Not(String name) {
        super(name, true);
    }

    @Override
    boolean simulate(Map<AGate, Boolean> gatesValues) {
        return !gatesValues.get(getInputs().get(0));
    }

}
