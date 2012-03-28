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

import net.minecraft.client.Minecraft;
import net.minecraft.src.BuildCraftCore;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ModLoaderMp;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.mod_zAdditionalPipes.chunkXZ;
import net.minecraft.src.buildcraft.api.APIProxy;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.gui.GuiAdvancedWoodPipe;
import net.minecraft.src.buildcraft.zeldo.gui.GuiDistributionPipe;
import net.minecraft.src.buildcraft.zeldo.gui.GuiItemTeleportPipe;
import net.minecraft.src.buildcraft.zeldo.gui.GuiLiquidTeleportPipe;
import net.minecraft.src.buildcraft.zeldo.gui.GuiPowerTeleportPipe;
import net.minecraft.src.forge.MinecraftForgeClient;



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
	
	public static boolean isOnServer()
	{
		return mc.theWorld.isRemote;
	}
	
	public static void AddChunkToList(int x, int z) {
		//if (isOnServer())
		//	return;
		MutiPlayerProxy.LoadChunkData();
		x = x >> 4;
		z = z >> 4;
		Iterator<chunkXZ> chunks = mod_zAdditionalPipes.keepLoadedChunks.iterator();
		while (chunks.hasNext()) {
			chunkXZ curChunk = chunks.next();
			if (curChunk.x == x && curChunk.z == z) {
				//System.out.println("Didn't need to add PermChunk @ " + x + "," + z);
				return;
			}

		}
		mod_zAdditionalPipes.keepLoadedChunks.add(new mod_zAdditionalPipes.chunkXZ(x, z));
		System.out.println("Added PermChunk @ " + x + "," + z);
		SaveChunkData();
	}
	public static void SaveChunkData() {
		if (isOnServer())
			return;
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
		if (isOnServer())
			return;
		if (!NeedsLoad)
			return;
		NeedsLoad = false;
		try {
			FileInputStream fis = new FileInputStream(getChunkSaveFile().getAbsolutePath());
			GZIPInputStream gzis = new GZIPInputStream(fis);
			ObjectInputStream in = new ObjectInputStream(gzis);
			List<mod_zAdditionalPipes.chunkXZ> loaded = (List<mod_zAdditionalPipes.chunkXZ>)in.readObject();
			in.close();
			mod_zAdditionalPipes.keepLoadedChunks = loaded;
			System.out.println("[AdditionalPipes] Loaded " + loaded.size() + " Forced Chunks");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void DeleteChunkFromList(int x, int z) {
		//if (isOnServer())
		//	return;
		MutiPlayerProxy.LoadChunkData();
		x = x >> 4;
		z = z >> 4;
		Iterator<chunkXZ> chunks = mod_zAdditionalPipes.keepLoadedChunks.iterator();
		while (chunks.hasNext()) {
			chunkXZ curChunk = chunks.next();
			if (curChunk.x == x && curChunk.z == z) {
				mod_zAdditionalPipes.keepLoadedChunks.remove(curChunk);
				System.out.println("Removed PermChunk @ " + x + "," + z);
				SaveChunkData();
				return;
			}

		}
	}
	public static File getChunkSaveFile() {
		//With MC being dumb we have to load the world location ourselfs
		if (WorldDir == null)
			WorldDir = mod_zAdditionalPipes.getSaveDirectory();
		return new File(WorldDir, "ChunkLoader.doNotTouch");
	}
	public static void bindTex()
	{
//		checkHdPatch();
//		if (HDFound) {
//			MinecraftForgeClient.unbindTexture();
//			MinecraftForgeClient.bindTexture(mod_zAdditionalPipes.MASTER_TEXTURE_FILE);
//		}
	}
	public static void checkHdPatch()
	{
		if (HDSet)
			return;

		Object o = ModLoader.getMinecraftInstance().renderEngine;
		try {
			o.getClass().getMethod("setTileSize", Minecraft.class);
			HDFound = true;
			System.out.println("[AdditionalPipes] HD Texture Patch found...");
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("[AdditionalPipes] HD Texture Patch not found...");
		}
		try {
			o.getClass().getMethod("checkHdTextures");
			OFFound = true;
	//		System.out.println("[AdditionalPipes] OptiFine found... Forced to override the base texture...");
	//		BuildCraftCore.customBuildCraftTexture = mod_zAdditionalPipes.MASTER_OVERRIDE_FILE;
	//		MinecraftForgeClient.preloadTexture(BuildCraftCore.customBuildCraftTexture);
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("[AdditionalPipes] OptiFine not found...");
		}
		HDSet = true;
	}

}
