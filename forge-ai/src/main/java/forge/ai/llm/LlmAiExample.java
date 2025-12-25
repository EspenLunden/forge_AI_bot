package forge.ai.llm;

/**
 * Example usage of the LLM AI integration system
 * 
 * This demonstrates how to:
 * 1. Initialize an LLM client
 * 2. Create an LLM-enabled AI player
 * 3. Run games with LLM decision-making
 * 4. Collect training data
 * 5. View decision history
 */
public class LlmAiExample {
    
    /**
     * Example 1: Basic usage with local LLM (no API keys needed)
     */
    public static void exampleBasicUsage() {
        System.out.println("=== Example 1: Basic Local LLM AI ===\n");
        
        // Create a local LLM client (works without API keys)
        LlmClient llmClient = LlmClientFactory.create("local");
        System.out.println("Created LLM client: " + llmClient.getName());
        System.out.println("Available: " + llmClient.isAvailable());
        
        // This client would be used in PlayerControllerLlm
        System.out.println("\nTo use in-game:");
        System.out.println("  PlayerControllerLlm controller = new PlayerControllerLlm(");
        System.out.println("      game, aiPlayer, lobbyPlayer, llmClient");
        System.out.println("  );");
    }
    
    /**
     * Example 2: Creating a decision and evaluating confidence
     */
    public static void exampleLlmDecision() {
        System.out.println("\n=== Example 2: LLM Decision Making ===\n");
        
        // Simulate available actions
        java.util.List<String> availableActions = new java.util.ArrayList<>();
        availableActions.add("Attack with Grizzly Bears (3/3)");
        availableActions.add("Cast Lightning Bolt targeting opponent");
        availableActions.add("Hold up mana for combat tricks");
        availableActions.add("Pass turn");
        
        // Create game context
        GameContext context = new GameContext();
        context.format = "Standard";
        context.turn = 5;
        context.phase = "MainPhase";
        context.lifeTotal = 15;
        context.opponentLifeTotal = 8;
        context.cardsInHand = 4;
        context.boardPresence = 3;
        
        System.out.println("Game State: " + context.toString());
        System.out.println("\nAvailable Actions:");
        for (int i = 0; i < availableActions.size(); i++) {
            System.out.println("  " + (i+1) + ". " + availableActions.get(i));
        }
        
        // Query LLM
        LlmClient client = LlmClientFactory.create("local");
        LlmDecision decision = client.queryForDecision(
            "{simulated game state}",
            availableActions,
            context
        );
        
        System.out.println("\nLLM Decision:");
        System.out.println(decision.toString());
    }
    
    /**
     * Example 3: Training mode with data collection
     */
    public static void exampleTrainingMode() {
        System.out.println("\n=== Example 3: Training Mode ===\n");
        
        // Create local client with training enabled
        LocalLlmClient client = new LocalLlmClient("./training_data/");
        client.setLogTrainingData(true);
        
        System.out.println("Training client created");
        System.out.println("Training data path: ./training_data/");
        System.out.println("Logging enabled: true");
        
        System.out.println("\nWhen PlayerControllerLlm is created with:");
        System.out.println("  PlayerControllerLlm controller = new PlayerControllerLlm(");
        System.out.println("      game, aiPlayer, lobbyPlayer,");
        System.out.println("      client,");
        System.out.println("      0.65,  // confidence threshold");
        System.out.println("      true   // training mode enabled");
        System.out.println("  );");
        
        System.out.println("\nThe system will:");
        System.out.println("  1. Log each decision with full game state");
        System.out.println("  2. Record confidence scores from LLM");
        System.out.println("  3. Store game results and turn counts");
        System.out.println("  4. Create training dataset for LLM fine-tuning");
    }
    
    /**
     * Example 4: Confidence-based fallback mechanism
     */
    public static void exampleConfidenceThreshold() {
        System.out.println("\n=== Example 4: Confidence Threshold ===\n");
        
        double[] thresholds = {0.4, 0.6, 0.8};
        
        System.out.println("Confidence Threshold Behavior:\n");
        
        for (double threshold : thresholds) {
            System.out.println(String.format("Threshold: %.2f", threshold));
            System.out.println("  - LLM confidence >= " + threshold + ": Use LLM suggestion");
            System.out.println("  - LLM confidence < " + threshold + ": Fall back to traditional AI");
            
            if (threshold < 0.5) {
                System.out.println("  -> Aggressive: Trusts LLM often");
            } else if (threshold < 0.7) {
                System.out.println("  -> Balanced: Good mix of LLM and traditional AI");
            } else {
                System.out.println("  -> Conservative: Prefers proven traditional AI");
            }
            System.out.println();
        }
    }
    
    /**
     * Example 5: Game state serialization
     */
    public static void exampleGameStateSerialization() {
        System.out.println("\n=== Example 5: Game State Serialization ===\n");
        
        GameStateSerializer serializer = new GameStateSerializer();
        
        System.out.println("Game state is serialized to JSON for LLM consumption:");
        System.out.println("""
            {
              "perspective": "AI Player",
              "turn": 5,
              "phase": "MainPhase",
              "self": {
                "life": 15,
                "library": 32,
                "hand": ["Grizzly Bears", "Lightning Bolt", "Mountain"],
                "battlefield": [
                  {"name": "Goblin Warrior", "power": 2, "toughness": 1, "tapped": false}
                ],
                "graveyard": [{"count": 3}]
              },
              "opponents": [
                {
                  "name": "Opponent",
                  "life": 8,
                  "library": 25,
                  "hand_size": 4,
                  "battlefield": [{"name": "Elvish Archer", "power": 1, "toughness": 2, "tapped": true}]
                }
              ]
            }
            """);
        
        System.out.println("This JSON is sent to the LLM along with available actions");
        System.out.println("The LLM uses it to evaluate and rank possible plays");
    }
    
    /**
     * Example 6: Implementing a custom LLM provider
     */
    public static void exampleCustomProvider() {
        System.out.println("\n=== Example 6: Custom LLM Provider ===\n");
        
        System.out.println("To implement OpenAI support:");
        System.out.println("""
            1. Create a class implementing LlmClient:
               
               public class OpenAiClient implements LlmClient {
                   private OpenAiService service;
                   
                   public OpenAiClient(String apiKey) {
                       this.service = new OpenAiService(apiKey);
                   }
                   
                   @Override
                   public LlmDecision queryForDecision(...) {
                       // Call OpenAI API
                       // Parse response
                       // Return LlmDecision
                   }
                   
                   // Implement other interface methods...
               }
            
            2. Register in LlmClientFactory:
               
               case "openai":
                   return new OpenAiClient(apiKey);
            
            3. Use in game:
               
               LlmClient client = LlmClientFactory.create("openai",
                   Map.of("apiKey", "sk-...")
               );
            """);
    }
    
    /**
     * Example 7: Full game integration
     */
    public static void exampleFullIntegration() {
        System.out.println("\n=== Example 7: Full Game Integration ===\n");
        
        System.out.println("Complete flow for using LLM AI in Forge:\n");
        
        System.out.println("// 1. Initialize LLM client");
        System.out.println("LlmClient llmClient = LlmClientFactory.create(\"local\");\n");
        
        System.out.println("// 2. Create lobby player");
        System.out.println("LobbyPlayerAi lobbyAi = new LobbyPlayerAi(\"LLM Bot\", null);\n");
        
        System.out.println("// 3. Create game and in-game player");
        System.out.println("Game game = new Game();\n");
        
        System.out.println("// 4. Create controller with LLM");
        System.out.println("PlayerControllerLlm controller = new PlayerControllerLlm(");
        System.out.println("    game, aiPlayer, lobbyAi, llmClient,");
        System.out.println("    0.65,  // confidence threshold");
        System.out.println("    true   // training mode");
        System.out.println(");\n");
        
        System.out.println("// 5. Set controller for player");
        System.out.println("aiPlayer.setFirstController(controller);\n");
        
        System.out.println("// 6. Play game...");
        System.out.println("game.play();\n");
        
        System.out.println("// 7. Provide feedback after game");
        System.out.println("controller.gameEnded(playerWon);\n");
        
        System.out.println("// 8. Examine decision history");
        System.out.println("for (String decision : controller.getDecisionHistory()) {");
        System.out.println("    System.out.println(decision);");
        System.out.println("}");
    }
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       Forge LLM AI Integration - Examples                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        exampleBasicUsage();
        exampleLlmDecision();
        exampleTrainingMode();
        exampleConfidenceThreshold();
        exampleGameStateSerialization();
        exampleCustomProvider();
        exampleFullIntegration();
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                 Getting Started                            ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Read LLM_AI_README.md in forge-ai/ directory           ║");
        System.out.println("║ 2. Run example: java -cp . forge.ai.llm.LlmAiExample      ║");
        System.out.println("║ 3. Integrate PlayerControllerLlm into your game code      ║");
        System.out.println("║ 4. Run games with training mode to collect data           ║");
        System.out.println("║ 5. Fine-tune LLM on collected data                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
    }
}
