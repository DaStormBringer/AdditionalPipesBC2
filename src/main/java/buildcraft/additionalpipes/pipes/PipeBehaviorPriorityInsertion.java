/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.gui.GuiHandler;
import buildcraft.api.core.EnumPipePart;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.lib.misc.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class PipeBehaviorPriorityInsertion extends APPipe {

	public byte sidePriorities[] = { 1, 1, 1, 1, 1, 1 };


	public PipeBehaviorPriorityInsertion(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
		
		sidePriorities = nbt.getByteArray("prioritiesArray");
	}

	public PipeBehaviorPriorityInsertion(IPipe pipe)
	{
		super(pipe);
	}

	@Override
	public int getTextureIndex(EnumFacing connection)
	{
		if(connection == null)
		{
			return 0;
		}
		
		return connection.ordinal();
	}
	
	@PipeEventHandler
    public void orderSides(PipeEventItem.SideCheck ordering) {
        for (EnumFacing face : EnumFacing.VALUES) 
        {
        	if(face != ordering.from)
        	{
                ordering.increasePriority(face, 100 + 10 * sidePriorities[face.ordinal()]); // note: PipeBehaviourClay adds 100 to priorities to override filters and things, so I'm following that precedent
        	}

        }
    }

	@Override
    public boolean onPipeActivate(EntityPlayer player, RayTraceResult trace, float hitX, float hitY, float hitZ, EnumPipePart part) 
	{
        if (EntityUtil.getWrenchHand(player) != null) 
        {
            return super.onPipeActivate(player, trace, hitX, hitY, hitZ, part);
        }
        
        if (!player.world.isRemote) 
        {
        	BlockPos pipePos = pipe.getHolder().getPipePos();
        	player.openGui(AdditionalPipes.instance, GuiHandler.PIPE_PRIORITY, pipe.getHolder().getPipeWorld(), pipePos.getX(), pipePos.getY(), pipePos.getZ());
        }
        return true;
    }

	@Override
	public NBTTagCompound writeToNbt() 
	{
		NBTTagCompound nbt = super.writeToNbt();
			
		nbt.setByteArray("prioritiesArray", sidePriorities);
		
		return nbt;
	}

}
