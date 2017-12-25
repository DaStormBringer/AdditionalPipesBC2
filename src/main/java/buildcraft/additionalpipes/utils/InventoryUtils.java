package buildcraft.additionalpipes.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;

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
		int itemsLeftToAdd = stack.getCount();
		for(int index = 0; index <= size; ++index)
		{
			ItemStack slotStack = inventory.getStackInSlot(index);
			if(slotStack == null)
			{
				return true;
			}
			else if(slotStack.getItem() == stack.getItem() && slotStack.getItemDamage() == stack.getItemDamage())
			{
				if(slotStack.getCount() + itemsLeftToAdd <= stackLimit)
				{
					return true;
				}
				else
				{
					itemsLeftToAdd -= stackLimit - slotStack.getCount();
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
	public static boolean containsItem(boolean matchMeta, boolean matchNBT, ItemStack stack, IItemHandler inventory)
	{
		int size = inventory.getSlots() - 1;
		for(int index = 0; index <= size; ++index)
		{
			ItemStack slotStack = inventory.getStackInSlot(index);
			if(slotStack != null)
			{
				if(slotStack.getItem() == stack.getItem())
				{
					if(!matchMeta || stack.getItemDamage() == slotStack.getItemDamage())
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
	
	/**
	 * Returns an ordered list of all stacks in the ItemHandler
	 * @param handler
	 * @return
	 */
	public static NonNullList<ItemStack> getItems(IItemHandler handler)
	{
		int invSize = handler.getSlots();
		
		NonNullList<ItemStack> stacks = NonNullList.withSize(invSize, ItemStack.EMPTY);
		
		for(int index = 0; index < invSize; ++index)
		{
			ItemStack stack = handler.getStackInSlot(index);
			
			if(stack != null)
			{
				stacks.add(stack);
			}
		}
		
		return stacks;
	}
}
