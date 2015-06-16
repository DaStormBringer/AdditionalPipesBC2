package buildcraft.additionalpipes.gui;

import java.util.ArrayList;

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
	
	//holds references to the slots for each side
	ArrayList<ArrayList<Slot>> sideSlots;

	public ContainerJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
		
		sideSlots = new ArrayList<ArrayList<Slot>>();
		
        // Add the jeweled pipe slots
		for(int side = 0; side < 6; ++side)
		{
			ArrayList<Slot> currentSide = new ArrayList<Slot>();
			
			// add the slots off to the side initially
			for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
		    {
	            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
	            {
	            	Slot newSlot = new Slot(pipe.filterData[side], filterColumnIndex + filterRowIndex * 9, ((3 * side) + filterColumnIndex) * 18, 200 + filterRowIndex * 18);
	                this.addSlotToContainer(newSlot);
	            	currentSide.add(newSlot);
	            }
		    }
			
			sideSlots.add(currentSide);
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
        
        setFilterTab((byte) 0);
    }
	
	
	/**
	 * Change the pipe inventory slots to be the inventory in the specified tab.
	 * 
	 * Updates the guiTab class variable
	 * @param tab
	 */
	public void setFilterTab(byte newTab)
	{
		if(newTab > pipeItemsJeweled.filterData.length)
		{
			throw new IllegalArgumentException();
		}
		
		ArrayList<Slot> oldTabSlots = sideSlots.get(currentSide);		
		ArrayList<Slot> newTabSlots = sideSlots.get(newTab);

		//move the old slots off to the side 
		for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
	    {
            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
            {
            	Slot currentSlot = oldTabSlots.get(9 * filterRowIndex + filterColumnIndex);
                currentSlot.xDisplayPosition = ((3 * currentSide) + filterColumnIndex) * 18;
                currentSlot.yDisplayPosition = 200 + filterRowIndex * 18;
            }
	    }
		
		//move the new slots onto the GUI
		for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
	    {
            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
            {
            	Slot currentSlot = newTabSlots.get(9 * filterRowIndex + filterColumnIndex);
                currentSlot.xDisplayPosition = 8 + filterColumnIndex * 18;
                currentSlot.yDisplayPosition = 34 + filterRowIndex * 18;
            }
	    }
			
		currentSide = newTab;
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
