package forge.ai.llm;

import java.util.*;

// RankedAction is defined in the same package

/**
 * Response from LLM containing ranked actions and reasoning
 */
public class LlmDecision {
    public List<RankedAction> rankedActions;
    public Map<String, Double> confidenceScores;
    public String reasoning;
    public long queryTimeMs;
    
    public LlmDecision() {
        this.rankedActions = new ArrayList<>();
        this.confidenceScores = new HashMap<>();
    }
    
    public RankedAction getTopAction() {
        return rankedActions.isEmpty() ? null : rankedActions.get(0);
    }
    
    public double getTopConfidence() {
        if (rankedActions.isEmpty()) {
            return 0.0;
        }
        return confidenceScores.getOrDefault(rankedActions.get(0).action, 0.0);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LLM Decision:\n");
        for (int i = 0; i < rankedActions.size(); i++) {
            RankedAction action = rankedActions.get(i);
            double confidence = confidenceScores.getOrDefault(action.action, 0.0);
            sb.append(String.format("%d. [%.2f] %s\n", i+1, confidence, action.action));
            if (action.explanation != null && !action.explanation.isEmpty()) {
                sb.append(String.format("   -> %s\n", action.explanation));
            }
        }
        if (reasoning != null && !reasoning.isEmpty()) {
            sb.append("\nReasoning: ").append(reasoning);
        }
        return sb.toString();
    }
}
