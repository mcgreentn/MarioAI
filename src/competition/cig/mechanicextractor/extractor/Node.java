package competition.cig.mechanicextractor.extractor;

import java.util.ArrayList;

public class Node {

    private ArrayList<Node> inputs;
    private ArrayList<Node> outputs;
    private String name;

    public Node() {
        outputs = new ArrayList<Node>();
        inputs = new ArrayList<Node>();
    }

    public Node(String name) {
        this.name = name;
    }
    /**
     * Sets the name of this entity
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this entity
     * @return
     */
    public String getName() {
        return name;
    }


    public void addOutput(Node out) {
        outputs.add(out);
    }

    public ArrayList<Node> getOutputs() {
        return outputs;
    }

    public void addInput(Node in) {
        inputs.add(in);
    }

    public ArrayList<Node> getInputs() {
        return inputs;
    }


}
