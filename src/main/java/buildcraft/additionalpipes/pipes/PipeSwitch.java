package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public class PipeSwitch<pipeType extends PipeTransport> extends APPipe<pipeType> {

	private final int textureIndex;

	public PipeSwitch(pipeType transport, Item item, int textureIndex) {
		super(transport, item);
		this.textureIndex = textureIndex;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		
		if(direction == ForgeDirection.UNKNOWN)
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
		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = container.getTile(direction);
			if(tile instanceof TileGenericPipe) {
				((TileGenericPipe) tile).scheduleNeighborChange();
			}
		}
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		if(container == null) return false;
		World world = getWorld();
		return world != null && super.canPipeConnect(tile, side) && !world.isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord, container.zCoord);

	}

}
