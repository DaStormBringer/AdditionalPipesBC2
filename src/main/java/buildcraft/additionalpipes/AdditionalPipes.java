package buildcraft.additionalpipes;

import java.io.File;

import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.chunkloader.BlockTeleportTether;
import buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import buildcraft.additionalpipes.chunkloader.TileTeleportTether;
import buildcraft.additionalpipes.gates.GateProvider;
import buildcraft.additionalpipes.gates.TriggerPipeClosed;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.item.ItemDogDeaggravator;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.test.TeleportManagerTest;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.lib.registry.CreativeTabManager;
import buildcraft.lib.registry.CreativeTabManager.CreativeTabBC;
import buildcraft.silicon.BCSiliconItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

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
	
	// chunk load boundaries
	public ChunkLoadViewDataProxy chunkLoadViewer;
	
	public CreativeTabBC creativeTab;


	// teleport scanner TODO
	// public Item teleportScanner;


	
	
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
	
	// Switch pipes
	public Item pipePowerSwitch;
	public Item pipeItemsSwitch;
	public Item pipeLiquidsSwitch;
	// water pump pipe
	public Item pipeLiquidsWaterPump;
	// obsidian fluid pipe
	public Item pipeLiquidsObsidian;
	
	// chunk loader
	public Block blockTeleportTether;
	
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
		creativeTab = CreativeTabManager.createTab("apcreativetab");
				
		Log.info("Registering pipes");
		APPipeDefintions.createPipes();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	
		
		if(APConfiguration.enableChunkloader)
		{
			Log.info("Registering chunk load handler");
			ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
			chunkLoadViewer = new ChunkLoadViewDataProxy(APConfiguration.chunkSightRange);
			MinecraftForge.EVENT_BUS.register(chunkLoadViewer);
			
			// register Teleport Tether block
			blockTeleportTether = new BlockTeleportTether();
			blockTeleportTether.setRegistryName("teleportTether");
			
			GameRegistry.register(blockTeleportTether);
			GameRegistry.registerTileEntity(TileTeleportTether.class, "TeleportTether");
			GameRegistry.addRecipe(new ShapedOreRecipe(blockTeleportTether, "iii", "iLi", "ici", 'i', "ingotIron", 'L', "gemLapis", 'c', BCSiliconItems.redstoneChipset));
			
			// the lasers key function depends on the chunk loading code, so it can only be enabled if the chunk loader is
			proxy.registerKeyHandler();

		}
		APConfiguration.loadConfigs(true, configFile);

		
		//set creative tab icon
		creativeTab.setItem(new ItemStack(pipeItemsTeleport));

		triggerPipeClosed = new TriggerPipeClosed();
		StatementManager.registerTriggerProvider(new GateProvider());

		dogDeaggravator = new ItemDogDeaggravator();
		GameRegistry.register(dogDeaggravator);
		GameRegistry.addRecipe(new ShapedOreRecipe(dogDeaggravator, "gsg", "gig", "g g", 'i', "ingotIron", 'g', "ingotGold", 's', "stickWood"));	
		
		Log.info("Running Teleport Manager Tests");
		TeleportManagerTest.runAllTests();
		
		//set the reference in the API
		TeleportManagerBase.INSTANCE = TeleportManager.instance;
		
		Log.info("Setting up renderings...");
		proxy.registerRendering();

	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// For Logistics Pipes compatibility
		
		
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandAdditionalPipes());
		TeleportManager.instance.reset();
	}

	
	private void loadPipes() {
		/*
		
		// Item Teleport Pipe
		pipeItemsTeleport = PipeCreator.createPipeTooltip((Class<? extends APPipe>) PipeItemsTeleport.class, "tip.teleportPipe");
		
		Set<StackDefinition> tpRecipeIngredients = new HashSet<StackDefinition>();
		tpRecipeIngredients.add(ArrayStackFilter.definition(new ItemStack(BCSiliconItems.redstoneChipset, 1, 4)));
		tpRecipeIngredients.add(ArrayStackFilter.definition(8, new ItemStack(BCTransportItems.pipeItemDiamond)));
		tpRecipeIngredients.add(ArrayStackFilter.definition(new ItemStack(BCSiliconItems.redstoneChipset, 1, 3)));
		
		AssemblyRecipeRegistry.INSTANCE.addRecipe(new AssemblyRecipe("teleportPipe", 10000, tpRecipeIngredients, new ItemStack(pipeItemsTeleport, 8)));


		// Liquid Teleport Pipe
		pipeLiquidsTeleport = PipeCreator.createPipeTooltip((Class<? extends APPipe>>) PipeLiquidsTeleport.class, "tip.teleportPipe");
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
		pipeItemsJeweled = PipeCreator.createPipeAndRecipe(2, PipeItemsJeweled.class, false, " D ", "DGD", " D ", 'D', BCTransportItems.pipeItemDiamond, 'G', "gearGold");
		
		// Distributor Pipe
		pipeItemsDistributor = PipeCreator.createPipeAndRecipe(1, PipeItemsDistributor.class, false, " r ", "IgI", 'r', "dustRedstone", 'I', "ingotIron", 'g', "blockGlass");
		
		pipeItemsPriority = PipeCreator.createPipeAndRecipe(2, PipeItemsPriorityInsertion.class, true, pipeItemsDistributor, pipeItemsAdvancedInsertion);
		
		// Gravity Feed Pipe
		pipeItemsGravityFeed = PipeCreator.createPipeAndRecipe(1, PipeItemsGravityFeed.class, false, "   ", "IgI", " I ", 'S', "stone", 'I', "ingotIron", 'g', "blockGlass");
		
	
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
		*/
	}


}
