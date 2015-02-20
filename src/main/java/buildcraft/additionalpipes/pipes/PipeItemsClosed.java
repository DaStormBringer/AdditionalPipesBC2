package buildcraft.additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.core.inventory.Transactor;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.core.utils.Utils;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsClosed extends APPipe<PipeTransportItems> implements IInventory {

	private ItemStack[] inventory = new ItemStack[10];

	public PipeItemsClosed(Item item) {
		super(new PipeTransportItems(), item);
		//((PipeTransportItems) transport).travelHook = this;
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		ItemStack equippedItem = player.getCurrentEquippedItem();
		if(equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem())) {
			return false;
		}
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_CLOSED, getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public void dropContents() {
		super.dropContents();
		Utils.preDestroyBlock(getWorld(), container.xCoord, container.yCoord, container.zCoord);
	}
	
	public void eventHandler(PipeEventItem.DropItem event)
	{
		Transactor transactor = new TransactorSimple(this);
		transactor.add(event.item.getItemStack().copy(), ForgeDirection.UNKNOWN, true);
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
	public int getIconIndex(ForgeDirection direction) {
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
	public String getInventoryName() {
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
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void markDirty() {
		container.markDirty();
		
	}

	@Override
	public void openInventory() 
	{
		
	}

	@Override
	public void closeInventory()
	{
		
	}

}
