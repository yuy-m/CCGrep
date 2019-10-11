@echo off

set DPATH=%~dp0
set JARNAME=CCGrep.jar
set INSTPATH=C:\Program Files\ccgrep

if "%DPATH%" == "%INSTPATH%\bin\" (
  if exist "%INSTPATH%\%JARNAME%" (
    java -jar "%INSTPATH%\%JARNAME%" %*
    exit /b
  )
)

if exist "%DPATH%\%JARNAME%" (
  java -jar "%DPATH%\%JARNAME%" %*
) else (
  echo "%JARNAME% not found."
  exit /b 2
)