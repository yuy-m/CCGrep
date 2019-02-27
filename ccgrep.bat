@echo off

set DPATH=%~dp0
set JARNAME="ccgrep.jar"

if %DPATH% == "C:\Program Files\ccgrep\bin" (
  set DPATH="C:\Program Files\ccgrep"
)

if exist "%DPATH%\%JARNAME%" (
  java -jar "%DPATH%\%JARNAME%" %*
) else (
  echo "%JARNAME% not found."
  exit 2
)