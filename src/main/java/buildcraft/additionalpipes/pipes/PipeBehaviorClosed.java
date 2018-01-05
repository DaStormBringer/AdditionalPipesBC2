package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.InventoryUtils;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.inventory.IItemTransactor;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipeHolder.PipeMessageReceiver;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.lib.inventory.ItemTransactorHelper;
import buildcraft.lib.misc.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class PipeBehaviorClosed extends APPipe implements ICapabilityProvider {
	
	// note: if you change this, you will also have to change the GUI code
	public static final int INVENTORY_SIZE = 9;
	
	ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE);
	
	// true if the pipe has any "stuffed" (to use the Thermal Expansion term) items, and should display the appropriate texture.
	// calculated on the server, gets updated through the network on the client
	boolean isClosed;

	public boolean isClosed()
	{
		return isClosed;
	}

	public PipeBehaviorClosed(IPipe pipe)
	{
		super(pipe);
	}

	public PipeBehaviorClosed(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
		
		inventory.deserializeNBT(nbt.getCompoundTag("closedInventory"));
		
		if(isServer())
		{
			updateClosedStatus();
		}
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
	
	@PipeEventHandler
	public void onDrop(PipeEventItem.Drop event)
	{
		if(isClient())
		{
			return;
		}
		
		IItemTransactor transactor = ItemTransactorHelper.getTransactor(this, EnumFacing.UP); // note: face argument doesn't actually matter
		ItemStack overflow = transactor.insert(event.getStack(), true, true);
		
		if(overflow != ItemStack.EMPTY)
		{
			// move every stack backward, and delete the last stack, to make room for the new stack
			for(int i = INVENTORY_SIZE - 1; i > 1; --i)
			{
				inventory.setStackInSlot(i, inventory.getStackInSlot(i - 1));
			}
			
			inventory.setStackInSlot(0, ItemStack.EMPTY);
		}
		
		// now guaranteed to work
		transactor.insert(event.getStack(), false, false);
		
		event.setStack(ItemStack.EMPTY);
		updateClosedStatus();
	}

	/**
	 * Call this on the server whenever the inventory contents change.
	 * Detects if there any items left in the pipe and updates clients about this so that the texture can change.
	 */
	public void updateClosedStatus()
	{
		
		boolean newIsClosed = false;
		
		for(int i = 0; i < INVENTORY_SIZE; i++)
		{
			if(!inventory.getStackInSlot(i).isEmpty())
			{
				newIsClosed = true;
				break;
			}
		}
		
		if(newIsClosed != isClosed)
		{
			isClosed = newIsClosed;
			pipe.getHolder().scheduleNetworkUpdate(PipeMessageReceiver.BEHAVIOUR);
		}
	}


	@Override
	public void writePayload(PacketBuffer buffer, Side side)
	{
		Log.debug("wrote payload");
		
		buffer.writeBoolean(isClosed);
	}

	@Override
	public void readPayload(PacketBuffer buffer, Side side, MessageContext ctx)
	{
		Log.debug("read payload");

		isClosed = buffer.readBoolean();
		pipe.markForUpdate();
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
		return (isClosed ? 1 : 0);
	}

}
