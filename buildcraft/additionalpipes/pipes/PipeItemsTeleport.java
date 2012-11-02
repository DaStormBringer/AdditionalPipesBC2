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

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;

public class PipeItemsTeleport extends PipeTeleport implements IPipeTransportItemsHook {

	private PipeTransportItems transport;

	public PipeItemsTeleport(int itemID) {
		super(new PipeTransportItems(), new PipeLogicTeleport(), itemID);
		transport = (PipeTransportItems) super.transport;
	}

	@Override
	public void readjustSpeed(IPipedItem item) {
		transport.defaultReajustSpeed(item);
	}

	@Override
	public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, IPipedItem item) {
		return possibleOrientations;
	}

	@Override
	public void entityEntered(IPipedItem item, Orientations orientation) {
		List<PipeTeleport> connectedTeleportPipes = TeleportManager.instance.getConnectedPipes(this, false);
		//no teleport pipes connected, use default
		if (connectedTeleportPipes.size() <= 0) {
			return;
		}

		//output to random pipe
		LinkedList<Orientations> outputOrientations = new LinkedList<Orientations>();
		PipeTeleport otherPipe = connectedTeleportPipes.get(
				rand.nextInt(connectedTeleportPipes.size()));

		//find possible output orientations
		for (int o = 0; o < 6; o++) {
			if (otherPipe.outputOpen(Orientations.values()[o]))
				outputOrientations.add(Orientations.values()[o]);
		}
		//no outputs found, default behaviour
		if (outputOrientations.size() <= 0) {
			return;
		}

		Orientations newOrientation = outputOrientations.get(rand.nextInt(outputOrientations.size()));
		TileGenericPipe destination = (TileGenericPipe) otherPipe.container.getTile(newOrientation);
		item.setContainer(destination);
		item.setPosition(destination.xCoord + 0.5, destination.yCoord, destination.zCoord + 0.5);
		destination.pipe.transport.entityEntering(item, newOrientation);
		transport.scheduleRemoval(item);
		AdditionalPipes.instance.logger.info("Teleported item from " + getPosition() + " to " + item.getPosition());
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}

}
