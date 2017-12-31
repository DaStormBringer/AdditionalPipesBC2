package buildcraft.additionalpipes.pipes;

import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Class for all 3 types of switch pipe
 * @author jamie
 *
 */
public class PipeBehaviorSwitch extends APPipe
{

	public PipeBehaviorSwitch(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
	}

	public PipeBehaviorSwitch(IPipe pipe)
	{
		super(pipe);
	}

	@Override
	public int getTextureIndex(EnumFacing direction)
	{
		if(direction == null)
		{
			return 0;
		}
		
		return (canConnect() ? 0 : 1);
	}
	
	/*
	@Override
	public boolean canConnectRedstone() {
		return true;
	}
	*/
	
	

	/**
	 * Overload of canConnect() that takes no arguments, to call from getTextureIndex()
	 * @param facing
	 * @return
	 */
	private boolean canConnect()
	{
		return pipe.getHolder().getPipeWorld().isBlockPowered(getPos());
	}

	@Override
	public boolean canConnect(EnumFacing face, PipeBehaviour other)
	{
		return canConnect();
	}

	@Override
	public boolean canConnect(EnumFacing face, TileEntity oTile)
	{
		return canConnect();
	}

}
