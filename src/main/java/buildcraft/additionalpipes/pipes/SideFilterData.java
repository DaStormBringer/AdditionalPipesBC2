package buildcraft.additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import buildcraft.additionalpipes.utils.Log;

/**
 * The purpose of this class is to store data for one of the sides of a Jeweled Transport Pipe.
 * @author Jamie
 *
 */
public class SideFilterData implements IInventory
{
	public static  final int INVENTORY_SIZE = 27;
	
	private ItemStack[] inventory;
	
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
		inventory = new ItemStack[INVENTORY_SIZE];
	}
	
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        // Write the ItemStacks in the inventory to NBT
    	//this code from Pahimar's ee3
        NBTTagList tagList = new NBTTagList();
        for (int currentIndex = 0; currentIndex < inventory.length; ++currentIndex)
        {
            if (inventory[currentIndex] != null)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) currentIndex);
                inventory[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        nbtTagCompound.setTag("Items", tagList);
        
        nbtTagCompound.setBoolean("matchNBT", matchNBT);
        nbtTagCompound.setBoolean("matchMetadata", matchMetadata);
        nbtTagCompound.setBoolean("acceptUnsortedItems", acceptUnsortedItems);
    }
    
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        // Read in the ItemStacks in the inventory from NBT
    	//this code from Pahimar's ee3
        NBTTagList tagList = nbtTagCompound.getTagList("Items", 10);
        inventory = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            byte slotIndex = tagCompound.getByte("Slot");
            if (slotIndex >= 0 && slotIndex < inventory.length)
            {
                inventory[slotIndex] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
        
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
		
		for(int index = 0; index < INVENTORY_SIZE; ++index)
		{
			ItemStack slotStack = inventory[index];
			if(slotStack != null)
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
	public int getSizeInventory()
	{
		return INVENTORY_SIZE;
	}

    @Override
    public ItemStack getStackInSlot(int slotIndex)
    {
        return inventory[slotIndex];
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int decrementAmount)
    {
        ItemStack itemStack = getStackInSlot(slotIndex);
        if (itemStack != null)
        {
            if (itemStack.stackSize <= decrementAmount)
            {
                setInventorySlotContents(slotIndex, null);
            }
            else
            {
                itemStack = itemStack.splitStack(decrementAmount);
                if (itemStack.stackSize == 0)
                {
                    setInventorySlotContents(slotIndex, null);
                }
            }
        }

        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemStack)
    {
        inventory[slotIndex] = itemStack;

        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
        {
            itemStack.stackSize = this.getInventoryStackLimit();
        }


        this.markDirty();
    }

	@Override
	public String getName()
	{
		return "gui.jeweled_pipe";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
		//do nothing
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return true;
	}

	@Override
	public IChatComponent getDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getField(int id)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear()
	{
		inventory = new ItemStack[INVENTORY_SIZE];
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack requestedItem = inventory[index];
		
		inventory[index] = null;
		
		return requestedItem;
	}

	@Override
	public int getFieldCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
