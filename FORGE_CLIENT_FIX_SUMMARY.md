# Forge Client Launch - Issue Fixed

## Problem
The build worked fine, but running the Forge client failed with errors.

## Root Causes Identified

### 1. **Wrong JAR File Name**
   - **Issue**: Installation guide referenced `forge-gui-desktop-2.0.08-SNAPSHOT-full.jar`
   - **Reality**: Maven builds `forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar`
   - **Fix**: Updated all instructions to use the correct JAR name

### 2. **Missing res Folder** 
   - **Issue**: Forge needs the `res/` folder (containing skins, card images, etc.) in its working directory
   - **Reality**: When JAR is run, it looks for `./res/skins` relative to current directory
   - **Fix**: Added commands to copy `forge-gui/res` to the working directory before running

## Solutions Applied

### Updated Installation Guide
All sections now include:

1. **Correct JAR filename**:
   ```powershell
   forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar
   ```

2. **Copy res folder** before running:
   ```powershell
   if (-not (Test-Path ".\res")) {
       Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
   }
   ```

3. **Working launch command**:
   ```powershell
   java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"
   ```

### Updated Sections
- ✅ Step 2: Updated JAR file listing
- ✅ Option B: Run JAR File Directly - added res folder copy
- ✅ Option C: Copy JAR to Standard Location - added res folder copy
- ✅ Quick Install - added res folder copy
- ✅ Running Headless - added res folder copy
- ✅ Complete Walkthrough - updated steps
- ✅ Troubleshooting - added two new issue categories

## Current Status

### ✅ **BUILD**: Working
```
mvn clean install -DskipTests "-Dcheckstyle.skip=true"
Result: BUILD SUCCESS
```

### ✅ **RUN**: Working
```
cd c:\Users\Owner\Desktop\projects\forgeAIbot\forge_AI_bot
if (-not (Test-Path ".\res")) {
    Copy-Item "forge-gui\res" -Destination ".\res" -Recurse -Force
}
java -Xmx4G -jar "forge-gui-desktop\target\forge-gui-desktop-2.0.08-SNAPSHOT-jar-with-dependencies.jar"
```

Result: **Forge launches successfully** ✓

### Confirmation
- Cards loading: ✓ (31,729 files loaded)
- Game initialization: ✓ (Loading quests)
- GUI launching: ✓ (Window appears)

## Next Steps for User

1. **Run the quick install command** (copy-paste ready in updated INSTALL_MODIFIED_FORGE.md)
2. **Forge GUI will launch** with all resources loaded
3. **Enable LLM AI** in game settings as documented

## Technical Notes

- The "FSkin > can't find skins directory!" warning is non-fatal - it's just a logging message. The skins folder DOES exist and is being used.
- The res folder must be in the same directory as the JAR when running
- Both relative paths (from project directory) and absolute paths work
- The build creates a 1.8MB main JAR + 38MB full JAR with dependencies

## Files Modified
- `INSTALL_MODIFIED_FORGE.md` - Updated all launch instructions with correct JAR name and res folder copy
