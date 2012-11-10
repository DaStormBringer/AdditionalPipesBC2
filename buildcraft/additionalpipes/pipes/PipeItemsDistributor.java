/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;

import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.pipes.logic.PipeLogicDistributor;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;

public class PipeItemsDistributor extends APPipe implements IPipeTransportItemsHook {

	public PipeItemsDistributor(int itemID) {
		super(new PipeTransportItems(), new PipeLogicDistributor(), itemID);
	}

	@Override
	public int getTextureIndex(ForgeDirection connection) {
		switch (connection) {
		case DOWN:
			return 10;
		case UP:
			return 11;
		case NORTH: //-z
			return 12;
		case SOUTH: //+z
			return 13;
		case WEST: //-x
			return 14;
		case EAST: //+x
		default:
			return 9;
		}
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(LinkedList<ForgeDirection> possibleOrientations, Position pos, IPipedItem item) {
		PipeLogicDistributor pipeLogic = (PipeLogicDistributor) logic;

		LinkedList<ForgeDirection> result = new LinkedList<ForgeDirection>();

		if (pipeLogic.curTick >= pipeLogic.distData[pipeLogic.distSide]) {
			nextOpenValidInventory();
		}

		for (ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS) {
			if (!(item.getPosition().orientation == orientation.getOpposite())) {
				if (canReceivePipeObjects(container.getTile(orientation))) {
					result.add(orientation);
				}
			}
		}

		pipeLogic.curTick += item.getItemStack().stackSize;

		return result;
	}

	private void nextOpenValidInventory() {
		PipeLogicDistributor pipeLogic = (PipeLogicDistributor) logic;
		pipeLogic.curTick = 0;
		for (int o = 0; o < 6; ++o) {
			pipeLogic.distSide = (pipeLogic.distSide + 1) % pipeLogic.distData.length;
			ForgeDirection orientation = ForgeDirection.values()[o];
			if (pipeLogic.distData[pipeLogic.distSide] > 0 &&
					canReceivePipeObjects(container.getTile(orientation))) {
				break;
			}
		}
	}

	public boolean canReceivePipeObjects(TileEntity entity) {
		if (!Utils.checkPipesConnections(container, entity)) {
			return false;
		}
		if (entity instanceof TileGenericPipe) {
			TileGenericPipe pipe = (TileGenericPipe) entity;
			return pipe.pipe.transport instanceof PipeTransportItems;
		}
		if (entity instanceof IInventory) {
			return true;
		}
		return false;
	}

	@Override
	public void entityEntered(IPipedItem item, ForgeDirection orientation) {
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
	}

}
