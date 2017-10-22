package ll.gates;

import java.util.LinkedHashMap;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class And extends AGate {

    public And(String name) {
        super(name);
    }

    @Override
    public boolean simulate(long t, LinkedHashMap<AGate, Boolean> gatesOutputs) {
        for (AGate gate : getInputs()) {
            if (!gatesOutputs.get(gate)) {
                return false;
            }
        }
        return getInputs().length != 0;
    }

}
