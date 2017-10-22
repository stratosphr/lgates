package ll;

import ll.gates.AGate;
import utilities.ICallBack;
import utilities.Time;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:19
 */
public final class Circuit {

    private final List<AGate> gates;
    private long granularity;

    public Circuit(LinkedHashMap<String, AGate> gates) {
        this(gates, 10000000);
    }

    @SuppressWarnings("WeakerAccess")
    public Circuit(LinkedHashMap<String, AGate> gates, long granularity) {
        this.gates = new ArrayList<>(gates.values());
        this.granularity = granularity;
    }

    @SuppressWarnings("SameParameterValue")
    public void simulate(double seconds, ICallBack callBack) {
        LinkedHashMap<AGate, Boolean> oldOutputs = new LinkedHashMap<>();
        LinkedHashMap<AGate, Boolean> newOutputs;
        gates.forEach(gate -> oldOutputs.put(gate, false));
        newOutputs = new LinkedHashMap<>(oldOutputs);
        long startTime = Time.now();
        System.out.println("init");
        System.out.println(oldOutputs);
        while (Time.now() - startTime <= seconds * 1.0E09 + granularity) {
            gates.forEach(gate -> newOutputs.put(gate, gate.run(newOutputs)));
            if (!newOutputs.equals(oldOutputs)) {
                callBack.apply(newOutputs);
            }
            newOutputs.forEach(oldOutputs::put);
        }
        gates.forEach(AGate::reset);
    }

}
