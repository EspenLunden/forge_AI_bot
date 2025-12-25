# How to Run the Forge LLM AI

## Quick Start - Run the Demo

### 1. Run the Standalone Demo
The LLM system comes with a working demo that doesn't require the full Forge build:

**Windows (PowerShell):**
```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot
$javac = "C:\Program Files\BellSoft\LibericaJDK-17\bin\javac.exe"
$java = "C:\Program Files\BellSoft\LibericaJDK-17\bin\java.exe"
mkdir build\llm-demo -Force > $null
& "$javac" -encoding UTF-8 -d build\llm-demo `
  forge-ai/src/main/java/forge/ai/llm/GameContext.java `
  forge-ai/src/main/java/forge/ai/llm/RankedAction.java `
  forge-ai/src/main/java/forge/ai/llm/LlmDecision.java `
  forge-ai/src/main/java/forge/ai/llm/LlmClient.java `
  forge-ai/src/main/java/forge/ai/llm/LocalLlmClient.java `
  forge-ai/src/main/java/forge/ai/llm/LlmClientFactory.java `
  forge-ai/src/main/java/forge/ai/llm/LlmAiStandaloneDemo.java
& "$java" -cp build\llm-demo forge.ai.llm.LlmAiStandaloneDemo
```

**Linux/Mac:**
```bash
cd forge_AI_bot
javac -encoding UTF-8 -d build/llm-demo \
  forge-ai/src/main/java/forge/ai/llm/GameContext.java \
  forge-ai/src/main/java/forge/ai/llm/RankedAction.java \
  forge-ai/src/main/java/forge/ai/llm/LlmDecision.java \
  forge-ai/src/main/java/forge/ai/llm/LlmClient.java \
  forge-ai/src/main/java/forge/ai/llm/LocalLlmClient.java \
  forge-ai/src/main/java/forge/ai/llm/LlmClientFactory.java \
  forge-ai/src/main/java/forge/ai/llm/LlmAiStandaloneDemo.java
java -cp build/llm-demo forge.ai.llm.LlmAiStandaloneDemo
```

This demo shows:
- Creating LLM clients
- Decision-making with confidence scores  
- How confidence thresholds work
- Training data collection format

## Building the Full Forge Project

To build the entire Forge project with LLM AI integration:

### Prerequisites
- Java 17 (OpenJDK 17 confirmed working)
- Maven 3.6+ (automatically installed at `~/.maven/maven-3.9.11(1)/bin`)

### Build Status
✅ **forge-core**: Builds successfully (150 source files)  
✅ **forge-game**: Builds successfully (767 source files)  
✅ **forge-ai**: Builds successfully with LLM classes (195 source files + 10 LLM classes)

### Build the Core AI Modules

```bash
# Navigate to project root
cd forge_AI_bot

# Build just the essential AI modules (fastest)
mvn -pl :forge-core,:forge-game,:forge-ai clean install -DskipTests "-Dcheckstyle.skip=true"

# Or build the entire project
mvn clean install -DskipTests "-Dcheckstyle.skip=true"
```

**Build Times:**
- First build: ~25-30 seconds (downloads dependencies)
- Subsequent builds: ~5-10 seconds
- Generates: `forge-ai/target/forge-ai-2.0.08-SNAPSHOT.jar` (1 MB)

## Integration into Your Game

### Step 1: Create LLM Client

```java
// In your game initialization code
import forge.ai.llm.*;

// Create a local LLM client (no API key needed for demo)
LlmClient llmClient = LlmClientFactory.create("local");
```

### Step 2: Create AI Player with LLM

```java
// Create the AI player's controller
PlayerControllerLlm controller = new PlayerControllerLlm(
    game,           // Game instance
    aiPlayer,       // Player object
    lobbyPlayer,    // LobbyPlayer (can be LobbyPlayerAi)
    llmClient,      // LLM client from step 1
    0.65,           // Confidence threshold (0.0-1.0)
    true            // Enable training mode
);

// Set as the player's controller
aiPlayer.setFirstController(controller);
```

### Step 3: Enable Training Data Collection

```java
// Optional: enable training data logging
if (llmClient instanceof LocalLlmClient) {
    LocalLlmClient localClient = (LocalLlmClient) llmClient;
    localClient.setLogTrainingData(true);
}
```

### Step 4: Play Your Game

The LLM AI will:
- Make decisions using the LLM
- Fall back to traditional AI if confidence is too low
- Log decisions and game state (if training mode enabled)
- Collect win/loss data for training

### Step 5: Get Game Results

```java
// When game ends, provide feedback
controller.gameEnded(playerWon);

// View decision history
System.out.println("=== Decision History ===");
for (String decision : controller.getDecisionHistory()) {
    System.out.println(decision);
}
```

## Directory Structure

Created files:
```
forge-ai/
├── src/main/java/forge/ai/llm/
│   ├── GameContext.java              # Game state context
│   ├── GameStateSerializer.java      # JSON serialization
│   ├── LlmClient.java               # Interface
│   ├── LlmClientFactory.java        # Factory
│   ├── LlmDecision.java             # Response wrapper
│   ├── LlmAiExample.java            # Usage examples
│   ├── LocalLlmClient.java          # Local implementation
│   ├── PlayerControllerLlm.java     # Main integration
│   └── RankedAction.java            # Action wrapper
└── LLM_AI_README.md                  # Full documentation
```

## Configuration via AI Profile

Create `res/ai_profiles/llm.ai`:

```properties
# LLM-based AI profile
PLAY_AGGRO=true
CHANCE_TO_ATTACK_INTO_TRADE=60
ENABLE_LLM=true
LLM_PROVIDER=local
LLM_CONFIDENCE_THRESHOLD=0.65
LLM_TRAINING_MODE=true
LLM_TRAINING_PATH=./llm_training_data/
```

Then use in game:
```java
lobbyAi.setAiProfile("llm");
```

## Training Data Output

When training mode is enabled, data is saved to:
```
./llm_training_data/
├── decision_1702155600000.json      # Game state + chosen action
├── decision_1702155602000.json
├── feedback_1702155700000.json      # Game outcome
└── ...
```

Each decision JSON contains:
- Timestamp
- Game state (full JSON)
- Chosen action
- Confidence score

## Troubleshooting

**Compilation errors about missing classes?**
- Ensure you're building with Maven: `mvn clean install -DskipTests`
- Check that all files were created in `forge-ai/src/main/java/forge/ai/llm/`

**LLM not making decisions?**
- Check `llmClient.isAvailable()` returns true
- Verify `PlayerControllerLlm.chooseSpellAbilityToPlay()` is being called
- Enable debug logging to see what's happening

**Training data not being collected?**
- Verify `LocalLlmClient.setLogTrainingData(true)` was called
- Check directory path is writable: `./llm_training_data/`
- Ensure `controller.gameEnded()` is called after each game

**Want to use OpenAI/Claude instead?**
- Implement `OpenAiClient` class extending `LlmClient`
- Register it in `LlmClientFactory.create()`
- Set `LLM_PROVIDER=openai` in config
- Provide API key via environment variable or config

## Next Steps

1. **Build the project**: `mvn clean install -DskipTests`
2. **Run the examples**: `java -cp . forge.ai.llm.LlmAiExample`
3. **Integrate into your game**: Follow "Integration into Your Game" section above
4. **Collect training data**: Play games with training mode enabled
5. **Fine-tune LLM**: Use collected data to train/fine-tune an actual LLM model
6. **Deploy**: Switch to production LLM provider (OpenAI, Claude, etc.)

## Architecture Overview

```
PlayerControllerLlm (extends PlayerControllerAi)
    ↓
LlmClient (interface)
    ↓
LocalLlmClient (works without API keys)
    ↓
GameStateSerializer (convert game→JSON)
    ↓
LLM Decision (rank actions with confidence)
    ↓
Fallback to Traditional AI (if confidence low)
```

## For More Details

- Full documentation: See `LLM_AI_README.md`
- API reference: Javadoc in each .java file
- Examples: Run `LlmAiExample.main()`
