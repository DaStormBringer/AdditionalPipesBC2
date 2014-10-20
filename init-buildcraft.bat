git submodule update --init --remote

xcopy /E /Y /Q BuildCraft\common\buildcraft src\main\java\buildcraft
xcopy /E /Y /Q /I BuildCraft\api\buildcraft\api src\main\java\buildcraft\api
xcopy /E /Y /Q /I BuildCraft\buildcraft_resources\assets src\main\resources\assets
