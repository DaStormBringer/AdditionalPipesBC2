package buildcraft.additionalpipes;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.pipes.PipeItemTeleport;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsRedstone;
import buildcraft.additionalpipes.pipes.PipeLiquidsRedstone;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.core.Box;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.TransportProxyClient;

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
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import buildcraft.api.core.LaserKind;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import buildcraft.additionalpipes.network.NetworkHandler;

@Mod(modid = mod_AdditionalPipes.MODID, dependencies = "",
version = mod_AdditionalPipes.FULL_VERSION)
@NetworkMod(channels = { mod_AdditionalPipes.CHANNEL },
clientSideRequired = true, packetHandler = NetworkHandler.class)
public class mod_AdditionalPipes {
	public static final String MODID = "AdditionalPipes";
	public static final String VERSION = "2.1.3";
	public static final String FULL_VERSION =
			VERSION + "(Minecraft 1.4.2, Buildcraft 3.2.0pre9, Forge 6.0.1.337)";
	public static final String CHANNEL = "AdditionalPipes";

	@Instance(MODID)
	public static mod_AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MutiPlayerProxyClient",
			serverSide = "buildcraft.additionalpipes.MutiPlayerProxy")
	public static MutiPlayerProxy proxy;
	
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface ConfigId {
		public boolean block() default false;
	}
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface ConfigBool {}

	public ChunkLoadViewer chunkLoadViewer = new ChunkLoadViewer();

	public static final String PATH = "/buildcraft/additionalpipes/";
	public static String MASTER_TEXTURE_FILE = PATH + "gui/block_textures.png";

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
	public static Item pipeItemTeleport;
	public static @ConfigId(block=true) int itemTeleportId = 4047;
	public static String DEFUALT_ITEM_TELEPORT_TEXTURE_FILE = PATH + "BlueItem.png";
	//Liquid Teleport
	public static Item pipeLiquidTeleport;
	public static @ConfigId(block=true) int liquidTeleportId = 4048;
	public static String DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE = PATH + "gui/BlueLiquid.png";
	//Power Teleport
	public static Item pipePowerTeleport;
	public static @ConfigId(block=true) int powerTeleportId = 4049;
	public static String DEFUALT_POWER_TELEPORT_TEXTURE_FILE = PATH + "gui/BluePower.png";
	//Distributor
	public static Item pipeDistributor;
	public static @ConfigId(block=true) int distributorTeleportId = 4046;
	public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE = PATH + "gui/Dist";
	public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE = PATH + "gui/DistributionOpen.png";
	public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE_CLOSED = PATH + "gui/DistributionClosed.png";
	//Advanced Wood
	public static Item pipeAdvancedWood;
	public static @ConfigId(block=true) int advancedWoodId = 4045;
	public static String DEFUALT_ADVANCEDWOOD_FILE = PATH + "gui/AdvancedWood.png";
	public static String DEFUALT_ADVANCEDWOOD_FILE_CLOSED = PATH + "gui/AdvancedWoodClosed.png";
	//Advanced Insertion
	public static Item pipeAdvancedInsertion;
	public static @ConfigId(block=true) int insertionId = 4044;
	public static String DEFUALT_Insertion_FILE = PATH + "gui/AdvInsert.png";
	//Redstone
	public static Item pipeRedStone;
	public static @ConfigId(block=true) int redstoneId = 4043;
	public static String DEFUALT_RedStone_FILE = PATH + "gui/RS.png";
	public static String DEFUALT_RedStone_FILE_POWERED = PATH + "gui/RSP.png";
	//Redstone Liquid
	public static Item pipeRedStoneLiquid;
	public static @ConfigId(block=true) int redstoneLiquidId = 4042;
	public static String DEFUALT_RedStoneLiquid_FILE = PATH + "gui/RSL.png";
	public static String DEFUALT_RedStoneLiquid_FILE_POWERED = PATH + "gui/RSLP.png";
	//chunk loader
	public static Block blockChunkLoader;
	public static @ConfigId(block=true) int chunkLoaderId = 189;

	// Config Setup
	private static Configuration config;
	public KeyBinding showLaser = new KeyBinding("laserKeyBinding", 64);
	public static boolean isInGame = false;
	public static @ConfigBool boolean lagFix = false;
	public static @ConfigBool boolean wrenchOpensGui = false;
	public static @ConfigBool boolean allowWPRemove = false; //Remove waterproofing/redstone
	public static double powerLossCfg = .95;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(
				event.getSuggestedConfigurationFile());
		try {
			config.load();
			Field[] fields = mod_AdditionalPipes.class.getFields();
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
					"powerLoss", (int) (powerLossCfg * 1000));
			powerLoss.comment = "Percentage of power a power teleport pipe transmits. Between 0 and 100.";
			powerLossCfg = powerLoss.getInt() / 100.0;
			if(powerLossCfg > 1.00) {
				powerLossCfg = 0.99;
			} else if(powerLossCfg < 0.0) {
				powerLossCfg = 0.0;
			}
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "Failed to load Additiona Pipes configs.");
		} finally {
			config.save();
		}
	}

	@Init
	public void init(FMLInitializationEvent event) {
		ModLoader.addLocalization("laserKeyBinding", "Turn on/off chunk loader boundries");
		ForgeChunkManager.setForcedChunkLoadingCallback(this,  new ChunkLoadingHandler());
	}

	@PostInit
	public void modsLoaded(FMLPostInitializationEvent event) {
		CraftingManager craftingmanager = CraftingManager.getInstance();

		// Item Teleport Pipe
		pipeItemTeleport = createPipe(itemTeleportId, PipeItemTeleport.class, "Item Teleport Pipe");
		if (loadItemTeleport) {
			craftingmanager.addRecipe(new ItemStack(pipeItemTeleport, 4), new Object[]{"dgd", 'd', BuildCraftCore.diamondGearItem, 'g', Block.glass});
		}

		// Liquid Teleport Pipe
		pipeLiquidTeleport = createPipe(liquidTeleportId, PipeLiquidsTeleport.class, "Waterproof Teleport Pipe");
		if (loadLiquidsTeleport) {
			craftingmanager.addRecipe(new ItemStack(pipeLiquidTeleport), new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeItemTeleport});
		}

		// Power Teleport Pipe
		pipePowerTeleport = createPipe(powerTeleportId, PipePowerTeleport.class, "Power Teleport Pipe");
		if (loadPowerTeleport) {
			craftingmanager.addRecipe(new ItemStack(pipePowerTeleport), new Object[]{"r", "P", 'r', Item.redstone, 'P', pipeItemTeleport});
		}

		// Distributor Pipe
		pipeDistributor = createPipe(distributorTeleportId, PipeItemsDistributor.class, "Distribution Transport Pipe");
		if (loadItemsDistributor) {
			craftingmanager.addRecipe(new ItemStack(pipeDistributor, 8), new Object[]{" r ", "IgI", 'r', Item.redstone, 'I', Item.ingotIron, 'g', Block.glass});
		}

		// Advanced Wooded Pipe
		pipeAdvancedWood = createPipe(advancedWoodId, PipeItemsAdvancedWood.class, "Advanced Wooden Transport Pipe");
		if (loadItemsAdvancedWood) {
			craftingmanager.addRecipe(new ItemStack(pipeAdvancedWood, 8), new Object[]{" r ", "WgW", 'r', Item.redstone, 'W', Block.planks, 'g', Block.glass});
		}

		// Advanced Insertion Pipe
		pipeAdvancedInsertion = createPipe(insertionId, PipeItemsAdvancedInsertion.class, "Advanced Insertion Pipe");
		if (loadItemsAdvancedInsertion) {
			craftingmanager.addRecipe(new ItemStack(pipeAdvancedInsertion, 8), new Object[]{" r ", "SgS", 'r', Item.redstone, 'S', Block.stone, 'g', Block.glass});
		}

		// Redstone Pipe
		pipeRedStone = createPipe(redstoneId, PipeItemsRedstone.class, "Redstone Transport Pipe");
		if (loadItemsRedstone) {
			craftingmanager.addRecipe(new ItemStack(pipeRedStone, 8), new Object[]{"RgR", 'R', Item.redstone, 'g', Block.glass});
		}

		// Redstone Liquid Pipe
		pipeRedStoneLiquid = createPipe(redstoneLiquidId, PipeLiquidsRedstone.class, "Waterproof Redstone Pipe");
		if (loadLiquidsRedstone) {
			craftingmanager.addRecipe(new ItemStack(pipeRedStoneLiquid), new Object[]{"w", "P", 'w', BuildCraftTransport.pipeWaterproof, 'P', pipeRedStone});
		}

		if (allowWPRemove) {
			//Additional Pipes
			craftingmanager.addRecipe(new ItemStack(pipeItemTeleport), new Object[]{"A", 'A', pipePowerTeleport});
			craftingmanager.addRecipe(new ItemStack(pipeItemTeleport), new Object[]{"A", 'A', pipeLiquidTeleport});
			craftingmanager.addRecipe(new ItemStack(pipeRedStone), new Object[]{"A", 'A', pipeRedStoneLiquid});
			//BC Liquid
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsCobblestone});
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsGold});
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsIron});
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsStone});
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[]{"A", 'A', BuildCraftTransport.pipeLiquidsWood});
			//BC Power
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[]{"A", 'A', BuildCraftTransport.pipePowerGold});
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[]{"A", 'A', BuildCraftTransport.pipePowerStone});
			craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[]{"A", 'A', BuildCraftTransport.pipePowerWood});
		}

		//ChunkLoader
		blockChunkLoader = new BlockChunkLoader(chunkLoaderId, 0);
		blockChunkLoader.setBlockName("Teleport Tether");
		LanguageRegistry.addName(blockChunkLoader, "Teleport Tether");
		GameRegistry.registerBlock(blockChunkLoader);
		GameRegistry.registerTileEntity(TileChunkLoader.class, "Teleport Tether");
		if (loadChunkLoader) {
			craftingmanager.addRecipe(new ItemStack(blockChunkLoader), new Object[]{"iii", "iLi", "iii", Character.valueOf('i'), Item.ingotIron, Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4)});
		}
	}


	private Item createPipe(int id, Class<? extends Pipe> clas, String description) {
		Item res = BlockGenericPipe.registerPipe(id, clas);
		res.setItemName(clas.getSimpleName());
		CoreProxy.proxy.addName(res, description);
		proxy.registerPipeRendering(res);
		return res;
	}

	private static Item createPipe(int id, Class<? extends Pipe> clas) {
		ItemPipe res = BlockGenericPipe.registerPipe(id, clas);
		return res;
	}


	public static boolean isPipe(Item item) {

		if (BlockGenericPipe.pipes.containsKey(item.shiftedIndex)) {
			return true;
		}

		return false;
	}
}
