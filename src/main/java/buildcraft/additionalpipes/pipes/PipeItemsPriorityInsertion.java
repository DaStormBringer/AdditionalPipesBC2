/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;

import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.core.lib.inventory.ITransactor;
import buildcraft.core.lib.inventory.Transactor;
import buildcraft.transport.PipeTransportItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class PipeItemsPriorityInsertion extends APPipe {

	public int sidePriorities[] = { 1, 1, 1, 1, 1, 1 };

	public PipeItemsPriorityInsertion(Item item) {
		super(new PipeTransportItems(), item);
	}

	@Override
	public int getIconIndex(EnumFacing connection)
	{
		if(connection == null)
		{
			return 25;
		}
		
		switch(connection) {
		case DOWN: // -y
			return 26;
		case UP: // +y
			return 27;
		case NORTH: // -z
			return 28;
		case SOUTH: // +z
			return 29;
		case WEST: // -x
			return 30;
		case EAST: // +x
		default:
			return 25;
		}
	}
	
	public void eventHandler(PipeEventItem.FindDest event)
	{
		ArrayList<EnumFacing> result = new ArrayList<EnumFacing>();

		for(int checkingPriority = 6; checkingPriority >= 1; --checkingPriority)
		{
			boolean foundAny = false;
			
			for(EnumFacing side : EnumFacing.VALUES)
			{
				if(sidePriorities[side.ordinal()] == checkingPriority)
				{
					TileEntity entity = container.getTile(side);
					if (entity instanceof IInventory)
					{
						ITransactor transactor = Transactor.getTransactorFor(entity, side.getOpposite());
						if (transactor.add(event.item.getItemStack(), true).stackSize > 0)
						{
							result.add(side);
						}
						
						foundAny = true;
					}
				}
			}
			
			if(foundAny)
			{
				break;
			}
		}
		
		if(!result.isEmpty())
		{
			event.destinations.clear();
			event.destinations.addAll(result);
		}
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

		if(player.worldObj.isRemote) return true;
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_PRIORITY, container.getWorld(), container.getPos().getX(), container.getPos().getY(), container.getPos().getZ());

		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
			
		nbt.setIntArray("priorities", sidePriorities);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if(nbt.hasKey("priorities"))
		{
			sidePriorities = nbt.getIntArray("priorities");
		}
	}

}
