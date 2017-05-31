package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.transport.TileGenericPipe;

public class ContainerDistributionPipe extends Container {
	private PipeItemsDistributor pipe;
	public int[] lastDistData;

	public ContainerDistributionPipe(TileGenericPipe container) {
		pipe = (PipeItemsDistributor) container.pipe;
		lastDistData = new int[pipe.distData.length];
		for(int i = 0; i < lastDistData.length; i++) {
			lastDistData[i] = -1;
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
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(Object obj : crafters) {
			ICrafting crafter = (ICrafting) obj;
			for(int i = 0; i < lastDistData.length; i++) {
				if(lastDistData[i] != pipe.distData[i]) {
					crafter.sendProgressBarUpdate(this, i, pipe.distData[i]);
				}
			}
		}
		for(int i = 0; i < lastDistData.length; i++) {
			lastDistData[i] = pipe.distData[i];
		}
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if(i >= 0 && i < pipe.distData.length) {
			pipe.distData[i] = j;
		}
	}
}
