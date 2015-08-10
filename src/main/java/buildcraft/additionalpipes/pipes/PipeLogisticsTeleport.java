/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import logisticspipes.transport.LPTravelingItem;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.tuples.LPPosition;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.api.PipeType;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.core.Position;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.events.PipeEventItem;
import buildcraft.transport.utils.TransportUtils;

public class PipeLogisticsTeleport extends PipeTeleport<PipeTransportItems> implements IPipeInformationProvider {
	private static final int ICON = 0;

	public PipeLogisticsTeleport(Item items) {
		super(new PipeTransportItems(), items, PipeType.ITEMS);
	}
	
	public void eventHandler(PipeEventItem.Entered event)
	{
		if(getWorld().isRemote) 
		{
			return;
		}
		
		List<PipeTeleport<?>> connectedTeleportPipes = TeleportManager.instance.getConnectedPipes(this, false, true);
		
		// no teleport pipes connected, use default
		if(connectedTeleportPipes.size() <= 0 || (state & 0x1) == 0) {
			return;
		}

		// output to random pipe
		LinkedList<ForgeDirection> outputOrientations = new LinkedList<ForgeDirection>();
		PipeTeleport<?> otherPipe;
		
		int originalPipeNumber = rand.nextInt(connectedTeleportPipes.size());
		int currentPipeNumber = originalPipeNumber;
		
		boolean found = false;
		int numberOfTries = 0;
		
		// find a pipe with something connected to it
		// The logic for this is... pretty complicated, actually.
		do
		{
			++numberOfTries;
			otherPipe = connectedTeleportPipes.get(currentPipeNumber);
			
			for(ForgeDirection o : ForgeDirection.VALID_DIRECTIONS)
			{
				if(otherPipe.outputOpen(o))
				{
					outputOrientations.add(o);
				}
			}
			
			// no outputs found, try again
			if(outputOrientations.size() <= 0) 
			{
				++currentPipeNumber;
				
				//loop back to the start
				if(currentPipeNumber >= connectedTeleportPipes.size())
				{
					currentPipeNumber = 0;
				}
			}
			else
			{
				found = true;
			}
		}
		while(numberOfTries < connectedTeleportPipes.size() && !found);

		//couldn't find any, so give up
		if(!found)
		{
			return;
		}

		ForgeDirection newOrientation = outputOrientations.get(rand.nextInt(outputOrientations.size()));
		TileGenericPipe destination = (TileGenericPipe) otherPipe.container.getTile(newOrientation);

		if(destination == null) {
			return;
		}
		
		Position insertPoint = new Position(destination.xCoord + 0.5, destination.yCoord + TransportUtils.getPipeFloorOf(event.item.getItemStack()), destination.zCoord + 0.5, newOrientation.getOpposite());
		insertPoint.moveForwards(0.5);
		event.item.setPosition(insertPoint.x, insertPoint.y, insertPoint.z);
		
		((PipeTransportItems) destination.pipe.transport).injectItem(event.item, newOrientation);

		Log.debug(event.item + " from " + getPosition() + " to " + otherPipe.getPosition() + " " + newOrientation);
		event.cancelled = true;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}

	//------------------------------------------------------------------------
	// IPipeInformationProvider functions
	//------------------------------------------------------------------------
	
	@Override
	public TileEntity getTile()
	{
		return container;
	}

	@Override
	public boolean isCorrect()
	{
		//what does this do??
		return true;
	}

	@Override
	public int getX()
	{
		return container.x();
	}

	@Override
	public int getY()
	{
		return container.y();

	}

	@Override
	public int getZ()
	{
		return container.z();

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
		return container.getTile(direction);
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
		return false;
	}

	@Override
	public boolean isOutputOpen(ForgeDirection direction)
	{
		if(direction == ForgeDirection.UNKNOWN || direction == getOpenOrientation())
		{
			return true;
		}
		
		return false;
	}

	@Override
	public boolean canConnect(TileEntity to, ForgeDirection direction, boolean flag)
	{
		return transport.canPipeConnect(to, direction);
	}

	@Override
	public double getDistance()
	{
		//what is this??
		return 0;
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
		return false;
	}

	@Override
	public double getDistanceTo(int destinationint, ForgeDirection ignore,
			ItemIdentifier ident, boolean isActive, double travled, double max,
			List<LPPosition> visited)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean acceptItem(LPTravelingItem item, TileEntity from)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshTileCacheOnSide(ForgeDirection side)
	{
		// TODO Auto-generated method stub
		
	}

}
