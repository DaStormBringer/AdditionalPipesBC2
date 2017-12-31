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
import buildcraft.additionalpipes.sound.APSounds;
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

@Mod(modid = AdditionalPipes.MODID, name = AdditionalPipes.NAME, dependencies = "after:BuildCraft|Transport[7.99,);after:BuildCraft|Silicon;after:BuildCraft|Transport;after:BuildCraft|Factory", version = AdditionalPipes.VERSION)
public class AdditionalPipes {
	public static final String MODID = "additionalpipes";
	public static final String NAME = "Additional Pipes";
	public static final String VERSION = "6.0.0";

	@Instance(MODID)
	public static AdditionalPipes instance;

	@SidedProxy(clientSide = "buildcraft.additionalpipes.MultiPlayerProxyClient", serverSide = "buildcraft.additionalpipes.MultiPlayerProxy")
	public static MultiPlayerProxy proxy;

	public File configFile;
	
	// chunk load boundaries
	//public ChunkLoadViewDataProxy chunkLoadViewer;
	
	public CreativeTabBC creativeTab;
	


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
		APPipeDefintions.setFluidCapacities();
		
		Log.info("Registering sounds");
		APSounds.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	
		
		if(APConfiguration.enableChunkloader)
		{
			Log.info("Registering chunk load handler");
			ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
			//chunkLoadViewer = new ChunkLoadViewDataProxy(APConfiguration.chunkSightRange);
			//MinecraftForge.EVENT_BUS.register(chunkLoadViewer);
			
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
		creativeTab.setItem(new ItemStack(APPipeDefintions.itemsTeleportPipeItem));

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
		
	}

	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandAdditionalPipes());
		TeleportManager.instance.reset();
	}

	
	private void loadPipes() {
		/*
		// obsidian fluid pipe
		//set fluid capacity
		PipeTransportFluids.fluidCapacities.put(PipeLiquidsObsidian.class, 100);
		pipeLiquidsObsidian = PipeCreator.createPipeAndRecipe(1, PipeLiquidsObsidian.class, true, BuildCraftTransport.pipeItemsObsidian, BuildCraftTransport.pipeWaterproof);
		*/
	}


}
