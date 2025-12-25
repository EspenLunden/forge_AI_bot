package forge.ai.llm;

import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Local LLM client for testing without external API calls
 * Can be replaced with actual OpenAI/Claude client
 */
public class LocalLlmClient implements LlmClient {
    
    private static final String NAME = "LocalLLM";
    private final String trainingDataPath;
    private boolean logTrainingData = false;
    
    public LocalLlmClient() {
        this("./llm_training_data/");
    }
    
    public LocalLlmClient(String trainingDataPath) {
        this.trainingDataPath = trainingDataPath;
        // Create training data directory
        try {
            Files.createDirectories(Paths.get(trainingDataPath));
        } catch (IOException e) {
            System.err.println("Failed to create training data directory: " + e.getMessage());
        }
    }
    
    @Override
    public LlmDecision queryForDecision(String gameState, List<String> availableActions, GameContext context) {
        long startTime = System.currentTimeMillis();
        
        LlmDecision decision = new LlmDecision();
        
        // Simple heuristic-based decision making for demo purposes
        // In production, this would call actual LLM API
        List<String> rankedActions = rankActionsLocally(availableActions, context);
        
        for (int i = 0; i < rankedActions.size(); i++) {
            String action = rankedActions.get(i);
            double confidence = 1.0 - (i * 0.15);  // Decreasing confidence
            confidence = Math.max(0.1, confidence);
            
            RankedAction ranked = new RankedAction(action, confidence);
            ranked.explanation = getExplanationForAction(action, context);
            
            decision.rankedActions.add(ranked);
            decision.confidenceScores.put(action, confidence);
        }
        
        decision.reasoning = generateReasoning(context);
        decision.queryTimeMs = System.currentTimeMillis() - startTime;
        
        return decision;
    }
    
    @Override
    public void logDecision(String gameState, String chosenAction, double confidence) {
        if (!logTrainingData) return;
        
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = trainingDataPath + "decision_" + timestamp + ".json";
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(timestamp).append(",\n");
            json.append("  \"chosen_action\": \"").append(escapeJson(chosenAction)).append("\",\n");
            json.append("  \"confidence\": ").append(confidence).append(",\n");
            json.append("  \"game_state\": ").append(gameState).append("\n");
            json.append("}\n");
            
            Files.write(Paths.get(filename), json.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Failed to log decision: " + e.getMessage());
        }
    }
    
    @Override
    public void provideFeedback(boolean won, int turnsPlayed) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = trainingDataPath + "feedback_" + timestamp + ".json";
            
            String json = String.format(
                "{\"timestamp\": %d, \"won\": %b, \"turns\": %d}\n",
                System.currentTimeMillis(), won, turnsPlayed
            );
            
            Files.write(Paths.get(filename), json.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to log feedback: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isAvailable() {
        return true;  // Local client always available
    }
    
    @Override
    public String getName() {
        return NAME;
    }
    
    public void setLogTrainingData(boolean log) {
        this.logTrainingData = log;
    }
    
    // Helper methods
    
    private List<String> rankActionsLocally(List<String> actions, GameContext context) {
        // Simple scoring: prioritize actions based on keywords
        List<ActionScore> scored = new ArrayList<>();
        
        for (String action : actions) {
            double score = 0.5;  // Base score
            
            // Prioritize attacking when ahead
            if (action.toLowerCase().contains("attack")) {
                score += (context.lifeTotal >= context.opponentLifeTotal) ? 0.3 : -0.1;
            }
            // Prioritize defense when behind
            else if (action.toLowerCase().contains("block") || action.toLowerCase().contains("defense")) {
                score += (context.lifeTotal < context.opponentLifeTotal) ? 0.3 : -0.1;
            }
            // Prioritize card draw
            else if (action.toLowerCase().contains("draw")) {
                score += 0.2;
            }
            // Prioritize removal of threats
            else if (action.toLowerCase().contains("destroy") || action.toLowerCase().contains("remove")) {
                score += 0.15;
            }
            // Penalize passing
            else if (action.toLowerCase().contains("pass")) {
                score -= 0.1;
            }
            
            scored.add(new ActionScore(action, score));
        }
        
        // Sort by score descending
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<String> result = new ArrayList<>();
        for (ActionScore as : scored) {
            result.add(as.action);
        }
        return result;
    }
    
    private String getExplanationForAction(String action, GameContext context) {
        if (action.toLowerCase().contains("attack")) {
            return "Aggressive play to close out the game";
        } else if (action.toLowerCase().contains("block")) {
            return "Defensive play to mitigate damage";
        } else if (action.toLowerCase().contains("draw")) {
            return "Card advantage to improve hand quality";
        } else if (action.toLowerCase().contains("destroy")) {
            return "Remove opponent's threats";
        }
        return "Optimal play based on game state";
    }
    
    private String generateReasoning(GameContext context) {
        StringBuilder reasoning = new StringBuilder();
        reasoning.append("Turn ").append(context.turn).append(": ");
        
        int lifeGap = context.lifeTotal - context.opponentLifeTotal;
        if (lifeGap > 5) {
            reasoning.append("Ahead on life, continue aggressive strategy. ");
        } else if (lifeGap < -5) {
            reasoning.append("Behind on life, need defensive plays. ");
        } else {
            reasoning.append("Life totals are close, balanced approach needed. ");
        }
        
        reasoning.append("Current board presence: ").append(context.boardPresence).append(" permanents.");
        
        return reasoning.toString();
    }
    
    private String escapeJson(String str) {
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
    
    private static class ActionScore {
        String action;
        double score;
        
        ActionScore(String action, double score) {
            this.action = action;
            this.score = score;
        }
    }
}
