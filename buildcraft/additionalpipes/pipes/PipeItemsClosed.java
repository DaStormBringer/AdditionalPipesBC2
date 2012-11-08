/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import buildcraft.api.core.Orientations;
import buildcraft.core.DefaultProps;
import buildcraft.core.inventory.Transactor;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.core.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.PipeLogic;

public class PipeItemsClosed extends Pipe implements IInventory {

	private ItemStack[] inventory = new ItemStack[27];

	public PipeItemsClosed(int itemID) {
		super(new PipeTransportItems(), new PipeLogic(), itemID);
	}

	@Override
	public void dropContents() {
		super.dropContents();
		Utils.dropItems(worldObj, this, xCoord, yCoord, zCoord);
	}

	@Override
	public void onDropped(EntityItem item) {
		super.onDropped(item);
		Transactor transactor = new TransactorSimple(this);
		transactor.add(item.item.copy(), Orientations.Unknown, true);
		item.item.stackSize = 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList list = new NBTTagList();

		for (ItemStack stack : inventory) {
			if(stack != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}

		nbttagcompound.setTag("closedInventory", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList list = nbttagcompound.getTagList("closedInventory");
		for (int i = 0; i < list.tagCount() && i < inventory.length; i++) {
			NBTTagCompound stackTag = (NBTTagCompound) list.tagAt(i);
			inventory[i] = ItemStack.loadItemStackFromNBT(stackTag);
		}

	}

	@Override
	public String getTextureFile() {
		return DefaultProps.TEXTURE_BLOCKS;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 1 * 16 + 13;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int amt) {
		ItemStack stack = inventory[i].splitStack(amt);
		if (inventory[i].stackSize == 0) {
			inventory[i] = null;
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = inventory[i];
		inventory[i] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inventory[i] = stack;
	}

	@Override
	public String getInvName() {
		return "pipeItemsClosed";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

}
