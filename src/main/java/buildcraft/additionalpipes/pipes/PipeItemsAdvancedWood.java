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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.tools.IToolWrench;
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

		if(container.getWorld().isRemote)
		{
			return;
		}
		
		ticksSincePull++;

		if(shouldTick())
		{
			
			int meta = container.getBlockMetadata();

			if(meta > 5)
			{
				return;
			}
	        EnumFacing side = EnumFacing.getFront(meta);
				
	        TileEntity tile = container.getTile(side);


			
			ticksSincePull = 0;

			if(tile instanceof IInventory)
			{
					
				IInventory inventory = (IInventory) tile;

				ItemStack extracted = checkExtract(inventory, true, EnumFacing.values()[meta].getOpposite());

				if(extracted == null || extracted.stackSize == 0) {
					return;
				}

                Vec3 entPos = Utils.convertMiddle(tile.getPos()).add(Utils.convert(side, -0.6));

				TravelingItem entity = TravelingItem.make(entPos, extracted);
				((PipeTransportItems) transport).injectItem(entity, side);
			}

			battery.setEnergy(0);
		}
	}

	public ItemStack checkExtract(IInventory inventory, boolean doRemove, EnumFacing from) {
		IInventory inv = InvUtils.getInventory(inventory);
		int first = 0;
		int last = inv.getSizeInventory() - 1;
		if(inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int[] accessibleSlots = sidedInv.getSlotsForFace(from);
			ItemStack result = checkExtractGeneric(inv, doRemove, accessibleSlots);
			return result;
		}
		ItemStack result = checkExtractGeneric(inv, doRemove, first, last);
		return result;
	}

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove, int start, int stop) {
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

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove, int[] slots) {
		for(int i : slots)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if(slot != null && slot.stackSize > 0 && canExtract(slot)) {
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
	public int getIconIndex(EnumFacing direction) 
	{
		if(direction != null && container != null && container.getBlockMetadata() == direction.ordinal())
			return 7;
		else
			return 6;
		
	}
	
	@Override
	public boolean blockActivated(EntityPlayer entityplayer, EnumFacing direction)
	{
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if(equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, container.getPos())) {
			((PipeTransportAdvancedWood) transport).switchSource();
			((IToolWrench) equipped).wrenchUsed(entityplayer, container.getPos());
			return true;
		}
		if(APConfiguration.filterRightclicks && AdditionalPipes.isPipe(equipped))
		{
			return false;
		}

		entityplayer.openGui(AdditionalPipes.instance, GuiHandler.PIPE_WOODEN_ADV, container.getWorld(), container.getPos().getX(), container.getPos().getY(), container.getPos().getZ());
		return true;
	}

	@Override
	public boolean doDrop()
	{
		Utils.preDestroyBlock(getWorld(), container.getPos());
		return true;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing dir)
	{
		return true;
	}


	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return battery.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return battery.getMaxEnergyStored();
	}

}
