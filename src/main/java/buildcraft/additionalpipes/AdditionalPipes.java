package buildcraft.additionalpipes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import buildcraft.BuildCraftSilicon;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.additionalpipes.gates.GateProvider;
import buildcraft.additionalpipes.gates.TriggerPipeClosed;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.item.ItemDogDeaggravator;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.pipes.APPipe;
import buildcraft.additionalpipes.pipes.PipeItemsAddition;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsGravityFeed;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsObsidian;
import buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import buildcraft.additionalpipes.pipes.PipeLiquidsWaterPump;
import buildcraft.additionalpipes.pipes.PipePowerTeleport;
import buildcraft.additionalpipes.pipes.PipeSwitchFluids;
import buildcraft.additionalpipes.pipes.PipeSwitchItems;
import buildcraft.additionalpipes.pipes.PipeSwitchPower;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.test.TeleportManagerTest;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.additionalpipes.utils.PipeCreator;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.core.BCCreativeTab;
import buildcraft.core.recipes.AssemblyRecipeManager;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipeFluidsGold;
import buildcraft.transport.pipes.PipeFluidsIron;
import buildcraft.transport.pipes.PipePowerGold;
import buildcraft.transport.pipes.PipePowerIron;

@Mod(modid = AdditionalPipes.MODID, name = AdditionalPipes.NAME, dependencies = "after:BuildCraft|Transport[7.1.5,);after:BuildCraft|Silicon;after:BuildCraft|Transport;after:BuildCraft|Factory", version = AdditionalPipes.VERSION)
public class AdditionalPipes {
	public static final String MODID = "additionalpipes";
	public static final String NAME = "Additional Pipes";
	public static final String VERSION = "5.0.1";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MultiPlayerProxyClient", serverSide = "buildcraft.additionalpipes.MultiPlayerProxy")
	public static MultiPlayerProxy proxy;

	public File configFile;
	
	public boolean logisticsPipesInstalled;

	// chunk load boundaries
	public ChunkLoadViewDataProxy chunkLoadViewer;
	
	public BCCreativeTab creativeTab;


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
	// Gravity Feed
	public Item pipeItemsGravityFeed;
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
	// Logistics Teleport
	public Item pipeLogisticsTeleport;
	// Items Closed
	public Item pipeItemsClosed;
	// Switch pipes
	public Item pipePowerSwitch;
	public Item pipeItemsSwitch;
	public Item pipeLiquidsSwitch;
	// water pump pipe
	public Item pipeLiquidsWaterPump;
	// obsidian fluid pipe
	public Item pipeLiquidsObsidian;
	// chunk loader
	public Block blockChunkLoader;
	
	//dog deaggravator
	public Item dogDeaggravator;
	
	public ITriggerInternal triggerPipeClosed;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		
		PacketHandler.init();

		configFile = event.getSuggestedConfigurationFile();
		APConfiguration.loadConfigs(false, configFile);
		MinecraftForge.EVENT_BUS.register(this);
		
		//create BuildCraft creative tab
		creativeTab = new BCCreativeTab("apcreativetab");
		
		
		Log.info("Registering pipes");
		loadPipes();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		
		logisticsPipesInstalled = Loader.isModLoaded("LogisticsPipes");
		//EntityList.addMapping(EntityBetterCat.class, "betterCat", 79, 0xEDCE21, 0x564434);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	
		if(APConfiguration.enableChunkloader)
		{
			Log.info("Registering chunk load handler");
			ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
			chunkLoadViewer = new ChunkLoadViewDataProxy(APConfiguration.chunkSightRange);
			MinecraftForge.EVENT_BUS.register(chunkLoadViewer);
			
			// register Teleport Tether block
			blockChunkLoader = new BlockChunkLoader();
			blockChunkLoader.setUnlocalizedName("teleportTether");
			GameRegistry.registerBlock(blockChunkLoader, ItemBlock.class, "chunkLoader");
			GameRegistry.registerTileEntity(TileChunkLoader.class, "TeleportTether");
			GameRegistry.addRecipe(new ShapedOreRecipe(blockChunkLoader, "iii", "iLi", "ici", 'i', "ingotIron", 'L', "gemLapis", 'c', BuildCraftSilicon.redstoneChipset));
			
			// the lasers key function depends on the chunk loading code, so it can only be enabled if the chunk loader is
			proxy.registerKeyHandler();

		}
		
		proxy.registerRendering();

		APConfiguration.loadConfigs(true, configFile);

		
		//set creative tab icon
		creativeTab.setIcon(new ItemStack(pipeItemsTeleport));

		triggerPipeClosed = new TriggerPipeClosed();
		StatementManager.registerTriggerProvider(new GateProvider());

		if(APConfiguration.allowWRRemove)
		{
			// Additional Pipes
			GameRegistry.addShapelessRecipe(new ItemStack(pipeItemsTeleport), new Object[] {pipePowerTeleport});
			GameRegistry.addShapelessRecipe(new ItemStack(pipeItemsTeleport), new Object[] {pipeLiquidsTeleport});
			
			if(logisticsPipesInstalled)
			{
				//GameRegistry.addShapelessRecipe(new ItemStack(pipeItemsTeleport), new Object[] {pipeLogisticsTeleport});
			}
			
			GameRegistry.addShapelessRecipe(new ItemStack(pipeItemsSwitch), new Object[] {pipeLiquidsSwitch});
			GameRegistry.addShapelessRecipe(new ItemStack(pipeItemsSwitch), new Object[] {pipePowerSwitch});
			
			//it looks like Buildcraft might be adding these itself.  I need to look into removing this. -JS
			
			// BC Liquid
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone), new Object[] { BuildCraftTransport.pipeFluidsCobblestone });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[] { BuildCraftTransport.pipeFluidsGold });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron), new Object[] { BuildCraftTransport.pipeFluidsIron });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[] { BuildCraftTransport.pipeFluidsStone });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[] { BuildCraftTransport.pipeFluidsWood });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsDiamond), new Object[] { BuildCraftTransport.pipeFluidsDiamond });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsEmerald), new Object[] { BuildCraftTransport.pipeFluidsEmerald });
			
			// BC Power
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold), new Object[] { BuildCraftTransport.pipePowerGold });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone), new Object[] { BuildCraftTransport.pipePowerStone });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood), new Object[] { BuildCraftTransport.pipePowerWood });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone), new Object[] { BuildCraftTransport.pipePowerCobblestone });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsEmerald), new Object[] { BuildCraftTransport.pipePowerEmerald });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsDiamond), new Object[] { BuildCraftTransport.pipePowerDiamond });
			GameRegistry.addShapelessRecipe(new ItemStack(BuildCraftTransport.pipeItemsQuartz), new Object[] { BuildCraftTransport.pipePowerQuartz });

		}

		dogDeaggravator = new ItemDogDeaggravator();
		GameRegistry.registerItem(dogDeaggravator, ItemDogDeaggravator.NAME);
		GameRegistry.addRecipe(new ShapedOreRecipe(dogDeaggravator, "gsg", "gig", "g g", 'i', "ingotIron", 'g', "ingotGold", 's', "stickWood"));
		
	     //register renders
	     if(event.getSide() == Side.CLIENT)
	     {
		     RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		    
		     //blocks
		     renderItem.getItemModelMesher().register(Item.getItemFromBlock(blockChunkLoader), 0, new ModelResourceLocation(MODID + ":" + blockChunkLoader.getUnlocalizedName(), "inventory"));
		     renderItem.getItemModelMesher().register(dogDeaggravator, 0, new ModelResourceLocation(MODID + ":" + ItemDogDeaggravator.NAME, "inventory"));
	     
	     }		
		
		Log.info("Running Teleport Manager Tests");
		TeleportManagerTest.runAllTests();
		
		//set the reference in the API
		TeleportManagerBase.INSTANCE = TeleportManager.instance;
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// For Logistics Pipes compatibility
		
		if(logisticsPipesInstalled)
		{
			//would like to do this, but it causes a NoClassDefFoundError if LP is not installed
			//SimpleServiceLocator.specialpipeconnection.registerHandler(new APSpecialPipedConnection());

			Log.info("Commencing morass of reflection to try and integrate with Logistics Pipes");
			boolean success = false;
			try
			{
				//all of this to call ONE function
				//in a class which may or may not be present
				Class<?> SimpleServiceLocator = Class.forName("logisticspipes.proxy.SimpleServiceLocator");
				Class<?> SpecialPipeConnection = Class.forName("logisticspipes.proxy.specialconnection.SpecialPipeConnection");
				Class<?> ISpecialPipedConnection = Class.forName("logisticspipes.interfaces.routing.ISpecialPipedConnection");
				Class<?> APSpecialPipedConnection = Class.forName("buildcraft.additionalpipes.pipes.APSpecialPipedConnection");
				
				Field specialpipeconnectionField = SimpleServiceLocator.getDeclaredField("specialpipeconnection");
				Object specialpipeconnection = specialpipeconnectionField.get(null);
				Method registerHandler = SpecialPipeConnection.getDeclaredMethod("registerHandler", ISpecialPipedConnection);
				Object apSpecialPC = APSpecialPipedConnection.newInstance();
				registerHandler.invoke(specialpipeconnection, apSpecialPC);
				
				success = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			Log.info("Integration " + (success ? "succeeded" : "failed"));
		}
		else
		{
			Log.info("Logistics Pipes not detected");
		}
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandAdditionalPipes());
		TeleportManager.instance.reset();
	}

	
	private void loadPipes() {
		// Item Teleport Pipe
		pipeItemsTeleport = PipeCreator.createPipeTooltip((Class<? extends APPipe<?>>) PipeItemsTeleport.class, "tip.teleportPipe");
		
		AssemblyRecipeManager.INSTANCE.addRecipe("teleportPipe", 10000, new ItemStack(pipeItemsTeleport, 8), new Object[] { new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 4), new ItemStack(BuildCraftTransport.pipeItemsDiamond, 8),
				new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 3) });


		// Liquid Teleport Pipe
		pipeLiquidsTeleport = PipeCreator.createPipeTooltip((Class<? extends APPipe<?>>) PipeLiquidsTeleport.class, "tip.teleportPipe");
		if(pipeItemsTeleport != null) {
			GameRegistry.addShapelessRecipe(new ItemStack(pipeLiquidsTeleport), new Object[] {BuildCraftTransport.pipeWaterproof, pipeItemsTeleport});
		}

		// Power Teleport Pipe
		
		pipePowerTeleport = PipeCreator.createPipeTooltip((Class<? extends APPipe<?>>) PipePowerTeleport.class, "tip.teleportPipe");
		if(pipeItemsTeleport != null) {
			GameRegistry.addShapelessRecipe(new ItemStack(pipePowerTeleport), new Object[] {Items.redstone, pipeItemsTeleport});
		}
		
//		if(logisticsPipesInstalled)
//		{
//			// Logistics Teleport Pipe
//			pipeLogisticsTeleport = PipeCreator.createPipeTooltip((Class<? extends APPipe<?>>) PipeLogisticsTeleport.class, "tip.teleportLogisticsPipe");
//			if(pipeItemsTeleport != null) {
//				GameRegistry.addShapelessRecipe(new ItemStack(pipeLogisticsTeleport), new Object[] {pipeItemsTeleport, BuildCraftSilicon.redstoneChipset});
//			}
//		}

		//Jeweled Pipe
		pipeItemsJeweled = PipeCreator.createPipeAndRecipe(2, PipeItemsJeweled.class, false, " D ", "DGD", " D ", 'D', BuildCraftTransport.pipeItemsDiamond, 'G', "gearGold");
		
		// Distributor Pipe
		pipeItemsDistributor = PipeCreator.createPipeAndRecipe(1, PipeItemsDistributor.class, false, " r ", "IgI", 'r', "dustRedstone", 'I', "ingotIron", 'g', "blockGlass");

		// Advanced Insertion Pipe
		pipeItemsAdvancedInsertion = PipeCreator.createPipeAndRecipe(8, PipeItemsAdvancedInsertion.class, false, "IgI", 'I', "gearIron", 'g', "blockGlass");
		
		// Advanced Insertion Pipe
		pipeItemsAddition = PipeCreator.createPipeAndRecipe(1, PipeItemsAddition.class, false, " R ", "RIR", " R ", 'I', pipeItemsAdvancedInsertion, 'R', "dustRedstone");
		
		pipeItemsPriority = PipeCreator.createPipeAndRecipe(2, PipeItemsPriorityInsertion.class, true, pipeItemsDistributor, pipeItemsAdvancedInsertion);
		
		// Advanced Wooden Pipe
		pipeItemsAdvancedWood = PipeCreator.createPipeAndRecipe(8, PipeItemsAdvancedWood.class, false, "WgW", 'W', "gearWood", 'g', "blockGlass");

		// Gravity Feed Pipe
		pipeItemsGravityFeed = PipeCreator.createPipeAndRecipe(1, PipeItemsGravityFeed.class, false, "   ", "IgI", " I ", 'S', "stone", 'I', "ingotIron", 'g', "blockGlass");
		
		// Closed Items Pipe
		pipeItemsClosed = PipeCreator.createPipeAndRecipe(1, PipeItemsClosed.class, true, BuildCraftTransport.pipeItemsVoid, "gearIron");
		
		// switch pipes
		pipeItemsSwitch = PipeCreator.createPipeAndRecipe(8, PipeSwitchItems.class, false, "GgI", 'g', "blockGlass", 'G', "gearGold", 'I', "gearIron");
		
		//set power capacity to the average between iron and gold
		int switchPowerCapacity = (PipeTransportPower.powerCapacities.get(PipePowerGold.class) + PipeTransportPower.powerCapacities.get(PipePowerIron.class))/ 2;
		
		PipeTransportPower.powerCapacities.put(PipeSwitchPower.class, switchPowerCapacity);
		pipePowerSwitch = PipeCreator.createPipeAndRecipe(1, PipeSwitchPower.class, true, pipeItemsSwitch, "dustRedstone");
		
		//set fluid capacity to the average between iron and gold
		int switchFluidCapacity = (PipeTransportFluids.fluidCapacities.get(PipeFluidsGold.class) + PipeTransportFluids.fluidCapacities.get(PipeFluidsIron.class))/ 2;
		
		PipeTransportFluids.fluidCapacities.put(PipeSwitchFluids.class, switchFluidCapacity);
		pipeLiquidsSwitch = PipeCreator.createPipeAndRecipe(1, PipeSwitchFluids.class, true, pipeItemsSwitch, BuildCraftTransport.pipeWaterproof);

		// water pump pipe
		//set fluid capacity
		PipeTransportFluids.fluidCapacities.put(PipeLiquidsWaterPump.class, APConfiguration.waterPumpWaterPerTick);
		pipeLiquidsWaterPump = PipeCreator.createPipeAndRecipe(1, PipeLiquidsWaterPump.class, false, " L ", "rPr", " W ", 'r', "dustRedstone", 'P', "gearIron", 'L',
				BuildCraftTransport.pipeFluidsGold, 'w', BuildCraftTransport.pipeWaterproof, 'W', BuildCraftTransport.pipeFluidsWood);
		
		// obsidian fluid pipe
		//set fluid capacity
		PipeTransportFluids.fluidCapacities.put(PipeLiquidsObsidian.class, 100);
		pipeLiquidsObsidian = PipeCreator.createPipeAndRecipe(1, PipeLiquidsObsidian.class, true, BuildCraftTransport.pipeItemsObsidian, BuildCraftTransport.pipeWaterproof);
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
		Textures.registerIcons(event.map);
	}
}
