package net.minecraft.src.buildcraft.zeldo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ModLoaderMp;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.mod_zAdditionalPipes.chunkXZ;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.gui.ContainerTeleportPipe;
import net.minecraft.src.buildcraft.zeldo.gui.CraftingAdvancedWoodPipe;
import net.minecraft.src.buildcraft.zeldo.pipes.PipeItemTeleport;
import net.minecraft.src.buildcraft.zeldo.pipes.PipeItemsAdvancedWood;
import net.minecraft.src.buildcraft.zeldo.pipes.PipeItemsDistributor;
import net.minecraft.src.buildcraft.zeldo.pipes.PipeLiquidsTeleport;
import net.minecraft.src.buildcraft.zeldo.pipes.PipePowerTeleport;

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

    public static void AddChunkToList(int i, int j) {
        mod_zAdditionalPipes.chunksToAdd.add(new chunkXZ(i, j));
    }
    public static void AddChunkToList2(int i, int j) {
        LoadChunkData();
        i >>= 4;
        j >>= 4;

        for(Iterator iterator = mod_zAdditionalPipes.keepLoadedChunks.iterator(); iterator.hasNext();) {
            mod_zAdditionalPipes.chunkXZ chunkxz = (mod_zAdditionalPipes.chunkXZ)iterator.next();

            if(chunkxz.x == i && chunkxz.z == j) {
                return;
            }
        }

        mod_zAdditionalPipes.keepLoadedChunks.add(new mod_zAdditionalPipes.chunkXZ(i, j));
        SaveChunkData();
    }

    public static void SaveChunkData() {
        try {

            //System.out.println("Saving ChunkLoader data...");
            FileOutputStream fos = new FileOutputStream(getChunkSaveFile().getAbsolutePath());
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(gzos);
            out.writeObject(mod_zAdditionalPipes.keepLoadedChunks);
            out.flush();
            out.close();
            //System.out.println("Saved ChunkLoader data...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    public static void LoadChunkData() {
        if (!NeedsLoad) {
            return;
        }

        NeedsLoad = false;

        try {
            FileInputStream fis = new FileInputStream(getChunkSaveFile().getAbsolutePath());
            GZIPInputStream gzis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gzis);
            List<mod_zAdditionalPipes.chunkXZ> loaded = (List<mod_zAdditionalPipes.chunkXZ>)in.readObject();
            in.close();
            mod_zAdditionalPipes.keepLoadedChunks = loaded;
            mc.log("[AdditionalPipes] Loaded " + loaded.size() + " Forced Chunks");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DeleteChunkFromList(int i, int j) {
        mod_zAdditionalPipes.chunksToRemove.add(new chunkXZ(i, j));
    }
    public static void DeleteChunkFromList2(int i, int j) {
        LoadChunkData();
        i >>= 4;
        j >>= 4;

        for(Iterator iterator = mod_zAdditionalPipes.keepLoadedChunks.iterator(); iterator.hasNext();) {
            mod_zAdditionalPipes.chunkXZ chunkxz = (mod_zAdditionalPipes.chunkXZ)iterator.next();

            if(chunkxz.x == i && chunkxz.z == j) {
                mod_zAdditionalPipes.keepLoadedChunks.remove(chunkxz);
                SaveChunkData();
                return;
            }
        }
    }


    public static File getChunkSaveFile() {
        //With MC being dumb we have to load the world location ourselves
        if (WorldDir == null) {
            WorldDir = mod_zAdditionalPipes.getSaveDirectory();
        }

        return new File(WorldDir, "ChunkLoader.doNotTouch");
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
