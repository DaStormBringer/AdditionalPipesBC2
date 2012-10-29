package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Position;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public abstract class PipeTeleport extends APPipe {

	public final PipeLogicTeleport logic;

	public static List<PipeTeleport> teleportPipes = new LinkedList<PipeTeleport>();

	public PipeTeleport(PipeTransport transport, PipeLogicTeleport logic, int itemID) {
		super(transport, logic, itemID);
		this.logic = logic;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!teleportPipes.contains(this)) {
			teleportPipes.add(this);
		}
	}

	public List<PipeTeleport> getConnectedPipes(boolean forceReceive) {
		List<PipeTeleport> connected = new LinkedList<PipeTeleport>();
		removeOldPipes();

		PipeLogicTeleport logic = this.logic;

		for (PipeTeleport pipe : teleportPipes) {
			if (!this.getClass().equals(pipe.getClass())) {
				continue;
			}

			PipeLogicTeleport pipeLogic = pipe.logic;
			if (pipeLogic.freq == logic.freq &&
					(pipeLogic.canReceive || forceReceive) &&
					pipeLogic.owner.equalsIgnoreCase(logic.owner)) {
				if (xCoord != pipe.xCoord || yCoord != pipe.yCoord || zCoord != pipe.zCoord ) {
					connected.add(pipe);
				}
			}
		}

		return connected;
	}

	public void removeOldPipes() {
		LinkedList <PipeTeleport> toRemove = new LinkedList <PipeTeleport>();
		for (PipeTeleport pipe : teleportPipes) {
			if (!(worldObj.getBlockTileEntity(pipe.xCoord, pipe.yCoord, pipe.zCoord) instanceof TileGenericPipe)) {
				toRemove.add(pipe);
			}
		}
		teleportPipes.removeAll(toRemove);
	}

	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

}
