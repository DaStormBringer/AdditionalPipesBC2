/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;
import java.util.LinkedList;

import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.transport.pipe.flow.PipeFlowItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PipeItemsTeleport extends PipeTeleport
{
	final private static double TELEPORTED_ITEM_SPEED = .1;
	
	// side of the pipe that teleported items enter and exit from
	private EnumFacing teleportSide = null;
	
	public PipeItemsTeleport(IPipe pipe, NBTTagCompound tagCompound)
	{
		super(pipe, tagCompound, TeleportPipeType.ITEMS);
		readFromNBT(tagCompound);
	}

	public PipeItemsTeleport(IPipe pipe)
	{
		super(pipe, TeleportPipeType.ITEMS);
	}

	@Override
	public NBTTagCompound writeToNbt()
	{
		NBTTagCompound nbt = super.writeToNbt();
		
		nbt.setByte("teleportSide", (byte) teleportSide.ordinal());
		
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		teleportSide = EnumFacing.VALUES[nbt.getByte("teleportSide")];
	}
	
	/**
	 * Get the side of the pipe that items are teleported into and out of.
	 * Items that come from this side are not teleported again.
	 * 
	 * The teleport side will change only if a pipe is connected to teleportSide, or disconnected from the opposite of teleportSide.
	 * 
	 * If this returns null, then the pipe is connected on no sides.
	 * @return
	 */
	public EnumFacing getTeleportSide()
	{
		// check if we need to recalculate the teleport side
		if(teleportSide == null || pipe.isConnected(teleportSide) || !pipe.isConnected(teleportSide.getOpposite()))
		{
			teleportSide = null;
			
			// for recalculation: 
			// find the first unconnected side that is opposite to a connected side
			for(EnumFacing side : EnumFacing.VALUES)
			{
				if(pipe.isConnected(side) && !pipe.isConnected(side.getOpposite()))
				{
					teleportSide = side;
				}
			}
		}
		
		return teleportSide;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PipeEventHandler
	public void onReachEnd(PipeEventItem.ReachEnd event)
	{
		if(pipe.getHolder().getPipeWorld().isRemote || !canSend()) 
		{
			return;
		}
		
		// if the item is going to the teleportSide, teleport it.
		if(getTeleportSide() == event.to)
		{
			ArrayList<PipeItemsTeleport> connectedTeleportPipes = (ArrayList)TeleportManager.instance.getConnectedPipes(this, false, true);
			
			// no teleport pipes connected, use default
			if(connectedTeleportPipes.size() <= 0 || (state & 0x1) == 0) {
				return;
			}
	
			// output to random pipe
			LinkedList<EnumFacing> outputOrientations = new LinkedList<EnumFacing>();
			PipeItemsTeleport otherPipe;
			
			int originalPipeNumber = pipe.getHolder().getPipeWorld().rand.nextInt(connectedTeleportPipes.size());
			int currentPipeNumber = originalPipeNumber;
			
			boolean found = false;
			int numberOfTries = 0;
			
			// find a pipe with something connected to it
			// The logic for this is... pretty complicated, actually.
			do
			{
				++numberOfTries;
				otherPipe = connectedTeleportPipes.get(currentPipeNumber);
				
				for(EnumFacing o : EnumFacing.values())
				{
					if(otherPipe.pipe.isConnected(o))
					{
						outputOrientations.add(o);
					}
				}
				
				// no outputs found, try again
				if(outputOrientations.size() <= 0) 
				{
					++currentPipeNumber;
					
					//loop back to the start
					if(currentPipeNumber >= connectedTeleportPipes.size())
					{
						currentPipeNumber = 0;
					}
				}
				else
				{
					found = true;
				}
			}
			while(numberOfTries < connectedTeleportPipes.size() && !found);
	
			//couldn't find any, so give up
			if(!found)
			{
				return;
			}
			
			((PipeFlowItems)otherPipe.pipe.getFlow()).injectItem(event.getStack(), true, otherPipe.getTeleportSide(), null, TELEPORTED_ITEM_SPEED); 
			event.setStack(ItemStack.EMPTY);
		}
	}
	
	@PipeEventHandler
	public void orderSides(PipeEventItem.SideCheck event)
	{
		if(event.from != getTeleportSide())
		{
			event.increasePriority(getTeleportSide(), 100);
		}
	}
}
