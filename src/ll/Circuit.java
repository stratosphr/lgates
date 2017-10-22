package ll;

import ll.gates.AGate;
import utilities.ICallBack;
import utilities.Time;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:19
 */
public final class Circuit {

    private final AGate[] gates;
    private volatile AtomicBoolean output;
    private volatile AtomicBoolean changed;
    private volatile AtomicBoolean watchedChanged;
    private volatile AtomicLong t;

    @SuppressWarnings("WeakerAccess")
    public Circuit(LinkedHashMap<String, AGate> gates) {
        this.gates = gates.values().toArray(new AGate[gates.size()]);
        this.output = new AtomicBoolean();
        this.changed = new AtomicBoolean();
        this.watchedChanged = new AtomicBoolean();
        this.watchedChanged = new AtomicBoolean();
        this.t = new AtomicLong();
    }

    @SuppressWarnings("SameParameterValue")
    public void simulate(double duration, ICallBack callBack, String... watchedNames) {
        long end = Math.round(duration * 1E9);
        LinkedHashMap<AGate, Boolean> oldOutputs = new LinkedHashMap<>();
        for (AGate gate : gates) {
            oldOutputs.put(gate, false);
        }
        LinkedHashMap<AGate, Boolean> newOutputs = new LinkedHashMap<>(oldOutputs);
        boolean output;
        boolean changed = false;
        boolean watchedChanged = false;
        LinkedHashSet<AGate> watchedGates = Arrays.stream(gates).filter(gate -> Arrays.stream(watchedNames).anyMatch(name -> gate.getName().equals(name))).collect(Collectors.toCollection(LinkedHashSet::new));
        long now = Time.now();
        for (long t = 0; t <= end; ++t) {
            for (AGate gate : gates) {
                output = gate.run(t, oldOutputs);
                if (output != newOutputs.put(gate, output)) {
                    changed = true;
                    watchedChanged |= watchedGates.contains(gate);
                }
            }
            if (changed) {
                if (watchedChanged || watchedGates.isEmpty()) {
                    callBack.apply(t, newOutputs);
                    watchedChanged = false;
                }
                newOutputs.forEach(oldOutputs::put);
                changed = false;
            }
        }
        System.out.println((Time.now() - now) / 1.0E9);
    }

}
