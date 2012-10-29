package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.GuiHandler;
import buildcraft.api.tools.IToolWrench;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicTeleport extends PipeLogic {

	public int freq = 0;
	public boolean canReceive = false;
	public String owner = "";

	public PipeLogicTeleport() {
		super();
	}

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if(!AdditionalPipes.proxy.isServer(player.worldObj)) return true;
		if (owner == null || owner.equalsIgnoreCase("")) {
			owner = player.username;
		}
		ItemStack equippedItem = player.getCurrentEquippedItem();
		if (equippedItem != null) {
			if (AdditionalPipes.isPipe(equippedItem.getItem()))  {
				return false;
			}
			if (equippedItem.getItem() instanceof IToolWrench && !AdditionalPipes.wrenchOpensGui) {
				return false;
			}
		}
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_TP, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		Pipe pipe = null;
		if (tile instanceof TileGenericPipe) {
			pipe = ((TileGenericPipe) tile).pipe;
		}
		if (BuildCraftTransport.alwaysConnectPipes) {
			return super.isPipeConnected(tile);
		}
		else {
			if (pipe == null) {
				return false;
			}
			if (container.pipe.getClass().equals(pipe.getClass()) && super.isPipeConnected(tile)) {
				return true;
			}
			return true;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("freq", freq);
		nbttagcompound.setBoolean("canReceive", canReceive);
		nbttagcompound.setString("owner", owner);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		freq = nbttagcompound.getInteger("freq");
		canReceive = nbttagcompound.getBoolean("canReceive");
		owner = nbttagcompound.getString("owner");
	}

}
