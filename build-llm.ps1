# Forge LLM AI Build Script (PowerShell)
# Compiles LLM AI classes without needing Maven

param(
    [string]$JavaHome = "C:\Program Files\BellSoft\LibericaJDK-17",
    [string]$BuildDir = ".\build\classes"
)

# Colors for output
$host.ui.RawUI.ForegroundColor = "Green"

Write-Host ""
Write-Host "============================================================"
Write-Host "Forge LLM AI - Direct Java Compilation"
Write-Host "============================================================"
Write-Host ""

# Check if Java exists
$javaExe = "$JavaHome\bin\javac.exe"
if (-not (Test-Path $javaExe)) {
    Write-Host "ERROR: Java compiler not found at: $javaExe" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Java or set JAVA_HOME correctly" -ForegroundColor Red
    exit 1
}

# Create build directory
$buildPath = Join-Path (Get-Location) $BuildDir
if (-not (Test-Path $buildPath)) {
    New-Item -ItemType Directory -Path $buildPath -Force | Out-Null
    Write-Host "Created build directory: $buildPath"
}

# Change to LLM source directory
$llmDir = ".\forge-ai\src\main\java\forge\ai\llm"
if (-not (Test-Path $llmDir)) {
    Write-Host "ERROR: LLM source directory not found: $llmDir" -ForegroundColor Red
    exit 1
}

Write-Host "Compiling LLM AI classes..."
Write-Host ""

# Get all Java files
$javaFiles = @(
    "GameContext.java",
    "RankedAction.java",
    "LlmDecision.java",
    "LlmClient.java",
    "LocalLlmClient.java",
    "LlmClientFactory.java",
    "GameStateSerializer.java",
    "PlayerControllerLlm.java",
    "LlmAiExample.java",
    "LlmAiDemo.java"
)

# Verify all files exist
$allExist = $true
foreach ($file in $javaFiles) {
    $filePath = Join-Path $llmDir $file
    if (-not (Test-Path $filePath)) {
        Write-Host "  Missing: $file" -ForegroundColor Red
        $allExist = $false
    } else {
        Write-Host "  Found: $file" -ForegroundColor Gray
    }
}

if (-not $allExist) {
    Write-Host ""
    Write-Host "ERROR: Some source files are missing!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Starting compilation..."
Write-Host ""

# Build the javac command
$javacCmd = @("$javaExe", "-d", $buildPath)
foreach ($file in $javaFiles) {
    $javacCmd += (Join-Path $llmDir $file)
}

# Run javac
& $javacCmd 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "SUCCESS! LLM classes compiled to: $buildPath" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Green
    Write-Host "  1. Review the documentation:"
    Write-Host "     - QUICKSTART_LLM.md"
    Write-Host "     - LLM_AI_README.md"
    Write-Host ""
    Write-Host "  2. Run the LLM examples:"
    Write-Host "     java -cp $buildPath forge.ai.llm.LlmAiExample"
    Write-Host ""
    Write-Host "  3. Run the interactive demo:"
    Write-Host "     java -cp $buildPath forge.ai.llm.LlmAiDemo"
    Write-Host ""
    Write-Host "  4. Integrate into your game code (see QUICKSTART_LLM.md)"
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "ERROR: Compilation failed (exit code: $LASTEXITCODE)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Check the error messages above for details" -ForegroundColor Red
    exit 1
}
