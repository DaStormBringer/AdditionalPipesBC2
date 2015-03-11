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

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.api.core.Position;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.events.PipeEventItem;
import buildcraft.transport.utils.TransportUtils;

public class PipeItemsTeleport extends PipeTeleport<PipeTransportItems> {
	private static final int ICON = 0;

	public PipeItemsTeleport(Item items) {
		super(new PipeTransportItems(), items);
	}
	
	public void eventHandler(PipeEventItem.Entered event)
	{
		/*if(!AdditionalPipes.proxy.isServer(getWorld())) {
			return;
		}*/
		
		List<PipeTeleport<?>> connectedTeleportPipes = TeleportManager.instance.getConnectedPipes(this, false);
		// no teleport pipes connected, use default
		if(connectedTeleportPipes.size() <= 0 || (state & 0x1) == 0) {
			return;
		}

		// output to random pipe
		LinkedList<EnumFacing> outputOrientations = new LinkedList<EnumFacing>();
		PipeTeleport<?> otherPipe = connectedTeleportPipes.get(rand.nextInt(connectedTeleportPipes.size()));

		// find possible output orientations
		for(EnumFacing o : EnumFacing.values()) {
			if(otherPipe.outputOpen(o))
				outputOrientations.add(o);
		}
		// no outputs found, default behaviour
		if(outputOrientations.size() <= 0) {
			return;
		}

		EnumFacing newOrientation = outputOrientations.get(rand.nextInt(outputOrientations.size()));
		TileGenericPipe destination = (TileGenericPipe) otherPipe.container.getTile(newOrientation);

		if(destination == null) {
			return;
		}
		
		Position insertPoint = new Position(destination.getPos().getX() + 0.5, destination.getPos().getY() + TransportUtils.getPipeFloorOf(event.item.getItemStack()), destination.getPos().getZ() + 0.5, newOrientation.getOpposite());
		insertPoint.moveForwards(0.5);
		event.item.setPosition(insertPoint.x, insertPoint.y, insertPoint.z);
		
		((PipeTransportItems) destination.pipe.transport).injectItem(event.item, newOrientation);

		AdditionalPipes.instance.logger.info(event.item + " from " + getPosition() + " to " + otherPipe.getPosition() + " " + newOrientation);
		event.cancelled = true;
	}

	@Override
	public int getIconIndex(EnumFacing direction) {
		return ICON;
	}

}
