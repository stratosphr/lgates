package ll.gates;

import java.util.LinkedHashMap;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class Or extends AGate {

    public Or(String name) {
        super(name);
    }

    @Override
    public boolean simulate(long t, LinkedHashMap<AGate, Boolean> gatesOutputs) {
        //return getInputs().stream().map(gatesOutputs::get).reduce(false, Boolean::logicalOr);
        for (AGate gate : getInputs()) {
            if (gatesOutputs.get(gate)) {
                return true;
            }
        }
        return false;
    }

}
