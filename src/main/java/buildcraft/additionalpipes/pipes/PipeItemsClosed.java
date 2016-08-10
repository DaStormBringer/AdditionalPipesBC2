package buildcraft.additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.core.lib.inventory.Transactor;
import buildcraft.core.lib.inventory.TransactorSimple;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsClosed extends APPipe<PipeTransportItems> implements IInventory {

	private ItemStack[] inventory = new ItemStack[10];

	public PipeItemsClosed(Item item) {
		super(new PipeTransportItems(), item);
		//((PipeTransportItems) transport).travelHook = this;
	}

	@Override
	public boolean blockActivated(EntityPlayer player, EnumFacing side)
	{
		ItemStack equippedItem = player.getCurrentEquippedItem();
		if(equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem()))
		{
			return false;
		}
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_CLOSED, getWorld(), container.getPos().getX(), container.getPos().getY(), container.getPos().getZ());
		return true;
	}

	@Override
	public void dropContents() {
		super.dropContents();
		Utils.preDestroyBlock(getWorld(), container.getPos());
	}
	
	public void eventHandler(PipeEventItem.DropItem event)
	{
		Transactor transactor = new TransactorSimple(this, event.direction);
		transactor.add(event.item.getItemStack().copy(), true);
		if(inventory[inventory.length - 1] != null)
		{
			for(int i = 1; i < inventory.length; i++)
			{
				inventory[i - 1] = inventory[i];
			}
		}
		
		inventory[inventory.length - 1] = null;
		event.item.getItemStack().stackSize = 0;
		container.scheduleRenderUpdate();
		event.entity = null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList list = new NBTTagList();

		for(ItemStack stack : inventory) {
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
		NBTTagList list = nbttagcompound.getTagList("closedInventory", 10);
		for(int i = 0; i < list.tagCount() && i < inventory.length; i++) {
			NBTTagCompound stackTag = (NBTTagCompound) list.getCompoundTagAt(i);
			inventory[i] = ItemStack.loadItemStackFromNBT(stackTag);
		}

	}

	@Override
	public int getIconIndex(EnumFacing direction) {
		return 18 + (getStackInSlot(0) == null ? 0 : 1);
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
		if(inventory[i].stackSize == 0) {
			inventory[i] = null;
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inventory[i] = stack;
	}

	@Override
	public String getName() {
		return "pipeItemsClosed";
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) 
	{
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
	public IChatComponent getDisplayName()
	{
		return null;
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
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
		
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{

	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack requestedItem = inventory[index];
		
		inventory[index] = null;
		
		return requestedItem;
	}

}
