package additionalpipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import additionalpipes.AdditionalPipes;
import additionalpipes.gui.GuiHandler;
import buildcraft.core.inventory.Transactor;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IItemTravelingHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;

public class PipeItemsClosed extends APPipe implements IInventory, IItemTravelingHook
{

	private final ItemStack[] inventory = new ItemStack[10];

	public PipeItemsClosed(int itemID)
	{
		super(new PipeTransportItems(), itemID);
		((PipeTransportItems) transport).travelHook = this;
	}

	@Override
	public boolean blockActivated(EntityPlayer player)
	{
		final ItemStack equippedItem = player.getCurrentEquippedItem();
		if ((equippedItem != null) && AdditionalPipes.isPipe(equippedItem.getItem())) return false;
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_CLOSED, getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public void dropContents()
	{
		super.dropContents();
		Utils.preDestroyBlock(getWorld(), container.xCoord, container.yCoord, container.zCoord);
	}

	@Override
	public void drop(PipeTransportItems transport, TravelingItem item)
	{
		final Transactor transactor = new TransactorSimple(this);
		transactor.add(item.getItemStack().copy(), ForgeDirection.UNKNOWN, true);
		if (inventory[inventory.length - 1] != null)
		{
			for (int i = 1; i < inventory.length; i++)
			{
				inventory[i - 1] = inventory[i];
			}
		}
		inventory[inventory.length - 1] = null;
		item.getItemStack().stackSize = 0;
		container.scheduleRenderUpdate();
	}

	@Override
	public void centerReached(PipeTransportItems transport, TravelingItem item)
	{
	}

	@Override
	public boolean endReached(PipeTransportItems transport, TravelingItem item, TileEntity tile)
	{
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		final NBTTagList list = new NBTTagList();

		for (final ItemStack stack : inventory)
		{
			if (stack != null)
			{
				final NBTTagCompound stackTag = new NBTTagCompound();
				stack.writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}

		nbttagcompound.setTag("closedInventory", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		final NBTTagList list = nbttagcompound.getTagList("closedInventory");
		for (int i = 0; (i < list.tagCount()) && (i < inventory.length); i++)
		{
			final NBTTagCompound stackTag = (NBTTagCompound) list.tagAt(i);
			inventory[i] = ItemStack.loadItemStackFromNBT(stackTag);
		}

	}

	@Override
	public int getIconIndex(ForgeDirection direction)
	{
		return 18 + (getStackInSlot(0) == null ? 0 : 1);
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i)
	{
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int amt)
	{
		final ItemStack stack = inventory[i].splitStack(amt);
		if (inventory[i].stackSize == 0)
		{
			inventory[i] = null;
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		final ItemStack stack = inventory[i];
		inventory[i] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack stack)
	{
		inventory[i] = stack;
	}

	@Override
	public String getInvName()
	{
		return "pipeItemsClosed";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void onInventoryChanged()
	{
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return false;
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}

}
