package buildcraft.additionalpipes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;

import net.minecraft.src.Block;
import net.minecraft.src.EnumRarity;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.Property;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsRedstone;
import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsRedstone;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
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
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid=AdditionalPipes.MODID, name=AdditionalPipes.NAME,
dependencies="required-after:BuildCraft|Transport", version=AdditionalPipes.VERSION)
@NetworkMod(channels={AdditionalPipes.CHANNEL},
clientSideRequired=true, packetHandler=NetworkHandler.class)
public class AdditionalPipes {
	public static final String MODID = "AdditionalPipes";
	public static final String NAME = "Additional Pipes for BuildCraft";
	public static final String VERSION = "2.1.3u3";
	public static final String CHANNEL = MODID;

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MutiPlayerProxyClient",
			serverSide = "buildcraft.additionalpipes.MutiPlayerProxy")
	public static MutiPlayerProxy proxy;

	@Retention(RetentionPolicy.RUNTIME)
	private static @interface ConfigId {
		public boolean block() default false;
	}
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface ConfigBool {}

	public ChunkLoadViewDataProxy chunkLoadViewer = new ChunkLoadViewDataProxy();
	public static @ConfigBool boolean chunkSight = true;
	public static int chunkSightRange = 5;

	public static final String BASE_PATH = "/buildcraft/additionalpipes";
	public static final String TEXTURE_PATH = BASE_PATH + "/sprites";
	public static final String TEXTURE_MASTER = TEXTURE_PATH + "/textures.png";
	public static final String TEXTURE_PIPES = TEXTURE_PATH + "/pipes.png";
	public static final String TEXTURE_BLOCKS = TEXTURE_PATH + "/blocks.png";

	public static final String TEXTURE_GUI_TELEPORT = TEXTURE_PATH + "/blankSmallGui.png";
	public static final String TEXTURE_GUI_ADVANCEDWOOD = TEXTURE_PATH + "/advancedWoodGui.png";
	public static final String TEXTURE_GUI_DISTRIBUTION = TEXTURE_PATH + "/distributionGui.png";

	public static @ConfigBool boolean loadItemsAdvancedInsertion = true,
			loadItemsAdvancedWood = true,
			loadItemsDistributor = true,
			loadItemsRedstone = true,
			loadLiquidsRedstone = true,
			loadItemTeleport = true,
			loadLiquidsTeleport = true,
			loadPowerTeleport = true,
			loadChunkLoader = true;
	//Item Teleport
	public Item pipeItemTeleport;
	public static @ConfigId int itemTeleportId = 4047;
	//Liquid Teleport
	public Item pipeLiquidTeleport;
	public static @ConfigId int liquidTeleportId = 4048;
	//Power Teleport
	public Item pipePowerTeleport;
	public static @ConfigId int powerTeleportId = 4049;
	//Distributor
	public Item pipeDistributor;
	public static @ConfigId int distributorTransportId = 4046;
	//Advanced Wood
	public Item pipeAdvancedWood;
	public static @ConfigId int advancedWoodId = 4045;
	//Advanced Insertion
	public Item pipeAdvancedInsertion;
	public static @ConfigId int insertionId = 4044;
	//Redstone
	public Item pipeRedStone;
	public static @ConfigId int redstoneId = 4043;
	//Redstone Liquid
	public Item pipeRedStoneLiquid;
	public static @ConfigId int redstoneLiquidId = 4042;
	//chunk loader
	public Block blockChunkLoader;
	public static @ConfigId(block=true) int chunkLoaderId = 189;

	//configs
	private Configuration config;
	public static int laserKeyCode = 64; //config option (& in options menu)
	public static KeyBinding laserKey;
	public static @ConfigBool boolean wrenchOpensGui = true;
	public static @ConfigBool boolean allowWRRemove = false;
	public static double powerLossCfg = .95; //config option

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(
				event.getSuggestedConfigurationFile());
		try {
			config.load();
			Field[] fields = AdditionalPipes.class.getFields();
			for(Field field : fields){
				ConfigId annotation = field.getAnnotation(ConfigId.class);
				if(annotation != null){
					int id = field.getInt(null);
					if(annotation.block()){
						id = config.getBlock(field.getName(), id).getInt();
					}else{
						id = config.getItem(field.getName(), id).getInt();
					}
					field.setInt(null, id);
				} else {
					ConfigBool boolAnnotation = field.getAnnotation(ConfigBool.class);
					if(boolAnnotation != null){
						boolean bool = field.getBoolean(null);
						bool = config.get(Configuration.CATEGORY_GENERAL,
								field.getName(), bool).getBoolean(bool);
						field.setBoolean(null, bool);
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
			chunkLoadSightRange.comment = "Range of chunk load lasers.";
			chunkLoadViewer.setSightRange(chunkLoadSightRange.getInt());

			Property laserKey = config.get(Configuration.CATEGORY_GENERAL,
					"laserKeyChar", laserKeyCode);
			laserKey.comment = "Default key to toggle chunk load lasers. (can be overridden in options.txt/ingame GUI)";
			laserKeyCode = laserKey.getInt();
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "Failed to load Additional Pipes configs.");
		} finally {
			config.save();
		}
	}

	@Init
	public void init(FMLInitializationEvent event) {
		laserKey = new KeyBinding("laserKeyBinding", laserKeyCode);
		//ModLoader.addLocalization("laserKeyBinding", "Turn on/off chunk loader boundries");
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		ForgeChunkManager.setForcedChunkLoadingCallback(this,  new ChunkLoadingHandler());
		proxy.registerKeyHandler();
		proxy.registerRendering();

		Properties en_US = null;
		Localization.addLocalization(BASE_PATH + "/lang/", "en_US");
		try {
			en_US = new Properties();
			en_US.load(AdditionalPipes.class.getResourceAsStream((BASE_PATH + "/lang/en_US.properties")));
			LanguageRegistry.instance().addStringLocalization(en_US);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PostInit
	public void modsLoaded(FMLPostInitializationEvent event) {
		// Item Teleport Pipe
		pipeItemTeleport = createPipeSpecial(itemTeleportId, PipeItemsTeleport.class, EnumRarity.rare);
		if (loadItemTeleport) {
			GameRegistry.addRecipe(new ItemStack(pipeItemTeleport, 6), new Object[]{"dgd", 'd', BuildCraftCore.diamondGearItem, 'g', Block.glass});
		}

		// Liquid Teleport Pipe
		pipeLiquidTeleport = createPipeSpecial(liquidTeleportId, PipeLiquidsTeleport.class, EnumRarity.rare);
		if (loadLiquidsTeleport) {
			GameRegistry.addRecipe(new ItemStack(pipeLiquidTeleport), new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemTeleport});
		}

		// Power Teleport Pipe
		pipePowerTeleport = createPipeSpecial(powerTeleportId, PipePowerTeleport.class, EnumRarity.rare);
		if (loadPowerTeleport) {
			GameRegistry.addRecipe(new ItemStack(pipePowerTeleport), new Object[]{"r", "P", 'r', Item.redstone, 'P', pipeItemTeleport});
		}

		// Distributor Pipe
		pipeDistributor = createPipe(distributorTransportId, PipeItemsDistributor.class);
		if (loadItemsDistributor) {
			GameRegistry.addRecipe(new ItemStack(pipeDistributor, 8), new Object[]{" r ", "IgI", 'r', Item.redstone, 'I', Item.ingotIron, 'g', Block.glass});
		}

		// Advanced Wooded Pipe
		pipeAdvancedWood = createPipe(advancedWoodId, PipeItemsAdvancedWood.class);
		if (loadItemsAdvancedWood) {
			GameRegistry.addRecipe(new ItemStack(pipeAdvancedWood, 8), new Object[]{" r ", "WgW", 'r', Item.redstone, 'W', Block.planks, 'g', Block.glass});
		}

		// Advanced Insertion Pipe
		pipeAdvancedInsertion = createPipe(insertionId, PipeItemsAdvancedInsertion.class);
		if (loadItemsAdvancedInsertion) {
			GameRegistry.addRecipe(new ItemStack(pipeAdvancedInsertion, 8), new Object[]{" r ", "OgO", 'r', Item.redstone, 'O', Block.obsidian, 'g', Block.glass});
		}

		// Redstone Pipe
		pipeRedStone = createPipe(redstoneId, PipeItemsRedstone.class);
		if (loadItemsRedstone) {
			GameRegistry.addRecipe(new ItemStack(pipeRedStone, 8), new Object[]{"RgR", 'R', Item.redstone, 'g', Block.glass});
		}

		// Redstone Liquid Pipe
		pipeRedStoneLiquid = createPipe(redstoneLiquidId, PipeLiquidsRedstone.class);
		if (loadLiquidsRedstone) {
			GameRegistry.addRecipe(new ItemStack(pipeRedStoneLiquid), new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeRedStone});
		}

		if (allowWRRemove) {
			//Additional Pipes
			GameRegistry.addRecipe(new ItemStack(pipeItemTeleport), new Object[]{"A", 'A', pipePowerTeleport});
			GameRegistry.addRecipe(new ItemStack(pipeItemTeleport), new Object[]{"A", 'A', pipeLiquidTeleport});
			GameRegistry.addRecipe(new ItemStack(pipeRedStone), new Object[]{"A", 'A', pipeRedStoneLiquid});
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
		blockChunkLoader = new BlockChunkLoader(chunkLoaderId, 0);
		blockChunkLoader.setBlockName("TeleportTether");
		GameRegistry.registerBlock(blockChunkLoader);
		GameRegistry.registerTileEntity(TileChunkLoader.class, "TeleportTether");
		if (loadChunkLoader) {
			GameRegistry.addRecipe(new ItemStack(blockChunkLoader), new Object[]{"iii", "iLi", "iii", 'i', Item.ingotIron, 'L', new ItemStack(Item.dyePowder, 1, 4)});
		}
	}


	private static Item createPipe(int id, Class<? extends Pipe> clas) {
		Item res = BlockGenericPipe.registerPipe(id, clas);
		res.setItemName(clas.getSimpleName());
		proxy.registerPipeRendering(res);
		return res;
	}

	//special pipe code
	private static class ItemPipeAP extends ItemPipe {
		private EnumRarity rarity;
		protected ItemPipeAP(int i) {
			super(i);
			rarity = EnumRarity.common;
		}
		public EnumRarity getRarity(ItemStack stack){
			return rarity;
		}
		public void setRarity(EnumRarity rarity){
			this.rarity = rarity;
		}
	}

	private static Item createPipeSpecial(int id, Class<? extends Pipe> clas, EnumRarity rarity){
		ItemPipeAP item = new ItemPipeAP(id);
		item.setItemName(clas.getSimpleName());
		item.setRarity(rarity);
		proxy.registerPipeRendering(item);

		BlockGenericPipe.pipes.put(item.shiftedIndex, clas);

		try {
			Pipe dummyPipe = clas.getConstructor(int.class).newInstance(id);
			if (dummyPipe != null){
				item.setTextureFile(dummyPipe.getTextureFile());
				item.setTextureIndex(dummyPipe.getTextureIndexForItem());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}
	//special pipe code

	public static boolean isPipe(Item item) {
		if (item != null && BlockGenericPipe.pipes.containsKey(item.shiftedIndex)) {
			return true;
		}
		return false;
	}
}
