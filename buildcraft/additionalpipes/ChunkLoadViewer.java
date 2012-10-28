package buildcraft.additionalpipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayerMP;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import buildcraft.api.core.LaserKind;
import buildcraft.core.Box;

import com.google.common.collect.SetMultimap;

import cpw.mods.fml.client.FMLClientHandler;

public class ChunkLoadViewer {
	public static final int MAX_SIGHT_RANGE = 63;

	public int sightRange = 5;
	public List<Box> lasers = new LinkedList<Box>();
	public ChunkCoordIntPair[] persistentChunks = new ChunkCoordIntPair[0];

	//client methods
	public void toggleLasers(){
		if(lasersActive()){
			deactivateLasers();
		} else {
			activateLasers();
		}
	}

	public void activateLasers(){
		deactivateLasers();
		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		int playerY = (int) player.posY;
		for (ChunkCoordIntPair coords : persistentChunks) {
			int chunkX = coords.chunkXPos * 16;
			int chunkZ = coords.chunkZPos * 16;
			Box outsideLaser = new Box();
			outsideLaser.initialize(chunkX, playerY, chunkZ, chunkX + 16, playerY, chunkZ + 16);
			outsideLaser.createLasers(player.worldObj, LaserKind.Blue);
			lasers.add(outsideLaser);

			Box insideLaser = new Box();
			insideLaser.initialize(chunkX + 7, playerY, chunkZ + 7, chunkX + 9, playerY, chunkZ + 9);
			insideLaser.createLasers(player.worldObj, LaserKind.Red);
			lasers.add(insideLaser);
		}
	}

	public void deactivateLasers(){
		for (Box laser : lasers) {
			laser.deleteLasers();
		}
		lasers.clear();
	}

	public boolean lasersActive(){
		return !lasers.isEmpty();
	}

	public void recievePersistentChunks(ChunkCoordIntPair[] chunks) {
		persistentChunks = chunks;
		activateLasers();
	}

	//server methods
	public void sendPersistentChunksToPlayer(EntityPlayerMP player) {
		if(sightRange > MAX_SIGHT_RANGE) sightRange = MAX_SIGHT_RANGE;

		SetMultimap<ChunkCoordIntPair, Ticket> persistentChunks =
				ForgeChunkManager.getPersistentChunksFor(player.worldObj);
		List<ChunkCoordIntPair> chunksInRange = new LinkedList<ChunkCoordIntPair>();
		int playerX = (((int) player.posX) >> 4) - sightRange / 2,
				playerZ = (((int) player.posZ) >> 4) - sightRange / 2;

		for(int i = 0; i  < sightRange; i++) {
			for(int j = 0; j < sightRange; j++) {
				ChunkCoordIntPair coords = new ChunkCoordIntPair(playerX + i, playerZ + j);
				if(persistentChunks.containsKey(coords)) {
					chunksInRange.add(coords);
				}
			}
		}

		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.CHUNKLOAD_DATA, false);
		packet.writeInt(chunksInRange.size());
		for(ChunkCoordIntPair coords : chunksInRange) {
			packet.writeInt(coords.chunkXPos);
			packet.writeInt(coords.chunkZPos);
		}
		player.playerNetServerHandler.sendPacketToPlayer(packet.makePacket());
	}

}
