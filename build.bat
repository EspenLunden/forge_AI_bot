@echo off
REM Forge LLM AI Build Script
REM This script builds the Forge project with the new LLM AI system

setlocal enabledelayedexpansion

REM Set Java home
set JAVA_HOME=C:\Program Files\BellSoft\LibericaJDK-17

REM Check if Maven is available globally
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Maven found in PATH, using global Maven...
    call mvn clean install -DskipTests -pl forge-ai
    goto :end
)

REM If Maven not found, try to download and use Maven wrapper if available
echo Maven not found in PATH
echo.
echo Option 1: Install Maven manually from https://maven.apache.org/download.cgi
echo           Then add Maven bin directory to your PATH
echo.
echo Option 2: Use the Maven wrapper (if pom.xml has one)
echo           Run: mvnw clean install -DskipTests -pl forge-ai
echo.
echo Option 3: Build just the LLM classes without full Maven
echo           Run: build-llm-classes.bat
echo.

:end
endlocal
