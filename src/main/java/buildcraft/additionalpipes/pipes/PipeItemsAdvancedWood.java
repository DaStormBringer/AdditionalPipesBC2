/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.core.IStackFilter;
import buildcraft.api.transport.pipe.IFlowItems;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipeHolder.PipeMessageReceiver;
import buildcraft.lib.inventory.filter.ArrayStackOrListFilter;
import buildcraft.lib.inventory.filter.InvertedStackFilter;
import buildcraft.lib.misc.EntityUtil;
import buildcraft.transport.pipe.behaviour.PipeBehaviourWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import scala.actors.threadpool.Arrays;

public class PipeItemsAdvancedWood extends PipeBehaviourWood
{	
    public PipeItemsAdvancedWood(IPipe pipe) {
        super(pipe);
    }

    public PipeItemsAdvancedWood(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
        readFromNBT(nbt);
    }	
    
    public static final int INVENTORY_SIZE = 9;
	public ItemStack[] items = new ItemStack[INVENTORY_SIZE];
	private IStackFilter filter;
	
	public boolean exclude = false;

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		exclude = nbttagcompound.getBoolean("exclude");

		NBTTagList nbttaglist = nbttagcompound.getTagList("items", 10);

		for(int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist.getCompoundTagAt(j);
			int index = nbttagcompound2.getInteger("index");
			items[index] = new ItemStack(nbttagcompound2);
		}
	}

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbttagcompound = super.writeToNbt();
		
		nbttagcompound.setBoolean("exclude", exclude);

		NBTTagList nbttaglist = new NBTTagList();

		for(int j = 0; j < INVENTORY_SIZE; ++j) {
			if(items[j] != null && items[j].getCount() > 0) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttaglist.appendTag(nbttagcompound2);
				nbttagcompound2.setInteger("index", j);
				items[j].writeToNBT(nbttagcompound2);
			}
		}

		nbttagcompound.setTag("items", nbttaglist);
		
		return nbttagcompound;
	}
	
	/**
	 * Override the wooden pipe's extraction behavior to use our filter
	 */
    @Override
    protected int extractItems(IFlowItems flow, EnumFacing dir, int count, boolean simulate) {

        int extracted = flow.tryExtractItems(count, dir, null, filter, simulate);
        if (extracted > 0 && !simulate) {
            pipe.getHolder().scheduleNetworkUpdate(PipeMessageReceiver.BEHAVIOUR);
        }
        return extracted;
    }

    /**
     * Call this whenever items or exclude changes to re-create the item filter
     */
    public void regenerateFilter()
    {
	   if(exclude)
	   {
		   filter = new InvertedStackFilter(new ArrayStackOrListFilter(items));
	   }
	   else
	   {
		   filter = new ArrayStackOrListFilter(items);
	   }
    }

	@SuppressWarnings("unchecked")
	@Override
	public void addDrops(NonNullList<ItemStack> toDrop, int fortune)
	{
		super.addDrops(toDrop, fortune);
		toDrop.addAll(Arrays.asList(items));
	}

	@Override
    public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part) 
	{
        if (EntityUtil.getWrenchHand(player) != null) 
        {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        
        if (player.isServerWorld()) 
        {
        	BlockPos pipePos = pipe.getHolder().getPipePos();
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_WOODEN_ADV, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
    }

}
