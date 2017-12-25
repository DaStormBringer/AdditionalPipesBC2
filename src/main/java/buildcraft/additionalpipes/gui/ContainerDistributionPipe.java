package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

public class ContainerDistributionPipe extends Container {
	private PipeItemsDistributor pipe;
	public int[] lastDistData;

	public ContainerDistributionPipe(PipeItemsDistributor container) {
		pipe = (PipeItemsDistributor) container.pipe;
		lastDistData = new int[pipe.distData.length];
		for(int i = 0; i < lastDistData.length; i++) {
			lastDistData[i] = -1;
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
