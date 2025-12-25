package forge.ai.llm;

/**
 * A single action ranked by the LLM with confidence score
 */
public class RankedAction {
    public String action;           // Description of the action
    public double score;            // Score from 0-1
    public String explanation;      // Why the LLM chose this action
    public String cardName;         // Associated card name, if any
    
    public RankedAction(String action, double score) {
        this.action = action;
        this.score = score;
        this.explanation = "";
    }
    
    public RankedAction(String action, double score, String explanation) {
        this(action, score);
        this.explanation = explanation;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%.2f)", action, score);
    }
}
