package net.minecraft.src.buildcraft.additionalpipes;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.buildcraft.additionalpipes.gui.*;
import net.minecraft.src.buildcraft.api.APIProxy;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.mod_zAdditionalPipes;



public class MutiPlayerProxy {
    public static boolean NeedsLoad = true;
    public static File WorldDir;
    public static boolean isServer = false;
    public static boolean HDSet = false;
    public static boolean HDFound = false;
    public static boolean OFFound = false;

    public static Minecraft mc = ModLoader.getMinecraftInstance();

    public static void displayGUIItemTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        if (!APIProxy.isClient(APIProxy.getWorld())) {
            mc.displayGuiScreen(new GuiItemTeleportPipe(tilePipe));
        }
    }
    public static void displayGUILiquidTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        if (!APIProxy.isClient(APIProxy.getWorld())) {
            mc.displayGuiScreen(new GuiLiquidTeleportPipe(tilePipe));
        }
    }
    public static void displayGUIPowerTeleport(EntityPlayer entityplayer, TileGenericPipe tilePipe) {
        if (!APIProxy.isClient(APIProxy.getWorld())) {
            mc.displayGuiScreen(new GuiPowerTeleportPipe(tilePipe));
        }
    }
    public static void displayGUIAdvancedWood(EntityPlayer entityplayer, TileGenericPipe container) {
        if (!APIProxy.isClient(APIProxy.getWorld())) {
            mc.displayGuiScreen(new GuiAdvancedWoodPipe(entityplayer.inventory, container, container));
        }
    }
    public static void displayGUIDistribution(EntityPlayer entityplayer, TileGenericPipe container) {
        if (!APIProxy.isClient(APIProxy.getWorld())) {
            mc.displayGuiScreen(new GuiDistributionPipe(container));
        }
    }
    public static void requestItemTeleport(int x, int y, int z) {
        if (APIProxy.isClient(APIProxy.getWorld())) {
            //System.out.println("Send Request for pipe");
            ModLoaderMp.sendPacket(mod_zAdditionalPipes.instance, requestUpdatePacket( x, y, z, mod_zAdditionalPipes.PACKET_REQ_ITEM));
        }
    }
    public static Packet230ModLoader requestUpdatePacket(int x, int y, int z, int PacketID) {
        Packet230ModLoader packet = new Packet230ModLoader();

        packet.modId = mod_zAdditionalPipes.instance.getId();
        packet.packetType = PacketID;
        packet.isChunkDataPacket = true;

        packet.dataInt = new int [3];

        packet.dataInt [0] = x;
        packet.dataInt [1] = y;
        packet.dataInt [2] = z;

        return packet;
    }

    public static boolean isOnServer() {
        return mc.theWorld.isRemote;
    }

    public static void bindTex() {
//		checkHdPatch();
//		if (HDFound) {
//			MinecraftForgeClient.unbindTexture();
//			MinecraftForgeClient.bindTexture(mod_zAdditionalPipes.MASTER_TEXTURE_FILE);
//		}
    }
    public static void checkHdPatch() {
        if (HDSet) {
            return;
        }

        Object o = ModLoader.getMinecraftInstance().renderEngine;

        try {
            o.getClass().getMethod("setTileSize", Minecraft.class);
            HDFound = true;
            System.out.println("[AdditionalPipes] HD Texture Patch found...");
        }
        catch (Exception e) {
            //e.printStackTrace();
            System.out.println("[AdditionalPipes] HD Texture Patch not found...");
        }

        try {
            o.getClass().getMethod("checkHdTextures");
            OFFound = true;
            //		System.out.println("[AdditionalPipes] OptiFine found... Forced to override the base texture...");
            //		BuildCraftCore.customBuildCraftTexture = mod_zAdditionalPipes.MASTER_OVERRIDE_FILE;
            //		MinecraftForgeClient.preloadTexture(BuildCraftCore.customBuildCraftTexture);
        }
        catch (Exception e) {
            //e.printStackTrace();
            System.out.println("[AdditionalPipes] OptiFine not found...");
        }

        HDSet = true;
    }

}
