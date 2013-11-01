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
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.core.Position;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.CoreConstants;
import buildcraft.core.utils.Utils;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;

public class PipeItemsAdvancedWood extends APPipe implements IPowerReceptor {

	private final PowerHandler powerProvider;
	public final PipeTransportAdvancedWood transport;

	public PipeItemsAdvancedWood(int itemID) {
		super(new PipeTransportAdvancedWood(), itemID);
		transport = (PipeTransportAdvancedWood) super.transport;

		powerProvider = new PowerHandler(this, Type.MACHINE);
		powerProvider.configurePowerPerdition(64, 1);
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return powerProvider.getPowerReceiver();
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		if(powerProvider.getEnergyStored() <= 0)
			return;

		World w = getWorld();

		int meta = w.getBlockMetadata(container.xCoord, container.yCoord, container.zCoord);

		if(meta > 5)
			return;

		Position pos = new Position(container.xCoord, container.yCoord, container.zCoord, ForgeDirection.VALID_DIRECTIONS[meta]);
		pos.moveForwards(1);
		int blockId = w.getBlockId((int) pos.x, (int) pos.y, (int) pos.z);
		TileEntity tile = w.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

		if(tile instanceof IInventory) {
			if(!PipeManager.canExtractItems(this, w, (int) pos.x, (int) pos.y, (int) pos.z))
				return;

			IInventory inventory = (IInventory) tile;

			ItemStack extracted = checkExtract(inventory, true, pos.orientation.getOpposite());

			if(extracted == null || extracted.stackSize == 0) {
				powerProvider.useEnergy(1, 1, false);
				return;
			}

			Position entityPos = new Position(pos.x + 0.5, pos.y + CoreConstants.PIPE_MIN_POS, pos.z + 0.5, pos.orientation.getOpposite());
			entityPos.moveForwards(0.5);
			TravelingItem entity = new TravelingItem(entityPos.x, entityPos.y, entityPos.z, extracted);
			((PipeTransportItems) transport).injectItem(entity, entityPos.orientation);
		}
	}

	public ItemStack checkExtract(IInventory inventory, boolean doRemove, ForgeDirection from) {
		IInventory inv = Utils.getInventory(inventory);
		int first = 0;
		int last = inv.getSizeInventory() - 1;
		if(inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int[] accessibleSlots = sidedInv.getAccessibleSlotsFromSide(from.ordinal());
			ItemStack result = checkExtractGeneric(inv, doRemove, from, accessibleSlots);
			return result;
		}
		ItemStack result = checkExtractGeneric(inv, doRemove, from, first, last);
		return result;
	}

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove, ForgeDirection from, int start, int stop) {
		for(int k = start; k <= stop; ++k) {
			ItemStack slot = inventory.getStackInSlot(k);

			if(slot != null && slot.stackSize > 0 && canExtract(slot)) {
				if(doRemove) {
					return inventory.decrStackSize(k, (int) powerProvider.useEnergy(1, slot.stackSize, true));
				} else {
					return slot;
				}
			}
		}
		return null;
	}

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove, ForgeDirection from, int[] slots) {
		for(int i : slots) {
			ItemStack slot = inventory.getStackInSlot(i);

			if(slot != null && slot.stackSize > 0 && canExtract(slot)) {
				if(doRemove) {
					return inventory.decrStackSize(i, (int) powerProvider.useEnergy(1, slot.stackSize, true));
				} else {
					return slot;
				}
			}
		}
		return null;
	}

	public boolean canExtract(ItemStack item) {
		for(int i = 0; i < transport.getSizeInventory(); i++) {
			ItemStack stack = transport.getStackInSlot(i);
			if(stack != null && stack.itemID == item.itemID) {
				if((Item.itemsList[item.itemID].isDamageable())) {
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
	public boolean blockActivated(EntityPlayer entityplayer) {
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		if(equipped instanceof IToolWrench && ((IToolWrench) equipped).canWrench(entityplayer, container.xCoord, container.yCoord, container.zCoord)) {
			((PipeTransportAdvancedWood) transport).switchSource();
			((IToolWrench) equipped).wrenchUsed(entityplayer, container.xCoord, container.yCoord, container.zCoord);
			return true;
		}
		if(AdditionalPipes.isPipe(equipped)) {
			return false;
		}

		entityplayer.openGui(AdditionalPipes.instance, GuiHandler.PIPE_WOODEN_ADV, container.worldObj, container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public boolean doDrop() {
		Utils.preDestroyBlock(getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}
}
