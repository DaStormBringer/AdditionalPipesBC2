/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import buildcraft.core.inventory.ITransactor;
import buildcraft.core.inventory.Transactor;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TransportConstants;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsAdvancedInsertion extends APPipe<PipeTransportItems> {
	private static final int ICON = 8;

	public PipeItemsAdvancedInsertion(Item item) {
		super(new PipeTransportItems(), item);
	}
	
	public void eventHandler(PipeEventItem.FindDest event)
	{
		LinkedList<EnumFacing> newOris = new LinkedList<EnumFacing>();

		for (int o = 0; o < 6; ++o) 
		{
			EnumFacing orientation = EnumFacing.values()[o];
			
			//commented out during port from BC 4.2 to 6.1
			//I don't know what the equivalent to the Position argument to filterPossibleMovements() is in the new eventHandler system
			//if(orientation != pos.orientation.getOpposite())
			{
				TileEntity entity = container.getTile(orientation);
				if (entity instanceof IInventory)
				{
					if (event.item.output == orientation.getOpposite())
					{
						// continue;
					}
					ITransactor transactor = Transactor.getTransactorFor(entity);
					if (transactor.add(event.item.getItemStack(), orientation.getOpposite(), false).stackSize > 0)
					{
						newOris.add(orientation);
					}
				}
			}
		}

		if (!newOris.isEmpty())
		{
			event.destinations.clear();
			event.destinations.addAll(newOris);
		} 
		else 
		{
			
		}
	}
	
	public void readjustSpeed(TravelingItem item) 
	{
		if (item.getSpeed() > TransportConstants.PIPE_NORMAL_SPEED) {
			item.setSpeed(item.getSpeed() - TransportConstants.PIPE_NORMAL_SPEED / 2.0F);
		}
		if (item.getSpeed() < TransportConstants.PIPE_NORMAL_SPEED) {
			item.setSpeed(TransportConstants.PIPE_NORMAL_SPEED);
		}
	}

	@Override
	public int getIconIndex(EnumFacing direction)
	{
		return ICON;
	}

}
