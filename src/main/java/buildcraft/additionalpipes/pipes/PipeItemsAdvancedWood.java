/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.core.Position;
import buildcraft.api.tools.IToolWrench;
import buildcraft.core.CoreConstants;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;
import cofh.api.energy.IEnergyHandler;

public class PipeItemsAdvancedWood extends APPipe<PipeTransportAdvancedWood> implements IEnergyHandler
{
	
	protected RFBattery battery = new RFBattery(640, 640, 0);
	
	public final PipeTransportAdvancedWood transport;
	
	private int ticksSincePull = 0;

	public PipeItemsAdvancedWood(Item item) {
		super(new PipeTransportAdvancedWood(), item);
		transport = (PipeTransportAdvancedWood) super.transport;
	}
	
	private boolean shouldTick() {
		if (battery.getEnergyStored() >= 64 * 10) {
			return true;
		} else {
			return ticksSincePull >= 16 && battery.getEnergyStored() >= 10;
		}
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

			int meta = container.getBlockMetadata();

			if(meta > 5)
			{
				return;
			}
				
			Position pos = new Position(container.xCoord, container.yCoord, container.zCoord, ForgeDirection.VALID_DIRECTIONS[meta]);
			pos.moveForwards(1);
			TileEntity tile = w.getTileEntity((int) pos.x, (int) pos.y, (int) pos.z);
			
			ticksSincePull = 0;

			if(tile instanceof IInventory)
			{
					
				IInventory inventory = (IInventory) tile;

				ItemStack extracted = checkExtract(inventory, true, pos.orientation.getOpposite());

				if(extracted == null || extracted.stackSize == 0) {
					return;
				}

				Position entityPos = new Position(pos.x + 0.5, pos.y + CoreConstants.PIPE_MIN_POS, pos.z + 0.5, pos.orientation.getOpposite());
				entityPos.moveForwards(0.5);
				TravelingItem entity = TravelingItem.make(entityPos.x, entityPos.y, entityPos.z, extracted);
				((PipeTransportItems) transport).injectItem(entity, entityPos.orientation);
			}

			battery.setEnergy(0);
		}
	}

	public ItemStack checkExtract(IInventory inventory, boolean doRemove, ForgeDirection from) {
		IInventory inv = InvUtils.getInventory(inventory);
		int first = 0;
		int last = inv.getSizeInventory() - 1;
		if(inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int[] accessibleSlots = sidedInv.getAccessibleSlotsFromSide(from.ordinal());
			ItemStack result = checkExtractGeneric(sidedInv, doRemove, from, accessibleSlots);
			return result;
		}
		ItemStack result = checkExtractGeneric(inv, doRemove, from, first, last);
		return result;
	}

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove, ForgeDirection from, int start, int stop) {
		for(int k = start; k <= stop; ++k) {
			ItemStack slot = inventory.getStackInSlot(k);

			if(slot != null && slot.stackSize > 0 && canExtract(slot)) {
				if(doRemove)
				{
					int itemsExtracted = battery.getEnergyStored() / 10 >= slot.stackSize ? slot.stackSize : MathHelper.floor_double(battery.getEnergyStored() / 10);
					
					battery.extractEnergy(itemsExtracted * 10, false);
					
					return inventory.decrStackSize(k, (int) itemsExtracted);
				}
				else 
				{
					return slot;
				}
			}
		}
		return null;
	}

	public ItemStack checkExtractGeneric(ISidedInventory inventory, boolean doRemove, ForgeDirection from, int[] slots) {
		for(int i : slots)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if(slot != null && slot.stackSize > 0 && canExtract(slot) && inventory.canExtractItem(i, slot, from.ordinal())) {
				if(doRemove)
				{
					int itemsExtracted = battery.getEnergyStored() / 10 >= slot.stackSize ? slot.stackSize : MathHelper.floor_double(battery.getEnergyStored() / 10);
					
					battery.extractEnergy(itemsExtracted * 10, false);
					
					return inventory.decrStackSize(i, (int) itemsExtracted);
				}
				else
				{
					return slot;
				}
			}
		}
		return null;
	}

	public boolean canExtract(ItemStack item) {
		for(int i = 0; i < transport.getSizeInventory(); i++) {
			ItemStack stack = transport.getStackInSlot(i);
			if(stack != null && stack.getItem() == item.getItem()) {
				if((stack.getItem().isDamageable())) {
					return !transport.exclude;
				} else if(stack.getItemDamage() == item.getItemDamage()) {
					return !transport.exclude;
				}
			}
		}
		return transport.exclude;
	}


	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if(direction == ForgeDirection.UNKNOWN)
			return 6;
		else {
			int metadata = container.getBlockMetadata();
			if(metadata == direction.ordinal())
				return 7;
			else
				return 6;
		}
	}
	
	@Override
	public boolean blockActivated(EntityPlayer entityplayer, ForgeDirection direction)
	{
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if(equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, container.xCoord, container.yCoord, container.zCoord)) {
			transport.switchSource();
			((IToolWrench) equipped).wrenchUsed(entityplayer, container.xCoord, container.yCoord, container.zCoord);
			return true;
		}
		if(APConfiguration.filterRightclicks && AdditionalPipes.isPipe(equipped))
		{
			return false;
		}

		if(entityplayer.worldObj.isRemote) return true;
		entityplayer.openGui(AdditionalPipes.instance, GuiHandler.PIPE_WOODEN_ADV, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public boolean doDrop() {
		Utils.preDestroyBlock(getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection dir)
	{
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return battery.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return battery.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return battery.getMaxEnergyStored();
	}
}
