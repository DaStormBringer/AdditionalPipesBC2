
set BCBRANCH=6.3.6
git clone --branch %BCBRANCH% --depth 1 https://github.com/BuildCraft/BuildCraft.git

cd BuildCraft/common

7z a -r ..\..\buildcraft-%BCBRANCH%-src.zip . 1>nul