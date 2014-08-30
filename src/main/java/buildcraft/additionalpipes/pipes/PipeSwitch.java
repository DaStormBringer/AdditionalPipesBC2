package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public class PipeSwitch extends APPipe {

	private final int textureIndex;

	public PipeSwitch(PipeTransport transport, Item item, int textureIndex) {
		super(transport, item);
		this.textureIndex = textureIndex;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return textureIndex + (canPipeConnect(null, direction) ? 0 : 1);
	}

	@Override
	public boolean canConnectRedstone() {
		return true;
	}

	@Override
	public void initialize() {
		super.initialize();
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
		return world != null && !world.isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord, container.zCoord);
	}

}
