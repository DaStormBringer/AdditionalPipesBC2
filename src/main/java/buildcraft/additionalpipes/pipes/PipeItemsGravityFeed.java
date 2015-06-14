package buildcraft.additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.api.core.Position;
import buildcraft.api.tools.IToolWrench;
import buildcraft.core.CoreConstants;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;

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

		if(container.getWorldObj().isRemote)
		{
			return;
		}
		
		ticksSincePull++;

		if(shouldTick())
		{
			
			World w = getWorld();
			TileEntity tile = w.getTileEntity(container.xCoord, container.yCoord + 1, container.zCoord);

			ticksSincePull = 0;

			if(tile instanceof IInventory)
			{
					
				IInventory inventory = (IInventory) tile;

				ItemStack extracted = removeItem(inventory, true, ForgeDirection.DOWN);

				if(extracted == null || extracted.stackSize == 0)
				{
					return;
				}
				
				Position entityPos = new Position(container.xCoord + 0.5, container.yCoord + 1 + CoreConstants.PIPE_MIN_POS, container.zCoord + 0.5, ForgeDirection.DOWN);
				entityPos.moveForwards(0.5);

				TravelingItem entity = TravelingItem.make(entityPos.x, entityPos.y, entityPos.z, extracted);
				((PipeTransportItems) transport).injectItem(entity, ForgeDirection.DOWN);
			}

		}
	}

	public ItemStack removeItem(IInventory inventory, boolean doRemove, ForgeDirection from) {
		IInventory inv = InvUtils.getInventory(inventory);
		int first = 0;
		int last = inv.getSizeInventory() - 1;
		if(inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int[] accessibleSlots = sidedInv.getAccessibleSlotsFromSide(from.ordinal());
			ItemStack result = removeItemSided(sidedInv, doRemove, from, accessibleSlots);
			return result;
		}
		ItemStack result = removeItemNormal(inv, doRemove, from, first, last);
		return result;
	}

	public ItemStack removeItemNormal(IInventory inventory, boolean doRemove, ForgeDirection from, int start, int stop) {
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

	public ItemStack removeItemSided(ISidedInventory inventory, boolean doRemove, ForgeDirection from, int[] slots) {
		for(int i : slots)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if(slot != null && slot.stackSize > 0 && inventory.canExtractItem(i, slot, from.ordinal())) {
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
	public int getIconIndex(ForgeDirection direction) 
	{
		if(direction == ForgeDirection.UP)
		{
			return 33;
		}
		return 32;
	}
	
	@Override
	public boolean blockActivated(EntityPlayer entityplayer)
	{
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if(equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, container.xCoord, container.yCoord, container.zCoord)) {
			((PipeTransportAdvancedWood) transport).switchSource();
			((IToolWrench) equipped).wrenchUsed(entityplayer, container.xCoord, container.yCoord, container.zCoord);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean doDrop() {
		Utils.preDestroyBlock(getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}
}
