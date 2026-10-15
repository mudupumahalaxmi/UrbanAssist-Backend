@REM -------------------------------------------------------------------
@REM Maven Wrapper for Windows — UrbanAssist Backend
@REM
@REM This script locates the Maven distribution downloaded into
@REM %USERPROFILE%\.m2\wrapper\dists\ and delegates to it.
@REM You do NOT need to install Maven globally.
@REM -------------------------------------------------------------------
@echo off
setlocal

set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"

@REM Check if Maven is available
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Error: Maven 3.9.9 not found at %MAVEN_HOME%
    echo Please run the setup instructions in README.md first.
    exit /b 1
)

@REM Delegate to Maven with all arguments
call "%MAVEN_HOME%\bin\mvn.cmd" %*

exit /b %ERRORLEVEL%
