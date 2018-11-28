@echo off

echo Not implemented yet.
exit

set DPATH=%~dp0
set INSTPATH="C:\Program Files\%DPATH%"
set BINPATH="%INSTPATH%\bin"

openfiles > NUL 2>&1 
if %ERRORLEVEL% equ 0 (
    xcopy /E %DPATH% "C:\Program Files\"
    move "%INSTPATH%\ccgrep.bat" "%BINPATH%"
    setx PATH "%PATH%;%BINPATH%"
    set PATH="%PATH%;%BINPATH%"
) else (
    echo Error: Not administrator mode.
)


