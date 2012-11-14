package buildcraft.additionalpipes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.Block;
import net.minecraft.src.EnumRarity;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.Property;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftSilicon;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.gates.GateProvider;
import buildcraft.additionalpipes.gates.TriggerPipeClosed;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsRedstone;
import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsRedstone;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipePowerSwitch;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.recipes.AssemblyRecipe;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;

@Mod(modid=AdditionalPipes.MODID, name=AdditionalPipes.NAME,
dependencies="required-after:BuildCraft|Transport;required-after:BuildCraft|Silicon",
version=AdditionalPipes.VERSION)
@NetworkMod(channels={AdditionalPipes.CHANNEL, AdditionalPipes.CHANNELNBT},
clientSideRequired=true, serverSideRequired=true, packetHandler=NetworkHandler.class)
public class AdditionalPipes {
	public static final String MODID = "APUnofficial";
	public static final String NAME = "Additional Pipes Unofficial";
	public static final String VERSION = "@VERSION@";
	public static final String CHANNEL = MODID;
	public static final String CHANNELNBT = CHANNEL + "NBT";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MutiPlayerProxyClient",
			serverSide = "buildcraft.additionalpipes.MutiPlayerProxy")
	public static MutiPlayerProxy proxy;

	public Logger logger;

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface CfgId {
		public boolean block() default false;
	}
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface CfgBool {}

	//textures
	public static final String BASE_PATH = "/buildcraft/additionalpipes";
	public static final String TEXTURE_PATH = BASE_PATH + "/sprites";
	public static final String TEXTURE_MASTER = TEXTURE_PATH + "/textures.png";
	public static final String TEXTURE_TRIGGERS = TEXTURE_PATH + "/triggers.png";

	public static final String TEXTURE_GUI_TELEPORT = TEXTURE_PATH + "/blankSmallGui.png";
	public static final String TEXTURE_GUI_ADVANCEDWOOD = TEXTURE_PATH + "/advancedWoodGui.png";
	public static final String TEXTURE_GUI_DISTRIBUTION = TEXTURE_PATH + "/distributionGui.png";

	//chunk load boundaries
	public ChunkLoadViewDataProxy chunkLoadViewer;
	public @CfgBool boolean chunkSight = true;
	public int chunkSightRange = 4; //config option

	//teleport scanner TODO
	//public Item teleportScanner;
	//public @CfgId int teleportScannerId = 14061;
	//meter TODO
	//public Item powerMeter;
	//public @CfgId int powerMeterId = 14060;
	//Items Closed
	public Item pipePowerSwitch;
	public @CfgId int pipePowerSwitchId = 4051;
	//Items Closed
	public Item pipeItemsClosed;
	public @CfgId int pipeItemsClosedId = 4050;
	//Power Teleport
	public Item pipePowerTeleport;
	public @CfgId int pipePowerTeleportId = 4049;
	//Liquid Teleport
	public Item pipeLiquidsTeleport;
	public @CfgId int pipeLiquidsTeleportId = 4048;
	//Item Teleport
	public Item pipeItemsTeleport;
	public @CfgId int pipeItemsTeleportId = 4047;
	//Distributor
	public Item pipeItemsDistributor;
	public @CfgId int pipeItemsDistributorId = 4046;
	//Advanced Wood
	public Item pipeItemsAdvancedWood;
	public @CfgId int pipeItemsAdvancedWoodId = 4045;
	//Advanced Insertion
	public Item pipeItemsAdvancedInsertion;
	public @CfgId int pipeItemsAdvancedInsertionId = 4044;
	//Redstone
	public Item pipeItemsRedStone;
	public @CfgId int pipeItemsRedStoneId = 4043;
	//Redstone Liquid
	public Item pipeLiquidsRedstone;
	public @CfgId int pipeLiquidsRedstoneId = 4042;
	//chunk loader
	public Block blockChunkLoader;
	public @CfgId(block=true) int chunkLoaderId = 189;

	public @CfgBool boolean enableTriggers = true;
	public ITrigger triggerPipeClosed;

	//keybinding
	public int laserKeyCode = 64; //config option (& in options menu)
	//misc
	public @CfgBool boolean allowWRRemove = false;
	public double powerLossCfg = .95; //config option

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		loadConfigs(new Configuration(event.getSuggestedConfigurationFile()));

		logger = Logger.getLogger(MODID);
		logger.setParent(FMLLog.getLogger());
		logger.setLevel(Level.WARNING); //DEBUG

		Properties en_US = null;
		Localization.addLocalization(BASE_PATH + "/lang/", "en_US");
		try {
			en_US = new Properties();
			en_US.load(AdditionalPipes.class.getResourceAsStream((BASE_PATH + "/lang/en_US.properties")));
			LanguageRegistry.instance().addStringLocalization(en_US);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load default localization.", e);
		}
	}

	@Init
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		ForgeChunkManager.setForcedChunkLoadingCallback(this,  new ChunkLoadingHandler());
		chunkLoadViewer = new ChunkLoadViewDataProxy(chunkSightRange);
		TickRegistry.registerScheduledTickHandler(chunkLoadViewer, Side.CLIENT);
		proxy.registerKeyHandler();
		proxy.registerRendering();
	}

	@PostInit
	public void modsLoaded(FMLPostInitializationEvent event) {
		//powerMeter = new ItemPowerMeter(powerMeterId).setItemName("powerMeter");
		//LanguageRegistry.addName(powerMeter, "Power Meter");

		loadPipes();

		if(enableTriggers) {
			triggerPipeClosed = new TriggerPipeClosed(212);
			ActionManager.registerTriggerProvider(new GateProvider());
		}

		if (allowWRRemove) {
			//Additional Pipes
			GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport), new Object[]{"A", 'A', pipePowerTeleport});
			GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport), new Object[]{"A", 'A', pipeLiquidsTeleport});
			GameRegistry.addRecipe(new ItemStack(pipeItemsRedStone), new Object[]{"A", 'A', pipeLiquidsRedstone});
			//BC Liquid
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsCobblestone});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsGold});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsIron});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsStone});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsWood});
			//BC Power
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[]{"A", 'A', BuildCraftTransport.pipePowerGold});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[]{"A", 'A', BuildCraftTransport.pipePowerStone});
			GameRegistry.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[]{"A", 'A', BuildCraftTransport.pipePowerWood});
		}

		//ChunkLoader
		if(chunkLoaderId != 0) {
			blockChunkLoader = new BlockChunkLoader(chunkLoaderId > 0 ? chunkLoaderId : -chunkLoaderId, 32);
			blockChunkLoader.setBlockName("TeleportTether");
			GameRegistry.registerBlock(blockChunkLoader);
			GameRegistry.registerTileEntity(TileChunkLoader.class, "TeleportTether");
			if (chunkLoaderId > 0) {
				GameRegistry.addRecipe(new ItemStack(blockChunkLoader), new Object[]{"iii", "iLi", "iii", 'i', Item.ingotIron, 'L', new ItemStack(Item.dyePowder, 1, 4)});
			}
		}
	}

	@ServerStarting
	public void onServerStart(FMLServerStartingEvent event) {
		//event.registerServerCommand(new CommandAdditionalPipes());
		TeleportManager.instance.teleportPipes.clear();
	}

	private void loadConfigs(Configuration config) {
		try {
			config.load();
			config.addCustomCategoryComment(Configuration.CATEGORY_BLOCK, "Set id to 0 to disable loading the block, add - in front of id to disable recipe only.");
			config.addCustomCategoryComment(Configuration.CATEGORY_ITEM, "Set id to -1 to disable loading the item, add - in front of id to disable recipe only.");
			config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "Disabling items/blocks only disables recipes.");
			Field[] fields = AdditionalPipes.class.getFields();
			for(Field field : fields){
				if(!Modifier.isStatic(field.getModifiers())) {

					CfgId annotation = field.getAnnotation(CfgId.class);
					if(annotation != null) {
						int id = field.getInt(this);
						if(annotation.block()){
							id = config.getBlock(field.getName(), id).getInt();
						}else{
							id = config.getItem(field.getName(), id).getInt();
						}
						field.setInt(this, id);
					} else {
						if(field.isAnnotationPresent(CfgBool.class)){
							boolean bool = field.getBoolean(this);
							bool = config.get(Configuration.CATEGORY_GENERAL,
									field.getName(), bool).getBoolean(bool);
							field.setBoolean(this, bool);
						}
					}

				}
			}

			Property powerLoss = config.get(Configuration.CATEGORY_GENERAL,
					"powerLoss", (int) (powerLossCfg * 100));
			powerLoss.comment = "Percentage of power a power teleport pipe transmits. Between 0 and 100.";
			powerLossCfg = powerLoss.getInt() / 100.0;
			if(powerLossCfg > 1.00) {
				powerLossCfg = 0.99;
			} else if(powerLossCfg < 0.0) {
				powerLossCfg = 0.0;
			}

			Property chunkLoadSightRange = config.get(Configuration.CATEGORY_GENERAL,
					"chunkSightRange", chunkSightRange);
			chunkLoadSightRange.comment = "Range of chunk load boundaries.";

			Property laserKey = config.get(Configuration.CATEGORY_GENERAL,
					"laserKeyChar", laserKeyCode);
			laserKey.comment = "Default key to toggle chunk load boundaries.";
			laserKeyCode = laserKey.getInt();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading Additional Pipes configs.", e);
		} finally {
			config.save();
		}
	}

	private void loadPipes(){
		// Item Teleport Pipe
		if(pipeItemsTeleportId != 0) {
			pipeItemsTeleport = createPipeSpecial(pipeItemsTeleportId > 0 ? pipeItemsTeleportId : -pipeItemsTeleportId, PipeItemsTeleport.class);
			if (pipeItemsTeleportId > 0) {
				GameRegistry.addRecipe(new ItemStack(pipeItemsTeleport, 4), new Object[]{"dgd", 'd', BuildCraftCore.diamondGearItem, 'g', Block.glass});
				AssemblyRecipe.assemblyRecipes.add(
						new AssemblyRecipe(new ItemStack[]{
								new ItemStack(BuildCraftSilicon.redstoneChipset, 1 , 4),
								new ItemStack(BuildCraftTransport.pipeItemsDiamond, 2),
								new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 3)},
								10000, new ItemStack(pipeItemsTeleport, 2)));
			}

		}

		// Liquid Teleport Pipe
		if(pipeLiquidsTeleportId != 0) {
			pipeLiquidsTeleport = createPipeSpecial(pipeLiquidsTeleportId > 0 ? pipeLiquidsTeleportId : -pipeLiquidsTeleportId, PipeLiquidsTeleport.class);
			if (pipeItemsTeleport != null && pipeLiquidsTeleportId > 0) {
				GameRegistry.addRecipe(new ItemStack(pipeLiquidsTeleport), new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemsTeleport});
			}
		}

		// Power Teleport Pipe
		if(pipePowerTeleportId != 0) {
			pipePowerTeleport = createPipeSpecial(pipePowerTeleportId > 0 ? pipePowerTeleportId : -pipePowerTeleportId, PipePowerTeleport.class);
			if (pipeItemsTeleport != null && pipePowerTeleportId > 0) {
				GameRegistry.addRecipe(new ItemStack(pipePowerTeleport), new Object[]{"r", "P", 'r', Item.redstone, 'P', pipeItemsTeleport});
			}
		}

		// Distributor Pipe
		pipeItemsDistributor = doCreatePipeAndRecipe(pipeItemsDistributorId, PipeItemsDistributor.class,
				new Object[]{" r ", "IgI", 'r', Item.redstone, 'I', Item.ingotIron, 'g', Block.glass});

		// Advanced Wooded Pipe
		pipeItemsAdvancedWood = doCreatePipeAndRecipe(pipeItemsAdvancedWoodId, PipeItemsAdvancedWood.class,
				new Object[]{" r ", "WgW", 'r', Item.redstone, 'W', Block.planks, 'g', Block.glass});

		// Advanced Insertion Pipe
		pipeItemsAdvancedInsertion = doCreatePipeAndRecipe(pipeItemsAdvancedInsertionId, PipeItemsAdvancedInsertion.class,
				new Object[]{" r ", "OgO", 'r', Item.redstone, 'O', Block.obsidian, 'g', Block.glass});

		// Redstone Pipe
		pipeItemsRedStone = doCreatePipeAndRecipe(pipeItemsRedStoneId, PipeItemsRedstone.class,
				new Object[]{"RgR", 'R', Item.redstone, 'g', Block.glass});

		// Redstone Liquid Pipe
		pipeLiquidsRedstone = doCreatePipeAndRecipe(pipeLiquidsRedstoneId, PipeLiquidsRedstone.class,
				new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemsRedStone});

		// Closed Items Pipe
		pipeItemsClosed = doCreatePipeAndRecipe(pipeItemsClosedId, PipeItemsClosed.class,
				new Object[]{"r", "I", 'I', BuildCraftTransport.pipeItemsIron, 'r', Item.redstone});

		//power switch pipe
		pipePowerSwitch = doCreatePipeAndRecipe(pipePowerSwitchId, PipePowerSwitch.class,
				new Object[]{"r", "I", 'I', pipeItemsRedStone, 'r', Item.redstone});
	}

	private Item doCreatePipeAndRecipe(int id, Class<? extends Pipe> clas, Object[] recipe) {
		if(id == 0) return null;
		Item pipe = createPipe(id > 0 ? id : -id, clas);
		GameRegistry.addRecipe(new ItemStack(pipe), recipe);
		return pipe;
	}

	private static Item createPipe(int id, Class<? extends Pipe> clas) {
		Item res = BlockGenericPipe.registerPipe(id, clas);
		res.setItemName(clas.getSimpleName());
		proxy.registerPipeRendering(res);
		return res;
	}

	//special pipe code
	private static class ItemPipeAP extends ItemPipe {
		protected ItemPipeAP(int i) {
			super(i);
		}
		@Override
		public EnumRarity getRarity(ItemStack stack){
			return EnumRarity.rare;
		}
	}

	private Item createPipeSpecial(int id, Class<? extends Pipe> clas) {
		ItemPipe item = new ItemPipeAP(id);
		item.setItemName(clas.getSimpleName());
		proxy.registerPipeRendering(item);

		BlockGenericPipe.pipes.put(item.shiftedIndex, clas);

		try {
			Pipe dummyPipe = clas.getConstructor(int.class).newInstance(id);
			if (dummyPipe != null){
				item.setTextureFile(dummyPipe.getTextureFile());
				item.setTextureIndex(dummyPipe.getTextureIndexForItem());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error during special pipe creation.", e);
			item = (ItemPipe) createPipe(id, clas);
		}

		return item;
	}

	//legacy method
	public static boolean isPipe(Item item) {
		if (item != null && BlockGenericPipe.pipes.containsKey(item.shiftedIndex)) {
			return true;
		}
		return false;
	}
}
