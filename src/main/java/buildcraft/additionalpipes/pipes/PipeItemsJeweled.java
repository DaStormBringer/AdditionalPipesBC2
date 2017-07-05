/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.gui.GuiJeweledPipe;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsJeweled extends APPipe<PipeTransportItems> implements IDebuggable {

	public SideFilterData[] filterData = new SideFilterData[GuiJeweledPipe.NUM_TABS];
	public PipeItemsJeweled(Item item) 
	{
		super(new PipeTransportItems(), item);
		
		for(int index = 0; index < filterData.length; ++index)
		{
			filterData[index] = new SideFilterData();
		}
	}

	@Override
	public int getIconIndex(net.minecraftforge.common.util.ForgeDirection connection)
	{
		switch(connection) {
		case DOWN: // -y
			return 35;
		case UP: // +y
			return 36;
		case NORTH: // -z
			return 37;
		case SOUTH: // +z
			return 38;
		case WEST: // -x
			return 39;
		case EAST: // +x
		default:
			return 34;
		}
	}
	
	//adapted from Diamond Pipe code
	public void eventHandler(PipeEventItem.FindDest event)
	{
		LinkedList<ForgeDirection> filteredOrientations = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> defaultOrientations = new LinkedList<ForgeDirection>();

		ItemStack stack = event.item.getItemStack();
		
		// Filtered outputs
		for (ForgeDirection dir : event.destinations)
		{
			SideFilterData data = filterData[dir.ordinal()];

			if(data.matchesStack(stack))
			{
				filteredOrientations.add(dir);
			}
			else if(data.acceptsUnsortedItems())
			{
				defaultOrientations.add(dir);
			}
		}

		event.destinations.clear();

		if (!filteredOrientations.isEmpty()) 
		{
			event.destinations.addAll(filteredOrientations);
		}
		else
		{
			event.destinations.addAll(defaultOrientations);
		}
	}

	@Override
	public boolean blockActivated(EntityPlayer player, ForgeDirection direction)
	{
		if(player.isSneaking())
		{
			return false;
		}

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if(equipped != null) {
			if(APConfiguration.filterRightclicks && AdditionalPipes.isPipe(equipped)) {
				return false;
			}
		}

		if(player.worldObj.isRemote) return true;
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_JEWELED, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		NBTTagList filterList = new NBTTagList();
		for(int index = 0; index < filterData.length; ++index)
		{
			NBTTagCompound filterTag = new NBTTagCompound();
			filterData[index].writeToNBT(filterTag);
			filterList.appendTag(filterTag);
		}
		
		nbt.setTag("filterList", filterList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);

		NBTTagList filterList = nbt.getTagList("filterList", 10);
		for(int index = 0; index < filterData.length; ++index)
		{
			NBTTagCompound filterTag = filterList.getCompoundTagAt(index);
			filterData[index].readFromNBT(filterTag);
		}
	}
	
	@Override
	public void dropContents() 
	{
		super.dropContents();
		for(SideFilterData sideFilter : filterData)
		{
			InvUtils.dropItems(getWorld(), sideFilter, container.xCoord, container.yCoord, container.zCoord);
		}
	}

	@Override
	public void getDebugInfo(List<String> info, ForgeDirection side,
			ItemStack debugger, EntityPlayer player)
	{
		SideFilterData clickedSide = filterData[side.ordinal()];
		info.add("Accepts unsorted items: " + clickedSide.acceptsUnsortedItems());
		info.add("Matches NBT: " + clickedSide.matchNBT());
		info.add("Matches metadata: " + clickedSide.matchMetadata());
	}

}
