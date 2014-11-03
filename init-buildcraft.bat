@echo off

rem ==============================================
rem init-buildcraft.bat
rem Updates the buildcraft code mixed in with the mod code
rem ==============================================

echo Updating Buildcraft submodule from Github repo...

git submodule update --init --remote

rem remove files that were in the previous version but were removed
git submodule foreach git reset --hard

rem copy the sourcecode out
echo Removing mod source from source tree...
xcopy /E /Y /Q /I src\main\java\buildcraft\additionalpipes source.bak

rem remove the entire codebase
echo Cleaning source tree...
rmdir /Q /S src\main\java\buildcraft

rem copy the sourcecode back
echo Re-adding mod source...
xcopy /E /Y /Q /I source.bak src\main\java\buildcraft\additionalpipes
rmdir /Q /S source.bak

rem copy the buildcraft code back in
echo Re-adding Buildcraft source...
xcopy /E /Y /Q BuildCraft\common\buildcraft src\main\java\buildcraft
xcopy /E /Y /Q /I BuildCraft\api\buildcraft\api src\main\java\buildcraft\api
xcopy /E /Y /Q /I BuildCraft\buildcraft_resources\assets src\main\resources\assets