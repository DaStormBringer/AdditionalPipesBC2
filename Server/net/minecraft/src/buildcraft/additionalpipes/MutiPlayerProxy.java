package net.minecraft.src.buildcraft.additionalpipes;

import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemTeleport;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipePowerTeleport;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeLiquidsTeleport;
import java.io.File;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.additionalpipes.gui.CraftingAdvancedWoodPipe;
import net.minecraft.src.*;

public class MutiPlayerProxy {
    public static boolean NeedsLoad = true;
    public static File WorldDir;
    public static boolean isServer = true;

    public static MinecraftServer mc = ModLoader.getMinecraftServerInstance();

    public static void displayGUIItemTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeItemTeleport)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, mod_zAdditionalPipes.getCountPacket(((PipeItemTeleport)tilePipe.pipe).getConnectedPipes(true).size()));
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(0, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUILiquidTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeLiquidsTeleport)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, mod_zAdditionalPipes.getCountPacket(((PipeLiquidsTeleport)tilePipe.pipe).getConnectedPipes(true).size()));
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(1, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUIPowerTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipePowerTeleport)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, mod_zAdditionalPipes.getCountPacket(((PipePowerTeleport)tilePipe.pipe).getConnectedPipes(true).size()));
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(2, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUIDistribution(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeItemsDistributor)tilePipe.pipe).getDescPipe());
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, OpenGUI(3, tilePipe.xCoord, tilePipe.yCoord, tilePipe.zCoord));
    }
    public static void displayGUIAdvancedWood(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayer, ((PipeItemsAdvancedWood)tilePipe.pipe).getDescPacket());
        ModLoader.openGUI(entityplayer, mod_zAdditionalPipes.GUI_ADVANCEDWOOD_SEND, tilePipe, new CraftingAdvancedWoodPipe(entityplayer.inventory, tilePipe));
    }

    public static Packet230ModLoader OpenGUI(int Type, int x, int y, int z) {
        Packet230ModLoader packet = new Packet230ModLoader();

        packet.modId = mod_zAdditionalPipes.instance.getId();
        packet.packetType = mod_zAdditionalPipes.PACKET_OPEN_GUI;
        packet.isChunkDataPacket = true;

        packet.dataInt = new int [4];

        packet.dataInt [0] = x;
        packet.dataInt [1] = y;
        packet.dataInt [2] = z;
        packet.dataInt [3] = Type;

        return packet;

    }
    public static void requestItemTeleport(int x, int y, int z) {}
    public static boolean isOnServer() {
        return true;
    }
    public static boolean isOp(String entityplayermp) {
        if(mc.configManager.isOp(entityplayermp)) {
            return true;
        }

        return false;
    }
    public static void SendPacketToAll(Packet230ModLoader packet) {
        ModLoaderMp.sendPacketToAll(mod_zAdditionalPipes.instance, packet);
    }
    public static void SendPacket(Packet230ModLoader packet, EntityPlayer entityplayermp) {
        ModLoaderMp.sendPacketTo(mod_zAdditionalPipes.instance, (EntityPlayerMP)entityplayermp, packet);
    }
    public static void bindTex() {
    }
}
