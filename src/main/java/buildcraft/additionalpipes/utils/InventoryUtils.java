package buildcraft.additionalpipes.utils;

import net.minecraft.inventory.IInventory;

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
}
