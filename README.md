## Update of Additional Pipes for BC3+ and Minecraft 1.5+

### Cloning and Compiling ###
First, install [Gradle](https://gradle.org/) and put it on your path.

    git clone https://github.com/tcooc/AdditionalPipesBC.git
    cd AdditionalPipesBC
    gradle setupCiWorkspace
    gradle build
Compiled binaries can be found in `build/libs`.
    
### Setting Up Eclipse ###
1. Install Eclipse JDK.
2. Run the command `gradle setupDecompWorkspace --refresh-dependencies eclipse`
3. In Eclipse, go to File > Import... > Genral > Existing Projects into Workspace
4. Hit Next.  Click Browse... in the top right, and select the directory you cloned Additional Pipes into.  Check the box next to AdditionalPipesBC in the Projects list.
5. Hit Finish, and the mod project should be imported.
6. We need to work around an annoying bug in Forge.  Expand the "Referenced Libraries" section of your project in the package explorer, find `lwjgl-2.9.1.jar` near the bottom, right click it, and select properties.
7. Go to the Native Library tab, press the External Folder button, and browse to `path-to/.gradle/caches/minecraft/net/minecraft/minecraft_natives/1.7.10`.  Hit OK.
6. Set up the run configuration.  Click the dropdown next to the play button on the top bar, and select Run Configurations...
7. Hit the New icon in the top left.  Type AP Client as the name, and `net.minecraft.launchwrapper.Launch` as the main class.
8. Go to the Arguments tab.  In the VM arguments box put `-Dfml.ignoreInvalidMinecraftCertificates=true`
9. In the program arguments box, put for a 1.7.10 version:
``` --version 1.7 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker --accessToken modstest --username YourMCUsername --userProperties {} --assetIndex 1.7.10 --assetsDir path-to/.gradle/caches/minecraft/assets```

and for 1.8.0:

``` --version 1.8 --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker --accessToken modstest --username YourMCUsername --userProperties {} --assetIndex 1.8 --assetsDir path-to/.gradle/caches/minecraft/assets```

Replace YourMCUsername with your Minecraft username, and fill in the path to your .gradle folder.

*Done!*

### Download: [latest releases](https://github.com/tcooc/AdditionalPipesBC/releases) ####
To install, place in the standard minecraft/mods folder.

### Credits to Zeldo, DaStormBringer and [Additional Pipes Contributors](https://github.com/tcooc/AdditionalPipesBC/graphs/contributors) for their work on the mod.

1.2.5 Thread: http://www.minecraftforum.net/topic/856360-125bc2214-bc315rev213rev310-additional-pipes-for-buildcraft-teleport-pipes/

Pipes still generally do the same thing. Some GUIs and recipes have changed (check NEI for recipes).

#### Experimental Pipes & Gate Features ####

##### Power Switch Pipe #####

Just a nifty little pipe with nice texture effects. Complete credits to ABO for the idea (the code is original though).

##### Closed Pipe #####

This pipe has 3 functions:

1. Acts like a normal pipe when transporting items.

2. Acts like a void pipe in that it never drops items.

3. Stores the items it "drops" inside an buffer. This buffer will destroy the oldest stack to make space for new stack when full.

Closed pipes also have a "closed" gate trigger which activates when the buffer has item(s) inside.

##### Water Pump Pipe #####

Just a pipe that fills with water if a source block is under it.