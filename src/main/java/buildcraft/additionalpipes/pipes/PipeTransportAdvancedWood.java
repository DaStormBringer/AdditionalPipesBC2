/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeItemsWood;

public class PipeTransportAdvancedWood extends PipeTransportItems implements IInventory {

	
	public ItemStack[] items = new ItemStack[9];

	public boolean exclude = false;

	public void switchSource()
	{
		int meta = container.getBlockMetadata();
		int newMeta = 0;

		for(int i = meta + 1; i <= meta + 6; ++i) {
			EnumFacing o = EnumFacing.values()[i % 6];
			TileEntity tile = container.getTile(o);
			if(isInput(tile))
			{
				newMeta = o.ordinal();
				break;
			}
		}

		if(newMeta != meta)
		{
            IBlockState iblockstate = container.getWorld().getBlockState(container.getPos());
            container.getWorld().setBlockState(container.getPos(), iblockstate.withProperty(BuildCraftProperties.GENERIC_PIPE_DATA, newMeta));
			container.scheduleRenderUpdate();
		}
	}

	public boolean isInput(TileEntity tile) {
		return !(tile instanceof TileGenericPipe) && tile instanceof IInventory && Utils.checkPipesConnections(container, tile);
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, EnumFacing side) {
		Pipe<?> pipe2 = null;

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
			TileEntity tile = container.getTile(EnumFacing.values()[meta]);
			if(!isInput(tile))
				switchSource();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		exclude = nbttagcompound.getBoolean("exclude");

		NBTTagList nbttaglist = nbttagcompound.getTagList("items", 10);

		for(int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist.getCompoundTagAt(j);
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
	public void setInventorySlotContents(int i, ItemStack var2) {
		items[i] = var2;
	}

	@Override
	public String getName() {
		return "gui.PipeItemsAdvancedWood";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public void markDirty() {
		container.markDirty();
	}

	@Override
	public void openInventory(EntityPlayer playerIn)
	{

	}

	@Override
	public void closeInventory(EntityPlayer playerIn)
	{

	}

	@Override
	public IChatComponent getDisplayName()
	{
		return new ChatComponentText(getName());
	}

	@Override
	public int getField(int id)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear()
	{
		items = new ItemStack[9];
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack requestedItem = items[index];
		
		items[index] = null;
		
		return requestedItem;
	}


}
