package buildcraft.additionalpipes.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import buildcraft.additionalpipes.mod_AdditionalPipes;
import buildcraft.transport.TileGenericPipe;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;

public class NetworkHandler implements IPacketHandler {
	//server to client
	public static final byte PIPE_DESC = 1;
	public static final byte CHUNKLOAD_DATA = 15;
	//client to server
	public static final byte CHUNKLOAD_REQUEST = 65;

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte packetID;
		try {
			packetID = data.readByte();
			switch(packetID) {
			case PIPE_DESC:
				break;
			case CHUNKLOAD_DATA:
				handleChunkLoadData(data);
				break;
			case CHUNKLOAD_REQUEST:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleChunkLoadData(DataInputStream data) {
		try {
			int size = data.readInt();
			ChunkCoordIntPair[] chunks = new ChunkCoordIntPair[size];
			for(int i = 0; i < size; i++) {
				chunks[i] = new ChunkCoordIntPair(data.readInt(), data.readInt());
			}
			mod_AdditionalPipes.instance.chunkLoadViewer.recievePersistentChunks(chunks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
