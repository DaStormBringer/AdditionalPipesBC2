@echo off
rem enable extended ASCII
chcp 1252 >nul

echo » Invoking gradle...
call gradle build

pause