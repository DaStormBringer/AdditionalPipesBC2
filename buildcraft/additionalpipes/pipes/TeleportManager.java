package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.transport.TileGenericPipe;

public class TeleportManager {

	public static TeleportManager instance = new TeleportManager();

	public List<PipeTeleport> teleportPipes;

	private TeleportManager(){
		teleportPipes = new LinkedList<PipeTeleport>();
	}

	public void add(PipeTeleport pipe) {
		if(!AdditionalPipes.proxy.isServer(pipe.worldObj)) return;
		teleportPipes.add(pipe);
		AdditionalPipes.instance.logger.info(
				String.format("[TeleportManager] Pipe added: %s @ (%d, %d, %d), %d pipes in network",
						pipe.getClass().getSimpleName(), pipe.xCoord, pipe.yCoord, pipe.zCoord, teleportPipes.size()));
	}

	public void remove(PipeTeleport pipe){
		if(!AdditionalPipes.proxy.isServer(pipe.worldObj)) return;
		teleportPipes.remove(pipe);
		AdditionalPipes.instance.logger.info(
				String.format("[TeleportManager] Pipe removed: %s @ (%d, %d, %d), %d pipes in network",
						pipe.getClass().getSimpleName(), pipe.xCoord, pipe.yCoord, pipe.zCoord, teleportPipes.size()));
	}

	//returns all other teleport pipes of the same type (class) and frequency
	//if forceReceive is true. Otherwise, take away all pipes that aren't receiving
	public List<PipeTeleport> getConnectedPipes(PipeTeleport pipe, boolean forceReceive) {
		List<PipeTeleport> connected = new LinkedList<PipeTeleport>();
		PipeLogicTeleport logic = pipe.logic;

		for (PipeTeleport other : teleportPipes) {
			if (!pipe.getClass().equals(other.getClass()) || other.container.isInvalid()) {
				continue;
			}
			PipeLogicTeleport otherLogic = other.logic;

			//not the same pipe &&
			//same frequency &&
			//pipe is open or forceReceive &&
			//same owner (unimplemented)
			if ((pipe.xCoord != other.xCoord || pipe.yCoord != other.yCoord || pipe.zCoord != other.zCoord ) &&
					otherLogic.freq == logic.freq &&
					(otherLogic.canReceive || forceReceive) &&
					((logic.isPublic && otherLogic.isPublic)  || otherLogic.owner.equalsIgnoreCase(logic.owner))) {
				connected.add(other);
			}
		}
		return connected;
	}

	//legacy code (unused)
	public void removeOldPipes() {
		LinkedList <PipeTeleport> toRemove = new LinkedList <PipeTeleport>();
		for (PipeTeleport pipe : teleportPipes) {
			if (!(pipe.worldObj.getBlockTileEntity(pipe.xCoord, pipe.yCoord, pipe.zCoord) instanceof TileGenericPipe) ||
					pipe.container.isInvalid()) {
				toRemove.add(pipe);
			}
		}
		teleportPipes.removeAll(toRemove);
	}

}
