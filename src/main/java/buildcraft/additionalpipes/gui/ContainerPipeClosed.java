package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.lib.gui.ContainerBC_Neptune;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

//NOTE: uses the same texture and slot positions as the vanilla Dispenser
public class ContainerPipeClosed extends ContainerBC_Neptune {

	private PipeBehaviorClosed pipe;

	public ContainerPipeClosed(EntityPlayer player, PipeBehaviorClosed pipe)
	{
		super(player);
		this.pipe = (PipeBehaviorClosed) pipe;
		int row;
		int col;

		for(row = 0; row < 3; ++row) {
			for(col = 0; col < 3; ++col) {
				addSlotToContainer(new SlotItemHandler(this.pipe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), col + row * 3, 62 + col * 18, 17 + row * 18));
			}
		}

		for(row = 0; row < 3; ++row) {
			for(col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}

		for(row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(player.inventory, row, 8 + row * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		TilePipeHolder tile = (TilePipeHolder) pipe.pipe.getHolder();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}

	
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		
		// update the pipe's texture if the user has added or removed items
		if(!playerIn.world.isRemote)
		{
			pipe.updateClosedStatus();
		}
	}
	
	
}
