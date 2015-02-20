package buildcraft.additionalpipes.pipes;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.world.World;
import buildcraft.additionalpipes.AdditionalPipes;

public class TeleportManager {
	public static final TeleportManager instance = new TeleportManager();

	public final List<PipeTeleport<?>> teleportPipes;

	public final Map<Integer, String> frequencyNames;

	private TeleportManager() {
		teleportPipes = new LinkedList<PipeTeleport<?>>();
		frequencyNames = new HashMap<Integer, String>();
	}

	public void add(PipeTeleport<?> pipe) {
		if(!AdditionalPipes.proxy.isServer(pipe.getWorld()))
			return;
		teleportPipes.add(pipe);
		AdditionalPipes.instance.logger.info(String.format("[TeleportManager] Pipe added: %s @ (%d, %d, %d), %d pipes in network", pipe.getClass().getSimpleName(), pipe.container.xCoord, pipe.container.yCoord,
				pipe.container.zCoord, teleportPipes.size()));
	}

	public void remove(PipeTeleport<?> pipe) {
		if(!AdditionalPipes.proxy.isServer(pipe.getWorld()))
			return;
		teleportPipes.remove(pipe);
		AdditionalPipes.instance.logger.info(String.format("[TeleportManager] Pipe removed: %s @ (%d, %d, %d), %d pipes in network", pipe.getClass().getSimpleName(), pipe.container.xCoord, pipe.container.yCoord,
				pipe.container.zCoord, teleportPipes.size()));
	}

	public void reset() {
		teleportPipes.clear();
		frequencyNames.clear();
		AdditionalPipes.instance.logger.info("Reset teleport manager.");
	}

	// returns all other teleport pipes of the same type (class) and frequency
	// if forceReceive is true. Otherwise, take away all pipes that aren't
	// receiving
	public List<PipeTeleport<?>> getConnectedPipes(PipeTeleport<?> pipe, boolean forceReceive) {
		List<PipeTeleport<?>> connected = new LinkedList<PipeTeleport<?>>();

		for(PipeTeleport<?> other : teleportPipes) {
			if(!pipe.getClass().equals(other.getClass()) || other.container.isInvalid()) {
				continue;
			}

			// not the same pipe &&
			// same frequency &&
			// pipe is open or forceReceive &&
			// both public or same owner
			if((pipe.container.xCoord != other.container.xCoord || pipe.container.yCoord != other.container.yCoord || pipe.container.zCoord != other.container.zCoord) && other.getFrequency() == pipe.getFrequency()
					&& ((other.state & 0x2) > 0 || forceReceive) && (pipe.isPublic ? other.isPublic : other.owner.equalsIgnoreCase(pipe.owner))) {
				connected.add(other);
			}
		}
		return connected;
	}

	// FIXME unused
	public List<PipeTeleport<?>> getAllPipesInNetwork(PipeTeleport<?> pipe) {
		List<PipeTeleport<?>> pipes = new LinkedList<PipeTeleport<?>>();

		for(PipeTeleport<?> other : teleportPipes) {
			if(!pipe.getClass().equals(other.getClass()) || other.container.isInvalid()) {
				continue;
			}

			if(other.getFrequency() == pipe.getFrequency() && (pipe.isPublic ? other.isPublic : other.owner.equalsIgnoreCase(pipe.owner))) {
				pipes.add(other);
			}
		}
		return pipes;
	}

	public String getFrequencyName(int frequency) {
		String name = frequencyNames.get(frequency);
		return name == null ? "" : name;
	}

	public void setFrequencyName(int frequency, String name) {
		frequencyNames.put(frequency, name);
	}

	public File getWorldSave(World world) {
		return world.getSaveHandler().getMapFileFromName("foo").getParentFile().getParentFile();
	}

}
