
set BCBRANCH=7.0.6
git clone --branch %BCBRANCH% --depth 1 https://github.com/BuildCraft/BuildCraft.git

cd BuildCraft

7z a ..\buildcraft-%BCBRANCH%-src.zip api 1>nul

cd common

7z a -r ..\..\buildcraft-%BCBRANCH%-src.zip . 1>nul