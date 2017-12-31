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
import buildcraft.additionalpipes.utils.InventoryUtils;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.core.IStackFilter;
import buildcraft.api.transport.pipe.IFlowItems;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipeHolder.PipeMessageReceiver;
import buildcraft.lib.inventory.filter.DelegatingItemHandlerFilter;
import buildcraft.lib.misc.EntityUtil;
import buildcraft.lib.misc.StackUtil;
import buildcraft.transport.pipe.behaviour.PipeBehaviourWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class PipeBehaviorAdvWood extends PipeBehaviourWood implements ICapabilityProvider
{	
    public PipeBehaviorAdvWood(IPipe pipe) {
        super(pipe);
    }

    public PipeBehaviorAdvWood(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
        readFromNBT(nbt);
    }	
    
    public static final int INVENTORY_SIZE = 9;
	public ItemStackHandler items = new ItemStackHandler(INVENTORY_SIZE);
	private IStackFilter filter;
	
	public boolean exclude = false;
	
	void init()
	{
	   if(exclude)
	   {
		   // lambda capture?  We iz fancy, yes!
		   filter = new DelegatingItemHandlerFilter((ItemStack target, ItemStack toTest) -> exclude ? !StackUtil.isMatchingItem(target, toTest) : StackUtil.isMatchingItem(target, toTest), items);
	   }
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) 
	{
		exclude = nbttagcompound.getBoolean("exclude");
		items.deserializeNBT(nbttagcompound.getCompoundTag("filterItems"));
	}

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbttagcompound = super.writeToNbt();
		
		nbttagcompound.setBoolean("exclude", exclude);
		
		nbttagcompound.setTag("filterItems", items.serializeNBT());
		
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

	@Override
	public void addDrops(NonNullList<ItemStack> toDrop, int fortune)
	{
		super.addDrops(toDrop, fortune);
		toDrop.addAll(InventoryUtils.getItems(items));
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return (T) items;
		}
		
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return true;
		}
		
		return super.hasCapability(capability, facing);
	}
	
	

}
