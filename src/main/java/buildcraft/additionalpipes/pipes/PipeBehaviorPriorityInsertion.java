/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.inventory.IItemTransactor;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipe.ConnectedType;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.api.transport.pipe.PipeEventItem.ItemEntry;
import buildcraft.lib.inventory.ItemTransactorHelper;
import buildcraft.lib.misc.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class PipeBehaviorPriorityInsertion extends APPipe {

	public byte sidePriorities[] = { 1, 1, 1, 1, 1, 1 };


	public PipeBehaviorPriorityInsertion(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
		
		sidePriorities = nbt.getByteArray("prioritiesArray");
	}

	public PipeBehaviorPriorityInsertion(IPipe pipe)
	{
		super(pipe);
	}

	@Override
	public int getTextureIndex(EnumFacing connection)
	{
		if(connection == null)
		{
			return 0;
		}
		
		return connection.ordinal();
	}
	
	@PipeEventHandler
	public void splitStacks(PipeEventItem.Split splitEvent) 
	{
		ArrayList<ItemEntry>  newDistribution = new ArrayList<>();
		
		
		for(ItemEntry entry : splitEvent.items)
		{			
			if(entry.to == null)
			{
				entry.to = new ArrayList<EnumFacing>();
			}
			
			ItemStack itemsRemaining = entry.stack.copy();
			
			// keep looping until we run out of sides, or of items
			for(int checkingPriority = 6; checkingPriority >= 1 && itemsRemaining.getCount() > 0; --checkingPriority)
			{
				for(EnumFacing side : EnumFacing.VALUES)
				{
					if(sidePriorities[side.ordinal()] == checkingPriority)
					{
						if(pipe.isConnected(side))
			        	{
			        		if(pipe.getConnectedType(side) == ConnectedType.TILE)
			        		{
			        			// add as many items as will fit to this side
			        			IItemTransactor transactor = ItemTransactorHelper.getTransactor(pipe.getConnectedTile(side), side.getOpposite());
			        			ItemStack overflow = transactor.insert(itemsRemaining, false, true);
			        			
			        			if(overflow == ItemStack.EMPTY)
			        			{
			        				// we can add the entire thing
			        				itemsRemaining.setCount(0);
			        				entry.to.add(side);
			        				newDistribution.add(entry);
			        			}
			        			else if(overflow.getCount() < itemsRemaining.getCount())
			        			{
			        				// at least some can fit, so split the itementry and add the items that can fit
			        				ItemStack partialStack = itemsRemaining.copy();
			        				partialStack.setCount(itemsRemaining.getCount() - overflow.getCount());
			        				
			        				itemsRemaining.setCount(overflow.getCount());
			        				
			        				ItemEntry newEntry = new ItemEntry(null, partialStack, entry.from);
			        				newEntry.to = new ArrayList<>();
			        				newEntry.to.add(side);
			        				newDistribution.add(newEntry);
			        			}
			        		}
			        	}
					}
				}
			}
			
			// if there are still items left after that, just leave them on their default route since they have nowhere to go
			if(itemsRemaining.getCount() > 0)
			{
				entry.stack.setCount(itemsRemaining.getCount());
				newDistribution.add(entry);
			}
		}
		
		splitEvent.items.clear();
		splitEvent.items.addAll(newDistribution);
	}
	/*
	@PipeEventHandler
    public void orderSides(PipeEventItem.SideCheck ordering) 
	{
        for (EnumFacing face : EnumFacing.VALUES) 
        {
        	// don't send items back where they came from
        	if(face != ordering.from)
        	{
                continue;
        	}
        	
        	boolean shouldIncreasePriority = false;
        	
        	if(pipe.isConnected(face))
        	{
        		if(pipe.getConnectedType(face) == ConnectedType.PIPE)
        		{
        			// pipes always have space for more items, so we should always send items to them
        			shouldIncreasePriority = true;
        		}
        		else
        		{
        			// if we are connected to a tile on this side, we must ensure it has space for 
        		}
        	}
        	
        	ordering.increasePriority(face, 100 + 10 * sidePriorities[face.ordinal()]); // note: PipeBehaviourClay adds 100 to priorities to override filters and things, so I'm following that precedent

        }
    }
*/
	@Override
    public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part) 
	{
        if (EntityUtil.getWrenchHand(player) != null) 
        {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        
        if (!player.world.isRemote) 
        {
        	BlockPos pipePos = pipe.getHolder().getPipePos();
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_PRIORITY, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
    }

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbt = super.writeToNbt();
			
		nbt.setByteArray("prioritiesArray", sidePriorities);
		
		return nbt;
	}

}
