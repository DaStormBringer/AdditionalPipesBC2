package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.GuiHandler;
import buildcraft.api.core.Orientations;
import buildcraft.api.tools.IToolWrench;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicTeleport extends PipeLogic {

	public int freq = 0;
	public boolean canReceive = false;
	public String owner = "";

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if(!AdditionalPipes.proxy.isServer(player.worldObj)) return true;
		if (owner == null || "".equalsIgnoreCase(owner)) {
			owner = player.username;
		}
		ItemStack equippedItem = player.getCurrentEquippedItem();
		if (equippedItem != null) {
			if (AdditionalPipes.isPipe(equippedItem.getItem()))  {
				return false;
			}
			if (equippedItem.getItem() instanceof IToolWrench && !AdditionalPipes.instance.wrenchOpensGui) {
				return false;
			}
		}
		if(owner.equals(player.username) || player.capabilities.isCreativeMode) {
			player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_TP, worldObj, xCoord, yCoord, zCoord);
		} else {
			player.sendChatToPlayer(AdditionalPipes.MODID + ": This pipe is owned by " + owner);
		}
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
		} else {
			if(pipe != null && this.getClass().equals(pipe.logic.getClass())) {
				return false;
			}
			return pipe != null;
		}
	}

	@Override
	public boolean outputOpen(Orientations to) {
		return isPipeConnected(container.getTile(to));
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
