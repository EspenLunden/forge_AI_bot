package forge.ai.llm;

/**
 * Standalone Demo of LLM AI Integration
 * (No Forge dependencies required for this demo)
 */
public class LlmAiStandaloneDemo {
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("Forge LLM AI - Standalone Demo");
        System.out.println("==============================================\n");
        
        demo1BasicLlmClient();
        demo2Decisions();
        demo3Confidence();
        demo4TrainingData();
        
        System.out.println("\n==============================================");
        System.out.println("Demo Complete!");
        System.out.println("==============================================");
    }
    
    static void demo1BasicLlmClient() {
        System.out.println("Demo 1: Creating LLM Clients");
        System.out.println("------------------------------------------");
        
        LlmClient local = LlmClientFactory.create("local");
        System.out.println("Client Name: " + local.getName());
        System.out.println("Is Available: " + local.isAvailable());
        System.out.println("Provider: local (no API keys needed)");
        System.out.println();
    }
    
    static void demo2Decisions() {
        System.out.println("Demo 2: LLM Decision Making");
        System.out.println("------------------------------------------");
        
        LlmClient client = LlmClientFactory.create("local");
        
        // Simulate available actions
        java.util.List<String> actions = new java.util.ArrayList<>();
        actions.add("Attack with Grizzly Bears (3/3)");
        actions.add("Cast Lightning Bolt targeting opponent");
        actions.add("Play Mountain for mana");
        actions.add("Pass turn");
        
        // Create a game context
        GameContext context = new GameContext();
        context.format = "Standard";
        context.turn = 5;
        context.phase = "MainPhase";
        context.lifeTotal = 15;
        context.opponentLifeTotal = 8;
        context.cardsInHand = 3;
        context.cardsInLibrary = 32;
        context.boardPresence = 2;  // 2 creatures in play
        
        System.out.println("Game Context:");
        System.out.println("  Format: " + context.format);
        System.out.println("  Turn: " + context.turn);
        System.out.println("  Phase: " + context.phase);
        System.out.println("  Your Life: " + context.lifeTotal);
        System.out.println("  Opponent Life: " + context.opponentLifeTotal);
        System.out.println("  Hand Size: " + context.cardsInHand);
        System.out.println();
        
        System.out.println("Available Actions:");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println("  " + (i+1) + ". " + actions.get(i));
        }
        System.out.println();
    }
    
    static void demo3Confidence() {
        System.out.println("Demo 3: Confidence-Based Decision Making");
        System.out.println("------------------------------------------");
        
        LlmClient client = LlmClientFactory.create("local");
        
        System.out.println("How Confidence Threshold Works:");
        System.out.println("  Threshold: 0.65 (default)");
        System.out.println("  If confidence >= 0.65: Use LLM decision");
        System.out.println("  If confidence < 0.65: Fall back to traditional AI");
        System.out.println();
        
        System.out.println("Example Scenario:");
        System.out.println("  Action: 'Attack with Grizzly Bears'");
        System.out.println("  LLM Confidence: 0.72");
        System.out.println("  Result: Use LLM decision (72% confident this is correct)");
        System.out.println();
        
        System.out.println("Another Scenario:");
        System.out.println("  Action: 'Hold up mana for trick'");
        System.out.println("  LLM Confidence: 0.58");
        System.out.println("  Result: Use traditional AI (only 58% confident)");
        System.out.println();
    }
    
    static void demo4TrainingData() {
        System.out.println("Demo 4: Training Data Collection");
        System.out.println("------------------------------------------");
        
        LocalLlmClient client = (LocalLlmClient) LlmClientFactory.create("local");
        
        System.out.println("Training Data Features:");
        System.out.println("  1. Logs game state when decisions are made");
        System.out.println("  2. Records LLM confidence for each action");
        System.out.println("  3. Saves game outcome (win/loss)");
        System.out.println("  4. Enables fine-tuning of actual LLM models");
        System.out.println();
        
        System.out.println("Output Format:");
        System.out.println("  Directory: ./llm_training_data/");
        System.out.println("  Files:");
        System.out.println("    - decision_<timestamp>.json");
        System.out.println("    - feedback_<timestamp>.json");
        System.out.println();
        
        System.out.println("Sample decision_<timestamp>.json:");
        System.out.println("""
            {
              "timestamp": 1702155600000,
              "gameState": {
                "turn": 5,
                "phase": "MainPhase",
                "life": 15,
                "opponentLife": 8
              },
              "action": "Attack with Grizzly Bears",
              "confidence": 0.72
            }""");
        System.out.println();
        
        System.out.println("Sample feedback_<timestamp>.json:");
        System.out.println("""
            {
              "timestamp": 1702155700000,
              "gameOutcome": "win",
              "turnCount": 12,
              "aiPlayer": "LocalLlmAI"
            }""");
        System.out.println();
    }
}
