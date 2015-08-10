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
3. In Eclipse, go to `File > Import... > General > Existing Projects into Workspace`
4. Hit Next.  Click Browse... in the top right, and select the directory you cloned Additional Pipes into.  Check the box next to AdditionalPipesBC in the Projects list.
5. Hit Finish, and the mod project should be imported.
6. Set up the run configuration.  Go to `File > Import... > Run/Debug > Launch Configurations` and hit next.
7. Click the Browse... button and select the directory you cloned Additional Pipes into.  Check the box next to `Run AP Client.launch` in the right pane.
8. Hit finish.
9. Click the arrow next to the play button on the top bar, select `Run Configurations...`, and click on Run AP Client in the left pane.
10. If you want to set your usrname, go to the arguments tab and replace `APDev` with your Minecraft username in the `Program Arguments` box.
11.  Hit run.  If it works, you're done!
12.  If it doesn't, it's probably because your `.gradle` folder is not in your home directory, or you have unusual environment variables defined.  Either way, in the Arguments tab, replace both
 instances of `${env_var:userprofile}${env_var:HOME}/.gradle` with the full path to your `.gradle` folder.

*Done!*

*Look ma, no LWJGL native library errors!*  

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