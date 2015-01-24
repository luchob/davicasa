@echo off

SETLOCAL EnableDelayedExpansion

set JavaPath=
echo The script will try to start the davicasa tool under your windows shell.

REM search for the current version of the installed JRE
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Development Kit" /v CurrentVersion') DO (
	set "CurVer=%%B"
)

REM check which java version is preferred
IF "%CurVer%"=="" (
	goto JavaNotFound
) else (
    echo It seems that your default JRE is %CurVer% and I will try to use it.
)

REM now determine the hava path
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Development Kit\%CurVer%" /v JavaHome') DO set "JavaPath=%%B"

IF "%JavaPath%"=="" (
	goto JavaNotFound
)

echo Starting...
echo on
"%JavaPath%\bin\java.exe" -cp "%~dp0davicasa.jar" eu.balev.davicasa.Davicasa %*
echo off
GOTO :end

:JavaNotFound

echo Sorry, I cannot find JRE on your Windows installation. 
echo You can download one from: http://www.oracle.com/technetwork/java/javase/downloads/index.html.
echo davicasa is a java tool  and it is not able to run without JRE.
echo If you think that you have java try to run the tool manually, e.g. 
echo `java -cp davicasa-1.0-SNAPSHOT-jar-with-dependencies.jar eu.balev.davicasa.Davicasa -help`
echo The script will exit now. Bye!

:end

REM restore env variables
ENDLOCAL