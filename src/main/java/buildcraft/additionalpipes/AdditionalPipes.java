package buildcraft.additionalpipes;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
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
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsWaterPump;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
import buildcraft.additionalpipes.pipes.PipeSwitchFluids;
import buildcraft.additionalpipes.pipes.PipeSwitchItems;
import buildcraft.additionalpipes.pipes.PipeSwitchPower;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.StatementManager;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.recipes.AssemblyRecipeManager;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
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
	public static final String VERSION = "${version}";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MutiPlayerProxyClient", serverSide = "buildcraft.additionalpipes.MutiPlayerProxy")
	public static MutiPlayerProxy proxy;

	public File configFile;

	public Logger logger;

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface CfgBool {
	}

	public static final String LOC_PATH = "/buildcraft/additionalpipes";
	public static final String[] LOCALIZATIONS = {"es_ES", "ru_RU", "de_DE", "en_US"};

	// chunk load boundaries
	public ChunkLoadViewDataProxy chunkLoadViewer;
	public @CfgBool
	boolean chunkSight = true;
	public int chunkSightRange = 8; // config option
	public @CfgBool
	boolean chunkSightAutorefresh = true;

	// teleport scanner TODO
	// public Item teleportScanner;

	// Redstone Liquid
	public Item pipeLiquidsRedstone;
	// Redstone
	public Item pipeItemsRedStone;
	// Advanced Insertion
	public Item pipeItemsAdvancedInsertion;
	// Advanced Wood
	public Item pipeItemsAdvancedWood;
	// Distributor
	public Item pipeItemsDistributor;;
	// Item Teleport
	public Item pipeItemsTeleport;
	// Liquid Teleport
	public Item pipeLiquidsTeleport;
	// Power Teleport
	public Item pipePowerTeleport;
	// Items Closed
	public Item pipeItemsClosed;;
	// Switch pipes
	public Item pipePowerSwitch;
	public Item pipeItemsSwitch;
	public Item pipeLiquidsSwitch;;
	// water pump pipe
	public Item pipeLiquidsWaterPump;
	// chunk loader
	public Block blockChunkLoader;

	public @CfgBool
	boolean enableTriggers = true;
	public ITrigger triggerPipeClosed;

	public ITrigger triggerPhasedSignalRed;
	public ITrigger triggerPhasedSignalBlue;
	public ITrigger triggerPhasedSignalGreen;
	public ITrigger triggerPhasedSignalYellow;
	// keybinding
	public static int laserKeyCode = 64; // config option (& in options menu)
	// misc
	public @CfgBool
	boolean allowWRRemove = false;
	public float powerLossCfg = 0.90f; // config option

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = Logger.getLogger(MODID);
		//logger.setParent(FMLLog.getLogger());
		logger.setLevel(Level.INFO); // DEBUG

		configFile = event.getSuggestedConfigurationFile();
		loadConfigs(false);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
		chunkLoadViewer = new ChunkLoadViewDataProxy(chunkSightRange);
		FMLCommonHandler.instance().bus().register(chunkLoadViewer);
		proxy.registerKeyHandler();
		proxy.registerRendering();

		// powerMeter = new
		// ItemPowerMeter(powerMeterId).setItemName("powerMeter");
		// LanguageRegistry.addName(powerMeter, "Power Meter");
		loadConfigs(true);
		loadPipes();

		triggerPipeClosed = new TriggerPipeClosed("APClosed");
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
		blockChunkLoader = new BlockChunkLoader(32);
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
			
			config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "Disabling items/blocks only disables recipes.");
			
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
			
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Error loading Additional Pipes configs.", e);
		} finally {
			config.save();
		}
	}

	private void loadPipes() {
		// Item Teleport Pipe
		pipeItemsTeleport = createPipeSpecial(PipeItemsTeleport.class);
		
		GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport, 4), new Object[] { "dgd", 'd', BuildCraftCore.diamondGearItem, 'g', Blocks.glass });
		AssemblyRecipeManager.INSTANCE.addRecipe("teleportPipe", 1000, new ItemStack(pipeItemsTeleport, 8), new Object[] { new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 4), new ItemStack(BuildCraftTransport.pipeItemsDiamond, 8),
				new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 3) });


		// Liquid Teleport Pipe
		pipeLiquidsTeleport = createPipeSpecial(PipeLiquidsTeleport.class);
		if(pipeItemsTeleport != null) {
			GameRegistry.addRecipe(new ItemStack(pipeLiquidsTeleport), new Object[] { "w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemsTeleport });
		}

		// Power Teleport Pipe
		
		pipePowerTeleport = createPipeSpecial(PipePowerTeleport.class);
		if(pipeItemsTeleport != null) {
			GameRegistry.addRecipe(new ItemStack(pipePowerTeleport), new Object[] { "r", "P", 'r', Items.redstone, 'P', pipeItemsTeleport });
		}

		// Distributor Pipe
		pipeItemsDistributor = doCreatePipeAndRecipe(PipeItemsDistributor.class, new Object[] { " r ", "IgI", 'r', Items.redstone, 'I', Items.iron_ingot, 'g', Blocks.glass });

		// Advanced Wooded Pipe
		pipeItemsAdvancedWood = doCreatePipeAndRecipe(8, PipeItemsAdvancedWood.class, new Object[] { "WgW", 'W', BuildCraftCore.woodenGearItem, 'g', Blocks.glass });

		// Advanced Insertion Pipe
		pipeItemsAdvancedInsertion = doCreatePipeAndRecipe(8, PipeItemsAdvancedInsertion.class,
				new Object[] { "IgI", 'I', BuildCraftCore.ironGearItem, 'g', Blocks.glass });

		// Closed Items Pipe
		pipeItemsClosed = doCreatePipeAndRecipe(PipeItemsClosed.class, new Object[] { "r", "I", 'I', BuildCraftTransport.pipeItemsVoid, 'i', BuildCraftCore.ironGearItem });
		// switch pipes
		pipeItemsSwitch = doCreatePipeAndRecipe(8, PipeSwitchItems.class, new Object[] { "GgG", 'g', Blocks.glass, 'G', BuildCraftCore.goldGearItem });
		pipePowerSwitch = doCreatePipeAndRecipe(PipeSwitchPower.class, new Object[] { "r", "I", 'I', pipeItemsSwitch, 'r', Items.redstone });
		pipeLiquidsSwitch = doCreatePipeAndRecipe(PipeSwitchFluids.class, new Object[] { "w", "I", 'I', pipeItemsSwitch, 'w', BuildCraftTransport.pipeWaterproof });

		// water pump pipe
		pipeLiquidsWaterPump = doCreatePipeAndRecipe(PipeLiquidsWaterPump.class, new Object[] { " L ", "rPr", " W ", 'r', Items.redstone, 'P', BuildCraftCore.ironGearItem, 'L',
				BuildCraftTransport.pipeFluidsGold, 'w', BuildCraftTransport.pipeWaterproof, 'W', BuildCraftTransport.pipeFluidsWood });
	}

	private Item doCreatePipeAndRecipe(Class<? extends Pipe<?>> clas, Object[] recipe) 
	{
		return doCreatePipeAndRecipe(1, clas, recipe);
	}

	private Item doCreatePipeAndRecipe(int output, Class<? extends Pipe<?>> clas, Object[] recipe) {

		Item pipe = createPipe(clas);
		for(Object obj : recipe) {
			if(obj == null)
				return pipe;
		}
		GameRegistry.addRecipe(new ItemStack(pipe, output), recipe);
		return pipe;
	}

	private static Item createPipe(Class<? extends Pipe<?>> clas)
	{
		Item res = BlockGenericPipe.registerPipe(clas, CreativeTabBuildCraft.PIPES);
		res.setUnlocalizedName(clas.getSimpleName());
		proxy.registerPipeRendering(res);
		return res;
	}

	// special pipe code
	private static class ItemPipeAP extends ItemPipe {
		protected ItemPipeAP() {
			super(CreativeTabBuildCraft.PIPES);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public EnumRarity getRarity(ItemStack stack) {
			return EnumRarity.rare;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
			super.addInformation(stack, player, list, advanced);
			String key = "tip." + stack.getItem().getClass().getSimpleName();
			
			list.add(StatCollector.translateToLocal(key));
		}
	}

	private Item createPipeSpecial(Class<? extends Pipe<?>> clas)
	{
		ItemPipe item = new ItemPipeAP();
		item.setUnlocalizedName(clas.getSimpleName());
		proxy.registerPipeRendering(item);
		BlockGenericPipe.pipes.put(item, clas);
		proxy.createPipeSpecial(item, clas);

		return item;
	}

	// legacy method
	public static boolean isPipe(Item item) {
		if(item != null && BlockGenericPipe.pipes.containsKey(item)) {
			return true;
		}
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) throws IOException {
		Textures.registerIcons(event.map, event.map.getTextureType());
	}
}
