package buildcraft.additionalpipes.pipes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.PipeTransportPower;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class TeleportManager 
{
	public static final TeleportManager instance = new TeleportManager();

	public final Multimap<Integer, PipeTeleport<PipeTransportItems>> itemPipes;
	
	public final Multimap<Integer, PipeTeleport<PipeTransportFluids>> fluidPipes;

	public final Multimap<Integer, PipeTeleport<PipeTransportPower>> powerPipes;

	public final Map<Integer, String> frequencyNames;

	private TeleportManager() 
	{
		//create the three multimaps
		itemPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportItems>>create();
		
		fluidPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportFluids>>create();
		
		powerPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportPower>>create();
		
		frequencyNames = new HashMap<Integer, String>();
	}
	
	/**
	 * Get a collection containing the pipes in a channel (a channel consists of a frequency and a type).
	 * @param frequency
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Collection getPipesInChannel(int frequency, PipeTeleport.PipeType type)
	{
		//it seems that you can't cast from a Collection<PipeTeleport<PipeTransportFluids>>
		//to a Collection<PipeTeleport<?>>.  Why is that? -JS
		switch(type)
		{
		case ITEMS:
			return itemPipes.get(frequency);
		case FLUIDS:
			return fluidPipes.get(frequency);
		case POWER:
			return powerPipes.get(frequency);
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	public void add(PipeTeleport<?> pipe, int frequency)
	{
		if(!AdditionalPipes.proxy.isServer(pipe.getWorld()))
		{
			return;
		}
		
		switch(pipe.type)
		{
		case ITEMS:
			itemPipes.put(frequency, (PipeTeleport<PipeTransportItems>) pipe);
			break;
		case FLUIDS:
			fluidPipes.put(frequency, (PipeTeleport<PipeTransportFluids>) pipe);
			break;
		case POWER:
			powerPipes.put(frequency, (PipeTeleport<PipeTransportPower>) pipe);
			break;
		}

		Log.info(String.format("[TeleportManager] Pipe added: %s @ (%d, %d, %d), %d pipes in channel", pipe.type.toString(), pipe.container.xCoord, pipe.container.yCoord,
				pipe.container.zCoord, getPipesInChannel(frequency, pipe.type).size()));
	}

	@SuppressWarnings("unchecked")
	public void remove(PipeTeleport<?> pipe, int frequency)
	{
		if(!AdditionalPipes.proxy.isServer(pipe.getWorld()))
			return;
		switch(pipe.type)
		{
		case ITEMS:
			itemPipes.remove(frequency, (PipeTeleport<PipeTransportItems>) pipe);
			break;
		case FLUIDS:
			fluidPipes.remove(frequency, (PipeTeleport<PipeTransportFluids>) pipe);
			break;
		case POWER:
			powerPipes.remove(frequency, (PipeTeleport<PipeTransportPower>) pipe);
			break;
		}

		Log.info(String.format("[TeleportManager] Pipe removed: %s @ (%d, %d, %d), %d pipes in channel", pipe.type.toString(), pipe.container.xCoord, pipe.container.yCoord,
				pipe.container.zCoord, getPipesInChannel(frequency, pipe.type).size()));
	}

	public void reset() {
		itemPipes.clear();
		fluidPipes.clear();
		powerPipes.clear();

		frequencyNames.clear();
		Log.info("Reset teleport manager.");
	}

	// returns all other teleport pipes of the same type (class) and frequency
	// if includeReceive is true. Otherwise, take away all pipes that aren't
	// receiving.
	@SuppressWarnings("unchecked")
	public ArrayList<PipeTeleport<?>> getConnectedPipes(PipeTeleport<?> pipe, boolean includeReceive) 
	{
		Collection<PipeTeleport<?>> channel = getPipesInChannel(pipe.getFrequency(), pipe.type);
		
		ArrayList<PipeTeleport<?>> connected = new ArrayList<PipeTeleport<?>>();
		
		for(PipeTeleport<?> other : channel)
		{
			if(!pipe.getClass().equals(other.getClass()) || other.container.isInvalid())
			{
				continue;
			}

			// pipe is open or includeReceive &&
			// both public or same owner
			if(((other.state & 0x2) > 0 || includeReceive) && (pipe.isPublic ? other.isPublic : (other.ownerUUID != null && other.ownerUUID.equals(pipe.ownerUUID))))
			{
				connected.add(other);
			}
		}
		return connected;
	}

	// FIXME unused, these three functions need to be in the api if we ever have one
	public Collection<PipeTeleport<PipeTransportItems>> getAllItemPipesInNetwork() 
	{
		return itemPipes.values();
	}
	
	public Collection<PipeTeleport<PipeTransportFluids>> getAllFluidPipesInNetwork() 
	{
		return fluidPipes.values();
	}
	
	public Collection<PipeTeleport<PipeTransportPower>> getAllPowerPipesInNetwork() 
	{
		return powerPipes.values();
	}

	/**
	 * Get the name of the provided frequency.
	 * @param frequency
	 * @return the name of the frequency, or an empty string if it has no name.
	 */
	public String getFrequencyName(int frequency)
	{
		String name = frequencyNames.get(frequency);
		return name == null ? "" : name;
	}

	public void setFrequencyName(int frequency, String name) 
	{
		frequencyNames.put(frequency, name);
	}

	public File getWorldSave(World world) 
	{
		return world.getSaveHandler().getMapFileFromName("foo").getParentFile().getParentFile();
	}

}
