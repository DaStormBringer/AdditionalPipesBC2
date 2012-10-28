package net.minecraft.src.buildcraft.additionalpipes;

import java.io.File;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;

public class MutiPlayerProxy {
    public static boolean NeedsLoad = true;
    public static File WorldDir;
    public static boolean isServer = true;

    public static MinecraftServer mc = ModLoader.getMinecraftServerInstance();

    /*public static void displayGUIItemTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeItemTeleport)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, mod_AdditionalPipes.getCountPacket(((PipeItemTeleport)tilePipe.pipe).getConnectedPipes(true).size()));
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(0, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUILiquidTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeLiquidsTeleport)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, mod_AdditionalPipes.getCountPacket(((PipeLiquidsTeleport)tilePipe.pipe).getConnectedPipes(true).size()));
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(1, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUIPowerTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipePowerTeleport)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, mod_AdditionalPipes.getCountPacket(((PipePowerTeleport)tilePipe.pipe).getConnectedPipes(true).size()));
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(2, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUIDistribution(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeItemsDistributor)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(3, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUIAdvancedWood(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_AdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeItemsAdvancedWood)tilePipe.pipe).getDescPacket());
        ModLoader.openGUI(entityplayer, mod_AdditionalPipes.GUI_ADVANCEDWOOD_SEND, tilePipe, new CraftingAdvancedWoodPipe(entityplayer.inventory, tilePipe));
    }

    public static Packet230ModLoader OpenGUI(int Type, int x, int y, int z) {
        Packet230ModLoader packet = new Packet230ModLoader();

        packet.modId = mod_AdditionalPipes.instance.getId();
        packet.packetType = mod_AdditionalPipes.PACKET_OPEN_GUI;
        packet.isChunkDataPacket = true;

        packet.dataInt = new int [4];

        packet.dataInt [0] = x;
        packet.dataInt [1] = y;
        packet.dataInt [2] = z;
        packet.dataInt [3] = Type;

        return packet;

    }*/
    
    public static boolean isOnServer() {
        return true;
    }
    
    public static boolean isOp(String entityplayermp) {
        if(mc.configManager.isOp(entityplayermp)) {
            return true;
        }

        return false;
    }
}
