package buildcraft.additionalpipes.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageTelePipeData;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.core.lib.gui.BuildCraftContainer;

public class ContainerTeleportPipe extends BuildCraftContainer {

	public int connectedPipes = 0;

	private int ticks = 0;
	public PipeTeleport<?> pipe;
	private int freq;
	private byte state;
	private boolean isPublic;
	
	//true if the provided pipe is sending items to other pipes
	//and output locations should be shown on the ledger
	private boolean isSendingPipe;
	
	// only set on the server side
	private int originalfreq;

	public ContainerTeleportPipe(EntityPlayer player, PipeTeleport<?> pipe)
	{
		super(player, 0);
		this.pipe = pipe;

		//set these variables to invalid values so that they will be updated
		state = -1;
		isPublic = !pipe.isPublic;
		freq = -1;
		
		isSendingPipe = pipe.canSend();
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{			
			List<PipeTeleport<?>> connectedPipes = TeleportManager.instance.<PipeTeleport<?>>getConnectedPipes(pipe, !isSendingPipe, isSendingPipe);
			int[] locations = new int[connectedPipes.size() * 3];
			for(int i = 0; i < connectedPipes.size() && i < 9; i++) {
				PipeTeleport<?> connectedPipe = connectedPipes.get(i);
				locations[3 * i] = connectedPipe.container.getPos().getX();
				locations[3 * i + 1] = connectedPipe.container.getPos().getY();
				locations[3 * i + 2] = connectedPipe.container.getPos().getZ();
			}
			
			MessageTelePipeData message = new MessageTelePipeData(pipe.container.getPos(), locations, pipe.ownerUUID, pipe.ownerName);
			PacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP) player);
			
			//save the pipe's old frequency so it can be removed later
			originalfreq = pipe.getFrequency();
			
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int connectedPipesNew = connectedPipes;
		if(ticks % 20 == 0) { // reduce lag
			ticks = 0;
			Log.debug("Old connected:" + connectedPipesNew);
			connectedPipesNew = TeleportManager.instance.getConnectedPipes(pipe, !isSendingPipe, isSendingPipe).size();
			Log.debug("New connected:" + connectedPipesNew);
		}
		ticks++;
		for(Object crafter : crafters) {
			if(freq != pipe.getFrequency()) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 0, pipe.getFrequency());
			}
			if(state != pipe.state) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 1, pipe.state);
			}
			if(connectedPipesNew != connectedPipes) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 2, connectedPipesNew);
			}
			if(isPublic != pipe.isPublic) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 3, pipe.isPublic ? 1 : 0);
			}
		}
		state = pipe.state;
		freq = pipe.getFrequency();
		isPublic = pipe.isPublic;
		connectedPipes = connectedPipesNew;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			pipe.setFrequency(j);
			break;
		case 1:
			pipe.state = (byte) j;
			break;
		case 2:
			connectedPipes = j;
			break;
		case 3:
			pipe.isPublic = (j == 1);
			break;
		}
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			if(originalfreq != freq)
			{
				//remove the pipe from the old frequency
				TeleportManager.instance.remove(pipe, originalfreq);
				//re-add the pipe to the new frequency
				TeleportManager.instance.add(pipe, freq);
			}

		}
	}

}
