/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.Arrays;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsDistributor extends APPipe<PipeTransportItems> {

	public int distData[] = { 1, 1, 1, 1, 1, 1 };
	public int distSide = 0;
	public int curTick = Integer.MAX_VALUE;

	public PipeItemsDistributor(Item item) {
		super(new PipeTransportItems(), item);
	}

	@Override
	public int getIconIndex(EnumFacing connection)
	{
		if(connection == null)
		{
			return 10;
		}
		switch(connection) 
		{
		case DOWN: // -y
			return 10;
		case UP: // +y
			return 11;
		case NORTH: // -z
			return 12;
		case SOUTH: // +z
			return 13;
		case WEST: // -x
			return 14;
		case EAST: // +x
		default:
			return 9;
		}
	}
	
	public void eventHandler(PipeEventItem.FindDest event)
	{
		LinkedList<EnumFacing> result = new LinkedList<EnumFacing>();

		//curTick used to be initialized to 0
		//but the issue was that when the first item stack passes through the pipe, it always sent it downward whether or not anything was connected
		//so I changed curTick to be initialized to Integer.MAX_VALUE so that it will look for the correct output. -JS
		if(curTick >= distData[distSide]) 
		{
			toNextOpenSide();
		}

		result.add(EnumFacing.values()[distSide]);
		curTick += event.item.getItemStack().stackSize;

		event.destinations.clear();
		event.destinations.addAll(result);
	}

	private void toNextOpenSide() {
		curTick = 0;
		for(int o = 0; o < distData.length; ++o) {
			distSide = (distSide + 1) % distData.length;
			if(distData[distSide] > 0 && container.isPipeConnected(EnumFacing.values()[distSide])) {
				break;
			}
		}
		// no valid inventories found, do nothing
	}

	@Override
	public boolean blockActivated(EntityPlayer player, EnumFacing direction) {
		if(player.isSneaking()) {
			return false;
		}

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if(equipped != null) {
			if(APConfiguration.filterRightclicks && AdditionalPipes.isPipe(equipped)) {
				return false;
			}
		}

		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_DIST,container.getWorld(), container.getPos().getX(), container.getPos().getY(), container.getPos().getZ());

		return true;
	}

	private void sanityCheck()
	{
		for(int d : distData) {
			if(d > 0) {
				return;
			}
		}
		for(int i = 0; i < distData.length; i++) {
			Arrays.fill(distData, 1);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("curTick", curTick);
		nbt.setInteger("distSide", distSide);
		for(int i = 0; i < distData.length; i++) {
			nbt.setInteger("distData" + i, distData[i]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		curTick = nbt.getInteger("curTick");
		distSide = nbt.getInteger("distSide");
		for(int i = 0; i < distData.length; i++) {
			distData[i] = nbt.getInteger("distData" + i);
		}
		sanityCheck();
	}

}
