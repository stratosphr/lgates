import ll.Circuit;
import ll.gates.AGate;
import parsers.circuit.CircuitParser;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Circuit circuit = CircuitParser.parse(new File("resources/sample.xml"));
        circuit.simulate(10, gates -> {
            AGate q = gates.keySet().stream().filter(aGate -> aGate.getName().equals("q")).findFirst().orElse(null);
            AGate _q = gates.keySet().stream().filter(aGate -> aGate.getName().equals("\\q")).findFirst().orElse(null);
            if (gates.get(q) != gates.get(_q)) {
                System.out.println(q);
                System.out.println(_q);
                System.out.println("--------------------------------");
            }
        });
    }

}
