@echo off
REM Build LLM AI Classes Without Maven
REM This script compiles just the LLM AI classes without needing Maven

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo.
echo ============================================================
echo Forge LLM AI - Direct Java Compilation
echo ============================================================
echo.

set JAVA_HOME=C:\Program Files\BellSoft\LibericaJDK-17
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Create output directory
if not exist "build" mkdir build

echo Compiling LLM AI classes...
echo.

REM Compile all LLM classes
cd forge-ai\src\main\java\forge\ai\llm

javac -d ..\..\..\..\..\..\build\classes ^
  GameContext.java ^
  RankedAction.java ^
  LlmDecision.java ^
  LlmClient.java ^
  LocalLlmClient.java ^
  LlmClientFactory.java ^
  GameStateSerializer.java ^
  PlayerControllerLlm.java ^
  LlmAiExample.java ^
  LlmAiDemo.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo SUCCESS! LLM classes compiled to: build\classes
    echo.
    echo To run examples:
    echo   cd ..\..\..\..\..
    echo   java -cp build\classes forge.ai.llm.LlmAiExample
    echo.
) else (
    echo.
    echo ERROR: Compilation failed
    echo.
)

cd ..\..\..\..\..

endlocal
