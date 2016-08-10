package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public class PipeSwitch<pipeType extends PipeTransport> extends APPipe<pipeType> {

	private final int textureIndex;

	public PipeSwitch(pipeType transport, Item item, int textureIndex) {
		super(transport, item);
		this.textureIndex = textureIndex;
	}

	@Override
	public int getIconIndex(EnumFacing direction)
	{
		if(direction == null)
		{
			return textureIndex;
		}
		
		return textureIndex + (canPipeConnect(container.getNeighborTile(direction), direction) ? 0 : 1);
	}

	@Override
	public boolean canConnectRedstone() {
		return true;
	}

	@Override
	public void onNeighborBlockChange(int blockId) {
		super.onNeighborBlockChange(blockId);
		container.scheduleNeighborChange();
		for(EnumFacing direction : EnumFacing.values()) {
			TileEntity tile = container.getTile(direction);
			if(tile instanceof TileGenericPipe) {
				((TileGenericPipe) tile).scheduleNeighborChange();
			}
		}
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
		if(container == null && side == null) return false;
		World world = getWorld();
		return world != null && super.canPipeConnect(tile, side) && !world.isBlockPowered(container.getPos());

	}

}
