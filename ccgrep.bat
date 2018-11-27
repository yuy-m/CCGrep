@echo off
set DNAME=%~dp0
java -cp "%DNAME%\target\classes;%DNAME%\lib\antlr-runtime-4.7.1.jar;%DNAME%\lib\commons-cli-1.4.jar" jp.ac.osaka_u.ist.sel.ccgrep.CCGrep %*
