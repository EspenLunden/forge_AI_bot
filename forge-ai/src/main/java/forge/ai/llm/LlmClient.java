package forge.ai.llm;

import java.util.*;

/**
 * Interface for LLM integration
 * Implementations can use OpenAI, local models, Claude, etc.
 */
public interface LlmClient {
    
    /**
     * Query the LLM for decision-making
     * @param gameState serialized game state as JSON
     * @param availableActions list of possible actions in human-readable format
     * @param context game context information
     * @return ranked list of actions with confidence scores
     */
    LlmDecision queryForDecision(String gameState, List<String> availableActions, GameContext context);
    
    /**
     * Log decision for training data collection
     * @param gameState current game state
     * @param chosenAction the action that was selected
     * @param confidence confidence score from LLM
     */
    void logDecision(String gameState, String chosenAction, double confidence);
    
    /**
     * Provide feedback after game ends
     * @param won whether the AI won the game
     * @param turnsPlayed number of turns played
     */
    void provideFeedback(boolean won, int turnsPlayed);
    
    /**
     * Check if the LLM service is available
     */
    boolean isAvailable();
    
    /**
     * Get the name of this LLM provider
     */
    String getName();
}
