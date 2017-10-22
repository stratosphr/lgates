package ll.gates;

import java.util.LinkedHashMap;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class Clock extends AGate {

    private final boolean init;
    private final long freq;
    private final long duty;
    private final long period;
    private long start;

    public Clock(String name, boolean init, double freq) {
        this(name, init, freq, 0.5 / freq);
    }

    public Clock(String name, boolean init, double freq, double duty) {
        super(name);
        this.init = init;
        this.freq = Math.round(freq * 1E9);
        this.duty = Math.round(duty * 1E9);
        this.period = Math.round(1.0 / freq * 1E9);
        this.start = 0;
        if (this.duty > period) {
            throw new Error("Duty cycle (" + this.duty / 1.0E9 + "s) cannot be greater than the clock period (" + this.period / 1.0E9 + "s).");
        }
        if (this.duty == 0) {
            throw new Error("Duty cycle should be greater than 0.");
        }
    }

    @Override
    public boolean simulate(long t, LinkedHashMap<AGate, Boolean> gatesOutputs) {
        if (period - duty == 0) {
            return true;
        } else if (t % period == 0) {
            start = t;
            return init;
        } else if (init) {
            return t - start < duty;
        } else {
            return t - start >= period - duty;
        }
    }

}
