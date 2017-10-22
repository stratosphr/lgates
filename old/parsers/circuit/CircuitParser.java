package parsers.circuit;

import ll.Circuit;
import ll.gates.*;
import parsers.xml.XMLDocument;
import parsers.xml.XMLNode;
import parsers.xml.XMLParser;
import utilities.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utilities.Chars.NL;
import static utilities.Chars.TAB;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 18:49
 */
public final class CircuitParser {

    private static void check(XMLNode node, String... expected) {
        if (Arrays.stream(expected).noneMatch(s -> s.equals(node.getName()))) {
            throw new Error("Error: node with type \"" + node.getName() + "\" should have one of the following types:\n" + Arrays.stream(expected).map(type -> TAB + type).collect(Collectors.joining(NL)));
        }
    }

    public static Circuit parse(File file) {
        XMLDocument doc = XMLParser.parse(file, new File("resources/ll.xsd"));
        return parseCircuit(doc.getRoot());
    }

    private static Circuit parseCircuit(XMLNode root) {
        check(root, "circuit");
        LinkedHashMap<String, AGate> gates = new LinkedHashMap<>();
        LinkedHashMap<AGate, List<String>> inputsNames = new LinkedHashMap<>();
        root.getChildren().forEach(node -> {
            Pair<AGate, List<String>> pair = parseAGate(node);
            if (gates.containsKey(pair.getFirst().getName())) {
                throw new Error("Error: gate \"" + pair.getFirst().getName() + "\" was already declared in this scope.");
            } else {
                gates.put(pair.getFirst().getName(), pair.getFirst());
            }
            inputsNames.put(pair.getFirst(), pair.getSecond());
        });
        inputsNames.forEach((gate, names) -> {
            List<AGate> inputs = new ArrayList<>();
            names.forEach(name -> {
                if (!gates.containsKey(name)) {
                    throw new Error("Error: gate \"" + name + "\" was not declared in this scope.");
                } else {
                    inputs.add(gates.get(name));
                }
            });
            gate.setInputs(inputs);
        });
        return root.getAttributes().containsKey("granularity") ? new Circuit(gates, Long.parseLong(root.getAttributes().get("granularity"))) : new Circuit(gates);
    }

    private static Pair<AGate, List<String>> parseAGate(XMLNode node) {
        String name = node.getAttributes().get("name");
        AGate gate;
        switch (node.getName()) {
            case "true":
                gate = new True(name);
                break;
            case "false":
                gate = new False(name);
                break;
            case "clock":
                if (!node.getAttributes().containsKey("high")) {
                    gate = new Clock(name, Boolean.parseBoolean(node.getAttributes().get("start")), Double.parseDouble(node.getAttributes().get("freq")));
                } else {
                    gate = new Clock(name, Boolean.parseBoolean(node.getAttributes().get("start")), Double.parseDouble(node.getAttributes().get("freq")), Double.parseDouble(node.getAttributes().get("high")));
                }
                break;
            case "not":
                gate = new Not(name);
                break;
            case "and":
                gate = new And(name);
                break;
            case "or":
                gate = new Or(name);
                break;
            default:
                throw new Error("Error: node with type \"" + node.getName() + "\" should have one of the following types:\n" + Stream.of("true", "false", "clock", "not", "and", "or").map(type -> TAB + type).collect(Collectors.joining(NL)));
        }
        return new Pair<>(gate, node.getChildren().stream().map(child -> child.getAttributes().get("name")).collect(Collectors.toList()));
    }

}
