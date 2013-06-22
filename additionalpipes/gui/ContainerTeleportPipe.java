package buildcraft.additionalpipes.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketNBTTagData;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.core.gui.BuildCraftContainer;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ContainerTeleportPipe extends BuildCraftContainer {

	public int connectedPipes = 0;

	private int ticks = 0;
	private PipeTeleport pipe;
	private int freq;
	private byte state;
	private boolean isPublic;

	public ContainerTeleportPipe(EntityPlayer player, TileGenericPipe tile) {
		super(0);
		pipe = (PipeTeleport) tile.pipe;
		state = -1;
		isPublic = !pipe.logic.isPublic;
		freq = -1;

		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", NetworkHandler.TELE_PIPE_DATA);
		tag.setInteger("xCoord", pipe.xCoord);
		tag.setInteger("yCoord", pipe.yCoord);
		tag.setInteger("zCoord", pipe.zCoord);
		tag.setString("owner", pipe.logic.owner);

		List<PipeTeleport> pipes = TeleportManager.instance.getConnectedPipes(pipe, false);
		int[] locations = new int[pipes.size() * 3];
		for(int i = 0; i < pipes.size() && i < 9; i++) {
			Pipe pipe = pipes.get(i);
			locations[3 * i] = pipe.xCoord;
			locations[3 * i + 1] = pipe.yCoord;
			locations[3 * i + 2] = pipe.zCoord;
		}
		tag.setIntArray("network", locations);

		PacketNBTTagData packet = new PacketNBTTagData(AdditionalPipes.CHANNELNBT, NetworkHandler.TELE_PIPE_DATA, false, tag);
		PacketDispatcher.sendPacketToPlayer(packet.getPacket(), (Player) player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int connectedPipesNew = connectedPipes;
		if(ticks % 40 == 0) { // reduce lag
			ticks = 0;
			AdditionalPipes.instance.logger.info("Old connected:" + connectedPipesNew);
			connectedPipesNew = TeleportManager.instance.getConnectedPipes(pipe, false).size();
			AdditionalPipes.instance.logger.info("New connected:" + connectedPipesNew);
		}
		ticks++;
		for(Object crafter : crafters) {
			if(freq != pipe.logic.getFrequency()) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 0, pipe.logic.getFrequency());
			}
			if(state != pipe.logic.state) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 1, pipe.logic.state);
			}
			if(connectedPipesNew != connectedPipes) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 2, connectedPipesNew);
			}
			if(isPublic != pipe.logic.isPublic) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 3, pipe.logic.isPublic ? 1 : 0);
			}
		}
		state = pipe.logic.state;
		freq = pipe.logic.getFrequency();
		isPublic = pipe.logic.isPublic;
		connectedPipes = connectedPipesNew;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			pipe.logic.setFrequency(j);
			break;
		case 1:
			pipe.logic.state = (byte) j;
			break;
		case 2:
			connectedPipes = j;
			break;
		case 3:
			pipe.logic.isPublic = (j == 1);
			break;
		}
	}

}
