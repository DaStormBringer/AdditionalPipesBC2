package buildcraft.additionalpipes.logic;

import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.mod_AdditionalPipes;
import buildcraft.core.network.TileNetworkData;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;
import buildcraft.api.tools.*;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class PipeLogicTeleport extends PipeLogic {

	@TileNetworkData public int freq = 0;
	@TileNetworkData public boolean canReceive = false;
	@TileNetworkData public String owner = "";

	protected int guiId;

	public PipeLogicTeleport(int guiId) {
		super();
		this.guiId = guiId;
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {

		if (owner == null || owner.equalsIgnoreCase("")) {
			owner = entityplayer.username;
		}

		ItemStack equippedItem = entityplayer.getCurrentEquippedItem();

		if (equippedItem != null) {

			if (mod_AdditionalPipes.isPipe(equippedItem.getItem()))  {
				return false;
			}

			if (equippedItem.getItem() instanceof IToolWrench && !mod_AdditionalPipes.wrenchOpensGui) {
				return false;
			}
		}

		entityplayer.openGui(mod_AdditionalPipes.instance, guiId, 
				container.worldObj, container.xCoord, container.yCoord, container.zCoord);

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

			if (this.container.pipe.getClass().equals(pipe.getClass()) && super.isPipeConnected(tile)) {
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
