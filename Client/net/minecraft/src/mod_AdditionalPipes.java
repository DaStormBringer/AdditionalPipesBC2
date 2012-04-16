package net.minecraft.src;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.buildcraft.additionalpipes.chunkloader.BlockChunkLoader;
import net.minecraft.src.buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import net.minecraft.src.buildcraft.additionalpipes.chunkloader.ChunkLoadingHandler;
import net.minecraft.src.buildcraft.additionalpipes.gui.GuiHandler;
import net.minecraft.src.buildcraft.additionalpipes.MutiPlayerProxy;
import net.minecraft.src.buildcraft.additionalpipes.network.NetworkHandler;
import net.minecraft.src.buildcraft.additionalpipes.pipes.*;
import net.minecraft.src.buildcraft.api.LaserKind;
import net.minecraft.src.buildcraft.core.Box;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.transport.BlockGenericPipe;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.forge.NetworkMod;

public class mod_AdditionalPipes extends NetworkMod {

    public static mod_AdditionalPipes instance;

    @Override
    public boolean clientSideRequired() {
        return true;
    }

    @Override
    public boolean serverSideRequired() {
        return false;
    }

    @Override
    public String getPriorities() {
        return "after:mod_BuildCraftTransport";
    }

    @Override
    public String getVersion() {
        return "2.1.2 (Minecraft 1.2.5, Buildcraft 2.2.14, Forge 3.0.1.75)";
    }
    public static int MASTER_TEXTURE_OFFSET = 0;// 8 * 16;
    public static String MASTER_TEXTURE_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/block_textures.png";
    //Item Teleport
    public static Item pipeItemTeleport;
    public static int DEFUALT_ITEM_TELEPORT_ID = 4047;
    public static int DEFUALT_ITEM_TELEPORT_TEXTURE = 0;//8 * 16 + 0;
    public static String DEFUALT_ITEM_TELEPORT_TEXTURE_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/BlueItem.png";
    //Liquid Teleport
    public static Item pipeLiquidTeleport;
    public static int DEFUALT_LIQUID_TELEPORT_ID = 4048;
    public static int DEFUALT_LIQUID_TELEPORT_TEXTURE = 0;//8 * 16 + 2;
    public static String DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/BlueLiquid.png";
    //Power Teleport
    public static Item pipePowerTeleport;
    public static int DEFUALT_POWER_TELEPORT_ID = 4049;
    public static int DEFUALT_POWER_TELEPORT_TEXTURE = 0;//8 * 16 + 3;
    public static String DEFUALT_POWER_TELEPORT_TEXTURE_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/BluePower.png";
    //Distributor
    public static Item pipeDistributor;
    public static int DEFUALT_DISTRIBUTOR_TELEPORT_ID = 4046;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_0 = 0;//8*16+9;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_1 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_2 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_3 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_4 = 0;
    public static int DEFUALT_DISTRIBUTOR_TEXTURE_5 = 0;
    public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE = "/net/minecraft/src/buildcraft/additionalpipes/gui/Dist";
    public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/DistributionOpen.png";
    public static String DEFUALT_DISTRIBUTOR_TEXTURE_FILE_CLOSED = "/net/minecraft/src/buildcraft/additionalpipes/gui/DistributionClosed.png";
    //Advanced Wood
    public static Item pipeAdvancedWood;
    public static int DEFUALT_ADVANCEDWOOD_ID = 4045;
    public static int DEFUALT_ADVANCEDWOOD_TEXTURE = 0;//8*16+6;
    public static int DEFUALT_ADVANCEDWOOD_TEXTURE_CLOSED = 0;//8*16+7;
    public static String DEFUALT_ADVANCEDWOOD_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/AdvancedWood.png";
    public static String DEFUALT_ADVANCEDWOOD_FILE_CLOSED = "/net/minecraft/src/buildcraft/additionalpipes/gui/AdvancedWoodClosed.png";
    //Advanced Insertion
    public static Item pipeAdvancedInsertion;
    public static int DEFUALT_Insertion_ID = 4044;
    public static int DEFUALT_Insertion_TEXTURE = 0;//8*16+8;
    public static String DEFUALT_Insertion_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/AdvInsert.png";
    //Redstone
    public static Item pipeRedStone;
    public static int DEFUALT_RedStone_ID = 4043;
    public static int DEFUALT_RedStone_TEXTURE = 0;//8*16+4;
    public static int DEFUALT_RedStone_TEXTURE_POWERED = 0;//8*16+5;
    public static String DEFUALT_RedStone_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/RS.png";
    public static String DEFUALT_RedStone_FILE_POWERED = "/net/minecraft/src/buildcraft/additionalpipes/gui/RSP.png";
    //Redstone Liquid
    public static Item pipeRedStoneLiquid;
    public static int DEFUALT_RedStoneLiquid_ID = 4042;
    public static int DEFUALT_RedStoneLiquid_TEXTURE = 0;//8*16+1;
    public static int DEFUALT_RedStoneLiquid_TEXTURE_POWERED = 0;//8*16+15;
    public static String DEFUALT_RedStoneLiquid_FILE = "/net/minecraft/src/buildcraft/additionalpipes/gui/RSL.png";
    public static String DEFUALT_RedStoneLiquid_FILE_POWERED = "/net/minecraft/src/buildcraft/additionalpipes/gui/RSLP.png";
    public static Block blockChunkLoader;
    public static int DEFUALT_CHUNK_LOADER_ID = 189;
 

    public static int CurrentGUICount = 0;
    // Config Setup
    private static Configuration config;
    public static boolean isInGame = false;
    public static boolean lagFix = false;
    public static boolean wrenchOpensGui = false;
    public static boolean allowWPRemove = false; //Remove waterproofing/redstone
    public static double PowerLossCfg = .995;
    //Log
    public static final int LOG_ERROR = 1;
    public static final int LOG_WARNING = 2;
    public static final int LOG_INFO = 3;
    public int logLevel;
    public static Minecraft mc = ModLoader.getMinecraftInstance();
    public KeyBinding laserKeyBinding = new KeyBinding("laserKeyBinding", 67);
    public static List<Box> lasers = new LinkedList<Box>();

    @Override
    public void keyboardEvent(KeyBinding keybinding) {

        if (keybinding == laserKeyBinding) {
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
                    int chunkZ = coords.chunkZPosition * 16;

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

    public mod_AdditionalPipes() {

        ModLoader.setInGUIHook(this, true, true);
        ModLoader.registerKey(this, laserKeyBinding, false);
        ModLoader.addLocalization("laserKeyBinding", "Turn on/off chunk loader boundries");

        MinecraftForge.setGuiHandler(this, new GuiHandler());
        MinecraftForge.registerChunkLoadHandler(new ChunkLoadingHandler());
        MinecraftForge.registerConnectionHandler(new NetworkHandler());

    }

    public void log(String msg, int debugLevel) {

        if (debugLevel > logLevel) {
            return;
        }

        System.out.println("Additional Pipes: " + msg);
    }

    public static World getWorld(int dimension) {
        if (mc.theWorld.worldProvider.isHellWorld && dimension == -1) {
            return mc.theWorld;
        }
        else if (dimension == 0) {
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
                    //System.out.print("Cleared TeleportPipes...\n");
                    PipeTeleport.teleportPipes.clear();
                    //PipeItemTeleport.ItemTeleportPipes.clear();
                    //PipeLiquidsTeleport.LiquidTeleportPipes.clear();
                    //PipePowerTeleport.PowerTeleportPipes.clear();
                    MutiPlayerProxy.NeedsLoad = true;
                    isInGame = true;
                }
                else {
                    //System.out.println("MutiPlayer, Not Clearing");
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
        return ((SaveHandler) ModLoader.getMinecraftInstance().theWorld.saveHandler).getSaveDirectory();
    }

    @Override
    public void modsLoaded() {

        super.modsLoaded();

        instance = this;

        config = new Configuration(new File(CoreProxy.getBuildCraftBase(), "config/AdditionalPipes.cfg"));
        config.load();

        lagFix = Boolean.parseBoolean(config.getOrCreateBooleanProperty("saveLagFix", Configuration.CATEGORY_GENERAL, false).value);
        wrenchOpensGui = Boolean.parseBoolean(config.getOrCreateBooleanProperty("wrenchOpensGui", Configuration.CATEGORY_GENERAL, false).value);
        allowWPRemove = Boolean.parseBoolean(config.getOrCreateBooleanProperty("EnableWaterProofRemoval", Configuration.CATEGORY_GENERAL, false).value);
        //PowerLossCfg    = Double.parseDouble(config.getOrCreateProperty("powerloss", Configuration.CATEGORY_GENERAL, Double.toString(PowerLossCfg)).value);
        logLevel = Integer.parseInt(config.getOrCreateProperty("logLevel", Configuration.CATEGORY_GENERAL, "1").value);
        config.save();
        
        //System.out.println("Teleport Pipes Power Loss Configuration: " + PowerLossCfg);

        boolean loadItemTeleport = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableItemTeleport", Configuration.CATEGORY_ITEM, true).value);
        int ITEM_TELEPORT_ID = Integer.parseInt(config.getOrCreateIntProperty("ItemTeleport.Id", Configuration.CATEGORY_ITEM, DEFUALT_ITEM_TELEPORT_ID).value);

        boolean loadItemsAdvancedInsertion = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableItemsAdvancedInsertion", Configuration.CATEGORY_ITEM, true).value);
        int Insertion_ID = Integer.parseInt(config.getOrCreateIntProperty("ItemsAdvancedInsertion.Id", Configuration.CATEGORY_ITEM, DEFUALT_Insertion_ID).value);

        boolean loadItemsAdvancedWood = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableItemsAdvancedWood", Configuration.CATEGORY_ITEM, true).value);
        int ADVANCEDWOOD_ID = Integer.parseInt(config.getOrCreateIntProperty("ItemsAdvancedWood.Id", Configuration.CATEGORY_ITEM, DEFUALT_ADVANCEDWOOD_ID).value);

        boolean loadItemsDistributor = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableItemsDistributor", Configuration.CATEGORY_ITEM, true).value);
        int DISTRIBUTOR_TELEPORT_ID = Integer.parseInt(config.getOrCreateIntProperty("ItemsDistributor.Id", Configuration.CATEGORY_ITEM, DEFUALT_DISTRIBUTOR_TELEPORT_ID).value);

        boolean loadItemsRedstone = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableItemsRedstone", Configuration.CATEGORY_ITEM, true).value);
        int RedStone_ID = Integer.parseInt(config.getOrCreateIntProperty("ItemsRedstone.Id", Configuration.CATEGORY_ITEM, DEFUALT_RedStone_ID).value);

        boolean loadLiquidsRedstone = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableLiquidsRedstone", Configuration.CATEGORY_ITEM, true).value);
        int RedStoneLiquid_ID = Integer.parseInt(config.getOrCreateIntProperty("LiquidsRedstone.Id", Configuration.CATEGORY_ITEM, DEFUALT_RedStoneLiquid_ID).value);

        boolean loadLiquidsTeleport = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enableLiquidsTeleport", Configuration.CATEGORY_ITEM, true).value);
        int LIQUID_TELEPORT_ID = Integer.parseInt(config.getOrCreateIntProperty("LiquidTeleport.Id", Configuration.CATEGORY_ITEM, DEFUALT_LIQUID_TELEPORT_ID).value);

        boolean loadPowerTeleport = Boolean.parseBoolean(config.getOrCreateBooleanProperty("enablePowerTeleport", Configuration.CATEGORY_ITEM, true).value);
        int POWER_TELEPORT_ID = Integer.parseInt(config.getOrCreateIntProperty("PowerTeleport.Id", Configuration.CATEGORY_ITEM, DEFUALT_POWER_TELEPORT_ID).value);


        CraftingManager craftingmanager = CraftingManager.getInstance();

        // Item Teleport Pipe
        if (loadItemTeleport) {
            DEFUALT_ITEM_TELEPORT_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_ITEM_TELEPORT_TEXTURE_FILE);
            pipeItemTeleport = createPipe(ITEM_TELEPORT_ID, PipeItemTeleport.class, "Item Teleport Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeItemTeleport, 8), new Object[]{"dgd", Character.valueOf('d'), BuildCraftCore.diamondGearItem, Character.valueOf('g'), Block.glass});
        }

        // Liquid Teleport Pipe
        if (loadLiquidsTeleport) {
            DEFUALT_LIQUID_TELEPORT_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE);
            pipeLiquidTeleport = createPipe(LIQUID_TELEPORT_ID, PipeLiquidsTeleport.class, "Waterproof Teleport Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeLiquidTeleport, 1), new Object[]{"w", "P", Character.valueOf('w'), BuildCraftTransport.pipeWaterproof, Character.valueOf('P'), pipeItemTeleport});
        }

        // Power Teleport Pipe
        if (loadPowerTeleport) {
            DEFUALT_POWER_TELEPORT_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_POWER_TELEPORT_TEXTURE_FILE);
            pipePowerTeleport = createPipe(POWER_TELEPORT_ID, PipePowerTeleport.class, "Power Teleport Pipe");
            craftingmanager.addRecipe(new ItemStack(pipePowerTeleport, 1), new Object[]{"r", "P", Character.valueOf('r'), Item.redstone, Character.valueOf('P'), pipeItemTeleport});
        }

        // Distributor Pipe
        if (loadItemsDistributor) {
            DEFUALT_DISTRIBUTOR_TEXTURE_0 = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "0.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_1 = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "1.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_2 = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "2.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_3 = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "3.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_4 = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "4.png");
            DEFUALT_DISTRIBUTOR_TEXTURE_5 = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "5.png");
            pipeDistributor = createPipe(DISTRIBUTOR_TELEPORT_ID, PipeItemsDistributor.class, "Distribution Transport Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeDistributor, 8), new Object[]{" r ", "IgI", Character.valueOf('r'), Item.redstone, Character.valueOf('I'), Item.ingotIron, Character.valueOf('g'), Block.glass});
        }

        // Advanced Wooded Pipe
        if (loadItemsAdvancedWood) {
            DEFUALT_ADVANCEDWOOD_TEXTURE_CLOSED = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE_CLOSED);
            DEFUALT_ADVANCEDWOOD_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE);
            pipeAdvancedWood = createPipe(ADVANCEDWOOD_ID, PipeItemsAdvancedWood.class, "Advanced Wooden Transport Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeAdvancedWood, 8), new Object[]{" r ", "WgW", Character.valueOf('r'), Item.redstone, Character.valueOf('W'), Block.planks, Character.valueOf('g'), Block.glass});
        }

        // Advanced Insertion Pipe
        if (loadItemsAdvancedInsertion) {
            DEFUALT_Insertion_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_Insertion_FILE);
            pipeAdvancedInsertion = createPipe(Insertion_ID, PipeItemsAdvancedInsertion.class, "Advanced Insertion Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeAdvancedInsertion, 8), new Object[]{" r ", "SgS", Character.valueOf('r'), Item.redstone, Character.valueOf('S'), Block.stone, Character.valueOf('g'), Block.glass});
        }

        // Redstone Pipe
        if (loadItemsRedstone) {
            DEFUALT_RedStone_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_RedStone_FILE);
            DEFUALT_RedStone_TEXTURE_POWERED = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_RedStone_FILE_POWERED);
            pipeRedStone = createPipe(RedStone_ID, PipeItemsRedstone.class, "Redstone Transport Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeRedStone, 8), new Object[]{"RgR", Character.valueOf('R'), Item.redstone, Character.valueOf('g'), Block.glass});
        }

        // Redstone Liquid Pipe
        if (loadLiquidsRedstone) {
            DEFUALT_RedStoneLiquid_TEXTURE = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_RedStoneLiquid_FILE);
            DEFUALT_RedStoneLiquid_TEXTURE_POWERED = CoreProxy.addCustomTexture(mod_AdditionalPipes.DEFUALT_RedStoneLiquid_FILE_POWERED);
            pipeRedStoneLiquid = createPipe(RedStoneLiquid_ID, PipeLiquidsRedstone.class, "Waterproof Redstone Pipe");
            craftingmanager.addRecipe(new ItemStack(pipeRedStoneLiquid, 1), new Object[]{"w", "P", Character.valueOf('w'), BuildCraftTransport.pipeWaterproof, Character.valueOf('P'), pipeRedStone});
        }

        // Remove Redstone From Power TP Pipe
        craftingmanager.addRecipe(new ItemStack(pipeItemTeleport, 1), new Object[]{"A", Character.valueOf('A'), pipePowerTeleport});

        if (allowWPRemove) {

            //Mine
            craftingmanager.addRecipe(new ItemStack(pipeItemTeleport, 1), new Object[]{"A", Character.valueOf('A'), pipeLiquidTeleport});
            craftingmanager.addRecipe(new ItemStack(pipeRedStone, 1), new Object[]{"A", Character.valueOf('A'), pipeRedStoneLiquid});

            //BC Liquid
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsCobblestone, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsCobblestone});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsGold});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsIron, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsIron});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsStone});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipeLiquidsWood});

            //BC Power
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsGold, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipePowerGold});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsStone, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipePowerStone});
            craftingmanager.addRecipe(new ItemStack(BuildCraftTransport.pipeItemsWood, 1), new Object[]{"A", Character.valueOf('A'), BuildCraftTransport.pipePowerWood});
        }

        //ChunkLoader
        int ChunkLoaderID = Integer.parseInt(config.getOrCreateIntProperty("TeleportTether.id", Configuration.CATEGORY_BLOCK, DEFUALT_CHUNK_LOADER_ID).value);
        boolean Craftable = Boolean.parseBoolean(config.getOrCreateBooleanProperty("TeleportTether.Enabled", Configuration.CATEGORY_BLOCK, true).value);
        config.save();
        
        ModLoader.registerTileEntity(net.minecraft.src.buildcraft.additionalpipes.chunkloader.TileChunkLoader.class, "Teleport Tether");
        blockChunkLoader = new BlockChunkLoader(ChunkLoaderID, 0);
        ModLoader.registerBlock(blockChunkLoader);
        blockChunkLoader.setBlockName("Teleport Tether");
        ModLoader.addName(blockChunkLoader, "Teleport Tether");
   
        if (Craftable) {
            CraftingManager.getInstance().addRecipe(new ItemStack(blockChunkLoader, 4), new Object[]{"iii", "iLi", "iii", Character.valueOf('i'), Item.ingotIron, Character.valueOf('L'), new ItemStack(Item.dyePowder, 1, 4)});
        }

        config.save();
    }

   
    private Item createPipe(int id, Class<? extends Pipe> clas, String description) {
        Item res = BlockGenericPipe.registerPipe(id, clas);
        res.setItemName(clas.getSimpleName());
        CoreProxy.addName(res, description);
        MinecraftForgeClient.registerItemRenderer(res.shiftedIndex, mod_BuildCraftTransport.instance);
        return res;
    }

    public static boolean isPipe(Item item) {

        if (BlockGenericPipe.pipes.containsKey(item.shiftedIndex)) {
            return true;
        }

        return false;
    }

    @Override
    public void load() {

        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_ITEM_TELEPORT_TEXTURE_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStoneLiquid_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_POWER_TELEPORT_TEXTURE_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStone_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStone_FILE_POWERED);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE_CLOSED);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_Insertion_FILE);
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "0.png");
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "1.png");
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "2.png");
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "3.png");
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "4.png");
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "5.png");
        MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStoneLiquid_FILE_POWERED);
        
        MinecraftForgeClient.preloadTexture("/net/minecraft/src/buildcraft/additionalpipes/gui/chunkloader.png");
    }
}
