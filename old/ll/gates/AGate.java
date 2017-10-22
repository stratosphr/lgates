package ll.gates;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:04
 */
public abstract class AGate {

    private final String name;
    private final boolean start;
    private List<AGate> inputs;
    private boolean output;

    AGate(String name, boolean start) {
        this.name = name;
        this.start = start;
    }

    private final boolean run() {
        return run(Collections.emptyMap());
    }

    public final boolean run(Map<AGate, Boolean> gatesValues) {
        return (output = simulate(gatesValues));
    }

    abstract boolean simulate(Map<AGate, Boolean> gatesValues);

    public void reset() {
    }

    public final void setInputs(List<AGate> inputs) {
        this.inputs = inputs;
    }

    public final String getName() {
        return name;
    }

    final List<AGate> getInputs() {
        return inputs;
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof AGate && name.equals(((AGate) o).getName());
    }

    @Override
    public final String toString() {
        return name + "[" + output + "]";
    }

    public boolean getStartOutput() {
        return start;
    }

}
