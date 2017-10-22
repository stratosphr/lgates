package ll.gates;

import java.util.Map;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class And extends AGate {

    public And(String name) {
        super(name, false);
    }

    @Override
    synchronized boolean simulate(Map<AGate, Boolean> gatesValues) {
        return getInputs().stream().map(gatesValues::get).reduce(true, Boolean::logicalAnd);
    }

}
