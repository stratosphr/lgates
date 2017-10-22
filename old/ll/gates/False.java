package ll.gates;

import java.util.Map;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:04
 */
public final class False extends AGate {

    public False(String name) {
        super(name, false);
    }

    @Override
    boolean simulate(Map<AGate, Boolean> gatesValues) {
        return false;
    }

}
