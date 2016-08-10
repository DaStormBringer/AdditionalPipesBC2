package buildcraft.additionalpipes.pipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.PipeTransportItems;

public class PipeItemsGravityFeed extends APPipe<PipeTransportItems>
{
			
	private int ticksSincePull = 0;

	public PipeItemsGravityFeed(Item item) {
		super(new PipeTransportItems(), item);
	}
	
	private boolean shouldTick() {
		return ticksSincePull >= APConfiguration.gravityFeedPipeTicksPerPull;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if(container.getWorld().isRemote)
		{
			return;
		}
		
		ticksSincePull++;

		if(shouldTick())
		{
			
			World w = getWorld();
			TileEntity tile = w.getTileEntity(container.getPos().up());
			ticksSincePull = 0;

			if(tile instanceof IInventory)
			{
					
				IInventory inventory = (IInventory) tile;

				ItemStack extracted = removeItem(inventory, true, EnumFacing.DOWN);

				if(extracted == null || extracted.stackSize == 0)
				{
					return;
				}
				
				injectItem(extracted, EnumFacing.UP);
			}

		}
	}

	public ItemStack removeItem(IInventory inventory, boolean doRemove, EnumFacing from) {
		IInventory inv = InvUtils.getInventory(inventory);
		int first = 0;
		int last = inv.getSizeInventory() - 1;
		if(inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int[] accessibleSlots = sidedInv.getSlotsForFace(from);
			ItemStack result = removeItemSided(sidedInv, doRemove, from, accessibleSlots);
			return result;
		}
		ItemStack result = removeItemNormal(inv, doRemove, from, first, last);
		return result;
	}

	public ItemStack removeItemNormal(IInventory inventory, boolean doRemove, EnumFacing from, int start, int stop) {
		for(int k = start; k <= stop; ++k) {
			ItemStack slot = inventory.getStackInSlot(k);

			if(slot != null && slot.stackSize > 0) {
				if(doRemove)
				{	
					return inventory.decrStackSize(k, 1);
				}
				else 
				{
					return slot;
				}
			}
		}
		return null;
	}

	public ItemStack removeItemSided(ISidedInventory inventory, boolean doRemove, EnumFacing from, int[] slots) {
		for(int i : slots)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if(slot != null) {
				if(doRemove)
				{
					return inventory.decrStackSize(i, 1);
				}
				else
				{
					return slot;
				}
			}
		}
		return null;
	}

	@Override
	public int getIconIndex(EnumFacing direction) 
	{
		if(direction == EnumFacing.UP)
		{
			return 33;
		}
		return 32;
	}
	

	@Override
	public boolean doDrop() {
		Utils.preDestroyBlock(getWorld(), container.getPos());
		return true;
	}
}
