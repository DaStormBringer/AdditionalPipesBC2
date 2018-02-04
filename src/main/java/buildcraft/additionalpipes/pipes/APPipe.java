package buildcraft.additionalpipes.pipes;

import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.transport.pipe.flow.PipeFlowItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class APPipe extends PipeBehaviour 
{
	public APPipe(IPipe pipe) {
		super(pipe);
	}
	
	public APPipe(IPipe pipe, NBTTagCompound nbt) {
		super(pipe, nbt);
	}
	
	/**
	 * Inject an item into the pipe.  Don't call this if the pipe isn't an item pipe!
	 * 
	 * @param toInject the ItemStack to inject
	 * @param fromSide the side that the item should come from.
	 * @return the items that could not be injected for whatever reason
	 */
	protected ItemStack injectItem(ItemStack toInject, EnumFacing fromSide)
	{				
		return ((PipeFlowItems)pipe.getFlow()).injectItem(toInject, true, fromSide.getOpposite(), null, .1f);
	}
	
	/**
	 * Shorthand to get position of pipe
	 */
	public BlockPos getPos()
	{
		if(pipe!= null && pipe.getHolder() != null)
		{
			return pipe.getHolder().getPipePos();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 
	 * @return The translation key for the localized name of the pipe
	 */
	public String getUnlocalizedName()
	{
		return "item.pipe.ap." + pipe.getDefinition().identifier.getResourcePath() + ".name";
	}
	
	/**
	 * Returns true if this behavior is instantiated on the client
	 * @return
	 */
	protected boolean isClient()
	{
		if(pipe == null)
		{
			return false;
		}
		return pipe.getHolder().getPipeWorld().isRemote;
	}
	
	/**
	 * Returns true if this behavior is instantiated on a dedicated or integrated server
	 * @return
	 */
	protected boolean isServer()
	{
		if(pipe == null)
		{
			return false;
		}
		
		return !pipe.getHolder().getPipeWorld().isRemote;
	}
}
