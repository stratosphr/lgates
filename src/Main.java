import ll.Circuit;
import parsers.circuit.CircuitParser;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Circuit circuit = CircuitParser.parseCircuit(new File("resources/sample.xml"));
        String[] watchedNames = new String[]{"true1"};
        double duration = 1E-1;
        System.out.printf("%fms\n", duration / 1.0E6);
        circuit.simulate(duration, (t, gates) -> {
            System.out.printf(t + " %fms " + gates.keySet().stream().filter(aGate -> Arrays.stream(watchedNames).anyMatch(name -> aGate.getName().equals(name))).map(gate -> gate + "=" + gates.get(gate)).collect(Collectors.joining(", ")) + "\n", t / 1.0E6);
        }, watchedNames);
        System.out.printf("%fms\n", duration / 1.0E6);
    }

}
