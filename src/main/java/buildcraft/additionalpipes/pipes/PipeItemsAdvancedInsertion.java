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
import buildcraft.api.core.Position;
import buildcraft.core.inventory.ITransactor;
import buildcraft.core.inventory.Transactor;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TransportConstants;
import buildcraft.transport.TravelingItem;

public class PipeItemsAdvancedInsertion extends APPipe {
	private static final int ICON = 8;

	public PipeItemsAdvancedInsertion(Item item) {
		super(new PipeTransportItems(), item);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}

	public LinkedList<ForgeDirection> filterPossibleMovements(
			LinkedList<ForgeDirection> possibleOrientations, Position pos,
			TravelingItem item) {
		LinkedList<ForgeDirection> newOris = new LinkedList<ForgeDirection>();

		for (int o = 0; o < 6; ++o) {
			ForgeDirection orientation = ForgeDirection.VALID_DIRECTIONS[o];
			if (orientation != pos.orientation.getOpposite()) {
				TileEntity entity = container.getTile(orientation);
				if (entity instanceof IInventory) {
					if (item.output == orientation
							.getOpposite()) {
						// continue;
					}
					ITransactor transactor = Transactor
							.getTransactorFor(entity);
					if (transactor.add(item.getItemStack(),
							orientation.getOpposite(), false).stackSize > 0) {
						newOris.add(orientation);
					}
				}
			}
		}
		if (newOris.size() > 0) {
			return newOris;
		}

		return possibleOrientations;
	}
	
	public void readjustSpeed(TravelingItem item) {
		if (item.getSpeed() > TransportConstants.PIPE_NORMAL_SPEED) {
			item.setSpeed(item.getSpeed() - TransportConstants.PIPE_NORMAL_SPEED / 2.0F);
		}
		if (item.getSpeed() < TransportConstants.PIPE_NORMAL_SPEED) {
			item.setSpeed(TransportConstants.PIPE_NORMAL_SPEED);
		}
	}

}
