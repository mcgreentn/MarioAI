package ch.idsia.tools;

public class AgentResultObject {

    public int firstAgentResult;
    public int secondAgentResult;
    public double score;

    public AgentResultObject(int first, int second, double score) {
        firstAgentResult = first;
        secondAgentResult = second;
        this.score = score;
    }
}
