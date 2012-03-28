package net.minecraft.src;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.src.buildcraft.api.APIProxy;
import net.minecraft.src.buildcraft.api.LaserKind;
import net.minecraft.src.buildcraft.core.Box;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.transport.BlockGenericPipe;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.ChunkLoader.BlockChunkLoader;
import net.minecraft.src.buildcraft.zeldo.ChunkLoader.TileChunkLoader;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;
import net.minecraft.src.buildcraft.zeldo.gui.*;
import net.minecraft.src.buildcraft.zeldo.logic.PipeLogicAdvancedWood;
import net.minecraft.src.buildcraft.zeldo.pipes.*;
import net.minecraft.src.forge.*;

public class mod_zAdditionalPipes extends BaseModMp {
    
    public static mod_zAdditionalPipes instance;
    
    /*
    * ChuckLoader Handler
    */
    private class ChunkLoadingHandler implements IChunkLoadHandler {

        @Override
        public void addActiveChunks(World world, Set<ChunkCoordIntPair> chunkList) {
            
            for (TileChunkLoader tile : TileChunkLoader.chunkLoaderList) {
                
                List<ChunkCoordIntPair> loadArea = tile.getLoadArea();
                for (ChunkCoordIntPair chunkCoords : loadArea) {

                    if (!chunkList.contains(chunkCoords)) {
                        chunkList.add(chunkCoords);
                        log("Adding chunk: " + chunkCoords, LOG_INFO);
                    }
                    else {
                        log(chunkCoords + " already there.", LOG_INFO);
                    }
                }
            }
        }

        @Override
        public boolean canUnloadChunk(Chunk chunk) {

            for (TileChunkLoader tile : TileChunkLoader.chunkLoaderList) {

                List<ChunkCoordIntPair> loadArea = tile.getLoadArea();
                for (ChunkCoordIntPair chunkCoords : loadArea) {
                    
                    if (chunk.worldObj.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPos).equals(chunk)) {
                        log("Keeping chunk: " + chunk.getChunkCoordIntPair(), LOG_INFO);
                        return false;
                    }
                }
            }
            
            log("Unloading chunk: " + chunk.getChunkCoordIntPair(), LOG_INFO);
            return true;
        }
    }

    @Override
    public String getVersion() {
        return "2.1.0 (Minecraft 1.2.4, Buildcraft 2.2.14, Forge 2.0.0.67)";
    }

    public static int MASTER_TEXTURE_OFFSET = 0;// 8 * 16;
    public static String MASTER_TEXTURE_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/block_textures.png";

    //Item Teleport
    public static Item pipeItemTeleport;
    public static int DEFUALT_ITEM_TELEPORT_ID = 4047;
    public static int DEFUALT_ITEM_TELEPORT_TEXTURE = 0;//8 * 16 + 0;
    public static String DEFUALT_ITEM_TELEPORT_TEXTURE_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/BlueItem.png";

    //Liquid Teleport
    public static Item pipeLiquidTeleport;
    public static int DEFUALT_LIQUID_TELEPORT_ID = 4048;
    public static int DEFUALT_LIQUID_TELEPORT_TEXTURE = 0;//8 * 16 + 2;
    public static String DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/BlueLiquid.png";

    //Power Teleport
    public static Item pipePowerTeleport;
    public static int DEFUALT_POWER_TELEPORT_ID = 4049;
    public static int DEFUALT_POWER_TELEPORT_TEXTURE = 0;//8 * 16 + 3;
    public static String DEFUALT_POWER_TELEPORT_TEXTURE_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/BluePower.png";

    //Distributor
    public static Item pipeDistributor;
    public static int DEFUALT_DISTRIBUTOR_TELEPORT_ID = 4046;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_0 = 0;//8*16+9;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_1 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_2 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_3 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_4 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_5 = 0;

    public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE 	= "/net/minecraft/src/buildcraft/zeldo/gui/Dist";
    public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE 		= "/net/minecraft/src/buildcraft/zeldo/gui/DistributionOpen.png";
    public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE_CLOSED = "/net/minecraft/src/buildcraft/zeldo/gui/DistributionClosed.png";

    //Advanced Wood
    public static Item pipeAdvancedWood;
    public static int DEFUALT_ADVANCEDWOOD_ID 		= 4045;
    public static int DEFUALT_ADVANCEDWOOD_TEXTURE 	= 0;//8*16+6;
    public static int DEFUALT_ADVANCEDWOOD_TEXTURE_CLOSED = 0;//8*16+7;
    public static String DEFUALT_ADVANCEDWOOD_FILE 	= "/net/minecraft/src/buildcraft/zeldo/gui/AdvancedWood.png";
    public static String DEFUALT_ADVANCEDWOOD_FILE_CLOSED = "/net/minecraft/src/buildcraft/zeldo/gui/AdvancedWoodClosed.png";

    //Advanced Insertion
    public static Item pipeAdvancedInsertion;
    public static int DEFUALT_Insertion_ID = 4044;
    public static int DEFUALT_Insertion_TEXTURE = 0;//8*16+8;
    public static String DEFUALT_Insertion_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/AdvInsert.png";

    //Redstone
    public static Item pipeRedStone;
    public static int DEFUALT_RedStone_ID = 4043;
    public static int DEFUALT_RedStone_TEXTURE = 0;//8*16+4;
    public static int DEFUALT_RedStone_TEXTURE_POWERED = 0;//8*16+5;
    public static String DEFUALT_RedStone_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/RS.png";
    public static String DEFUALT_RedStone_FILE_POWERED = "/net/minecraft/src/buildcraft/zeldo/gui/RSP.png";

    //Redstone Liquid
    public static Item pipeRedStoneLiquid;
    public static int DEFUALT_RedStoneLiquid_ID = 4042;
    public static int DEFUALT_RedStoneLiquid_TEXTURE = 0;//8*16+1;
    public static int DEFUALT_RedStoneLiquid_TEXTURE_POWERED = 0;//8*16+15;
    public static String DEFUALT_RedStoneLiquid_FILE = "/net/minecraft/src/buildcraft/zeldo/gui/RSL.png";
    public static String DEFUALT_RedStoneLiquid_FILE_POWERED = "/net/minecraft/src/buildcraft/zeldo/gui/RSLP.png";

    public static Block blockChunkLoader;
    public static int DEFUALT_CHUNK_LOADER_ID 	= 179;


    //Redstone ticker

    //GUI Packet Ids  Registered at Flans Google Doc
    // https://docs.google.com/spreadsheet/ccc?key=0At3NBGfCbPHadElSaEFUT2N1LXpSMjAwWVR0dGF4bUE&hl=en#gid=0

    public static byte GUI_ITEM_SEND 			= 103;
    public static byte GUI_LIQUID_SEND			= 104;
    public static byte GUI_ENERGY_SEND 			= 105;
    public static byte GUI_ADVANCEDWOOD_SEND 	= 106;
    public static byte GUI_ITEM_REC 			= 103;
    public static byte GUI_LIQUID_REC 			= 104;
    public static byte GUI_ENERGY_REC 			= 105;
    public static byte GUI_ADVANCEDWOOD_REC 	= 106;

    //Main Packet ID's
    public static int PACKET_SET_AW 	= 1;
    public static int PACKET_SET_ITEM 	= 2;
    public static int PACKET_SET_LIQUID = 3;
    public static int PACKET_SET_POWER 	= 4;
    public static int PACKET_REQ_ITEM 	= 5;
    public static int PACKET_REQ_LIQUID = 6;
    public static int PACKET_REQ_POWER 	= 7;
    public static int PACKET_GUI_COUNT 	= 8;
    public static int PACKET_OPEN_GUI 	= 9;
    public static int PACKET_SET_DIST 	= 10;

    public static int CurrentGUICount 	= 0;

    // Config Setup
    private static Configuration config;
    public static boolean isInGame 			= false;
    public static boolean lagFix 			= false;
    public static boolean wrenchOpensGui 	= false;
    public static boolean allowWPRemove 	= false; //Remove waterproofing/redstone
    public static double PowerLossCfg 		= .995;
    
    //Log
    public static final int LOG_ERROR = 1;
    public static final int LOG_WARNING = 2;
    public static final int LOG_INFO = 3;
    public int logLevel;

    public static Minecraft mc = ModLoader.getMinecraftInstance();

    public static List<Integer> pipeIds = new LinkedList<>();

    public KeyBinding laserKeyBinding = new KeyBinding("laserKeyBinding", 67);
    public static List<Box> lasers = new LinkedList<>();

    @Override
    public void keyboardEvent(KeyBinding keybinding) {

        if(keybinding == laserKeyBinding) {
            toggleLasers();
        }
    }
    
    public void toggleLasers() {
        
        if (!lasers.isEmpty()) {

            for (Box laser : lasers) {
                laser.deleteLasers();
            }

            lasers.clear();
        }
        else {

            int playerY = (int) mc.thePlayer.posY;

            //Loop through chunks to with chunkloader
            for (TileChunkLoader tile : TileChunkLoader.chunkLoaderList) {

                List<ChunkCoordIntPair> chunkCoords = tile.getLoadArea();

                for (ChunkCoordIntPair coords : chunkCoords) {

                    int chunkX = coords.chunkXPos * 16;
                    int chunkZ = coords.chunkZPos * 16;

                    Box outsideLaser = new Box();
                    outsideLaser.initialize(chunkX, playerY, chunkZ, chunkX + 16, playerY, chunkZ + 16);
                    outsideLaser.createLasers(mc.theWorld, LaserKind.Blue);
                    lasers.add(outsideLaser);

                    Box insideLaser = new Box();
                    insideLaser.initialize(chunkX + 7, playerY, chunkZ + 7, chunkX + 9, playerY, chunkZ + 9);
                    insideLaser.createLasers(mc.theWorld, LaserKind.Red);
                    lasers.add(insideLaser);
                }
            }
        }
    }

    public mod_zAdditionalPipes() {
        
        ModLoader.setInGUIHook(this, true, true);
        ModLoader.registerKey(this, laserKeyBinding, false);
        ModLoader.addLocalization("laserKeyBinding", "Turn on/off chunk loader boundries");
        
        MinecraftForge.registerChunkLoadHandler(new ChunkLoadingHandler());
        
    }
    
    public void log(String msg, int debugLevel) {
        
        if (debugLevel > logLevel) {
            return;
        }
        
        System.out.println("Additional Pipes: " + msg);
    }

    public static World getWorld(int dimension) {
        if(mc.theWorld.worldProvider.isHellWorld && dimension == -1) {
            return mc.theWorld;
        }
        else if(dimension == 0) {
            return mc.theWorld;
        }

        return null;
    }

    public boolean wasMutiPlayer = false;

    @Override
    public boolean onTickInGUI(float f, Minecraft minecraft, GuiScreen guiscreen) {
        if (minecraft.theWorld == null) {

            if (isInGame) {
                if (!wasMutiPlayer) {
                    System.out.print("Cleared TeleportPipes...\n");
                    PipeItemTeleport.ItemTeleportPipes.clear();
                    PipeLiquidsTeleport.LiquidTeleportPipes.clear();
                    PipePowerTeleport.PowerTeleportPipes.clear();
                    MutiPlayerProxy.NeedsLoad = true;
                    isInGame = true;
                }
                else {
                    System.out.println("MutiPlayer, Not Clearing");
                }

                isInGame = false;
            }
        }
        else {
            wasMutiPlayer = minecraft.theWorld.isRemote;
            isInGame = true;
        }

        return true;
    }
    public static File getSaveDirectory() {
        return ((SaveHandler)ModLoader.getMinecraftInstance().theWorld.saveHandler).getSaveDirectory();
    }

    @Override
    public void modsLoaded () {
        
        super.modsLoaded();
        ModLoaderMp.registerGUI(this, GUI_ADVANCEDWOOD_REC);
        ModLoaderMp.registerGUI(this, GUI_ENERGY_REC);
        ModLoaderMp.registerGUI(this, GUI_ITEM_REC);
        ModLoaderMp.registerGUI(this, GUI_LIQUID_REC);
        instance = this;

        config = new Configuration(new File(CoreProxy.getBuildCraftBase(), "config/AdditionalPipes.cfg"));
        config.load();

        lagFix 			= Boolean.parseBoolean(config.getOrCreateBooleanProperty("saveLagFix", Configuration.CATEGORY_GENERAL, false).value);
        wrenchOpensGui 	= Boolean.parseBoolean(config.getOrCreateBooleanProperty("wrenchOpensGui", Configuration.CATEGORY_GENERAL, false).value);
        allowWPRemove 	= Boolean.parseBoolean(config.getOrCreateBooleanProperty("EnableWaterProofRemoval", Configuration.CATEGORY_GENERAL, false).value);
        PowerLossCfg    = Double.parseDouble(config.getOrCreateProperty("powerloss", Configuration.CATEGORY_GENERAL, Double.toString(PowerLossCfg)).value);
        logLevel = Integer.parseInt(config.getOrCreateProperty("logLevel", Configuration.CATEGORY_GENERAL, "1").value);

        System.out.println("Teleport Pipes Power Loss Configuration: " + PowerLossCfg);

        AddImageOverride();
        config.save();
        pipeItemTeleport 		= createPipe(mod_zAdditionalPipes.DEFUALT_ITEM_TELEPORT_ID, PipeItemTeleport.class, "Item Teleport Pipe", BuildCraftCore.diamondGearItem, Block.glass, BuildCraftCore.diamondGearItem, null);
        pipeLiquidTeleport 		= createPipe(mod_zAdditionalPipes.DEFUALT_LIQUID_TELEPORT_ID, PipeLiquidsTeleport.class, "Waterproof Teleport Pipe", BuildCraftTransport.pipeWaterproof, pipeItemTeleport, null, null);
        pipePowerTeleport 		= createPipe(mod_zAdditionalPipes.DEFUALT_POWER_TELEPORT_ID, PipePowerTeleport.class, "Power Teleport Pipe", Item.redstone, pipeItemTeleport, null, null);
        pipeDistributor 		= createPipe(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TELEPORT_ID, PipeItemsDistributor.class, "Distribution Transport Pipe", Item.redstone, Item.ingotIron, Block.glass, Item.ingotIron);
        pipeAdvancedWood 		= createPipe(mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_ID, PipeItemsAdvancedWood.class, "Advanced Wooden Transport Pipe", Item.redstone, Block.planks, Block.glass, Block.planks);
        pipeAdvancedInsertion 	= createPipe(mod_zAdditionalPipes.DEFUALT_Insertion_ID, PipeItemsAdvancedInsertion.class, "Advanced Insertion Transport Pipe", Item.redstone, Block.stone, Block.glass, Block.stone);
        pipeRedStone 			= createPipe(mod_zAdditionalPipes.DEFUALT_RedStone_ID, PipeItemsRedstone.class, "Redstone Transport Pipe", Item.redstone, Block.glass, Item.redstone, null);
        pipeRedStoneLiquid 		= createPipe(mod_zAdditionalPipes.DEFUALT_RedStoneLiquid_ID, PipeLiquidsRedstone.class, "Waterproof Redstone Pipe", BuildCraftTransport.pipeWaterproof, pipeRedStone, null, null);


        MinecraftForgeClient.registerItemRenderer(pipeItemTeleport.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipeLiquidTeleport.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipePowerTeleport.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipeDistributor.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipeAdvancedWood.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipeAdvancedInsertion.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipeRedStone.shiftedIndex, mod_BuildCraftTransport.instance);
        MinecraftForgeClient.registerItemRenderer(pipeRedStoneLiquid.shiftedIndex, mod_BuildCraftTransport.instance);


        //ChunkLoader
        ModLoader.registerTileEntity(net.minecraft.src.buildcraft.zeldo.ChunkLoader.TileChunkLoader.class, "ChunkLoader");
        int ChunkLoaderID = Integer.parseInt(config.getOrCreateIntProperty("ChunkLoader.id", Configuration.CATEGORY_BLOCK, DEFUALT_CHUNK_LOADER_ID).value);
        config.save();
        blockChunkLoader = new BlockChunkLoader(ChunkLoaderID);
        ModLoader.registerBlock(blockChunkLoader);
        blockChunkLoader.setBlockName("ChunkLoading Block");
        ModLoader.addName(blockChunkLoader, "ChunkLoading Block");
        boolean Craftable = Boolean.parseBoolean(config.getOrCreateBooleanProperty("ChunkLoader.Enabled", Configuration.CATEGORY_BLOCK, true).value);
        config.save();

        if (Craftable)
            // Replaced shapeless with  IronBox with lapis in middle
            CraftingManager.getInstance().addRecipe(new ItemStack(blockChunkLoader, 4), new Object[] { "iii", "iLi", "iii", Character.valueOf('i'), Item.ingotIron, Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4) });
        //Finish ChunkLoader

        config.save();

        if (allowWPRemove) {
            CraftingManager craftingmanager = CraftingManager.getInstance();

            //Mine
            craftingmanager.addRecipe(new ItemStack(pipeItemTeleport, 1), new Object[] {"A", Character.valueOf('A'), pipeLiquidTeleport});
            craftingmanager.addRecipe(new ItemStack(pipeItemTeleport, 1), new Object[] {"A", Character.valueOf('A'), pipePowerTeleport});
            craftingmanager.addRecipe(new ItemStack(pipeRedStone, 1), new Object[] {"A", Character.valueOf('A'), pipeRedStoneLiquid});

            //BC Liquid
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsCobblestone});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsGold});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsIron});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsStone});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsWood});


            //BC Power
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipePowerGold});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipePowerStone});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood, 1), new Object[] {"A", Character.valueOf('A'), BuildCraftTransport.pipePowerWood});
        }

        RegisterPipeIds();

    }

    public static void AddImageOverride() {
        try {

            DEFUALT_ITEM_TELEPORT_TEXTURE 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_ITEM_TELEPORT_TEXTURE_FILE);
            DEFUALT_RedStoneLiquid_TEXTURE 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_RedStoneLiquid_FILE);
            DEFUALT_LIQUID_TELEPORT_TEXTURE = CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE);
            DEFUALT_POWER_TELEPORT_TEXTURE 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_POWER_TELEPORT_TEXTURE_FILE);
            DEFUALT_RedStone_TEXTURE 		= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_RedStone_FILE);
            DEFUALT_RedStone_TEXTURE_POWERED = CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_RedStone_FILE_POWERED);
            DEFUALT_ADVANCEDWOOD_TEXTURE_CLOSED = CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE_CLOSED);
            DEFUALT_ADVANCEDWOOD_TEXTURE 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE);
            DEFUALT_Insertion_TEXTURE 		= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_Insertion_FILE);
            DEFUALT_DISTRIBUTOR_TEXTURE_0 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "0.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_1 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "1.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_2 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "2.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_3 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "3.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_4 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "4.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_5 	= CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "5.png");
            DEFUALT_RedStoneLiquid_TEXTURE_POWERED = CoreProxy.addCustomTexture(mod_zAdditionalPipes.DEFUALT_RedStoneLiquid_FILE_POWERED);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public GuiScreen handleGUI(int inventoryType) {
        try {

            //		System.out.println("InvType: " + inventoryType);
            if(inventoryType == GUI_LIQUID_REC) {
                return new GuiLiquidTeleportPipe((TileGenericPipe)mc.theWorld.getBlockTileEntity(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
            }
            else if (inventoryType == GUI_ITEM_REC) {
                return new GuiItemTeleportPipe((TileGenericPipe)mc.theWorld.getBlockTileEntity(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
            }
            else if (inventoryType == GUI_ENERGY_REC) {
                return new GuiPowerTeleportPipe((TileGenericPipe)mc.theWorld.getBlockTileEntity(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
            }
            else if (inventoryType == GUI_ADVANCEDWOOD_REC) {
                TileGenericPipe tmp = new TileGenericPipe();
                tmp.pipe = new PipeItemsAdvancedWood(pipeAdvancedWood.shiftedIndex);

                return new GuiAdvancedWoodPipe(mc.thePlayer.inventory, tmp, (TileGenericPipe)mc.theWorld.getBlockTileEntity(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
            }
        }
        catch (Exception e) {
            System.out.println("Handled Error in HandleGUI...");
            e.printStackTrace();
        }


        return null;
    }

    public byte [] ReDim(byte [] array, int newSize) {
        byte [] abytNew = new byte[newSize];
        System.arraycopy(array, 0, abytNew, 0, array.length);
        return abytNew;
    }

    @Override
    public void handlePacket(Packet230ModLoader packet) {
        System.out.println("Packet: " + packet.packetType);

        if (packet.packetType == PACKET_SET_AW) {
            int x = packet.dataInt [0];
            int y = packet.dataInt [1];
            int z = packet.dataInt [2];

            if (APIProxy.getWorld().blockExists(x, y, z)) {
                TileGenericPipe tile = (TileGenericPipe) APIProxy.getWorld().getBlockTileEntity(x, y, z);
                boolean Exclude = intToBool(packet.dataInt[3]);
                ((PipeLogicAdvancedWood)tile.pipe.logic).exclude = Exclude;
            }
        }

        if (packet.packetType == PACKET_SET_ITEM) {
            int x = packet.dataInt [0];
            int y = packet.dataInt [1];
            int z = packet.dataInt [2];

            if (APIProxy.getWorld().blockExists(x, y, z)) {
                TileGenericPipe tile = (TileGenericPipe) APIProxy.getWorld().getBlockTileEntity(x, y, z);
                int freq = packet.dataInt[3];
                boolean canRec = intToBool(packet.dataInt[4]);
                String own = packet.dataString[0];
                ((PipeItemTeleport)tile.pipe).canReceive = canRec;
                ((PipeItemTeleport)tile.pipe).myFreq = freq;
                ((PipeItemTeleport)tile.pipe).Owner = own;
            }
        }

        if (packet.packetType == PACKET_SET_LIQUID) {
            int x = packet.dataInt [0];
            int y = packet.dataInt [1];
            int z = packet.dataInt [2];

            if (APIProxy.getWorld().blockExists(x, y, z)) {
                TileGenericPipe tile = (TileGenericPipe) APIProxy.getWorld().getBlockTileEntity(x, y, z);
                int freq = packet.dataInt[3];
                boolean canRec = intToBool(packet.dataInt[4]);
                String own = packet.dataString[0];
                ((PipeLiquidsTeleport)tile.pipe).canReceive = canRec;
                ((PipeLiquidsTeleport)tile.pipe).myFreq = freq;
                ((PipeLiquidsTeleport)tile.pipe).Owner = own;
            }
        }

        if (packet.packetType == PACKET_SET_POWER) {
            int x = packet.dataInt [0];
            int y = packet.dataInt [1];
            int z = packet.dataInt [2];

            if (APIProxy.getWorld().blockExists(x, y, z)) {
                TileGenericPipe tile = (TileGenericPipe) APIProxy.getWorld().getBlockTileEntity(x, y, z);
                int freq = packet.dataInt[3];
                boolean canRec = intToBool(packet.dataInt[4]);
                String own = packet.dataString[0];
                ((PipePowerTeleport)tile.pipe).canReceive = canRec;
                ((PipePowerTeleport)tile.pipe).myFreq = freq;
                ((PipePowerTeleport)tile.pipe).Owner = own;
            }
        }

        if (packet.packetType == PACKET_GUI_COUNT) {
            CurrentGUICount = packet.dataInt[0];
        }

        if (packet.packetType == PACKET_OPEN_GUI) {
            int x = packet.dataInt [0];
            int y = packet.dataInt [1];
            int z = packet.dataInt [2];
            TileGenericPipe tilePipe = (TileGenericPipe) APIProxy.getWorld().getBlockTileEntity(x, y, z);

            switch (packet.dataInt[3]) {
                case 0:
                    mc.displayGuiScreen(new GuiItemTeleportPipe(tilePipe));
                    break;

                case 1:
                    mc.displayGuiScreen(new GuiLiquidTeleportPipe(tilePipe));
                    break;

                case 2:
                    mc.displayGuiScreen(new GuiPowerTeleportPipe(tilePipe));
                    break;

                case 3:
                    mc.displayGuiScreen(new GuiDistributionPipe(tilePipe));
                    break;
            }
        }

        if (packet.packetType == PACKET_SET_DIST) {
            int x = packet.dataInt [0];
            int y = packet.dataInt [1];
            int z = packet.dataInt [2];

            if (APIProxy.getWorld().blockExists(x, y, z)) {
                TileGenericPipe tile = (TileGenericPipe) APIProxy.getWorld().getBlockTileEntity(x, y, z);
                PipeItemsDistributor a = (PipeItemsDistributor) tile.pipe;

                for (int i = 0; i < a.distData.length; i++) {
                    a.distData[i] = packet.dataInt[3 + i];
                }
            }
        }
    }
    public static boolean intToBool(int a) {
        return (a == 1);
    }

    public static int boolToInt(boolean a) {
        if (a) {
            return 1;
        }

        return 0;
    }
    private static Item createPipe (int defaultID, Class <? extends Pipe > clas, String descr, Object r1, Object r2, Object r3, Object r4) {
        String name = Character.toLowerCase(clas.getSimpleName().charAt(0))
                      + clas.getSimpleName().substring(1);

        Property prop = config
                        .getOrCreateIntProperty(name + ".id",
                                                Configuration.CATEGORY_ITEM, defaultID);
        Property propLoad = config
                            .getOrCreateBooleanProperty(name + ".Enabled",
                                    Configuration.CATEGORY_ITEM, true);
        config.save();
        int id = Integer.parseInt(prop.value);
        Item res =  BlockGenericPipe.registerPipe (id, clas);
        res.setItemName(clas.getSimpleName());
        CoreProxy.addName(res, descr);

        if (!Boolean.parseBoolean(propLoad.value)) {
            return res;
        }

        CraftingManager craftingmanager = CraftingManager.getInstance();

        if (r1 != null && r2 != null && r3 != null && r4 != null) {
            craftingmanager.addRecipe(new ItemStack(res, 8), new Object[] {
                                          " D ", "ABC", "   ",
                                          Character.valueOf('D'), r1,
                                          Character.valueOf('A'), r2,
                                          Character.valueOf('B'), r3,
                                          Character.valueOf('C'), r4
                                      });
        }
        else if (r1 != null && r2 != null && r3 != null) {
            craftingmanager.addRecipe(new ItemStack(res, 8), new Object[] {
                                          "   ", "ABC", "   ",
                                          Character.valueOf('A'), r1,
                                          Character.valueOf('B'), r2,
                                          Character.valueOf('C'), r3
                                      });
        }
        else if (r1 != null && r2 != null) {
            craftingmanager.addRecipe(new ItemStack(res, 1), new Object[] {
                                          "A ", "B ",
                                          Character.valueOf('A'), r1,
                                          Character.valueOf('B'), r2
                                      });
        }

        return res;
    }
    public static void RegisterPipeIds() {
        pipeIds.add(BuildCraftTransport.pipeItemsCobblestone.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeItemsDiamond.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeItemsGold.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeItemsIron.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeItemsObsidian.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeItemsStone.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeItemsWood.shiftedIndex);

        pipeIds.add(BuildCraftTransport.pipeLiquidsCobblestone.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeLiquidsGold.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeLiquidsIron.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeLiquidsStone.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipeLiquidsWood.shiftedIndex);

        pipeIds.add(BuildCraftTransport.pipePowerGold.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipePowerStone.shiftedIndex);
        pipeIds.add(BuildCraftTransport.pipePowerWood.shiftedIndex);

        pipeIds.add(mod_zAdditionalPipes.pipeAdvancedInsertion.shiftedIndex);
        pipeIds.add(mod_zAdditionalPipes.pipeAdvancedWood.shiftedIndex);
        pipeIds.add(mod_zAdditionalPipes.pipeDistributor.shiftedIndex);
        pipeIds.add(mod_zAdditionalPipes.pipeItemTeleport.shiftedIndex);
        pipeIds.add(mod_zAdditionalPipes.pipeLiquidTeleport.shiftedIndex);
        pipeIds.add(mod_zAdditionalPipes.pipePowerTeleport.shiftedIndex);
    }
    public static boolean ItemIsPipe(int ItemID) {
        if (pipeIds.contains(ItemID)) {
            return true;
        }

        return false;
    }

    @Override
    public void load() {
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_ITEM_TELEPORT_TEXTURE_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_RedStoneLiquid_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_POWER_TELEPORT_TEXTURE_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_RedStone_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_RedStone_FILE_POWERED);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE_CLOSED);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_Insertion_FILE);
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "0.png");
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "1.png");
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "2.png");
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "3.png");
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "4.png");
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "5.png");
        MinecraftForgeClient.preloadTexture(mod_zAdditionalPipes.DEFUALT_RedStoneLiquid_FILE_POWERED);
    }
}
