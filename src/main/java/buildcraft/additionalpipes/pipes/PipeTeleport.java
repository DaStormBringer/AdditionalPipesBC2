package buildcraft.additionalpipes.pipes;

import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.PlayerUtils;
import buildcraft.api.core.Position;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;

public abstract class PipeTeleport<pipeType extends PipeTransport> extends APPipe<pipeType> {
	protected static final Random rand = new Random();

	private int frequency = 0;
	// 00 = none, 01 = send, 10 = receive, 11 = both
	public byte state = 1;

	public UUID ownerUUID;
	public String ownerName;
	
	public int[] network = new int[0];
	public boolean isPublic = false;

	public PipeTeleport(pipeType transport, Item item) {
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
		if(ownerUUID == null)
		{
			//                   getUUIDFromProfile()
			ownerUUID = PlayerUtils.getUUID(player);
			ownerName = player.getCommandSenderName();
		}
		
		//test for player name change
		if(PlayerUtils.getUUID(player).equals(ownerUUID))
		{
			if(!player.getCommandSenderName().equals(ownerName))
			{
				ownerName = player.getCommandSenderName();
			}
		}
		
		if(APConfiguration.filterRightclicks)
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
		if(ownerUUID != null)
		{
			nbttagcompound.setString("ownerUUID", ownerUUID.toString());
			nbttagcompound.setString("ownerName", ownerName);
		}
		nbttagcompound.setBoolean("isPublic", isPublic);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		frequency = nbttagcompound.getInteger("freq");
		state = nbttagcompound.getByte("state");
		if(nbttagcompound.hasKey("ownerUUID"))
		{
			ownerUUID = UUID.fromString(nbttagcompound.getString("ownerUUID"));
			ownerName = nbttagcompound.getString("ownerName");
		}
		isPublic = nbttagcompound.getBoolean("isPublic");
	}

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeTeleport<?> pipe) {
		if(pipe.isPublic || pipe.ownerUUID.equals(PlayerUtils.getUUID(player)) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

	public Position getPosition() {
		return new Position(container.xCoord, container.yCoord, container.zCoord);
	}
}
