package buildcraft.additionalpipes.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class NetworkHandler implements IPacketHandler {
	//server to client
	public static final byte CHUNKLOAD_DATA = 15;
	//client to server
	public static final byte DIST_PIPE_DATA = 63;
	public static final byte TELE_PIPE_DATA = 64;
	public static final byte CHUNKLOAD_REQUEST = 65;

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte packetID;
		try {
			packetID = data.readByte();
			switch(packetID) {
			case TELE_PIPE_DATA:
				handleTelePipeData(player, data);
				break;
			case CHUNKLOAD_DATA:
				handleChunkLoadData(data);
				break;
			case CHUNKLOAD_REQUEST:
				AdditionalPipes.instance.chunkLoadViewer
				.sendPersistentChunksToPlayer((EntityPlayerMP) player);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleTelePipeData(Player player, DataInputStream data) {
		try {
			int x = data.readInt(), y= data.readInt(), z = data.readInt();
			TileEntity te = ((EntityPlayer) player).worldObj.getBlockTileEntity(x, y, z);
			if(te instanceof TileGenericPipe) {
				PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
				pipe.logic.freq = data.readInt();
				if(pipe.logic.freq < 0) {
					pipe.logic.freq = 0;
				}
				pipe.logic.canReceive = (data.read() == 1);
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
			AdditionalPipes.instance.chunkLoadViewer.recievePersistentChunks(chunks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
