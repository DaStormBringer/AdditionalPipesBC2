package buildcraft.additionalpipes.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils
{
	/**
	 * Find the first free slot in an inventory
	 * @param inventory
	 * @return index of the first free slot, or -1 if the container is full
	 */
	public static int getFirstFreeSlot(IInventory inventory)
	{
		int size = inventory.getSizeInventory() - 1;
		for(int index = 0; index <= size; ++index)
		{
			if(inventory.getStackInSlot(index) == null)
			{
				return index;
			}
		}
		
		return -1;
	}
	
	/**
	 * Test if an item can be added to an inventory
	 * @param inventory
	 * @return index of the first free slot, or -1 if the container is full
	 */
	public static boolean canItemFit(IInventory inventory, ItemStack stack)
	{
		int size = inventory.getSizeInventory() - 1;
		
		int stackLimit = inventory.getInventoryStackLimit();
		int itemsLeftToAdd = stack.stackSize;
		for(int index = 0; index <= size; ++index)
		{
			ItemStack slotStack = inventory.getStackInSlot(index);
			if(slotStack == null)
			{
				return true;
			}
			else if(slotStack.getItem() == stack.getItem() && slotStack.getItemDamage() == stack.getItemDamage())
			{
				if(slotStack.stackSize + itemsLeftToAdd <= stackLimit)
				{
					return true;
				}
				else
				{
					itemsLeftToAdd -= stackLimit - slotStack.stackSize;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Check if the given inventory contains an item
	 * @param matchMeta
	 * @param matchNBT
	 * @param stack
	 * @param inventory
	 * @return
	 */
	public static boolean containsItem(boolean matchMeta, boolean matchNBT, ItemStack stack, IInventory inventory)
	{
		int size = inventory.getSizeInventory() - 1;
		for(int index = 0; index <= size; ++index)
		{
			ItemStack slotStack = inventory.getStackInSlot(index);
			if(slotStack != null)
			{
				if(slotStack.getItem() == stack.getItem())
				{
					if(!matchMeta || stack.getItemDamage() == slotStack.getItemDamage())
					{
						if(!matchNBT || (slotStack.stackTagCompound == null && stack.stackTagCompound == null))
						{
							return true;
						}
						if((slotStack.stackTagCompound != null && stack.stackTagCompound != null) && slotStack.stackTagCompound.equals(stack.stackTagCompound))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
