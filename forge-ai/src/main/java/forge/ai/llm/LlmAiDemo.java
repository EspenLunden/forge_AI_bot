package forge.ai.llm;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone test/demo of the LLM AI system
 * This can be run without needing the full Forge game engine
 * 
 * Usage: javac LlmAiDemo.java && java LlmAiDemo
 */
public class LlmAiDemo {
    
    public static void main(String[] args) {
        System.out.println("========================================================");
        System.out.println("   Forge LLM AI System - Interactive Demo");
        System.out.println("========================================================\n");
        
        // Demo 1: Basic LLM Client Creation
        demoClientCreation();
        
        // Demo 2: Game State Serialization
        demoGameStateSerialization();
        
        // Demo 3: Decision Making
        demoDecisionMaking();
        
        // Demo 4: Training Data Logging
        demoTrainingLogging();
        
        System.out.println("\n========================================================");
        System.out.println("              Demo Complete!");
        System.out.println("");
        System.out.println("  Next Steps:");
        System.out.println("  1. Build: mvn clean install -DskipTests");
        System.out.println("  2. Integrate into your game code");
        System.out.println("  3. Check QUICKSTART_LLM.md for integration guide");
        System.out.println("========================================================\n");
    }
    
    private static void demoClientCreation() {
        System.out.println("DEMO 1: Creating LLM Clients");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // Create local client
        LlmClient localClient = LlmClientFactory.create("local");
        System.out.println("✓ Created Local LLM Client");
        System.out.println("  Name: " + localClient.getName());
        System.out.println("  Available: " + localClient.isAvailable());
        System.out.println("  Path: " + LocalLlmClient.class.getName());
        
        System.out.println("\nOther providers (not yet implemented):");
        System.out.println("  • OpenAI GPT-4: LlmClientFactory.create(\"openai\")");
        System.out.println("  • Claude: LlmClientFactory.create(\"claude\")");
        System.out.println("  • Custom: Implement LlmClient interface\n");
    }
    
    private static void demoGameStateSerialization() {
        System.out.println("\nDEMO 2: Game State Serialization");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // Create a game context
        GameContext context = new GameContext();
        context.format = "Standard";
        context.turn = 3;
        context.phase = "Main1";
        context.lifeTotal = 20;
        context.opponentLifeTotal = 17;
        context.cardsInHand = 5;
        context.cardsInLibrary = 40;
        context.boardPresence = 2;
        
        System.out.println("Game Context Created:");
        System.out.println("  " + context.toString());
        
        System.out.println("\nExample JSON Game State (simulated):");
        System.out.println("""
            {
              "perspective": "AI Player",
              "turn": 3,
              "phase": "Main1",
              "self": {
                "life": 20,
                "library": 40,
                "hand": ["Mountain", "Goblin Warrior", "Lightning Bolt"],
                "battlefield": [
                  {"name": "Grizzly Bears", "power": 2, "toughness": 2, "tapped": false}
                ]
              },
              "opponents": [
                {
                  "name": "Opponent",
                  "life": 17,
                  "library": 38,
                  "hand_size": 4,
                  "battlefield": [
                    {"name": "Elvish Archery", "power": 1, "toughness": 2, "tapped": false}
                  ]
                }
              ]
            }
            """);
        System.out.println("This JSON is sent to the LLM for analysis.\n");
    }
    
    private static void demoDecisionMaking() {
        System.out.println("\nDEMO 3: LLM Decision Making");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // Create LLM client and context
        LlmClient llmClient = LlmClientFactory.create("local");
        
        GameContext context = new GameContext();
        context.format = "Standard";
        context.turn = 3;
        context.phase = "Main1";
        context.lifeTotal = 20;
        context.opponentLifeTotal = 17;
        context.cardsInHand = 5;
        context.boardPresence = 1;
        
        // Available actions
        List<String> actions = new ArrayList<>();
        actions.add("Cast Grizzly Bears (2/2 for 1G)");
        actions.add("Cast Lightning Bolt targeting opponent (3 damage for 1R)");
        actions.add("Hold back and play it safe");
        actions.add("Pass turn");
        
        System.out.println("Available Actions:");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println("  " + (i+1) + ". " + actions.get(i));
        }
        
        // Query LLM
        System.out.println("\nQuerying LLM for decision...\n");
        LlmDecision decision = llmClient.queryForDecision(
            "{simulated game state}",
            actions,
            context
        );
        
        System.out.println("LLM Response:");
        System.out.println(decision.toString());
        
        System.out.println("\nTop Recommendation:");
        RankedAction top = decision.getTopAction();
        if (top != null) {
            System.out.println("  Action: " + top.action);
            System.out.println("  Confidence: " + String.format("%.2f", decision.getTopConfidence()));
            System.out.println("  Explanation: " + top.explanation);
        }
        
        System.out.println("\nConfidence Analysis:");
        System.out.println("  Query Time: " + decision.queryTimeMs + "ms");
        System.out.println("  Confidence Threshold (default): 0.65");
        System.out.println("  Decision Mode: " + 
            (decision.getTopConfidence() >= 0.65 ? "USE LLM" : "FALLBACK TO TRADITIONAL AI"));
        System.out.println();
    }
    
    private static void demoTrainingLogging() {
        System.out.println("\nDEMO 4: Training Data Collection");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // Create local client with training enabled
        LocalLlmClient client = new LocalLlmClient("./demo_training_data/");
        client.setLogTrainingData(true);
        
        System.out.println("✓ Training Mode Enabled");
        System.out.println("  Output Path: ./demo_training_data/");
        System.out.println("  Data Logged: true");
        
        System.out.println("\nSimulating game decisions...\n");
        
        // Simulate a few decisions
        for (int i = 1; i <= 3; i++) {
            System.out.println("Turn " + i + ":");
            String action = "Cast spell (action " + i + ")";
            double confidence = 0.6 + (i * 0.1);
            
            client.logDecision("{game state " + i + "}", action, confidence);
            System.out.println("  ✓ Logged: " + action);
            System.out.println("    Confidence: " + String.format("%.2f", confidence));
        }
        
        System.out.println("\nGame Ended:");
        client.provideFeedback(true, 15);  // Won, 15 turns
        System.out.println("  ✓ Game Result: WIN (15 turns)");
        
        System.out.println("\nGenerated Files:");
        System.out.println("  • decision_<timestamp>.json  (3 files)");
        System.out.println("  • feedback_<timestamp>.json  (1 file)");
        
        System.out.println("\nTraining Data Structure:");
        System.out.println("""
            Example decision file:
            {
              "timestamp": 1702155600000,
              "chosen_action": "Cast Lightning Bolt",
              "confidence": 0.75,
              "game_state": { ... }
            }
            
            Example feedback file:
            {
              "timestamp": 1702155700000,
              "won": true,
              "turns": 15
            }
            """);
        
        System.out.println("Use this data to fine-tune LLM models!\n");
    }
}
