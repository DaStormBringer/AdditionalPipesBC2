package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

public class ContainerPriorityInsertionPipe extends Container {
	private PipeItemsPriorityInsertion pipe;
	public int[] lastPriorityData;

	public ContainerPriorityInsertionPipe(PipeItemsPriorityInsertion pipe) {
		this.pipe = pipe;
		lastPriorityData = new int[pipe.sidePriorities.length];
		for(int i = 0; i < lastPriorityData.length; i++) {
			lastPriorityData[i] = -1;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		TilePipeHolder tile = (TilePipeHolder) pipe.pipe.getHolder();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}

	@Override
	public void detectAndSendChanges() 
	{
		super.detectAndSendChanges();
		for(IContainerListener crafter : listeners) 
		{
			for(int i = 0; i < lastPriorityData.length; i++) {
				if(lastPriorityData[i] != pipe.sidePriorities[i]) {
					crafter.sendProgressBarUpdate(this, i, pipe.sidePriorities[i]);
				}
			}
		}
		for(int i = 0; i < lastPriorityData.length; i++) {
			lastPriorityData[i] = pipe.sidePriorities[i];
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if(i >= 0 && i < pipe.sidePriorities.length) {
			pipe.sidePriorities[i] = j;
		}
	}
}
