package buildcraft.additionalpipes;

import java.util.EnumSet;
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
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ChunkLoadViewDataProxy implements IScheduledTickHandler {
	public static final int MAX_SIGHT_RANGE = 31;

	//used by server
	private int sightRange;

	//used by client
	private List<Box> lasers;
	public ChunkCoordIntPair[] persistentChunks;
	private boolean active = false;

	public ChunkLoadViewDataProxy(int chunkSightRange) {
		setSightRange(chunkSightRange);
		lasers = new LinkedList<Box>();
		persistentChunks = new ChunkCoordIntPair[0];
		active = false;
	}

	//laser methods (client)
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
		int playerY = (int) player.posY - 1;
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
		active = true;
	}

	public void deactivateLasers(){
		for (Box laser : lasers) {
			laser.deleteLasers();
		}
		lasers.clear();
		active = false;
	}

	public boolean lasersActive(){
		return active;
	}

	//packet methods

	//sets how far the server will search for chunkloaded chunks
	//when sending data to the player (server)
	public void setSightRange(int range) {
		sightRange = range;
		if(sightRange > MAX_SIGHT_RANGE)
			sightRange = MAX_SIGHT_RANGE;

	}

	//client
	public void requestPersistentChunks() {
		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.CHUNKLOAD_REQUEST, false);
		PacketDispatcher.sendPacketToServer(packet.makePacket());
	}

	//client
	public void receivePersistentChunks(ChunkCoordIntPair[] chunks) {
		persistentChunks = chunks;
		if(active) {
			activateLasers();
		}
	}

	//server
	public void sendPersistentChunksToPlayer(EntityPlayerMP player) {
		if(!AdditionalPipes.instance.chunkSight) { return;}
		if(sightRange > MAX_SIGHT_RANGE) sightRange = MAX_SIGHT_RANGE;

		SetMultimap<ChunkCoordIntPair, Ticket> persistentChunks =
				ForgeChunkManager.getPersistentChunksFor(player.worldObj);
		List<ChunkCoordIntPair> chunksInRange = new LinkedList<ChunkCoordIntPair>();
		int playerX = (((int) player.posX) >> 4) - sightRange / 2,
				playerZ = (((int) player.posZ) >> 4) - sightRange / 2;

		for(int i = -sightRange; i  <= sightRange; i++) {
			for(int j = -sightRange; j <= sightRange; j++) {
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
		AdditionalPipes.instance.logger.info("[ChunkLoadViewDataProxy] Sent chunks within " + sightRange + " of player.");
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(lasersActive()) {
			//requestPersistentChunks();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return getClass().getSimpleName();
	}

	@Override
	public int nextTickSpacing() {
		return 20;
	}
}
