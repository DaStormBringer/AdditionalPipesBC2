package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;

public class ContainerJeweledPipe extends Container
{
    private final int PLAYER_INVENTORY_ROWS = 3;
    private final int PLAYER_INVENTORY_COLUMNS = 9;
    
	PipeItemsJeweled _pipe;
	
	int currentSide = 0;
	
	protected int guiTab = 0;
	
	/*
	 * Mapping:
	 * 0 -> white      -> down
	 * 1 -> light blue -> up
	 * 2 -> dark blue  -> north
	 * 3 -> green      -> south
	 * 4 -> yellow     -> west
	 * 5 -> red        -> east
	 */

    public ContainerJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
    	_pipe = pipe;
    	
        // Add the filter inventory to the container
        for(int filterRowIndex = 0; filterRowIndex < PLAYER_INVENTORY_ROWS; ++filterRowIndex)
        {
            for(int filterColumnIndex = 0; filterColumnIndex < PLAYER_INVENTORY_COLUMNS; ++filterColumnIndex)
            {
                this.addSlotToContainer(new Slot(_pipe.filterData[currentSide], filterColumnIndex + filterRowIndex * 9, 8 + filterColumnIndex * 18, 8 + filterRowIndex * 18));
            }
        }

        // Add the player's inventory slots to the container
        for (int inventoryRowIndex = 0; inventoryRowIndex < PLAYER_INVENTORY_ROWS; ++inventoryRowIndex)
        {
            for (int inventoryColumnIndex = 0; inventoryColumnIndex < PLAYER_INVENTORY_COLUMNS; ++inventoryColumnIndex)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, 8 + inventoryColumnIndex * 18, 58 + inventoryRowIndex * 18));
            }
        }

        // Add the player's action bar slots to the container
        for (int actionBarSlotIndex = 0; actionBarSlotIndex < PLAYER_INVENTORY_COLUMNS; ++actionBarSlotIndex)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 116));
        }
        
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return true;
    }
    
    /**
     * Change the pipe inventory slots to be the inventory in the specified tab.
     * 
     * Updates the guiTab class variable
     * @param tab
     */
    public void setFilterTab(byte tab)
    {
    	if(tab > _pipe.filterData.length)
    	{
    		throw new IllegalArgumentException();
    	}
    		
    	guiTab = tab;
    	
    	//send updates to client containers
		for(Object obj : crafters)
		{
			ICrafting crafter = (ICrafting) obj;
			crafter.sendProgressBarUpdate(this, guiTab, 0);
		}
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
    {
       // return ItemHelper.transferStackInSlot(entityPlayer, tileEntityGlassBell, (Slot)inventorySlots.get(slotIndex), slotIndex, TileEntityGlassBell.INVENTORY_SIZE);
    	return null;
    }
    
	@Override
	public void updateProgressBar(int guiTab, int unused)
	{
		setFilterTab((byte) guiTab);
	}
}
