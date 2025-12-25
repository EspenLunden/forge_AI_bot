# Forge LLM AI Integration

A custom AI system for Forge that integrates with Large Language Models (LLMs) for Magic: The Gathering decision-making and training.

## Architecture Overview

The LLM AI system consists of:

- **LlmClient**: Interface for LLM providers (OpenAI, Claude, local, etc.)
- **LocalLlmClient**: Built-in local implementation with heuristic-based decisions
- **PlayerControllerLlm**: Extends traditional AI with LLM capabilities
- **GameStateSerializer**: Converts game state to JSON for LLM consumption
- **LlmClientFactory**: Factory for creating and managing LLM clients

## Quick Start

### 1. Basic Setup (Using Local AI)

The system comes with a `LocalLlmClient` that works out of the box without any external API keys.

```java
// In your game initialization code
LlmClient llmClient = LlmClientFactory.create("local");
Player aiPlayer = game.createPlayer();
PlayerController controller = new PlayerControllerLlm(game, aiPlayer, lobbyPlayer, llmClient);
aiPlayer.setFirstController(controller);
```

### 2. Enable Training Mode

To collect training data for LLM fine-tuning:

```java
LocalLlmClient client = new LocalLlmClient("./training_data/");
client.setLogTrainingData(true);

PlayerControllerLlm controller = new PlayerControllerLlm(
    game, aiPlayer, lobbyPlayer, 
    client, 
    0.65,  // confidence threshold
    true   // training mode enabled
);
```

### 3. Configure via Properties

Add to your AI profile file (e.g., `res/ai_profiles/llm.ai`):

```properties
# LLM Configuration
ENABLE_LLM=true
LLM_PROVIDER=local
LLM_CONFIDENCE_THRESHOLD=0.65
LLM_TRAINING_MODE=true
LLM_TRAINING_PATH=./llm_training_data/
```

## File Structure

```
forge-ai/src/main/java/forge/ai/llm/
├── GameContext.java                  # Game state context for LLM
├── GameStateSerializer.java          # Serializes game state to JSON
├── LlmClient.java                    # LLM interface
├── LlmClientFactory.java             # Factory for creating LLM clients
├── LlmDecision.java                  # LLM response wrapper
├── LocalLlmClient.java               # Local implementation (no API needed)
├── PlayerControllerLlm.java          # Integrated player controller
└── RankedAction.java                 # Ranked action with confidence
```

## Usage Examples

### Example 1: Create an LLM AI Opponent

```java
// Setup LLM client
LlmClient llmClient = LlmClientFactory.create("local");

// Create lobby player
LobbyPlayerAi lobbyAi = new LobbyPlayerAi("LLM Bot", null);
lobbyAi.setAiProfile("llm");

// Create game player with LLM controller
Player aiPlayer = new Player("LLM Bot", game, 0);
PlayerControllerLlm controller = new PlayerControllerLlm(game, aiPlayer, lobbyAi, llmClient);
aiPlayer.setFirstController(controller);

game.addPlayer(aiPlayer);
```

### Example 2: Training Mode with Feedback

```java
LocalLlmClient client = new LocalLlmClient("./training_data/");
client.setLogTrainingData(true);

PlayerControllerLlm controller = new PlayerControllerLlm(
    game, aiPlayer, lobbyAi, 
    client, 
    0.6,  // lower threshold for training
    true  // training enabled
);

// Play game...

// After game ends, provide feedback
controller.gameEnded(playerWon);

// View decision history
for (String decision : controller.getDecisionHistory()) {
    System.out.println(decision);
}
```

### Example 3: Custom LLM Configuration

```java
Map<String, String> config = new HashMap<>();
config.put("model", "gpt-4");
config.put("trainingPath", "/custom/path/");

LlmClient llmClient = LlmClientFactory.create("openai", config);
PlayerControllerLlm controller = new PlayerControllerLlm(game, aiPlayer, lobbyAi, llmClient);
```

## Configuration Properties

Add these to `AiProps.java` enum to control LLM behavior via profiles:

```java
ENABLE_LLM ("false"),
LLM_PROVIDER ("local"),
LLM_CONFIDENCE_THRESHOLD ("0.65"),
LLM_TRAINING_MODE ("false"),
LLM_TRAINING_PATH ("./llm_training_data/"),
```

## Training Data Collection

When `trainingMode=true`, the system automatically logs:

- **Decision logs**: `decision_<timestamp>.json` - Each decision with game state
- **Feedback logs**: `feedback_<timestamp>.json` - Game outcomes and results

Example training data directory structure:
```
llm_training_data/
├── decision_1702155600000.json
├── decision_1702155602000.json
├── feedback_1702155700000.json
└── ...
```

## Implementing Custom LLM Providers

To add support for OpenAI, Claude, or other providers:

```java
public class OpenAiClient implements LlmClient {
    private OpenAiService service;
    private String model;
    
    public OpenAiClient(String apiKey, String model) {
        this.service = new OpenAiService(apiKey);
        this.model = model;
    }
    
    @Override
    public LlmDecision queryForDecision(String gameState, List<String> availableActions, GameContext context) {
        // Call OpenAI API with game state
        // Parse response and return LlmDecision
        return decision;
    }
    
    @Override
    public void logDecision(String gameState, String chosenAction, double confidence) {
        // Log for training
    }
    
    @Override
    public void provideFeedback(boolean won, int turnsPlayed) {
        // Record game outcome
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public String getName() {
        return "OpenAI";
    }
}
```

Then register in `LlmClientFactory.create()`:

```java
case "openai":
    return new OpenAiClient(apiKey, model);
```

## Features

- ✅ **Local AI**: Works out of the box with heuristic-based decisions
- ✅ **Training Mode**: Automatically collect decision and outcome data
- ✅ **Confidence Scoring**: Evaluate LLM confidence for decisions
- ✅ **Fallback Mechanism**: Falls back to traditional AI if LLM unavailable
- ✅ **Game State Serialization**: Complete game state as JSON for LLM input
- ✅ **Extensible Architecture**: Easy to add new LLM providers
- ⏳ **OpenAI Integration**: Ready for implementation
- ⏳ **Claude Integration**: Ready for implementation
- ⏳ **Fine-tuning Pipeline**: Framework in place for LLM fine-tuning

## Performance Considerations

- LLM queries add latency (mitigated by confidence threshold)
- Training data can grow large; consider archiving old data
- Use `confidenceThreshold` to balance LLM suggestions with fallback AI
- Local client has negligible performance impact

## Troubleshooting

**LLM queries always fail:**
- Check `llmClient.isAvailable()` returns true
- Verify API keys if using external providers
- Check console for error messages

**Confidence threshold too high:**
- Reduce `confidenceThreshold` to allow more LLM suggestions
- Monitor decision history to tune optimal value

**Training data not being collected:**
- Verify `trainingMode=true` when creating controller
- Ensure directory path is writable
- Check that `gameEnded()` is called after game completion

## Next Steps

1. Run with local AI to test integration
2. Collect training data with multiple games
3. Implement OpenAI/Claude clients for production use
4. Fine-tune LLM on collected Magic format data
5. Create format-specific models (Standard, Modern, Limited, etc.)

## License

Same as Forge project
