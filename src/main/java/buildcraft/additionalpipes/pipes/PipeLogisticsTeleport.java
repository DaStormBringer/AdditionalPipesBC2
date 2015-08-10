/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.api.PipeType;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.core.Position;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.events.PipeEventItem;
import buildcraft.transport.utils.TransportUtils;

public class PipeLogisticsTeleport extends PipeTeleport<PipeTransportItems>  {
	private static final int ICON = 0;

	public PipeLogisticsTeleport(Item items) {
		super(new PipeTransportItemsLogistics(), items, PipeType.ITEMS);
	}
	
	public void eventHandler(PipeEventItem.Entered event)
	{
		if(getWorld().isRemote) 
		{
			return;
		}
		
		PipeLogisticsTeleport otherPipe = getConnectedPipe();
		
		// cannot teleport, use default
		if(otherPipe == null || !canSend()) {
			return;
		}

		ForgeDirection newOrientation = otherPipe.getOpenOrientation();
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

	@SuppressWarnings("unchecked")
	public PipeLogisticsTeleport getConnectedPipe()
	{
		List<PipeLogisticsTeleport> connectedPipes = (List<PipeLogisticsTeleport>)((List<?>)TeleportManager.instance.getConnectedPipes(this, true, true));
		if(connectedPipes.size() == 0)
		{
			return null;
		}
		else if(connectedPipes.size() > 1)
		{
			Log.unexpected("This Logistics Teleport Pipe has more than one other pipe on its channel.  Somewhere, somebody messed up!");
			return null;
		}
		
		return connectedPipes.get(0);
	}


}
