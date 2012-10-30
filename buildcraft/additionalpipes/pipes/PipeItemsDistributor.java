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
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicDistributor;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;

public class PipeItemsDistributor extends Pipe implements IPipeTransportItemsHook {

	public PipeItemsDistributor(int itemID) {
		super(new PipeTransportItems(), new PipeLogicDistributor(), itemID);
	}

	@Override
	public int getTextureIndex(Orientations connection) {
		switch (connection) {
		case YNeg:
			return 10;
		case YPos:
			return 11;
		case ZNeg:
			return 12;
		case ZPos:
			return 13;
		case XNeg:
			return 14;
		case XPos:
		default:
			return 9;
		}
	}

	@Override
	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, IPipedItem item) {
		PipeLogicDistributor pipeLogic = (PipeLogicDistributor) logic;

		LinkedList<Orientations> result = new LinkedList<Orientations>();

		if (pipeLogic.curTick >= pipeLogic.distData[pipeLogic.distSide]) {
			nextOpenValidInventory();
		}

		for (int o = 0; o < 6; ++o) {
			Orientations orientation = Orientations.values()[o];
			if (!(item.getPosition().orientation == orientation.reverse())) {
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
			Orientations orientation = Orientations.values()[o];
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
	public void entityEntered(IPipedItem item, Orientations orientation) {
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
	}

	@Override
	public String getTextureFile() {
		return AdditionalPipes.TEXTURE_PIPES;
	}

}
