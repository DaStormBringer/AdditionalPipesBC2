package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;

public class ContainerJeweledPipe extends Container
{
    private final int PLAYER_INVENTORY_ROWS = 3;
    private final int PLAYER_INVENTORY_COLUMNS = 9;
    	
	int currentSide = 0;
		
	/*
	 * Mapping:
	 * 0 -> white      -> down
	 * 1 -> light blue -> up
	 * 2 -> dark blue  -> north
	 * 3 -> green      -> south
	 * 4 -> yellow     -> west
	 * 5 -> red        -> east
	 */
	
	PipeItemsJeweled pipeItemsJeweled;

	public ContainerJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
        // Add the jeweled pipe slots
		for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
	    {
            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
            {
                this.addSlotToContainer(new Slot(pipe.filterData[currentSide], filterColumnIndex + filterRowIndex * 9, 8 + filterColumnIndex * 18, 34 + filterRowIndex * 18));
            }
	    }
		
		
        // Add the player's inventory slots to the container
        for (int inventoryRowIndex = 0; inventoryRowIndex < PLAYER_INVENTORY_ROWS; ++inventoryRowIndex)
        {
            for (int inventoryColumnIndex = 0; inventoryColumnIndex < PLAYER_INVENTORY_COLUMNS; ++inventoryColumnIndex)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, 8 + inventoryColumnIndex * 18, 130 + inventoryRowIndex * 18));
            }
        }

        // Add the player's action bar slots to the container
        for (int actionBarSlotIndex = 0; actionBarSlotIndex < PLAYER_INVENTORY_COLUMNS; ++actionBarSlotIndex)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 188));
        }
        
        pipeItemsJeweled = pipe;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
    {
    	return null;
    }
}
