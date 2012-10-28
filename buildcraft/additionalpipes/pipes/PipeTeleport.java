package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.MutiPlayerProxy;
import buildcraft.additionalpipes.mod_AdditionalPipes;
import buildcraft.additionalpipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Position;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;;

public abstract class PipeTeleport extends Pipe {

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

	public List<PipeTeleport> getConnectedPipes(boolean ignoreReceive) {

		List<PipeTeleport> temp = new LinkedList<PipeTeleport>();
		removeOldPipes();

		PipeLogicTeleport logic = this.logic;

		for (PipeTeleport pipe : teleportPipes) {

			if (!this.getClass().equals(pipe.getClass())) {
				continue;
			}

			PipeLogicTeleport pipeLogic = pipe.logic;

			if (pipeLogic.owner.equalsIgnoreCase(logic.owner) || !mod_AdditionalPipes.proxy.isOnServer(worldObj)) {

				if (pipeLogic.canReceive || ignoreReceive) {

					if (pipeLogic.freq == logic.freq) {

						if (xCoord != pipe.xCoord || yCoord != pipe.yCoord || zCoord != pipe.zCoord ) {

							temp.add(pipe);
						}
					}
				}
			}
		}

		return temp;
	}

	public void removeOldPipes() {

		LinkedList <PipeTeleport> toRemove = new LinkedList <PipeTeleport> ();

		for (PipeTeleport pipe : teleportPipes) {

			if (!(worldObj.getBlockTileEntity(pipe.xCoord, pipe.yCoord, pipe.zCoord) instanceof TileGenericPipe)) {
				toRemove.add(pipe);
			}
		}

		teleportPipes.removeAll(toRemove);
	}

	public Position getPosition() {
		return new Position (xCoord, yCoord, zCoord);
	}

}
