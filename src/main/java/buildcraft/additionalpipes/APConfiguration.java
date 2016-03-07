package buildcraft.additionalpipes;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import buildcraft.additionalpipes.utils.Log;


public class APConfiguration
{	
	public static int chunkSightRange; // config option
	
	public static boolean chunkSightAutorefresh = true;
	
	public static boolean enableDebugLog;
	
	// keybinding
	public static int laserKeyCode; // config option (& in options menu)
	// misc
	public static boolean allowWRRemove;
	
	public static float powerTransmittanceCfg; // config option
	
	public static int waterPumpWaterPerTick; // in millibuckets / tick
	
	public static int gravityFeedPipeTicksPerPull;
	
	public static boolean enableTriggers = true;
	
	//set from config
	public static boolean filterRightclicks = false;

	
	public static void loadConfigs(boolean init, File configFile)
	{
		if((!configFile.exists() && !init) || (configFile.exists() && init))
		{
			return;
		}
		Configuration config = new Configuration(configFile);
		try 
		{
			config.load();
						
			Property powerTransmittance = config.get(Configuration.CATEGORY_GENERAL, "powerTransmittance", 90);
			powerTransmittance.comment = "Percentage of power a power teleport pipe transmits. Between 0 and 100.";
			powerTransmittanceCfg = powerTransmittance.getInt() / 100.0f;
			if(powerTransmittanceCfg > 1.00)
			{
				powerTransmittanceCfg = 0.99f;
			}
			else if(powerTransmittanceCfg < 0.0)
			{
				powerTransmittanceCfg = 0.0f;
			}

			Property chunkSightRangeProperty = config.get(Configuration.CATEGORY_GENERAL, "chunkSightRange", 8);
			chunkSightRangeProperty.comment = "Range of chunk load boundaries.";
			chunkSightRange = chunkSightRangeProperty.getInt();

			Property laserKey = config.get(Configuration.CATEGORY_GENERAL, "laserKeyChar", 68);
			laserKey.comment = "Default key to toggle chunk load boundaries.";
			laserKeyCode = laserKey.getInt();
			
			Property filterRightclicksProperty = config.get(Configuration.CATEGORY_GENERAL, "filterRightclicks", false);
			filterRightclicksProperty.comment = "When right clicking on something with a gui, do not show the gui if you have a pipe in your hand";
			filterRightclicks = filterRightclicksProperty.getBoolean();
			
			Property enableDebugLogProperty = config.get(Configuration.CATEGORY_GENERAL, "enableDebugLog", false);
			enableDebugLogProperty.comment = "Enable debug logging for development";
			enableDebugLog = enableDebugLogProperty.getBoolean();
			
			Property allowWRRemoveProperty = config.get(Configuration.CATEGORY_GENERAL, "allowWRRemove", true);
			allowWRRemoveProperty.comment = "Turn on recipes for removing the redstone and sealant from pipes to turn them back to transport pipes";
			allowWRRemove = allowWRRemoveProperty.getBoolean();
			
			Property waterPerTickProperty = config.get(Configuration.CATEGORY_GENERAL, "waterPumpWaterPerTick", 90);
			waterPerTickProperty.comment = "Amount of water the Water Pump Pipe produces in millibuckets/tick";
			waterPumpWaterPerTick = waterPerTickProperty.getInt();
			
			Property gpPullRateProperty = config.get(Configuration.CATEGORY_GENERAL, "gravityFeedPipeTicksPerPull", 48);
			gpPullRateProperty.comment = "How many ticks the Gravity Feed Pipe needs to extract an item";
			gravityFeedPipeTicksPerPull = gpPullRateProperty.getInt();
		} 
		catch(Exception e)
		{
			Log.error("Error loading Additional Pipes configs." + e);
		}
		finally
		{
			config.save();
		}
	}

}
