package buildcraft.additionalpipes.gui;

import java.util.ArrayList;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageJeweledPipeOptionsClient;
import buildcraft.additionalpipes.pipes.PipeBehaviorJeweled;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;


public class ContainerJeweledPipe extends Container
{
    private final int PLAYER_INVENTORY_ROWS = 3;
    private final int PLAYER_INVENTORY_COLUMNS = 9;
    
    //x coordinate where the slots start
    private final int SLOT_START_X = 21;
    	
	byte currentSide = 1;
		
	/*
	 * Mapping:
	 * 0 -> white      -> down
	 * 1 -> light blue -> up
	 * 2 -> dark blue  -> north
	 * 3 -> green      -> south
	 * 4 -> yellow     -> west
	 * 5 -> red        -> east
	 */
	
	PipeBehaviorJeweled pipeItemsJeweled;
	
	//holds references to the slots for each side
	ArrayList<ArrayList<Slot>> sideSlots;

	public ContainerJeweledPipe(InventoryPlayer inventoryPlayer, PipeBehaviorJeweled pipe)
    {
		sideSlots = new ArrayList<ArrayList<Slot>>();
		
        // Add the jeweled pipe slots
		for(int side = 0; side < GuiJeweledPipe.NUM_TABS; ++side)
		{
			ArrayList<Slot> currentSide = new ArrayList<Slot>();
			
			// add the slots off to the side initially
			for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
		    {
	            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
	            {
	            	Slot newSlot = new SlotItemHandler(pipe.filterData[side].getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
	            			filterColumnIndex + filterRowIndex * 9, filterColumnIndex * 18, 1000 + ((3 * side) + filterRowIndex) * 18);
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
                this.addSlotToContainer(new Slot(inventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, SLOT_START_X + inventoryColumnIndex * 18, 128 + inventoryRowIndex * 18));
            }
        }

        // Add the player's action bar slots to the container
        for (int actionBarSlotIndex = 0; actionBarSlotIndex < PLAYER_INVENTORY_COLUMNS; ++actionBarSlotIndex)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, SLOT_START_X + actionBarSlotIndex * 18, 186));
        }
        
        pipeItemsJeweled = pipe;
        
        setFilterTab((byte) 1);
        
        //send the options to the client, since they are only loaded from NBT on the server
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{	
			MessageJeweledPipeOptionsClient message = new MessageJeweledPipeOptionsClient(pipe.getPos(), pipe.filterData);
			PacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP) inventoryPlayer.player);
		}
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
		
		ArrayList<Slot> oldTabSlots = sideSlots.get(currentSide - 1);		
		ArrayList<Slot> newTabSlots = sideSlots.get(newTab - 1);

		//move the old slots off to the side 
		for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
	    {
            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
            {
            	Slot currentSlot = oldTabSlots.get(9 * filterRowIndex + filterColumnIndex);
                currentSlot.xPos = filterColumnIndex * 18;
                currentSlot.yPos =  1000 + ((3 * (currentSide - 1)) + filterRowIndex) * 18;
            }
	    }
		
		//move the new slots onto the GUI
		for(int filterRowIndex = 0; filterRowIndex < 3; ++filterRowIndex)
	    {
            for(int filterColumnIndex = 0; filterColumnIndex < 9; ++filterColumnIndex)
            {
            	Slot currentSlot = newTabSlots.get(9 * filterRowIndex + filterColumnIndex);
                currentSlot.xPos = SLOT_START_X + filterColumnIndex * 18;
                currentSlot.yPos = 33 + filterRowIndex * 18;
            }
	    }
			
		currentSide = newTab;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		TilePipeHolder tile = (TilePipeHolder) pipeItemsJeweled.pipe.getHolder();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex)
    {
    	return ItemStack.EMPTY;
    }
}
