package buildcraft.additionalpipes.pipes;

import java.util.List;

import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.proxy.buildcraft.LPRoutedBCTravelingItem;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import logisticspipes.transport.LPTravelingItem;
import logisticspipes.transport.LPTravelingItem.LPTravelingItemServer;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.tuples.LPPosition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.core.CoreConstants;
import buildcraft.core.lib.TileBuffer;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.TravelingItem;

/**
 * Adapter which provides Logistics Pipes information about the Logistics Teleport Pipe
 * @author Jamie
 *
 */
public class PipeTeleportInformationProvider implements IPipeInformationProvider
{

	public PipeLogisticsTeleport pipe;
	
	
	public PipeTeleportInformationProvider(PipeLogisticsTeleport pipe)
	{
		this.pipe = pipe;
	}

	@Override
	public boolean isCorrect()
	{
		return BlockGenericPipe.isValid(pipe);
	}

	@Override
	public int getX()
	{
		return pipe.container.x();
	}

	@Override
	public int getY()
	{
		return pipe.container.y();

	}

	@Override
	public int getZ()
	{
		return pipe.container.z();

	}

	@Override
	public boolean isRouterInitialized()
	{
		return false;
	}

	@Override
	public boolean isRoutingPipe()
	{
		return false;
	}

	@Override
	public CoreRoutedPipe getRoutingPipe()
	{
		return null;
	}

	@Override
	public TileEntity getTile(ForgeDirection direction)
	{
		return pipe.container.getTile(direction);
	}

	@Override
	public boolean isFirewallPipe()
	{
		return false;
	}

	@Override
	public IFilter getFirewallFilter()
	{
		return null;
	}

	@Override
	public boolean divideNetwork()
	{
		return false;
	}

	@Override
	public boolean powerOnly()
	{
		return false;
	}

	@Override
	public boolean isOnewayPipe()
	{
		return pipe.canReceive() ^ pipe.canSend();
	}

	@Override
	public boolean isOutputOpen(ForgeDirection direction)
	{
		if(direction == pipe.getOpenOrientation())
		{
			return true;
		}
		
		if(direction == ForgeDirection.UNKNOWN && pipe.getConnectedPipe() != null)
		{
			return true;
		}
		
		return false;
	}

	@Override
	public boolean canConnect(TileEntity to, ForgeDirection direction, boolean flag)
	{
		return pipe.transport.canPipeConnect(to, direction);
	}

	@Override
	public double getDistance()
	{
		//what is this??
		return 1;
	}

	@Override
	public boolean isItemPipe()
	{
		return true;
	}

	@Override
	public boolean isFluidPipe()
	{
		return false;
	}

	@Override
	public boolean isPowerPipe()
	{
		return true;
	}

	@Override
	public double getDistanceTo(int destinationint, ForgeDirection ignore, ItemIdentifier ident, boolean isActive, double traveled, double max, List<LPPosition> visited) 
	{
		if (traveled >= max) {
			return Integer.MAX_VALUE;
		}
		PipeLogisticsTeleport connectedPipe = pipe.getConnectedPipe();
		if(connectedPipe != null)
		{
			LPPosition start = new LPPosition(pipe.container);
			LPPosition end = new LPPosition(connectedPipe.container);
			return start.distanceTo(end);
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean acceptItem(LPTravelingItem item, TileEntity from)
	{
		if (BlockGenericPipe.isValid(pipe) || pipe.canSend()) {
			TravelingItem bcItem = null;
			if (item instanceof LPTravelingItemServer) {
				LPRoutedBCTravelingItem lpBCItem = new LPRoutedBCTravelingItem();
				lpBCItem.setRoutingInformation(((LPTravelingItemServer) item).getInfo());
				lpBCItem.saveToExtraNBTData();
				bcItem = lpBCItem;
			} else {
				return true;
			}
			LPPosition p = new LPPosition(getX() + 0.5F, getY() + CoreConstants.PIPE_MIN_POS, getZ() + 0.5F);
			if (item.output.getOpposite() == ForgeDirection.DOWN) {
				p.moveForward(item.output.getOpposite(), 0.24F);
			} else if (item.output.getOpposite() == ForgeDirection.UP) {
				p.moveForward(item.output.getOpposite(), 0.74F);
			} else {
				p.moveForward(item.output.getOpposite(), 0.49F);
			}
			bcItem.setPosition(p.getXD(), p.getYD(), p.getZD());
			bcItem.setSpeed(item.getSpeed());
			if (item.getItemIdentifierStack() != null) {
				bcItem.setItemStack(item.getItemIdentifierStack().makeNormalStack());
			}
			pipe.transport.injectItem(bcItem, item.output);
			return true;
		}
		return false;
	}

	@Override
	public void refreshTileCacheOnSide(ForgeDirection side) {
		TileBuffer[] cache = pipe.container.getTileCache();
		if (cache != null) {
			cache[side.ordinal()].refresh();
		}
	}

	@Override
	public World getWorld()
	{
		return pipe.getWorld();
	}

	@Override
	public TileEntity getTile()
	{
		return pipe.container;
	}
}
