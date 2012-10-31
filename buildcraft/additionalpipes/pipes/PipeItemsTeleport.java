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
import java.util.Random;

import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;

public class PipeItemsTeleport extends PipeTeleport implements IPipeTransportItemsHook {

	LinkedList <Integer> idsToRemove = new LinkedList <Integer>();

	public PipeItemsTeleport(int itemID) {
		super(new PipeTransportItems(), new PipeLogicTeleport(), itemID);
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
		((PipeTransportItems) transport).defaultReajustSpeed(item);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		for (int id : idsToRemove) {
			((PipeTransportItems) transport).travelingEntities.remove(id);
		}
		idsToRemove.clear();
	}


	@Override
	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, IPipedItem item) {

		List<PipeTeleport> connectedPipes = TeleportManager.instance.getConnectedPipes(this, false);
		LinkedList<Orientations> result = new LinkedList<Orientations>();

		//no teleport pipes connected, use default
		if (connectedPipes.size() < 1) {
			return possibleOrientations;
		}

		Random pipeRand = new Random();
		int i = pipeRand.nextInt(connectedPipes.size());

		LinkedList<Orientations> temp = new LinkedList<Orientations>();

		Position pos1 = connectedPipes.get(i).getPosition();

		for (int o = 0; o < 6; ++o) {
			if (Orientations.values()[o] != pos1.orientation.reverse()
					&& container.pipe.outputOpen(Orientations.values()[o])) {
				if (((PipeTransportItems)transport).canReceivePipeObjects(Orientations.values()[o], item)) {
					temp.add(Orientations.values()[o]);
				}
			}
		}

		////System.out.println("Temp: " + Temp.size());
		if (temp.size() <= 0) {
			result.add(pos.orientation.reverse());
			return result;
		}

		Orientations newPos = temp.get(worldObj.rand.nextInt(temp.size()));
		////System.out.println(newPos.toString());
		Position destPos = new Position(connectedPipes.get(i).xCoord, connectedPipes.get(i).yCoord, connectedPipes.get(i).zCoord, newPos);
		destPos.moveForwards(1.0);

		TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);

		if (tile instanceof TileGenericPipe) {
			TileGenericPipe pipe = (TileGenericPipe)tile;
			if (pipe.pipe.transport instanceof PipeTransportItems) {
				//This pipe can actually receive items
				idsToRemove.add(item.getEntityId());
				((PipeTransportItems) transport).scheduleRemoval(item);
				Position newItemPos = getNewItemPos(destPos, newPos, Utils.getPipeFloorOf(item.getItemStack()));
				item.setPosition(newItemPos.x, newItemPos.y, newItemPos.z);
				((PipeTransportItems)pipe.pipe.transport).entityEntering(item, newPos);
			}
		}
		else if (tile instanceof IPipeEntry) {
			idsToRemove.add(item.getEntityId());
			((PipeTransportItems) transport).scheduleRemoval(item);
			Position newItemPos = getNewItemPos(destPos, newPos, Utils.getPipeFloorOf(item.getItemStack()));
			item.setPosition(newItemPos.x, newItemPos.y, newItemPos.z);
			((IPipeEntry) tile).entityEntering(item, newPos);
		}
		else if (tile instanceof IInventory) {
			TransactorSimple transactor = new TransactorSimple((IInventory) tile);
			if (AdditionalPipes.proxy.isServer(worldObj)) {
				if (transactor.add(item.getItemStack(), destPos.orientation.reverse(), true).stackSize == 0) {
					idsToRemove.add(item.getEntityId());
					((PipeTransportItems) transport).scheduleRemoval(item);
					// Do nothing, we're adding the object to the world
				}
			}
		}

		result.add(newPos);

		return result;
	}

	public Position getNewItemPos(Position Old, Orientations newPos, float f) {
		//Utils.getPipeFloorOf(data.item.item)
		double x = Old.x;
		double y = Old.y;
		double z = Old.z;

		if (newPos == Orientations.XNeg) {
			x += 1;
			y += .5;
			z += .5;
		}
		else if (newPos == Orientations.XPos) {
			//x += .6;
			y += f;
			z += .5;
		}
		else if (newPos == Orientations.YNeg) {
			x += .5;
			y += 1;
			z += .5;
		}
		else if (newPos == Orientations.YPos) {
			x += .5;
			//y += .6;
			z += .5;
		}
		else if (newPos == Orientations.ZNeg) {
			x += .5;
			y += f;
			z += 1;
		}
		else if (newPos == Orientations.ZPos) {
			x += .5;
			y += f;
			//z += .6;
		}

		return new Position(x, y, z);
	}


	@Override
	public void entityEntered(IPipedItem item, Orientations orientation) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}

}
