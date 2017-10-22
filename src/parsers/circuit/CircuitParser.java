package parsers.circuit;

import ll.Circuit;
import ll.gates.*;
import parsers.xml.XMLDocument;
import parsers.xml.XMLNode;
import parsers.xml.XMLParser;
import utilities.Pair;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utilities.Chars.NL;
import static utilities.Chars.TAB;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 18:49
 */
public final class CircuitParser {

    private final List<File> includedFiles;
    private final LinkedHashMap<String, List<String>> chipsetsInputs;
    private final LinkedHashMap<String, List<String>> chipsetsOutputs;
    private final LinkedHashMap<String, XMLNode> chipsetsComponents;
    private LinkedHashMap<String, String> links;

    private CircuitParser() {
        includedFiles = new ArrayList<>();
        chipsetsOutputs = new LinkedHashMap<>();
        chipsetsInputs = new LinkedHashMap<>();
        chipsetsComponents = new LinkedHashMap<>();
        links = new LinkedHashMap<>();
    }

    private static void check(XMLNode node, String... expected) {
        if (Arrays.stream(expected).noneMatch(s -> s.equals(node.getName()))) {
            throw new Error("Error: node with type \"" + node.getName() + "\" should have one of the following types:\n" + Arrays.stream(expected).map(type -> TAB + type).collect(Collectors.joining(NL)));
        }
    }

    public static Circuit parseCircuit(File file) {
        XMLDocument doc = XMLParser.parse(file, new File("resources/xsd/circuit.xsd"));
        CircuitParser parser = new CircuitParser();
        return parser.parseCircuit(doc.getRoot(), file);
    }

    private Circuit parseCircuit(XMLNode node, File file) {
        check(node, "circuit");
        List<XMLNode> includesNode = node.getChildren("includes");
        if (!includesNode.isEmpty()) {
            parseIncludes(includesNode.get(0), file);
        }
        LinkedHashMap<String, AGate> gates = new LinkedHashMap<>();
        LinkedHashMap<AGate, List<String>> inputsNames = new LinkedHashMap<>();
        List<Pair<AGate, List<String>>> components = parseComponents(node.getChildren("components").get(0));
        components.forEach(pair -> {
            gates.put(pair.getFirst().getName(), pair.getFirst());
            inputsNames.put(pair.getFirst(), pair.getSecond());
        });
        inputsNames.forEach((gate, names) -> {
            List<AGate> inputs = new ArrayList<>();
            names.forEach(name -> {
                if (!gates.containsKey(name) && !links.containsKey(name)) {
                    throw new Error("Error: gate \"" + name + "\" was not declared in this scope.");
                } else {
                    if (gates.containsKey(name)) {
                        inputs.add(gates.get(name));
                    } else {
                        inputs.add(gates.get(links.get(name)));
                    }
                }
            });
            gate.setInputs(inputs);
        });
        return new Circuit(gates);
    }

    private List<Pair<AGate, List<String>>> parseComponents(XMLNode node) {
        return parseComponents(node, "");
    }

    private List<Pair<AGate, List<String>>> parseComponents(XMLNode node, String chip) {
        check(node, "components");
        return node.getChildren().stream().map(child -> parseAGate(child, chip)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<Pair<AGate, List<String>>> parseAGate(XMLNode node, String chip) {
        check(node, "false", "true", "clock", "not", "and", "or", "chip");
        List<Pair<AGate, List<String>>> pairs = new ArrayList<>();
        String gateName = chip + (chip.isEmpty() ? "" : ".") + node.getAttributes().get("name");
        List<String> inputsNames = new ArrayList<>();
        if (!node.getName().equals("chip")) {
            inputsNames = node.getChildren().stream().map(child -> parseAInput(child, chip)).collect(Collectors.toList());
        }
        switch (node.getName()) {
            case "false":
                pairs.add(new Pair<>(new False(gateName), inputsNames));
                break;
            case "true":
                pairs.add(new Pair<>(new True(gateName), inputsNames));
                break;
            case "clock":
                Clock clock = node.getAttributes().containsKey("duty") ? new Clock(gateName, node.getAttributes().get("init").equals("true"), Double.parseDouble(node.getAttributes().get("freq")), Double.parseDouble(node.getAttributes().get("duty"))) : new Clock(gateName, node.getAttributes().get("init").equals("true"), Double.parseDouble(node.getAttributes().get("freq")));
                pairs.add(new Pair<>(clock, inputsNames));
                break;
            case "not":
                pairs.add(new Pair<>(new Not(gateName), inputsNames));
                break;
            case "and":
                pairs.add(new Pair<>(new And(gateName), inputsNames));
                break;
            case "or":
                pairs.add(new Pair<>(new Or(gateName), inputsNames));
                break;
            case "chip":
                pairs.addAll(parseChip(node, chip));
                break;
            default:
                throw new Error("Error: node with type \"" + node.getName() + "\" should have one of the following types:\n" + Stream.of("true", "false", "clock", "not", "and", "or", "chip").map(type -> TAB + type).collect(Collectors.joining(NL)));
        }
        return pairs;
    }

    private List<Pair<AGate, List<String>>> parseChip(XMLNode node, String chip) {
        check(node, "chip");
        String prefix = (chip.isEmpty() ? "" : chip + ".");
        String type = node.getAttributes().get("type");
        String name = node.getAttributes().get("name");
        if (!chipsetsComponents.containsKey(type)) {
            throw new Error("Error: undefined reference to chipset \"" + type + "\" for chip \"" + prefix + name + "\".");
        }
        node.getChildren("link").forEach(link -> {
            XMLNode input = link.getChildren().get(0);
            String inputName = prefix + (input.getName().equals("gate") ? input.getAttributes().get("name") : input.getAttributes().get("chip") + "." + input.getAttributes().get("name"));
            links.put(prefix + name + "." + link.getChildren().get(1).getAttributes().get("name"), inputName);
        });
        return parseComponents(chipsetsComponents.get(type), prefix + name);
    }

    private String parseAInput(XMLNode node) {
        return parseAInput(node, "");
    }

    private String parseAInput(XMLNode node, String chip) {
        check(node, "gate", "out");
        String prefix = chip.isEmpty() ? "" : chip + ".";
        switch (node.getName()) {
            case "gate":
                return prefix + node.getAttributes().get("name");
            case "out":
                return prefix + node.getAttributes().get("chip") + "." + node.getAttributes().get("name");
            default:
                throw new Error("Error: node with type \"" + node.getName() + "\" should have one of the following types:\n" + Stream.of("gate", "out").map(type -> TAB + type).collect(Collectors.joining(NL)));
        }
    }

    private void parseChipset(File file) {
        XMLDocument doc;
        try {
            doc = XMLParser.parse(file, new File("resources/xsd/chipset.xsd"));
        } catch (Error e) {
            throw new Error("Error: included file \"" + file.getAbsolutePath() + "\" does not correspond to a chip definition." + NL + e.fillInStackTrace());
        }
        parseChipset(doc.getRoot(), file);
    }

    private void parseChipset(XMLNode node, File file) {
        node.getChildren("include").forEach(child -> parseInclude(child, file));
        String name = node.getAttributes().get("name");
        List<XMLNode> includesNode = node.getChildren("includes");
        if (!includesNode.isEmpty()) {
            parseIncludes(includesNode.get(0), file);
        }
        chipsetsInputs.put(name, parseInputs(node.getChildren("inputs").get(0)));
        chipsetsOutputs.put(name, parseOutputs(node.getChildren("outputs").get(0)));
        chipsetsComponents.put(name, node.getChildren("components").get(0));
    }

    private List<String> parseInputs(XMLNode node) {
        check(node, "inputs");
        return node.getChildren().stream().map(this::parseInput).collect(Collectors.toList());
    }

    private String parseInput(XMLNode node) {
        check(node, "input");
        return node.getAttributes().get("name");
    }

    private List<String> parseOutputs(XMLNode node) {
        check(node, "outputs");
        return node.getChildren().stream().map(this::parseOutput).collect(Collectors.toList());
    }

    private String parseOutput(XMLNode node) {
        check(node, "output");
        return node.getAttributes().get("name");
    }

    private void parseIncludes(XMLNode node, File file) {
        check(node, "includes");
        node.getChildren("include").forEach(child -> parseInclude(child, file));
    }

    private void parseInclude(XMLNode node, File file) {
        check(node, "include");
        File includedFile = new File(file.getParentFile(), node.getAttributes().get("file"));
        if (includedFile.equals(file)) {
            throw new Error("Error: unable to include \"" + includedFile.getAbsolutePath() + "\" in itself.");
        }
        if (!includedFiles.contains(includedFile)) {
            if (!includedFile.exists()) {
                throw new Error("Error: file \"" + includedFile.getName() + "\" included from file \"" + file.getAbsolutePath() + "\" does not exist in directory \"" + includedFile.getParentFile().getAbsolutePath() + "\".");
            }
            includedFiles.add(includedFile);
            parseChipset(includedFile);
        }
    }

}
