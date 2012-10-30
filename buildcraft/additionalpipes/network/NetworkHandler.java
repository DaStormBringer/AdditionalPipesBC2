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
import buildcraft.additionalpipes.pipes.logic.PipeLogicAdvancedWood;
import buildcraft.additionalpipes.pipes.logic.PipeLogicDistributor;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class NetworkHandler implements IPacketHandler {
	//server to client
	public static final byte CHUNKLOAD_DATA = 15;
	//client to server
	public static final byte ADV_WOOD_DATA = 62;
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
			case ADV_WOOD_DATA:
				TileEntity te = getTileEntity(player, data);
				if(te instanceof TileGenericPipe) {
					PipeLogic logic = ((TileGenericPipe) te).pipe.logic;
					PipeLogicAdvancedWood advLogic = (PipeLogicAdvancedWood) logic;
					advLogic.exclude = !advLogic.exclude;
				}
				break;
			case DIST_PIPE_DATA:
				handleDistPipeData(player, data);
				break;
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

	private void handleDistPipeData(Player player, DataInputStream data) {
		try {
			TileEntity te = getTileEntity(player, data);
			if(te instanceof TileGenericPipe) {
				int index = data.read();
				int newData = data.readInt();
				PipeLogicDistributor logic = (PipeLogicDistributor) ((TileGenericPipe) te).pipe.logic;

				if(newData >= 0 && index >= 0 && index < logic.distData.length) {
					logic.distData[index] = newData;
					boolean found = newData > 0;
					if(!found) {
						for (int i = 0; i < logic.distData.length; i++) {
							if (logic.distData[i] > 0) {
								found = true;
							}
						}
					}
					if (!found) {
						for (int i = 0; i < logic.distData.length; i++) {
							logic.distData[i] = 1;
						}
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleTelePipeData(Player player, DataInputStream data) {
		try {
			TileEntity te = getTileEntity(player, data);
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
			AdditionalPipes.instance.chunkLoadViewer.receivePersistentChunks(chunks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TileEntity getTileEntity(Player player, DataInputStream data) {
		int x, y, z;
		TileEntity te = null;
		try {
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = ((EntityPlayer) player).worldObj.getBlockTileEntity(x, y, z);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return te;
	}
}
