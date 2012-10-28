/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.Random;

import buildcraft.additionalpipes.mod_AdditionalPipes;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.EntityPassiveItem;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.PipeLogicStone;

import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;

public class PipeItemsAdvancedInsertion extends Pipe implements IPipeTransportItemsHook {

	public PipeItemsAdvancedInsertion(int itemID) {
		super(new PipeTransportItems(), new PipeLogicStone (), itemID);

	}

	@Override
	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, IPipedItem item) {
		return filterPossibleMovements(possibleOrientations, pos, item, 0);
	}


	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, IPipedItem item, int Level) {
		LinkedList<Orientations> newOris = new LinkedList<Orientations>();
		LinkedList<Orientations> nullReturn = new LinkedList<Orientations>();
		nullReturn.add(Orientations.values()[0]);

		for (int o = 0; o < 6; ++o) {
			if (Orientations.values()[o] != pos.orientation.reverse()) {
				Position newPos = new Position(pos);
				newPos.orientation = Orientations.values()[o];
				newPos.moveForwards(1.0);

				TileEntity entity = worldObj.getBlockTileEntity((int) newPos.x, (int) newPos.y, (int) newPos.z);

				if (entity instanceof IInventory) {
					if (new StackUtil(item.getItemStack()).checkAvailableSlot((IInventory) entity, false, newPos.orientation.reverse())) {
						newOris.add(newPos.orientation);
					}
				}
			}
		}


		//System.out.println("NewOris Size: " + newOris.size() + " - PO Size: " + possibleOrientations.size() + " - Level: " + Level);
		if (newOris.size() > 0) {
			Position destPos =  new Position(pos.x, pos.y, pos.z, newOris.get( (new Random()) .nextInt(newOris.size()) ) );
			destPos.moveForwards(1.0);
			StackUtil utils = new StackUtil(item.getItemStack());
			TileEntity tile = worldObj.getBlockTileEntity((int) destPos.x, (int) destPos.y, (int) destPos.z);

			if (!APIProxy.isClient(worldObj)) {
				if (utils.checkAvailableSlot((IInventory) tile, true, destPos.orientation.reverse()) && utils.items.stackSize == 0) {
					item.remove();
					((PipeTransportItems) this.transport).scheduleRemoval(item);
				}
				else {
					item.setItemStack(utils.items);
					return this.filterPossibleMovements(possibleOrientations, pos, item, (Level + 1));
					//EntityItem dropped = item.toEntityItem(destPos.orientation);
				}
			}

			//System.out.println("Insertion Output 2 : " + destPos.orientation);
			return nullReturn;
		}

		if (Level == 0) {
			return possibleOrientations;
		}

		return ((PipeTransportItems)this.transport).getPossibleMovements(pos, item);
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
		if (item.getSpeed() > Utils.pipeNormalSpeed) {
			item.setSpeed(item.getSpeed() - Utils.pipeNormalSpeed / 2.0F);
		}
		if (item.getSpeed() < Utils.pipeNormalSpeed) {
			item.setSpeed(Utils.pipeNormalSpeed);
		}
	}

	@Override
	public String getTextureFile() {
		return mod_AdditionalPipes.DEFUALT_Insertion_FILE;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}

	@Override
	public void entityEntered(IPipedItem item, Orientations orientation) {		
	}

}
