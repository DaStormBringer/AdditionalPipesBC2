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
import buildcraft.api.transport.IPipeEntry;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.core.utils.Utils;
import buildcraft.energy.TileEngine;
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
		return 10;
	}

	@Override
	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, IPipedItem item) {

		PipeLogicDistributor pipeLogic = (PipeLogicDistributor) logic;

		((PipeLogicDistributor)logic).switchIfNeeded();

		LinkedList<Orientations> result = new LinkedList<Orientations>();

		for (int o = 0; o < 6; ++o) {
			if (container.pipe.outputOpen(Orientations.values()[o])) {
				Position newPos = new Position(pos);
				newPos.orientation = Orientations.values()[o];
				newPos.moveForwards(1.0);

				if (canReceivePipeObjects(newPos, item)) {
					result.add(newPos.orientation);
				}
			}
		}

		pipeLogic.curTick++;

		if (pipeLogic.curTick >= pipeLogic.distData[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)]) {
			((PipeLogicDistributor)logic).switchPosition();
			pipeLogic.curTick = 0;
		}


		worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
		return result;
	}

	public boolean canReceivePipeObjects(Position p,
			IPipedItem item) {
		TileEntity entity = worldObj.getBlockTileEntity((int) p.x, (int) p.y,
				(int) p.z);

		if (!Utils.checkLegacyPipesConnections(worldObj, (int) p.x, (int) p.y,
				(int) p.z, xCoord, yCoord, zCoord)) {
			return false;
		}

		if (entity instanceof IPipeEntry) {
			return true;
		}
		else if (entity instanceof TileEngine) {
			return false;
		}
		else if (entity instanceof TileGenericPipe) {
			TileGenericPipe pipe = (TileGenericPipe) entity;
			return pipe.pipe.transport instanceof PipeTransportItems;
		}
		else if (entity instanceof IInventory) {
			TransactorSimple transactor = new TransactorSimple((IInventory) entity);
			if (transactor.add(item.getItemStack(), p.orientation.reverse(), false).stackSize == 0) {
				return true;
			}
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
