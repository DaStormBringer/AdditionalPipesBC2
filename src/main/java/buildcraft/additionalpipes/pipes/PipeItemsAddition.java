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
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.utils.InventoryUtils;
import buildcraft.core.inventory.ITransactor;
import buildcraft.core.inventory.Transactor;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TransportConstants;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsAddition extends APPipe<PipeTransportItems> {
	private static final int ICON = 8;

	public PipeItemsAddition(Item item) {
		super(new PipeTransportItems(), item);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}
	
	public void eventHandler(PipeEventItem.FindDest event)
	{
		LinkedList<ForgeDirection> newOris = new LinkedList<ForgeDirection>();

		for (int o = 0; o < 6; ++o) 
		{
			ForgeDirection orientation = ForgeDirection.VALID_DIRECTIONS[o];
			
			//commented out during port from BC 4.2 to 6.1
			//I don't know what the equivalent to the Position argument to filterPossibleMovements() is in the new eventHandler system
			//if(orientation != pos.orientation.getOpposite())
			{
				TileEntity entity = container.getTile(orientation);
				if (entity instanceof IInventory)
				{
					IInventory inventory = (IInventory)entity;
					
					if(InventoryUtils.containsItem(true, false, event.item.getItemStack(), inventory))
					{
						ITransactor transactor = Transactor.getTransactorFor(entity);
						if (transactor.add(event.item.getItemStack(), orientation.getOpposite(), false).stackSize > 0)
						{
							newOris.add(orientation);
						}
					}
					else
					{
						event.destinations.remove(orientation);
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

}
