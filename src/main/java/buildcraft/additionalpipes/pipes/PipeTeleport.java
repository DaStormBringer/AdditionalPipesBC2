package buildcraft.additionalpipes.pipes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.PipeType;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.PlayerUtils;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeWire;
import buildcraft.transport.Gate;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.TileGenericPipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class PipeTeleport<pipeType extends PipeTransport> extends APPipe<pipeType> implements ITeleportPipe {
	protected static final Random rand = new Random();

	private int frequency = 0;
	// 0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	public byte state = 1;

	public UUID ownerUUID;
	public String ownerName = "";
	
	public int[] network = new int[0];
	public boolean isPublic = false;
	
	public final PipeType type;

	public PipeTeleport(pipeType transport, Item item, PipeType type)
	{
		super(transport, item);
		this.type = type;
		
		
		//initialize the static reflection fields
		if(updateSignalState == null)
		{	
			try
			{				
				//go up from PipeTeleportX to Pipe
				Class<?> pipeClass = getClass().getSuperclass().getSuperclass().getSuperclass();
	
				updateSignalState = pipeClass.getDeclaredMethod("updateSignalState", new Class<?>[]{PipeWire.class});
				
				updateSignalState.setAccessible(true);
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public byte getState()
	{
		return state;
	}

	public void setState(byte state)
	{
		this.state = state;
	}

	public UUID getOwnerUUID()
	{
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID)
	{
		this.ownerUUID = ownerUUID;
	}

	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	public boolean isPublic()
	{
		return isPublic;
	}

	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}

	public PipeType getType()
	{
		return type;
	}

	@Override
	public void initialize() {
		super.initialize();
		TeleportManager.instance.add(this, frequency);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		TeleportManager.instance.remove(this, frequency);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		TeleportManager.instance.remove(this, frequency);
	}
	
	@Override
	public boolean blockActivated(EntityPlayer player, ForgeDirection direction) {
		if(player.worldObj.isRemote)
			return true;
		
		if(ownerUUID == null)
		{
			ownerUUID = PlayerUtils.getUUID(player);
			ownerName = player.getCommandSenderName();
		}
		
		if(!isPublic)
		{
			//test for player name change
			if(PlayerUtils.getUUID(player).equals(ownerUUID))
			{
				if(!player.getCommandSenderName().equals(ownerName))
				{
					ownerName = player.getCommandSenderName();
				}
			}
			else
			{
				//access denied
				player.addChatMessage(new ChatComponentText("Access denied!  This pipe belongs to " + ownerName));
				
				//if we return false, this method can get called again with a different side, and it will show the message again
				return true;
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

		return super.canPipeConnect(tile, side);
	}

	@Override
	public boolean outputOpen(ForgeDirection to) {
		return container.isPipeConnected(to);
	}
	
	// Teleport Gates stuff
	// ---------------------------------------------
	
	//we need to access stuff private to Pipe, so we use reflection to do it.	
	static Method updateSignalState;

	@Override
	public void updateSignalState() 
	{
		ArrayList<PipeTeleport<?>> otherTeleportPipes = TeleportManager.instance.<PipeTeleport<?>>getConnectedPipes(this, true, true);
		
		for (PipeWire wire : PipeWire.values()) 
		{
			if(wireSet[wire.ordinal()])
			{
				myUpdateSignalStateForColor(wire, otherTeleportPipes);
			}
		}
	}

	//we cannot override this because it is private
	//so we override the function that calls it and invoke our version.
	private void myUpdateSignalStateForColor(PipeWire wire, ArrayList<PipeTeleport<?>> otherTeleportPipes)
	{
		try
		{
			int prevStrength = wireSignalStrength[wire.ordinal()];
			boolean isBroadcast = false;

			for (Gate g : gates) {
				if (g != null && (g.broadcastSignal & (1 << wire.ordinal())) != 0) {
					isBroadcast = true;
					break;
				}
			}
			
			//find connected pipes
			ArrayList<Pipe<?>> connectedPipes = new ArrayList<Pipe<?>>();
			int maxStrength = 0;

			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = container.getTile(dir);
				if (tile instanceof IPipeTile)
				{
					Pipe<?> pipe = (Pipe<?>) ((IPipeTile) tile).getPipe();
					if (isWireConnectedTo(tile, pipe, wire, dir))
					{
						connectedPipes.add(pipe);
						
						//may as well do this now instead of looping back through again later
						int pipeStrength = pipe.wireSignalStrength[wire.ordinal()];
						if (pipeStrength > maxStrength) 
						{
							maxStrength = pipeStrength;
						}
					}
				}
			}
			
			connectedPipes.addAll(otherTeleportPipes);

			if (isBroadcast) {
				if (prevStrength < 255)
				{
					myPropagateSignalState(wire, 255, connectedPipes);
				}
			} else
			{
				//look for a signal
				
				
				for (Pipe<?> pipe : otherTeleportPipes)
				{
					int pipeStrength = pipe.wireSignalStrength[wire.ordinal()];
					if (pipeStrength > maxStrength) {
						maxStrength = pipeStrength;
					}
				}

				if (maxStrength > prevStrength && maxStrength > 1) {
					wireSignalStrength[wire.ordinal()] = maxStrength - 1;
				} else {
					wireSignalStrength[wire.ordinal()] = 0;
				}

				if (prevStrength != wireSignalStrength[wire.ordinal()]) {
					container.scheduleRenderUpdate();
				}

				if (wireSignalStrength[wire.ordinal()] == 0) {
					for (Pipe<?> p : connectedPipes) 
					{
						if (p.wireSignalStrength[wire.ordinal()] > 0) {
							updateSignalState.invoke(p, wire);
						}
					}
				} else {
					for (Pipe<?> p : connectedPipes) {
						if (p.wireSignalStrength[wire.ordinal()] < (wireSignalStrength[wire.ordinal()] - 1)) {
							updateSignalState.invoke(p, wire);
						}
					}
				}
			}
			
		}
		
		//Man, I really want to be able to use Multi-Catch here
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{	
			e.printStackTrace();
		}
		
		
	}
	
	private void myPropagateSignalState(PipeWire wire, int strength, ArrayList<Pipe<?>> connectedPipes) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
	{
		wireSignalStrength[wire.ordinal()] = strength;
		for (Pipe<?> pipe : connectedPipes)
		{
			if (strength == 0)
			{
				if (pipe.wireSignalStrength[wire.ordinal()] > 0)
				{
					updateSignalState.invoke(pipe, wire);
				}
			} 
			else
			{
				if (pipe.wireSignalStrength[wire.ordinal()] < strength) 
				{
					updateSignalState.invoke(pipe, wire);
				}
			}
		}
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
	
	public boolean canReceive()
	{
		return (state & 0x2) > 0;
	}
	
	public boolean canSend()
	{
		return (state & 0x1) > 0;
	}
}
