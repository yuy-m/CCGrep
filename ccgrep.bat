@echo off

set DPATH=%~dp0
set JARNAME="CCGrep.jar"

if %DPATH% == "C:\Program Files\ccgrep\bin" (
  set DPATH="C:\Program Files\ccgrep"
)

if exist "%DPATH%\%JARNAME%" (
  java -jar "%DPATH%\%JARNAME%" %*
) else if exist "%DPATH%\target\classes" (
  java -cp "%DPATH%\target\classes;%DPATH%\lib\antlr-runtime-4.7.1.jar;%DPATH%\lib\commons-cli-1.4.jar" jp.ac.osaka_u.ist.sel.ccgrep.CCGrep %*
) else (
  echo "No jar-file or class-file"
  exit 2
)
pause