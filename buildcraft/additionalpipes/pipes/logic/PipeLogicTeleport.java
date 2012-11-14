package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicTeleport extends PipeLogic {

	public int freq = 0;
	public boolean canReceive = false;
	public String owner = "";
	public boolean isPublic = false;

	@Override
	public boolean blockActivated(EntityPlayer player) {
		if(!AdditionalPipes.proxy.isServer(player.worldObj)) return true;
		if (owner == null || "".equalsIgnoreCase(owner)) {
			owner = player.username;
		}
		ItemStack equippedItem = player.getCurrentEquippedItem();
		if (equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem()))  {
			return false;
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
		} else {
			if(pipe != null && this.getClass().equals(pipe.logic.getClass())) {
				return false;
			}
			return pipe != null;
		}
	}

	@Override
	public boolean outputOpen(ForgeDirection to) {
		return isPipeConnected(container.getTile(to));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("freq", freq);
		nbttagcompound.setBoolean("canReceive", canReceive);
		nbttagcompound.setString("owner", owner);
		nbttagcompound.setBoolean("isPublic", isPublic);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		freq = nbttagcompound.getInteger("freq");
		canReceive = nbttagcompound.getBoolean("canReceive");
		owner = nbttagcompound.getString("owner");
		isPublic = nbttagcompound.getBoolean("isPublic");
	}

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeLogicTeleport logic) {
		if(logic.isPublic || logic.owner.equals(player.username) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

}
