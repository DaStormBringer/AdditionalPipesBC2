
set BCBRANCH=mc1.8
git clone --branch %BCBRANCH% https://github.com/BuildCraft/BuildCraft.git

cd BuildCraft

7z a ..\buildcraft-%BCBRANCH%-src.zip/buildcraft api 1>nul

cd common

7z a -r ..\..\buildcraft-%BCBRANCH%-src.zip . 1>nul

//rmdir /Q /S BuildCraft