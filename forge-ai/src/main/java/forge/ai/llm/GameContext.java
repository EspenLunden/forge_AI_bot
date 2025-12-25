package forge.ai.llm;

/**
 * Context information about the current game state for LLM decision-making
 */
public class GameContext {
    public String format;           // Standard, Modern, Limited, Commander, etc
    public int turn;                // Current turn number
    public String phase;            // Current phase (Main1, Combat, Main2, etc)
    public int lifeTotal;           // AI's life total
    public int opponentLifeTotal;   // Opponent's life total
    public int cardsInHand;         // Number of cards in hand
    public int cardsInLibrary;      // Cards remaining in library
    public int boardPresence;       // Number of creatures/permanents in play
    
    @Override
    public String toString() {
        return String.format(
            "Turn %d, %s (%s) | Life: %d vs %d | Hand: %d | Board: %d",
            turn, format, phase, lifeTotal, opponentLifeTotal, cardsInHand, boardPresence
        );
    }
}
