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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeItemsWood;

public class PipeTransportAdvancedWood extends PipeTransportItems implements IInventory {

	public ItemStack[] items = new ItemStack[9];

	public boolean exclude = false;

	public void switchSource() {
		int meta = container.getBlockMetadata();
		int newMeta = 6;

		for(int i = meta + 1; i <= meta + 6; ++i) {
			ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i % 6];
			TileEntity tile = container.getTile(o);
			if(isInput(tile))
				if(PipeManager.canExtractItems(container.getPipe(), tile.worldObj, tile.xCoord, tile.yCoord, tile.zCoord)) {
					newMeta = o.ordinal();
					break;
				}
		}

		if(newMeta != meta) {
			getWorld().setBlockMetadataWithNotify(container.xCoord, container.yCoord, container.zCoord, newMeta, 2);
			container.scheduleRenderUpdate();
			// worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean isInput(TileEntity tile) {
		return !(tile instanceof TileGenericPipe) && tile instanceof IInventory && Utils.checkPipesConnections(container, tile);
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		Pipe pipe2 = null;

		if(tile instanceof TileGenericPipe) {
			pipe2 = ((TileGenericPipe) tile).pipe;
		}

		return (pipe2 == null || (!(pipe2 instanceof PipeItemsWood) && !(pipe2 instanceof PipeItemsAdvancedWood))) && super.canPipeConnect(tile, side);

	}

	@Override
	public void initialize() {
		super.initialize();
		switchSourceIfNeeded();
	}

	private void switchSourceIfNeeded() {
		int meta = container.getBlockMetadata();
		if(meta > 5)
			switchSource();
		else {
			TileEntity tile = container.getTile(ForgeDirection.VALID_DIRECTIONS[meta]);
			if(!isInput(tile))
				switchSource();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		exclude = nbttagcompound.getBoolean("exclude");

		NBTTagList nbttaglist = nbttagcompound.getTagList("items");

		for(int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist.tagAt(j);
			int index = nbttagcompound2.getInteger("index");
			items[index] = ItemStack.loadItemStackFromNBT(nbttagcompound2);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("exclude", exclude);

		NBTTagList nbttaglist = new NBTTagList();

		for(int j = 0; j < items.length; ++j) {
			if(items[j] != null && items[j].stackSize > 0) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttaglist.appendTag(nbttagcompound2);
				nbttagcompound2.setInteger("index", j);
				items[j].writeToNBT(nbttagcompound2);
			}
		}

		nbttagcompound.setTag("items", nbttaglist);
	}

	@Override
	public int getSizeInventory() {
		return items.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return items[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int amt) {
		ItemStack stack = getStackInSlot(i);
		if(stack != null) {
			if(stack.stackSize <= amt) {
				setInventorySlotContents(i, null);
			} else {
				stack = stack.splitStack(amt);
				if(stack.stackSize == 0) {
					setInventorySlotContents(i, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);
		if(stack != null) {
			setInventorySlotContents(i, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack var2) {
		items[i] = var2;
	}

	@Override
	public String getInvName() {
		return "item.PipeItemsAdvancedWood";
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
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}


	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

}
