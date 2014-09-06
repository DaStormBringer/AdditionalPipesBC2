package buildcraft.additionalpipes.pipes;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.core.Position;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public abstract class PipeTeleport extends APPipe {
	protected static final Random rand = new Random();

	private int frequency = 0;
	// 00 = none, 01 = send, 10 = receive, 11 = both
	public byte state = 1;

	public String owner = "";
	public int[] network = new int[0];
	public boolean isPublic = false;

	public PipeTeleport(PipeTransport transport, Item item) {
		super(transport, item);
	}

	@Override
	public void initialize() {
		super.initialize();
		TeleportManager.instance.add(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		TeleportManager.instance.remove(this);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		TeleportManager.instance.remove(this);
	}
	
	@Override
	public boolean blockActivated(EntityPlayer player) {
		if(!AdditionalPipes.proxy.isServer(player.worldObj))
			return true;
		if(owner == null || "".equalsIgnoreCase(owner)) {
			owner = player.getCommandSenderName();
		}
		
		if(AdditionalPipes.instance.filterRightclicks)
		{
			ItemStack equippedItem = player.getCurrentEquippedItem();
			
			if (equippedItem != null && AdditionalPipes.isPipe(equippedItem.getItem()))
			{
				return false;
			}
		}
		
		player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_TP, getWorld(), container.xCoord, container.yCoord, container.zCoord);
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	public void setFrequency(int freq) {
		frequency = freq;
	}

	public int getFrequency() {
		return frequency;
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		Pipe<?> pipe = null;
		if(tile instanceof TileGenericPipe) {
			pipe = ((TileGenericPipe) tile).pipe;
		}
		if(pipe != null && pipe instanceof PipeTeleport)
			return false;
		return pipe != null;
	}

	@Override
	public boolean outputOpen(ForgeDirection to) {
		return container.isPipeConnected(to);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("freq", frequency);
		nbttagcompound.setByte("state", state);
		nbttagcompound.setString("owner", owner);
		nbttagcompound.setBoolean("isPublic", isPublic);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		frequency = nbttagcompound.getInteger("freq");
		state = nbttagcompound.getByte("state");
		owner = nbttagcompound.getString("owner");
		isPublic = nbttagcompound.getBoolean("isPublic");
	}

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeTeleport pipe) {
		if(pipe.isPublic || pipe.owner.equals(player.getCommandSenderName()) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

	public Position getPosition() {
		return new Position(container.xCoord, container.yCoord, container.zCoord);
	}
}
