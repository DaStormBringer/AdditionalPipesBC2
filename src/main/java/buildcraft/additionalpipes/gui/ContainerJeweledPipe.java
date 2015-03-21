package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.transport.Pipe;

public class ContainerJeweledPipe extends Container {

	private PipeItemsJeweled pipe;

	public ContainerJeweledPipe(InventoryPlayer inventory, Pipe<?> pipe)
	{
		this.pipe = (PipeItemsJeweled) pipe;
		int x;
		int y;

		for(x = 0; x < 3; ++x)
		{
			for(y = 0; y < 9; ++y)
			{
				addSlotToContainer(new Slot(this.pipe.filterData[0], y + x * 3, 62 + y * 18, 17 + x * 18));
			}
		}

		for(x = 0; x < 3; ++x) {
			for(y = 0; y < 9; ++y) {
				addSlotToContainer(new Slot(inventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
			}
		}

		for(x = 0; x < 9; ++x) {
			addSlotToContainer(new Slot(inventory, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		ItemStack var3 = null;
		Slot var4 = (Slot) inventorySlots.get(par2);

		if(var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if(par2 < 9) {
				if(!mergeItemStack(var5, 9, 45, true)) {
					return null;
				}
			} else if(!mergeItemStack(var5, 0, 9, false)) {
				return null;
			}

			if(var5.stackSize == 0) {
				var4.putStack((ItemStack) null);
			} else {
				var4.onSlotChanged();
			}

			if(var5.stackSize == var3.stackSize) {
				return null;
			}

			var4.onPickupFromSlot(par1EntityPlayer, var5);
		}

		return var3;
	}
}
