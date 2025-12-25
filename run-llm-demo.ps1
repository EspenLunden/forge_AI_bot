# Simple LLM Demo Runner
# Runs the LLM examples without needing the full Forge build

$JAVA_HOME = "C:\Program Files\BellSoft\LibericaJDK-17"
$javac = "$JAVA_HOME\bin\javac.exe"
$java = "$JAVA_HOME\bin\java.exe"

$sourceDir = "forge-ai\src\main\java"
$buildDir = "build\llm-demo"

Write-Host "==============================================="
Write-Host "Forge LLM AI Demo Builder"
Write-Host "===============================================`n"

# Create build directory
if (-not (Test-Path $buildDir)) {
    mkdir $buildDir -Force > $null
    Write-Host "[+] Created build directory: $buildDir`n"
}

# Get all LLM source files
$llmDir = "$sourceDir\forge\ai\llm"
$sourceFiles = @(
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

Write-Host "[*] Checking source files...`n"
$missingFiles = @()
foreach ($file in $sourceFiles) {
    $filePath = "$llmDir\$file"
    if (Test-Path $filePath) {
        Write-Host "  [OK] $file"
    } else {
        Write-Host "  [MISSING] $file"
        $missingFiles += $file
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "`n[ERROR] Missing files: $($missingFiles -join ', ')"
    exit 1
}

Write-Host "`n[*] Compiling standalone demo classes...`n"

# Compile only classes that don't need Forge dependencies
$standaloneClasses = @(
    "GameContext.java",
    "RankedAction.java",
    "LlmDecision.java",
    "LlmClient.java",
    "LocalLlmClient.java",
    "LlmClientFactory.java",
    "LlmAiExample.java",
    "LlmAiDemo.java"
)

try {
    $classPath = @()
    foreach ($file in $standaloneClasses) {
        $classPath += "$llmDir\$file"
    }
    
    $cmd = "$javac -encoding UTF-8 -d $buildDir " + ($classPath -join ' ')
    Write-Host "Compiling..."
    Invoke-Expression $cmd 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Compilation failed"
        exit 1
    }
    
    Write-Host "[+] Compilation successful!`n"
} catch {
    Write-Host "[ERROR] Compilation error: $_"
    exit 1
}

# Run the demo
Write-Host "==============================================="
Write-Host "Running LLM AI Demo"
Write-Host "===============================================`n"

try {
    & $java -cp $buildDir forge.ai.llm.LlmAiExample
    
    Write-Host "`n==============================================="
    Write-Host "Demo completed successfully!"
    Write-Host "===============================================`n"
} catch {
    Write-Host "[ERROR] Runtime error: $_"
    exit 1
}
