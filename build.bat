@echo off
rem enable extended ASCII
chcp 1252 >nul

echo » Invoking gradle...
call gradle build

mkdir build-proc
cd build-proc

echo.
echo --------------------------------------------------------
echo.

for /f %%i in ('dir /b/a-d/od/t:c ..\build\libs') do set LAST=%%i
echo » Processing %LAST%...

xcopy /Y ..\build\libs\%LAST% . >nul

echo.

rem delete most of Buildcraft class files
for /f %%i in ('dir /b /a:d ..\src\main\java\buildcraft\') do (
	if not %%i==additionalpipes (
		if not %%i==api (
			echo » Deleting folder %%i from zip file...
			7z d %LAST% buildcraft\%%i >nul
		)
	)
)

echo.
echo » Deleting top level class files...
7z d %LAST% buildcraft\BuildCraftBuilders.class >nul
7z d %LAST% buildcraft\BuildCraftCore$RenderMode.class >nul
7z d %LAST% buildcraft\BuildCraftCore.class >nul
7z d %LAST% buildcraft\BuildCraftEnergy.class >nul
7z d %LAST% buildcraft\BuildCraftEnergy$1BiomeIdLimitException.class >nul
7z d %LAST% buildcraft\BuildCraftFactory.class >nul
7z d %LAST% buildcraft\BuildCraftFactory$QuarryChunkloadCallback.class >nul
7z d %LAST% buildcraft\BuildCraftMod.class >nul
7z d %LAST% buildcraft\BuildCraftSilicon.class >nul
7z d %LAST% buildcraft\BuildCraftTransport$1.class >nul
7z d %LAST% buildcraft\BuildCraftTransport$ExtractionHandler.class >nul
7z d %LAST% buildcraft\BuildCraftTransport$PipeRecipe.class >nul
7z d %LAST% buildcraft\BuildCraftTransport.class >nul

echo » Deleting assets...
7z d %LAST% assets\buildcraft >nul

echo.
echo » Done!

echo » Collect your finished jar, %LAST%, in the project root dir!
xcopy /Y %LAST% .. >nul

cd ..
rmdir /Q /S build-proc
