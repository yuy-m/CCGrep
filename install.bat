@echo off

set INSTPATH=C:\Program Files\ccgrep
set BINPATH=%INSTPATH%\bin

echo removing old files...
if exist "%BINPATH%\ccgrep.bat"   del "%BINPATH%\ccgrep.bat"
if exist "%INSTPATH%\install.bat" del "%INSTPATH%\install.bat"
if exist "%INSTPATH%\LICENSE.txt" del "%INSTPATH%\LICENSE.txt"
if exist "%INSTPATH%\README.md"   del "%INSTPATH%\README.md"
if exist "%INSTPATH%\CCGrep.jar"  del "%INSTPATH%\CCGrep.jar"
if exist "%INSTPATH%\ccgrep.bat"  del "%INSTPATH%\ccgrep.bat"

if not "%1" == "clean" (
    echo installing...
    set FROMPATH=%~dp0
    if not exist "%INSTPATH%" mkdir "%INSTPATH%"
    if not exist "%BINPATH%"  mkdir "%BINPATH%"
    copy "%FROMPATH%install.bat" "%INSTPATH%"
    copy "%FROMPATH%LICENSE.txt" "%INSTPATH%"
    copy "%FROMPATH%README.md"   "%INSTPATH%"
    copy "%FROMPATH%CCGrep.jar"  "%INSTPATH%"
    copy "%FROMPATH%ccgrep.bat"  "%BINPATH%"
    icacls "%BINPATH%\ccgrep.bat" /grant Everyone:RX
)
