package ll.gates;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:04
 */
public abstract class AGate {

    private final int hashCode;
    private final String name;
    private AGate[] inputs;
    private final double skew;
    private double voltage;

    public AGate(String name) {
        this(name, ThreadLocalRandom.current().nextDouble(0.01, 0.49));
    }

    public AGate(String name, double skew) {
        this.hashCode = name.hashCode();
        this.name = name;
        this.skew = skew;
        this.voltage = 0;
    }

    public final boolean run(long t, LinkedHashMap<AGate, Boolean> gatesOutputs) {
        return skew(simulate(t, gatesOutputs));
    }

    protected abstract boolean simulate(long t, LinkedHashMap<AGate, Boolean> gatesOutputs);

    private boolean skew(boolean up) {
        if (up) {
            voltage = voltage + skew > 1 ? 1 : voltage + skew;
        } else {
            voltage = voltage - skew < 0 ? 0 : voltage - skew;
        }
        return voltage >= 0.5;
    }

    public String getName() {
        return name;
    }

    public void setInputs(List<AGate> inputs) {
        this.inputs = inputs.toArray(new AGate[inputs.size()]);
    }

    AGate[] getInputs() {
        return inputs;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof AGate && name.equals(((AGate) o).name));
    }

}
