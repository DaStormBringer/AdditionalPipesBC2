package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.utils.Log;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * The purpose of this class is to store data for one of the sides of a Jeweled Transport Pipe.
 * @author Jamie
 *
 */
public class SideFilterData implements ICapabilityProvider
{
	public static final int INVENTORY_SIZE = 27;
	
	private ItemStackHandler inventory;
	
	private boolean matchNBT;
	
	private boolean matchMetadata;

	private boolean acceptUnsortedItems;
	
	public boolean matchNBT()
	{
		return matchNBT;
	}

	public void setMatchNBT(boolean matchNBT)
	{
		this.matchNBT = matchNBT;
	}

	public boolean matchMetadata()
	{
		return matchMetadata;
	}

	public void setMatchMetadata(boolean matchMetadata)
	{
		this.matchMetadata = matchMetadata;
	}

	public boolean acceptsUnsortedItems()
	{
		return acceptUnsortedItems;
	}

	public void setAcceptUnsortedItems(boolean acceptUnsortedItems)
	{
		this.acceptUnsortedItems = acceptUnsortedItems;
	}

	
	public SideFilterData()
	{
		inventory = new ItemStackHandler(INVENTORY_SIZE);
	}
	
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setTag("inventory", inventory.serializeNBT());
        
        nbtTagCompound.setBoolean("matchNBT", matchNBT);
        nbtTagCompound.setBoolean("matchMetadata", matchMetadata);
        nbtTagCompound.setBoolean("acceptUnsortedItems", acceptUnsortedItems);
    }
    
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
    	inventory.deserializeNBT(nbtTagCompound.getCompoundTag("inventory"));
        
        matchNBT = nbtTagCompound.getBoolean("matchNBT");
        matchMetadata = nbtTagCompound.getBoolean("matchMetadata");
        acceptUnsortedItems = nbtTagCompound.getBoolean("acceptUnsortedItems");
    }
    
    /**
     * Returns true if this side can accept the given item.
     * @param stack
     * @return
     */
    public boolean matchesStack(ItemStack stack)
    {		
		if(stack == null)
		{
			Log.error("SideFilterData.matchesSide() called with null argument!");
			return false;
		}
		
		if(stack == ItemStack.EMPTY)
		{
			Log.error("SideFilterData.matchesSide() called with empty argument!");
			return false;
		}
		
		for(int index = 0; index < INVENTORY_SIZE; ++index)
		{
			ItemStack slotStack = inventory.getStackInSlot(index);
			if(slotStack != ItemStack.EMPTY)
			{
				if(slotStack.getItem() == stack.getItem())
				{
					if(!matchMetadata || stack.getItemDamage() == slotStack.getItemDamage())
					{
						if(!matchNBT || (!slotStack.hasTagCompound() && !stack.hasTagCompound()))
						{
							return true;
						}
						if((slotStack.hasTagCompound() && stack.hasTagCompound()) && slotStack.getTagCompound().equals(stack.getTagCompound()))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
    }

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return (T) inventory;
		}
		else
		{
			return null;
		}
	}
}
