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
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.Position;
import buildcraft.core.inventory.ITransactor;
import buildcraft.core.inventory.Transactor;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;

public class PipeItemsAdvancedInsertion extends APPipe implements
		IPipeTransportItemsHook {

	public PipeItemsAdvancedInsertion(int itemID) {
		super(new PipeTransportItems(), itemID);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return 8;
	}

	@Override
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

	@Override
	public void entityEntered(TravelingItem item, ForgeDirection orientation) {
	}

	@Override
	public void readjustSpeed(TravelingItem item) {
		if (item.getSpeed() > Utils.pipeNormalSpeed) {
			item.setSpeed(item.getSpeed() - Utils.pipeNormalSpeed / 2.0F);
		}
		if (item.getSpeed() < Utils.pipeNormalSpeed) {
			item.setSpeed(Utils.pipeNormalSpeed);
		}
	}

}
