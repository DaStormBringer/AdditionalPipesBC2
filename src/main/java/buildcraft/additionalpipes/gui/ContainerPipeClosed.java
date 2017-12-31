package buildcraft.additionalpipes.gui;

import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

//from dispenser code
public class ContainerPipeClosed extends Container {

	private PipeBehaviorClosed pipe;

	public ContainerPipeClosed(InventoryPlayer inventory, PipeBehaviorClosed pipe) {
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
				addSlotToContainer(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}

		for(row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventory, row, 8 + row * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		TilePipeHolder tile = (TilePipeHolder) pipe.pipe.getHolder();
		if(tile.getWorld().getTileEntity(tile.getPos()) != tile) return false;
		if(entityplayer.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) > 64) return false;
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotNum);

		if(slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if(slotNum < 9) {
				if(!mergeItemStack(stackInSlot, 9, 45, true)) {
					return null;
				}
			} else if(!mergeItemStack(stackInSlot, 0, 9, false)) {
				return null;
			}

			if(stackInSlot.getCount() == 0) 
			{
				slot.putStack((ItemStack) null);
			}
			else 
			{
				slot.onSlotChanged();
			}

			if(stackInSlot.getCount() == stack.getCount()) {
				return null;
			}

			slot.onTake(player, stackInSlot);
		}

		return stack;
	}
}
