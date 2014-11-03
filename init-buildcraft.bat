rem ==============================================
rem init-buildcraft.bat
rem Updates the buildcraft code mixed in with the mod code
rem ==============================================

git submodule update --init --remote

rem remove files that were in the previous version but were removed
git submodule foreach git reset --hard

rem copy the sourcecode out
xcopy /E /Y /Q /I src\main\java\buildcraft\additionalpipes source.bak

rem remove the entire codebase
rmdir /Q /S src\main\java\buildcraft

rem copy the sourcecode back
xcopy /E /Y /Q /I source.bak src\main\java\buildcraft\additionalpipes
del /Q source.bak

rem copy the buildcraft code back in
xcopy /E /Y /Q BuildCraft\common\buildcraft src\main\java\buildcraft
xcopy /E /Y /Q /I BuildCraft\api\buildcraft\api src\main\java\buildcraft\api
xcopy /E /Y /Q /I BuildCraft\buildcraft_resources\assets src\main\resources\assets

pause