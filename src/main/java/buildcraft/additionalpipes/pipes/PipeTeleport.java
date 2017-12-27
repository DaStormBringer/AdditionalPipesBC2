package buildcraft.additionalpipes.pipes;

import java.util.Random;
import java.util.UUID;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.api.ITeleportPipe;
import buildcraft.additionalpipes.api.TeleportPipeType;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.additionalpipes.utils.PlayerUtils;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.lib.misc.EntityUtil;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;


public abstract class PipeTeleport extends APPipe implements ITeleportPipe 
{
	protected static final Random rand = new Random();

	private int frequency = 0;
	// 0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	public byte state = 1;

	public UUID ownerUUID;
	public String ownerName = "";
	
	public int[] network = new int[0]; // coordinates of connected pipes.  Used as a sort of cache variable by the teleport pipe GUI.
	public boolean isPublic = false;
	
	public final TeleportPipeType type;

	public PipeTeleport(IPipe pipe, TeleportPipeType type)
	{
		super(pipe);
		this.type = type;
	}
	
	public PipeTeleport(IPipe pipe, NBTTagCompound tagCompound, TeleportPipeType type)
	{
		super(pipe);
		this.type = type;
		readFromNBT(tagCompound);
	}
	
	@Override
	public byte getState()
	{
		return state;
	}

	@Override
	public void setState(byte state)
	{
		this.state = state;
	}

	@Override
	public UUID getOwnerUUID()
	{
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID)
	{
		this.ownerUUID = ownerUUID;
	}

	@Override
	public String getOwnerName()
	{
		return ownerName;
	}

	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}

	@Override
	public boolean isPublic()
	{
		return isPublic;
	}

	@Override
	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}
	
	@Override
	public TeleportPipeType getType()
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
	public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part)  
	{
		if(player.world.isRemote)
		{
			return true;
		}
		
		if(ownerUUID == null)
		{
			// set owner of pipe
			ownerUUID = PlayerUtils.getUUID(player);
			ownerName = player.getName();
		}
		
		if(!isPublic)
		{
			//test for player name change
			if(PlayerUtils.getUUID(player).equals(ownerUUID))
			{
				if(!player.getName().equals(ownerName))
				{
					ownerName = player.getName();
				}
			}
			else
			{
				//access denied
				player.sendMessage(new TextComponentTranslation("message.ap.accessdenied", ownerName));
				
				//if we return false, this method can get called again with a different side, and it will show the message again
				return true;
			}
		}
		
		if (EntityUtil.getWrenchHand(player) != null) 
        {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        
        if (!player.world.isRemote) 
        {
        	BlockPos pipePos = pipe.getHolder().getPipePos();
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_CLOSED, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
	}

	public void setFrequency(int freq) {
		frequency = freq;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}
	
	@Override
	public TilePipeHolder getContainer()
	{
		return (TilePipeHolder) pipe.getHolder();
	}

	@Override
	public boolean canConnect(EnumFacing face, PipeBehaviour other)
	{
		if(other instanceof PipeTeleport)
		{
			return false;
		}
		
		return super.canConnect(face, other);
	}
	

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbttagcompound = super.writeToNbt();
		nbttagcompound.setInteger("freq", frequency);
		nbttagcompound.setByte("state", state);
		if(ownerUUID != null)
		{
			nbttagcompound.setString("ownerUUID", ownerUUID.toString());
			nbttagcompound.setString("ownerName", ownerName);
		}
		nbttagcompound.setBoolean("isPublic", isPublic);
		
		return nbttagcompound;
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) 
	{
		frequency = nbttagcompound.getInteger("freq");
		state = nbttagcompound.getByte("state");
		if(nbttagcompound.hasKey("ownerUUID"))
		{
			ownerUUID = UUID.fromString(nbttagcompound.getString("ownerUUID"));
			ownerName = nbttagcompound.getString("ownerName");
		}
		isPublic = nbttagcompound.getBoolean("isPublic");
	}

	public static boolean canPlayerModifyPipe(EntityPlayer player, PipeTeleport pipe)
	{
		if(pipe.isPublic || pipe.ownerUUID.equals(PlayerUtils.getUUID(player)) || player.capabilities.isCreativeMode)
			return true;
		return false;
	}

	public BlockPos getPosition() {
		return getPos();
	}
	
	@Override
	public boolean canReceive()
	{
		return (state & 0x2) > 0;
	}
	
	@Override
	public boolean canSend()
	{
		return (state & 0x1) > 0;
	}
}
