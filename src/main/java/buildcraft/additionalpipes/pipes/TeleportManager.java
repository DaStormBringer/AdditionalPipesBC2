package buildcraft.additionalpipes.pipes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;
import buildcraft.additionalpipes.api.PipeTeleport;
import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.PipeTransportPower;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TeleportManager extends TeleportManagerBase
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
	@Override
	public void add(PipeTeleport<?> pipe, int frequency)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
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

		//if unit tests are being run, pipe.container will be null.
		if(pipe.container != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe added: %s @ (%d, %d, %d), %d pipes in channel", pipe.type.toString(), pipe.container.xCoord, pipe.container.yCoord,
					pipe.container.zCoord, getPipesInChannel(frequency, pipe.type).size()));
		}
	}

	@SuppressWarnings("unchecked")	
	@Override
	public void remove(PipeTeleport<?> pipe, int frequency)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			return;
		}
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

		//if unit tests are being run, pipe.container will be null.
		if(pipe.container != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe removed: %s @ (%d, %d, %d), %d pipes in channel", pipe.type.toString(), pipe.container.xCoord, pipe.container.yCoord,
				pipe.container.zCoord, getPipesInChannel(frequency, pipe.type).size()));
		}
	}

	@Override
	public void reset() {
		itemPipes.clear();
		fluidPipes.clear();
		powerPipes.clear();

		frequencyNames.clear();
		Log.info("Reset teleport manager.");
	}

	/**
	 * Get pipes connected to the provided one.
	 * @param pipe
	 * @param includeSend whether or not to return connected pipes that send stuff.
	 * @param includeReceive whether or not to return connected pipes that receive stuff.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PipeTeleport<?>> getConnectedPipes(PipeTeleport<?> pipe, boolean includeSend, boolean includeReceive) 
	{
		Collection<PipeTeleport<?>> channel = getPipesInChannel(pipe.getFrequency(), pipe.type);
		
		ArrayList<PipeTeleport<?>> connected = new ArrayList<PipeTeleport<?>>();
		
		for(PipeTeleport<?> other : channel)
		{
			if(other.container != null && other.container.isInvalid())
			{
				continue;
			}

			// pipe is open or includeReceive &&
			// both public or same owner
			if(other != pipe)
			{
                if(((other.state & 0x2) > 0 && includeReceive) || ((other.state & 0x1) > 0 && includeSend))
                {
                    if(pipe.isPublic ? other.isPublic : (other.ownerUUID != null && other.ownerUUID.equals(pipe.ownerUUID)))
        			{
        				connected.add(other);
        			}
                }
			}
		}
		return connected;
	}
	
	@Override
	public Collection<PipeTeleport<PipeTransportItems>> getAllItemPipesInNetwork() 
	{
		return itemPipes.values();
	}
	
	@Override
	public Collection<PipeTeleport<PipeTransportFluids>> getAllFluidPipesInNetwork() 
	{
		return fluidPipes.values();
	}
	
	@Override
	public Collection<PipeTeleport<PipeTransportPower>> getAllPowerPipesInNetwork() 
	{
		return powerPipes.values();
	}

	/**
	 * Get the name of the provided frequency.
	 * @param frequency
	 * @return the name of the frequency, or an empty string if it has no name.
	 */
	@Override
	public String getFrequencyName(int frequency)
	{
		String name = frequencyNames.get(frequency);
		return name == null ? "" : name;
	}

	@Override
	public void setFrequencyName(int frequency, String name) 
	{
		frequencyNames.put(frequency, name);
	}

	public File getWorldSave(World world) 
	{
		return world.getSaveHandler().getMapFileFromName("foo").getParentFile().getParentFile();
	}

}
