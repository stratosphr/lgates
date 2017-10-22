package ll.gates;

import java.util.LinkedHashMap;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class Not extends AGate {

    public Not(String name) {
        super(name);
    }

    @Override
    public boolean simulate(long t, LinkedHashMap<AGate, Boolean> gatesOutputs) {
        return !gatesOutputs.get(getInputs()[0]);
    }

}
