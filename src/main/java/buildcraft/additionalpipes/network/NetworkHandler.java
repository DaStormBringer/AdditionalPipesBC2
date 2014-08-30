package buildcraft.additionalpipes.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.ChunkCoordIntPair;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class NetworkHandler {
	// server to client
	public static final byte CHUNKLOAD_DATA = 15;
	public static final byte TELE_PIPE_DATA = 16;
	// client to server
	public static final byte ADV_WOOD_DATA = 62;
	public static final byte DIST_PIPE_DATA = 63;
	public static final byte TELE_PIPE_DATA_SET = 64;
	public static final byte CHUNKLOAD_REQUEST = 65;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, EntityPlayer player) {
		if(AdditionalPipes.CHANNEL.equals(packet.channel)) {
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
			byte packetID = -1;
			try {
				packetID = data.readByte();
				switch(packetID) {
				case ADV_WOOD_DATA:
					TileEntity te = getTileEntity(player, data);
					if(te instanceof TileGenericPipe) {
						PipeItemsAdvancedWood pipe = (PipeItemsAdvancedWood) ((TileGenericPipe) te).pipe;
						pipe.transport.exclude = !pipe.transport.exclude;
					}
					break;
				case DIST_PIPE_DATA:
					handleDistPipeData(player, data);
					break;
				case TELE_PIPE_DATA_SET:
					handleTelePipeData(player, data);
					break;
				case CHUNKLOAD_DATA:
					handleChunkLoadData(data);
					break;
				case CHUNKLOAD_REQUEST:
					AdditionalPipes.instance.chunkLoadViewer.sendPersistentChunksToPlayer((EntityPlayerMP) player);
					break;
				}
			} catch(IOException e) {
				AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling packet " + packetID, e);
			}
		} else if(AdditionalPipes.CHANNELNBT.equals(packet.channel)) {
			NBTTagCompound tag = PacketNBTTagData.getNBTFrom(packet);
			switch(tag.getInteger("id")) {
			case TELE_PIPE_DATA:
				TileEntity te = ((EntityPlayer) player).worldObj.getTileEntity(tag.getInteger("xCoord"), tag.getInteger("yCoord"), tag.getInteger("zCoord"));
				if(te instanceof TileGenericPipe) {
					PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
					pipe.owner = tag.getString("owner");
					pipe.network = tag.getIntArray("network");
				}
				break;
			}
		}
	}

	private void handleDistPipeData(EntityPlayer player, DataInputStream data) {

	}

	private void handleTelePipeData(EntityPlayer player, DataInputStream data) {
		try {
			TileEntity te = getTileEntity(player, data);
			if(te instanceof TileGenericPipe) {
				PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
				// only allow the owner to change pipe state
				EntityPlayerMP entityPlayer = (EntityPlayerMP) player;
				if(!PipeTeleport.canPlayerModifyPipe(entityPlayer, pipe)) {
					entityPlayer.addChatComponentMessage(new ChatComponentText("You may not change pipe state."));
					return;
				}
				int frequency = data.readInt();
				if(frequency < 0) {
					frequency = 0;
				}
				pipe.setFrequency(frequency);
				pipe.state = (byte) data.read();
				pipe.isPublic = (data.read() == 1);
			}
		} catch(IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling teleport pipe packet.", e);
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
		} catch(IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error handling chunk load data.", e);
		}
	}

	public TileEntity getTileEntity(EntityPlayer player, DataInputStream data)
	{
		int x, y, z;
		TileEntity te = null;
		try {
			x = data.readInt();
			y = data.readInt();
			z = data.readInt();
			te = ((EntityPlayer) player).worldObj.getTileEntity(x, y, z);
		} catch(IOException e) {
			AdditionalPipes.instance.logger.log(Level.SEVERE, "Error getting tileentity position from packet.", e);
		}
		return te;
	}
}
