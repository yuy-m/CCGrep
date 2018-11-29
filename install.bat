@echo off

echo Not implemented yet.
exit

set FROMPATH=%~dp0
set INSTPATH="C:\Program Files\ccgrep"
set BINPATH="%INSTPATH%\bin"

xcopy /E %FROMPATH% "C:\Program Files\"
move "%INSTPATH%\ccgrep.bat" %BINPATH%
setx PATH "%PATH%;%BINPATH%"
set PATH="%PATH%;%BINPATH%"
