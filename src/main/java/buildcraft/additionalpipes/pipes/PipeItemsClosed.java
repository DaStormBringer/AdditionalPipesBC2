package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.InventoryUtils;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.inventory.IItemTransactor;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.lib.inventory.ItemTransactorHelper;
import buildcraft.lib.misc.EntityUtil;
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

public class PipeItemsClosed extends APPipe implements ICapabilityProvider {
	
	// note: if you change this, you will also have to change the GUI code
	public static final int EFFECTIVE_INVENTORY_SIZE = 9;
	public static final int ACTUAL_INVENTORY_SIZE = EFFECTIVE_INVENTORY_SIZE + 1; // we have a 10 slot inventory, but any stacks entering the last slot will get deleted
	
	ItemStackHandler inventory = new ItemStackHandler(ACTUAL_INVENTORY_SIZE);

	public PipeItemsClosed(IPipe pipe)
	{
		super(pipe);
	}

	public PipeItemsClosed(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
		readFromNBT(nbt);
	}

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
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_CLOSED, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
    }

	@Override
	public void addDrops(NonNullList<ItemStack> toDrop, int fortune)
	{
		super.addDrops(toDrop, fortune);
		toDrop.addAll(InventoryUtils.getItems(inventory));
	}
	
	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbttagcompound = super.writeToNbt();
		
		nbttagcompound.setTag("closedInventory", inventory.serializeNBT());
		
		return nbttagcompound;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) 
	{
		inventory.deserializeNBT(nbttagcompound.getCompoundTag("closedInventory"));
	}
	
	@PipeEventHandler
	public void onDrop(PipeEventItem.Drop event)
	{
		IItemTransactor transactor = ItemTransactorHelper.getTransactor(this, EnumFacing.UP); // note: face argument doesn't actually matter
		transactor.insert(event.getStack(), false, false); 
		// the itemstack is guaranteed to fit because there will always be at least one free slot
		
		// ...because we thin the herd here
		if(inventory.getStackInSlot(ACTUAL_INVENTORY_SIZE - 1) != ItemStack.EMPTY)
		{
			for(int i = 1; i < ACTUAL_INVENTORY_SIZE; i++)
			{
				inventory.setStackInSlot(i-1, inventory.getStackInSlot(i));
			}
		}
		
		inventory.setStackInSlot(ACTUAL_INVENTORY_SIZE - 1, ItemStack.EMPTY);
		
		event.setStack(ItemStack.EMPTY);
		pipe.getHolder().scheduleRenderUpdate();
	}



	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
		{
			return true;
		}
		else
		{
			return super.hasCapability(capability, facing);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
		{
			return (T) inventory;
		}
		else
		{		
			return super.getCapability(capability, facing);
		}
	}

	@Override
	public int getTextureIndex(EnumFacing direction) 
	{
		return (inventory.getStackInSlot(0) == ItemStack.EMPTY ? 0 : 1);
	}

}
