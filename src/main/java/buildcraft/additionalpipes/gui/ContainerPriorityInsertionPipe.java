package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.transport.TileGenericPipe;

public class ContainerPriorityInsertionPipe extends Container {
	private PipeItemsPriorityInsertion pipe;
	public int[] lastPriorityData;

	public ContainerPriorityInsertionPipe(TileGenericPipe container) {
		pipe = (PipeItemsPriorityInsertion) container.pipe;
		lastPriorityData = new int[pipe.sidePriorities.length];
		for(int i = 0; i < lastPriorityData.length; i++) {
			lastPriorityData[i] = -1;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		TileGenericPipe tile = pipe.container;
		if(tile.getWorldObj().getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord) != tile) return false;
		if(player.getDistanceSq(tile.xCoord + 0.5D, tile.yCoord + 0.5D, tile.zCoord + 0.5D) > 64) return false;
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(Object obj : crafters) {
			ICrafting crafter = (ICrafting) obj;
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
