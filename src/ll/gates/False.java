package ll.gates;

import java.util.LinkedHashMap;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:04
 */
public final class False extends AGate {

    public False(String name) {
        super(name, 1);
    }

    @Override
    public boolean simulate(long t, LinkedHashMap<AGate, Boolean> gatesOutputs) {
        return false;
    }

}
