/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.src.Block;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.logic.PipeLogicAdvancedWood;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.liquids.ITankContainer;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import buildcraft.core.EntityPassiveItem;
import buildcraft.core.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.PipeLogicWood;

public class PipeItemsAdvancedWood extends Pipe implements IPowerReceptor {

	private IPowerProvider powerProvider;

	public PipeItemsAdvancedWood(int itemID) {

		super(new PipeTransportItems(), new PipeLogicAdvancedWood(), itemID);

		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(50, 1, 64, 1, 64);
		powerProvider.configurePowerPerdition(64, 1);
	}

	@Override
	public void setPowerProvider(IPowerProvider provider) {
		provider = powerProvider;
	}

	@Override
	public IPowerProvider getPowerProvider() {
		return powerProvider;
	}

	@Override
	public void doWork() {
		if (powerProvider.getEnergyStored() <= 0) {
			return;
		}

		World w = worldObj;

		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if (meta > 5) {
			return;
		}

		Position pos = new Position(xCoord, yCoord, zCoord,
				Orientations.values()[meta]);
		pos.moveForwards(1);
		int blockId = w.getBlockId((int) pos.x, (int) pos.y, (int) pos.z);
		TileEntity tile = w.getBlockTileEntity((int) pos.x, (int) pos.y,
				(int) pos.z);

		if (tile == null
				|| !(tile instanceof IInventory || tile instanceof ITankContainer)
				|| PipeLogicWood.isExcludedFromExtraction(Block.blocksList[blockId])) {
			return;
		}

		if (tile instanceof IInventory) {
			IInventory inventory = (IInventory) tile;

			ItemStack stack = checkExtract(inventory, true,
					pos.orientation.reverse());

			if (stack == null || stack.stackSize == 0) {
				powerProvider.useEnergy(1, 1, false);
				return;
			}

			Position entityPos = new Position(pos.x + 0.5, pos.y
					+ Utils.getPipeFloorOf(stack), pos.z + 0.5,
					pos.orientation.reverse());

			entityPos.moveForwards(0.5);

			EntityPassiveItem entity = new EntityPassiveItem(w, entityPos.x,
					entityPos.y, entityPos.z, stack);

			((PipeTransportItems) transport).entityEntering(entity,
					entityPos.orientation);
		}
	}

	/**
	 * Return the itemstack that can be if something can be extracted from this
	 * inventory, null if none. On certain cases, the extractable slot depends
	 * on the position of the pipe.
	 */
	public ItemStack checkExtract(IInventory inventory, boolean doRemove,
			Orientations from) {
		//		if (inventory instanceof ISpecialInventory) {
		//			//At the moment we are going to let special inventorys handle there own. Might change if popular demand
		//			return ((ISpecialInventory) inventory).extractItem(doRemove, from);
		//		}
		IInventory inv = Utils.getInventory(inventory);
		ItemStack result = checkExtractGeneric(inv, doRemove, from);
		return result;
	}

	public ItemStack checkExtractGeneric(IInventory inventory,
			boolean doRemove, Orientations from) {
		for (int k = 0; k < inventory.getSizeInventory(); ++k) {
			if (inventory.getStackInSlot(k) != null
					&& inventory.getStackInSlot(k).stackSize > 0) {

				ItemStack slot = inventory.getStackInSlot(k);

				if (slot != null && slot.stackSize > 0 && CanExtract(slot)) {
					if (doRemove) {
						return inventory.decrStackSize(k, (int) powerProvider.useEnergy(1, slot.stackSize, true));
					}
					else {
						return slot;
					}
				}
			}
		}

		return null;
	}
	public boolean CanExtract(ItemStack item) {
		for (int i = 0; i < logic.getSizeInventory(); i++) {
			ItemStack stack = logic.getStackInSlot(i);

			if (stack != null && stack.itemID == item.itemID) {
				if ((Item.itemsList[item.itemID].isDamageable())) {
					return !((PipeLogicAdvancedWood)logic).exclude;
				}
				else if (stack.getItemDamage() == item.getItemDamage()) {
					return !((PipeLogicAdvancedWood)logic).exclude;
				}
			}
		}

		return ((PipeLogicAdvancedWood)logic).exclude;
	}

	@Override
	public int powerRequest() {
		return getPowerProvider().getMaxEnergyReceived();
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
	public String getTextureFile() {
		return AdditionalPipes.TEXTURE_ADVANCEDWOOD;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}
}
