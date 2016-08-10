package buildcraft.additionalpipes.pipes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.PipeType;
import buildcraft.additionalpipes.api.TeleportManagerBase;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.PipeTransportPower;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


public class TeleportManager extends TeleportManagerBase
{
	public static final TeleportManager instance = new TeleportManager();

	public final Multimap<Integer, PipeTeleport<PipeTransportItems>> itemPipes;
	
	public final Multimap<Integer, PipeTeleport<PipeTransportFluids>> fluidPipes;

	public final Multimap<Integer, PipeTeleport<PipeTransportPower>> powerPipes;

	//public final Multimap<Integer, PipeTeleport<PipeTransportItemsLogistics>> logisticsPipes;

	public final Map<Integer, String> frequencyNames;

	private TeleportManager() 
	{
		//create the three multimaps
		itemPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportItems>>create();
		
		fluidPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportFluids>>create();
		
		powerPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportPower>>create();
		
		//logisticsPipes = LinkedListMultimap.<Integer, PipeTeleport<PipeTransportItemsLogistics>>create();
		
		frequencyNames = new HashMap<Integer, String>();
	}
	
	/**
	 * Get a collection containing the pipes in a channel (a channel consists of a frequency and a type).
	 * @param frequency
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Collection getPipesInChannel(int frequency, PipeType type)
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
		case LOGISTICS:
			
			//return logisticsPipes.get(frequency);
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(ITeleportPipe pipe, int frequency)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			return;
		}
		
		switch(pipe.getType())
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
		case LOGISTICS:
			//logisticsPipes.put(frequency, (PipeTeleport<PipeTransportItemsLogistics>) pipe);
			break;
		}

		//if unit tests are being run, pipe.container will be null.
		if(pipe.getContainer() != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe added: %s @ (%s), %d pipes in channel", pipe.getType().toString().toLowerCase(),
					pipe.getPosition().toString(), getPipesInChannel(frequency, pipe.getType()).size()));
		}
	}

	@SuppressWarnings("unchecked")	
	@Override
	public void remove(ITeleportPipe pipe, int frequency)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			return;

		}
		switch(pipe.getType())
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
		case LOGISTICS:
			//logisticsPipes.remove(frequency, (PipeTeleport<PipeTransportItemsLogistics>) pipe);
			break;
		}

		//if unit tests are being run, pipe.container will be null.
		if(pipe.getContainer() != null)
		{
			Log.debug(String.format("[TeleportManager] Pipe removed: %s @ (%s), %d pipes in channel", pipe.getType().toString().toLowerCase(),
					pipe.getPosition().toString(), getPipesInChannel(frequency, pipe.getType()).size()));
		}
	}

	@Override
	public void reset() {
		itemPipes.clear();
		fluidPipes.clear();
		powerPipes.clear();
		//logisticsPipes.clear();

		frequencyNames.clear();
		Log.info("Reset teleport manager.");
	}

	/**
	 * Get pipes connected to the provided one. (template function version)
	 * @param pipe
	 * @param includeSend whether or not to return connected pipes that send stuff.
	 * @param includeReceive whether or not to return connected pipes that receive stuff.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends ITeleportPipe> ArrayList<T> getConnectedPipes(T pipe, boolean includeSend, boolean includeReceive) 
	{
		Collection<T> channel = getPipesInChannel(pipe.getFrequency(), pipe.getType());
		
		ArrayList<T> connected = new ArrayList<T>();
		
		for(T other : channel)
		{
			if(other.getContainer() != null && other.getContainer().isInvalid())
			{
				continue;
			}
			
			// pipe is open or includeReceive &&
			// both public or same owner
			if(pipe != other)
			{
				if((other.canReceive() && includeReceive) || (other.canSend() && includeSend))
				{
					if(pipe.isPublic() ? other.isPublic() : (other.getOwnerUUID() != null && other.getOwnerUUID().equals(pipe.getOwnerUUID())))
					{
						connected.add(other);
					}	
				}
			}

		}
		return connected;
	}
	
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
	
//	public Collection<PipeTeleport<PipeTransportItemsLogistics>> getAllLogisticsPipesInNetwork() 
//	{
//		return logisticsPipes.values();
//	}

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
