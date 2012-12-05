package buildcraft.additionalpipes.pipes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;

public class TeleportManager {
	public static final TeleportManager instance = new TeleportManager();

	public final List<PipeTeleport> teleportPipes;
	public final Map<Integer, Integer[]> phasedSignals;

	private TeleportManager(){
		teleportPipes = new LinkedList<PipeTeleport>();
		phasedSignals = new HashMap<Integer, Integer[]>();
	}

	public void add(PipeTeleport pipe) {
		if(!AdditionalPipes.proxy.isServer(pipe.worldObj)) return;
		if(!phasedSignals.containsKey(pipe.logic.frequency)) {
			phasedSignals.put(pipe.logic.frequency, new Integer[] {0, 0, 0, 0});
		}
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

	public void reset() {
		teleportPipes.clear();
		phasedSignals.clear();
		AdditionalPipes.instance.logger.info("Reset teleport manager.");
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
			//both public or same owner
			if ((pipe.xCoord != other.xCoord || pipe.yCoord != other.yCoord || pipe.zCoord != other.zCoord ) &&
					otherLogic.frequency == logic.frequency &&
					(otherLogic.canReceive || forceReceive) &&
					(logic.isPublic ? otherLogic.isPublic  : otherLogic.owner.equalsIgnoreCase(logic.owner) )) {
				connected.add(other);
			}
		}
		return connected;
	}

	public List<PipeTeleport> getAllPipesInNetwork(PipeTeleport pipe) {
		List<PipeTeleport> pipes = new LinkedList<PipeTeleport>();
		PipeLogicTeleport logic = pipe.logic;

		for (PipeTeleport other : teleportPipes) {
			if (!pipe.getClass().equals(other.getClass()) || other.container.isInvalid()) {
				continue;
			}
			PipeLogicTeleport otherLogic = other.logic;

			if (otherLogic.frequency == logic.frequency &&
					(logic.isPublic ? otherLogic.isPublic  : otherLogic.owner.equalsIgnoreCase(logic.owner) )) {
				pipes.add(other);
			}
		}
		return pipes;
	}

}
