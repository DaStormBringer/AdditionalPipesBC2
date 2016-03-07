## Update of Additional Pipes for BC3+ and Minecraft 1.5+

## Many of you are probably wondering whether this mod is dead. 
The situation doesn't look good, huh.  The BuildCraft forums have been down for months, and may never return.  The future of BuildCraft itself is uncertain.  Asiekierka has stepped down as lead developer, and 1.8 is looming.
Is all really lost?  

No, it isn't, not by a long shot.  AlexIIL is now [developing BuildCraft](https://github.com/AlexIIL/BuildCraft) for 1.8 in a seperate repository.  Apparantly, it's nearly done, it just doesn't upconvert 1.7.10 data.
  I started working on a 1.8 port of AP a year ago, but abandoned it because
Buildcraft didn't have a working 1.8 version at that time.  Now that it does, and I will try to complete my port, and bring AP into the new version.  I'll probably have to take out the LP integration, at least for the time being, 
since it hasn't updated to 1.8. 

If you're looking for documentation on the mod, [an archived version of the forum thread is here](https://web.archive.org/web/20150919105906/http://mod-buildcraft.com/forums/showthread.php?tid=1467).

# And asie, if you're reading this, I don't CARE that the forum passwords were leaked!  You're causing far more damage by taking down the forums!  Just let everybody change their passwords and get on with their lives!

### Cloning and Compiling ###
First, install [Gradle](https://gradle.org/) and put it on your path.

    git clone https://github.com/tcooc/AdditionalPipesBC.git
    cd AdditionalPipesBC
    gradle setupDecompWorkspace
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
10. If you want to set your username, go to the arguments tab and replace `APDev` with your Minecraft username in the `Program Arguments` box.
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