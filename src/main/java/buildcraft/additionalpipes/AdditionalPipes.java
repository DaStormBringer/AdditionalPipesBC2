package buildcraft.additionalpipes;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftSilicon;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.gates.GateProvider;
import buildcraft.additionalpipes.gates.TriggerPipeClosed;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.pipes.APPipe;
import buildcraft.additionalpipes.pipes.PipeItemsAddition;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsWaterPump;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
import buildcraft.additionalpipes.pipes.PipeSwitchFluids;
import buildcraft.additionalpipes.pipes.PipeSwitchItems;
import buildcraft.additionalpipes.pipes.PipeSwitchPower;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.additionalpipes.utils.PipeCreator;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.core.recipes.AssemblyRecipeManager;
import buildcraft.transport.BlockGenericPipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = AdditionalPipes.MODID, name = AdditionalPipes.NAME, dependencies = "after:BuildCraft|Transport;after:BuildCraft|Silicon;after:BuildCraft|Transport;after:BuildCraft|Factory", version = AdditionalPipes.VERSION)
public class AdditionalPipes {
	public static final String MODID = "additionalpipes";
	public static final String NAME = "Additional Pipes";
	public static final String VERSION = "4.3.0";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MutiPlayerProxyClient", serverSide = "buildcraft.additionalpipes.MultiPlayerProxy")
	public static MultiPlayerProxy proxy;

	public File configFile;

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface CfgBool {
	}

	// chunk load boundaries
	public ChunkLoadViewDataProxy chunkLoadViewer;
	public @CfgBool
	boolean chunkSight = true;
	public int chunkSightRange = 8; // config option
	public @CfgBool
	boolean chunkSightAutorefresh = true;
	
	public @CfgBool boolean enableDebugLog = false;

	// teleport scanner TODO
	// public Item teleportScanner;

	// Redstone Liquid
	public Item pipeLiquidsRedstone;
	// Redstone
	public Item pipeItemsRedStone;
	// Advanced Insertion
	public Item pipeItemsAdvancedInsertion;
	// Advanced Insertion
	public Item pipeItemsAddition;
	// Advanced Wood
	public Item pipeItemsAdvancedWood;
	// Distributor
	public Item pipeItemsDistributor;
	// Jeweled
	public Item pipeItemsJeweled;

	
	//priority insertion pipe
	public Item pipeItemsPriority;
	// Item Teleport
	public Item pipeItemsTeleport;
	// Liquid Teleport
	public Item pipeLiquidsTeleport;
	// Power Teleport
	public Item pipePowerTeleport;
	// Items Closed
	public Item pipeItemsClosed;
	// Switch pipes
	public Item pipePowerSwitch;
	public Item pipeItemsSwitch;
	public Item pipeLiquidsSwitch;
	// water pump pipe
	public Item pipeLiquidsWaterPump;
	// chunk loader
	public Block blockChunkLoader;

	public @CfgBool
	boolean enableTriggers = true;
	
	//set from config
	public boolean filterRightclicks = false;
	
	public ITriggerInternal triggerPipeClosed;

	public ITriggerInternal triggerPhasedSignalRed;
	public ITriggerInternal triggerPhasedSignalBlue;
	public ITriggerInternal triggerPhasedSignalGreen;
	public ITriggerInternal triggerPhasedSignalYellow;
	// keybinding
	public static int laserKeyCode = 68; // config option (& in options menu)
	// misc
	public @CfgBool
	boolean allowWRRemove = false;
	public float powerLossCfg = 0.90f; // config option

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		
		PacketHandler.init();

		configFile = event.getSuggestedConfigurationFile();
		loadConfigs(false);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	
		Log.info("Registering chunk load handler");
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
		chunkLoadViewer = new ChunkLoadViewDataProxy(chunkSightRange);
		FMLCommonHandler.instance().bus().register(chunkLoadViewer);
		
		proxy.registerKeyHandler();
		
		proxy.registerRendering();

		loadConfigs(true);
		
		Log.info("Registering pipes");
		loadPipes();

		triggerPipeClosed = new TriggerPipeClosed();
		StatementManager.registerTriggerProvider(new GateProvider());

		if(allowWRRemove) {
			// Additional Pipes
			GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport), new Object[] { "A", 'A', pipePowerTeleport });
			GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport), new Object[] { "A", 'A', pipeLiquidsTeleport });
			GameRegistry.addRecipe(new ItemStack(pipeItemsRedStone), new Object[] { "A", 'A', pipeLiquidsRedstone });
			// BC Liquid
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone), new Object[] { "A", 'A', BuildCraftTransport.pipeFluidsCobblestone });
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[] { "A", 'A', BuildCraftTransport.pipeFluidsGold });
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron), new Object[] { "A", 'A', BuildCraftTransport.pipeFluidsIron });
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[] { "A", 'A', BuildCraftTransport.pipeFluidsStone });
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[] { "A", 'A', BuildCraftTransport.pipeFluidsWood });
			// BC Power
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[] { "A", 'A', BuildCraftTransport.pipePowerGold });
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[] { "A", 'A', BuildCraftTransport.pipePowerStone });
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[] { "A", 'A', BuildCraftTransport.pipePowerWood });
		}

		// ChunkLoader
		blockChunkLoader = new BlockChunkLoader();
		blockChunkLoader.setBlockName("TeleportTether");
		GameRegistry.registerBlock(blockChunkLoader, ItemBlock.class, "chunkLoader");
		GameRegistry.registerTileEntity(TileChunkLoader.class, "TeleportTether");
		GameRegistry.addRecipe(new ItemStack(blockChunkLoader), new Object[] { "iii", "iLi", "iii", 'i', Items.iron_ingot, 'L', new ItemStack(Items.dye, 1, 4) });
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		// event.registerServerCommand(new CommandAdditionalPipes());
		TeleportManager.instance.reset();
	}

	private void loadConfigs(boolean init) {
		if((!configFile.exists() && !init) || (configFile.exists() && init)) {
			return;
		}
		Configuration config = new Configuration(configFile);
		try {
			config.load();
						
			Property powerLoss = config.get(Configuration.CATEGORY_GENERAL, "powerLoss", (int) (powerLossCfg * 100));
			powerLoss.comment = "Percentage of power a power teleport pipe transmits. Between 0 and 100.";
			powerLossCfg = powerLoss.getInt() / 100.0f;
			if(powerLossCfg > 1.00) {
				powerLossCfg = 0.99f;
			} else if(powerLossCfg < 0.0) {
				powerLossCfg = 0.0f;
			}

			Property chunkLoadSightRange = config.get(Configuration.CATEGORY_GENERAL, "chunkSightRange", chunkSightRange);
			chunkLoadSightRange.comment = "Range of chunk load boundaries.";

			Property laserKey = config.get(Configuration.CATEGORY_GENERAL, "laserKeyChar", laserKeyCode);
			laserKey.comment = "Default key to toggle chunk load boundaries.";
			laserKeyCode = laserKey.getInt();
			
			Property filterRightclicksProperty = config.get(Configuration.CATEGORY_GENERAL, "filterRightclicks", false);
			filterRightclicksProperty.comment = "When right clicking on something with a gui, do not show the gui if you have a pipe in your hand";
			filterRightclicks = filterRightclicksProperty.getBoolean();
			
			Property enableDebugLogProperty = config.get(Configuration.CATEGORY_GENERAL, "enableDebugLog", false);
			enableDebugLogProperty.comment = "Enable debug logging for development";
			enableDebugLog = enableDebugLogProperty.getBoolean();
			
			
		} catch(Exception e) {
			Log.error("Error loading Additional Pipes configs." + e);
		} finally {
			config.save();
		}
	}

	private void loadPipes() {
		// Item Teleport Pipe
		pipeItemsTeleport = PipeCreator.createPipeSpecial((Class<? extends APPipe<?>>) PipeItemsTeleport.class);
		
		GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport, 4), new Object[] { "dgd", 'd', BuildCraftCore.diamondGearItem, 'g', Blocks.glass });
		AssemblyRecipeManager.INSTANCE.addRecipe("teleportPipe", 10000, new ItemStack(pipeItemsTeleport, 8), new Object[] { new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 4), new ItemStack(BuildCraftTransport.pipeItemsDiamond, 8),
				new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 3) });


		// Liquid Teleport Pipe
		pipeLiquidsTeleport = PipeCreator.createPipeSpecial((Class<? extends APPipe<?>>) PipeLiquidsTeleport.class);
		if(pipeItemsTeleport != null) {
			GameRegistry.addShapelessRecipe(new ItemStack(pipeLiquidsTeleport), new Object[] {BuildCraftTransport.pipeWaterproof, pipeItemsTeleport});
		}

		// Power Teleport Pipe
		
		pipePowerTeleport = PipeCreator.createPipeSpecial((Class<? extends APPipe<?>>) PipePowerTeleport.class);
		if(pipeItemsTeleport != null) {
			GameRegistry.addShapelessRecipe(new ItemStack(pipePowerTeleport), new Object[] {Items.redstone, pipeItemsTeleport});
		}

		//Jeweled Pipe
		//disabled since I can't get the GUI to work
		//pipeItemsJeweled = doCreatePipeAndRecipe(PipeItemsJeweled.class, new Object[] { " D ", "DGD", " D ", 'D', BuildCraftTransport.pipeItemsDiamond, 'G', BuildCraftCore.goldGearItem});
		
		// Distributor Pipe
		pipeItemsDistributor = PipeCreator.createPipeAndRecipe(1, PipeItemsDistributor.class, new Object[] { " r ", "IgI", 'r', Items.redstone, 'I', Items.iron_ingot, 'g', Blocks.glass }, false);

		// Advanced Insertion Pipe
		pipeItemsAdvancedInsertion = PipeCreator.createPipeAndRecipe(8, PipeItemsAdvancedInsertion.class,
				new Object[] { "IgI", 'I', BuildCraftCore.ironGearItem, 'g', Blocks.glass }, false);
		
		// Advanced Insertion Pipe
		pipeItemsAddition = PipeCreator.createPipeAndRecipe(1, PipeItemsAddition.class,
				new Object[] { " R ", "RIR", " R ", 'I', pipeItemsAdvancedInsertion, 'R', Items.redstone}, false);
		
		pipeItemsPriority = PipeCreator.createPipeAndRecipe(2, PipeItemsPriorityInsertion.class, new Object[] {pipeItemsDistributor, pipeItemsAdvancedInsertion}, true);
		
		// Advanced Wooded Pipe
		pipeItemsAdvancedWood = PipeCreator.createPipeAndRecipe(8, PipeItemsAdvancedWood.class, new Object[] { "WgW", 'W', BuildCraftCore.woodenGearItem, 'g', Blocks.glass }, false);

		// Closed Items Pipe
		pipeItemsClosed = PipeCreator.createPipeAndRecipe(1, PipeItemsClosed.class, new Object[] {BuildCraftTransport.pipeItemsVoid, BuildCraftCore.ironGearItem}, true);
		// switch pipes
		pipeItemsSwitch = PipeCreator.createPipeAndRecipe(8, PipeSwitchItems.class, new Object[] { "GgI", 'g', Blocks.glass, 'G', BuildCraftCore.goldGearItem, 'I', BuildCraftCore.ironGearItem}, false);
		pipePowerSwitch = PipeCreator.createPipeAndRecipe(1, PipeSwitchPower.class, new Object[] {pipeItemsSwitch, Items.redstone }, true);
		pipeLiquidsSwitch = PipeCreator.createPipeAndRecipe(1, PipeSwitchFluids.class, new Object[] {pipeItemsSwitch, BuildCraftTransport.pipeWaterproof }, true);

		// water pump pipe
		pipeLiquidsWaterPump = PipeCreator.createPipeAndRecipe(1, PipeLiquidsWaterPump.class, new Object[] { " L ", "rPr", " W ", 'r', Items.redstone, 'P', BuildCraftCore.ironGearItem, 'L',
				BuildCraftTransport.pipeFluidsGold, 'w', BuildCraftTransport.pipeWaterproof, 'W', BuildCraftTransport.pipeFluidsWood }, false);
	}

	// legacy method
	public static boolean isPipe(Item item)
	{
		if(item != null && BlockGenericPipe.pipes.containsKey(item))
		{
			return true;
		}
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) throws IOException 
	{
		Textures.registerIcons(event.map, event.map.getTextureType());
	}
}
