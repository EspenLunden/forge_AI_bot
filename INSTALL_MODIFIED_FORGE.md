# Installing Modified Forge with LLM AI Integration

## Quick Overview

The LLM AI integration is **built into the Forge source code** and compiled as part of the standard Forge build process. To use it, you need to:

1. Build the modified Forge project
2. Run/install the compiled Forge client
3. Enable LLM AI in game settings

---

## Step 1: Build the Modified Forge Project

### Prerequisites

**Java 17+ (Required)**
```powershell
# Check your Java version
java -version

# If you don't have Java 17, you already have LibericaJDK-17 installed at:
C:\Program Files\BellSoft\LibericaJDK-17\bin\java.exe
```

**Maven 3.6+ (Required)**
```powershell
# Maven is already installed at:
mvn --version

# If not in PATH, use:
c:\Users\Owner\.maven\maven-3.9.11(1)\bin\mvn --version
```

### Build Command

**Option A: Build Core AI Modules Only (Faster - 40 seconds)**
```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

$env:PATH = "c:\Users\Owner\.maven\maven-3.9.11(1)\bin;$env:PATH"

mvn -pl :forge-core,:forge-game,:forge-ai clean install -DskipTests "-Dcheckstyle.skip=true"
```

**Option B: Build Full Forge Project (Slower - 2-3 minutes)**
```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

$env:PATH = "c:\Users\Owner\.maven\maven-3.9.11(1)\bin;$env:PATH"

mvn clean install -DskipTests "-Dcheckstyle.skip=true"
```

### Build Verification

Successful build looks like:
```
[INFO] Forge Core ......................................... SUCCESS [  7.67 s]
[INFO] Forge Game ......................................... SUCCESS [ 10.20 s]
[INFO] Forge AI ........................................... SUCCESS [  3.26 s]
[INFO] Forge GUI .......................................... SUCCESS [  4.47 s]
[INFO] Forge Mobile ....................................... SUCCESS [  4.78 s]
[INFO] Forge .............................................. SUCCESS [  8.31 s]
[INFO] Forge Desktop GUI .................................. SUCCESS [  8.31 s]
[INFO] 
[INFO] BUILD SUCCESS
[INFO] Total time:  39.05 s
```

---

## Step 2: Locate Compiled Forge Client

After successful build, the compiled JAR files are in:

```
forge_AI_bot/
├── forge-gui-desktop/target/
│   ├── forge-gui-desktop-2.0.08-SNAPSHOT.jar          ← Desktop Client
│   ├── forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar  ← ✓ Use this one!
│   └── ... (other build artifacts)
│
├── forge-gui-mobile/target/
│   └── forge-gui-mobile-2.0.08-SNAPSHOT.jar           ← Mobile Client
│
└── forge-ai/target/
    └── forge-ai-2.0.08-SNAPSHOT.jar                    ← LLM AI Module
```

**Important**: Forge needs the `res` folder (skins, card images, etc.) in its working directory. This folder is located at `forge-gui/res/` and is **not** included in the JAR file.

---

## Step 3: Run the Compiled Forge Client

### Option A: Run from Source (Development)

```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

$env:PATH = "c:\Users\Owner\.maven\maven-3.9.11(1)\bin;$env:PATH"

# Run desktop client
mvn -pl :forge-gui-desktop exec:java "-Dexec.mainClass=forge.Singletons"
```

### Option B: Run JAR File Directly (Deployment)

**Step 1: Navigate to project directory**
```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot
```

**Step 2: Copy the res folder to project directory**
```powershell
# If not already present, copy resources
if (-not (Test-Path ".\res")) {
    Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
}
```

**Step 3: Run the JAR**
```powershell
java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"
```

**Alternative: Run with absolute paths** (no need to copy res)
```powershell
# First, navigate to project directory
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# Copy res folder if not present
if (-not (Test-Path ".\res")) {
    Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
}

# Run Forge
java -Xmx4G -jar "c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot\forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"
```

### Option C: Copy JAR to Standard Location

```powershell
# First, navigate to project
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# Create deployment directory
$deployDir = "C:\Forge"
New-Item -ItemType Directory -Path $deployDir -Force | Out-Null

# Copy JAR file
Copy-Item "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar" `
  -Destination "$deployDir\forge.jar" -Force

# Copy res folder (IMPORTANT: Forge needs this!)
Copy-Item "forge-gui\res" -Destination "$deployDir\res" -Recurse -Force

# Run from new location
java -Xmx4G -jar "$deployDir\forge.jar"
```

**Note**: Forge must have the `res` folder in the same directory as the JAR. If you copy the JAR, always copy the `res` folder too!

---

## Step 4: Verify LLM AI Installation

Once Forge starts:

1. **Create a New Game**
   - Click "New Game" or equivalent

2. **Look for AI Player Settings**
   - In game setup, select an AI player slot

3. **Access AI Options** (Desktop)
   - **Right-click** on the AI radio button (not left-click)
   - You should see popup menu with:
     ```
     ☐ Use Simulation
     ☐ Use LLM AI          ← This is the new option!
     ```

4. **Enable LLM AI**
   - Check the "Use LLM AI" checkbox

5. **Start Game**
   - Click "Start Game"
   - AI will now use LLM decision-making

### Verification Checklist

- ✅ Forge launches without errors
- ✅ AI options menu appears
- ✅ "Use LLM AI" checkbox is visible
- ✅ Can check/uncheck it
- ✅ Game starts with LLM AI enabled
- ✅ Training data directory created: `./llm_training_data/`

---

## Common Installation Issues & Solutions

### Issue 1: "mvn is not recognized"

**Problem**: Maven not in PATH
**Solution**:
```powershell
# Add Maven to PATH for this session
$env:PATH = "c:\Users\Owner\.maven\maven-3.9.11(1)\bin;$env:PATH"

# Or add permanently to Windows environment variables
```

### Issue 2: "Java not found"

**Problem**: Java not in PATH
**Solution**:
```powershell
# Check if Java is installed
C:\Program Files\BellSoft\LibericaJDK-17\bin\java.exe -version

# Add to PATH
$env:PATH = "C:\Program Files\BellSoft\LibericaJDK-17\bin;$env:PATH"
```

### Issue 3: Build fails with "Could not collect dependencies"

**Problem**: Incomplete or corrupt Maven build
**Solution**:
```powershell
# Clean Maven cache
rm -r ~/.m2/repository/forge

# Rebuild
mvn clean install -DskipTests "-Dcheckstyle.skip=true"
```

### Issue 4: "can't find skins directory" error

**Problem**: Forge launches but immediately crashes with "FSkin > can't find skins directory!"

**Cause**: The `res` folder (containing skins and card images) is not in Forge's working directory

**Solution**:
```powershell
# Navigate to project directory
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# Copy res folder to current directory
Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force

# Now try running again
java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"
```

**Or** run from the correct directory where res already exists:
```powershell
# Method 1: Copy JAR to a new directory WITH res
$deployDir = "C:\Forge"
New-Item -ItemType Directory -Path $deployDir -Force | Out-Null
Copy-Item "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar" `
  -Destination "$deployDir\forge.jar" -Force
Copy-Item "forge-gui\res" -Destination "$deployDir\res" -Recurse -Force
java -Xmx4G -jar "$deployDir\forge.jar"
```

### Issue 5: Wrong JAR filename

**Problem**: Build creates `forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar` not `-full.jar`

**Cause**: Maven assembly creates a differently-named JAR

**Solution**: Use the correct filename:
```powershell
# Correct:
java -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"

# Not this (which doesn't exist):
java -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-full.jar"
```

### Issue 6: Forge Won't Start

---

## Complete Installation Walkthrough

### Step-by-Step for Windows

```powershell
# 1. Navigate to project
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# 2. Set Maven in PATH
$env:PATH = "c:\Users\Owner\.maven\maven-3.9.11(1)\bin;$env:PATH"

# 3. Build project (full build recommended)
mvn clean install -DskipTests "-Dcheckstyle.skip=true"

# Wait for "BUILD SUCCESS" message...
# This takes ~40-60 seconds

# 4. Ensure res folder is present (CRITICAL)
if (-not (Test-Path ".\res")) {
    Write-Host "Copying res folder..."
    Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
}

# 5. Run Forge
java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"

# Forge GUI should now launch!

# 6. In Forge:
#    - Create game
#    - Select AI player
#    - Right-click AI button  
#    - Check "Use LLM AI"
#    - Start game!
```

---

## Running Headless (No GUI)

If you want to just have the Forge server running and connect remotely:

```powershell
# First, ensure res folder is present
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot
if (-not (Test-Path ".\res")) {
    Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
}

# Run with minimal UI
java -Xmx4G -Djava.awt.headless=true -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"

# Or with specific IP/port
java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar" --port=8888 --bind=0.0.0.0
```

---

## Installation Variations

### For Different Systems

**Linux/Mac**:
```bash
cd ~/forge_AI_bot
./mvn clean install -DskipTests "-Dcheckstyle.skip=true"
java -Xmx4G -jar forge-gui-desktop/target/forge-gui-desktop-2.0.08-SNAPSHOT-full.jar
```

**Docker** (if you have Docker):
```dockerfile
FROM openjdk:17
WORKDIR /forge
COPY . .
RUN mvn clean install -DskipTests
CMD ["java", "-Xmx4G", "-jar", "forge-gui-desktop/target/forge-gui-desktop-2.0.08-SNAPSHOT-full.jar"]
```

---

## Post-Installation Configuration

### Training Data Directory

The LLM AI automatically creates and uses:
```
./llm_training_data/
├── decision_<timestamp>.json    # Game state + decisions
├── decision_<timestamp>.json
└── feedback_<timestamp>.json    # Game outcomes
```

You can:
- Monitor this directory to verify LLM AI is working
- Collect data for future fine-tuning
- Analyze decision patterns

### Memory Configuration

For better performance with LLM AI:
```powershell
# Standard
java -Xmx4G -jar forge.jar

# High memory (for complex games)
java -Xmx8G -jar forge.jar

# Low memory (for weaker systems)
java -Xmx2G -jar forge.jar
```

---

## Updating/Reinstalling

### To Update with Latest Changes

```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# Pull latest changes
git pull origin master

# Rebuild
mvn clean install -DskipTests "-Dcheckstyle.skip=true"

# Redeploy if using JAR
Copy-Item "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-full.jar" `
  -Destination "C:\Forge\forge.jar" -Force
```

### To Completely Clean Install

```powershell
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# Remove all build artifacts
mvn clean

# Remove Maven cache (optional, slow rebuild)
rm -r ~/.m2/repository/forge

# Rebuild from scratch
mvn install -DskipTests "-Dcheckstyle.skip=true"
```

---

## Verifying Installation Success

### Checklist

- ✅ Maven build completes with "BUILD SUCCESS"
- ✅ All 6-7 modules compile without errors
- ✅ JAR file created: `forge-gui-desktop/target/forge-gui-desktop-*-full.jar`
- ✅ Forge starts without errors
- ✅ Game setup screen appears
- ✅ AI options menu shows "Use LLM AI" checkbox
- ✅ Can enable LLM AI and start game
- ✅ Game runs without crashes
- ✅ Training data directory created automatically

### Testing the LLM AI

Once installed:

```powershell
# 1. Start Forge
java -Xmx4G -jar forge.jar

# 2. Create a test game:
#    - New Game
#    - 2 players
#    - Player 1: Human (you)
#    - Player 2: AI
#    - Standard format

# 3. Enable LLM AI:
#    - Right-click AI player slot
#    - Check "Use LLM AI"
#    - Start game

# 4. Monitor console for:
#    - No error messages
#    - Game plays normally
#    - AI makes decisions

# 5. Check for training data:
#    - Open file explorer
#    - Go to: ./llm_training_data/
#    - Should contain decision_*.json files
```

---

## Troubleshooting Installation

### Build Fails Midway

```powershell
# Check for corruption, try clean rebuild
mvn clean
mvn install -DskipTests "-Dcheckstyle.skip=true" -U

# -U forces update of dependencies
```

### Forge Won't Start

```powershell
# Check for Java version compatibility
java -version  # Should show 17 or higher

# Try running with verbose output
java -verbose:class -Xmx4G -jar forge.jar 2>&1 | head -50

# Check for missing dependencies
mvn dependency:tree
```

### LLM AI Doesn't Appear in Settings

```powershell
# Ensure desktop GUI was built
mvn -pl :forge-gui-desktop clean install -DskipTests "-Dcheckstyle.skip=true"

# Use the full JAR (not just the regular jar)
java -jar forge-gui-desktop/target/forge-gui-desktop-*-full.jar
```

---

## Summary

### Quick Install (Copy-Paste Ready)

```powershell
# 1. Navigate to project
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot

# 2. Set Maven in PATH
$env:PATH = "c:\Users\Owner\.maven\maven-3.9.11(1)\bin;$env:PATH"

# 3. Build project (full build recommended)
mvn clean install -DskipTests "-Dcheckstyle.skip=true"

# Wait for "BUILD SUCCESS" message...
# This takes ~40-60 seconds

# 4. Ensure res folder exists in current directory
if (-not (Test-Path ".\res")) {
    Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
}

# 5. Run Forge
java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"

# 6. In Forge:
#    - Create game
#    - Select AI player
#    - Right-click AI button
#    - Check "Use LLM AI"
#    - Start game!
```

### Expected Results

- Forge launches with full UI
- Game setup includes LLM AI option
- Can enable "Use LLM AI" in AI settings
- Games play with LLM decision-making
- Training data collected automatically

**Installation Status**: ✅ Ready to deploy!
