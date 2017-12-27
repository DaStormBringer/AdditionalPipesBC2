package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerDistributionPipe extends Container {
	private PipeItemsDistributor pipe;
	
	public ContainerDistributionPipe(PipeItemsDistributor container) {
		pipe = (PipeItemsDistributor) container.pipe;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		TilePipeHolder tile = (TilePipeHolder) pipe.pipe.getHolder();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}
}
