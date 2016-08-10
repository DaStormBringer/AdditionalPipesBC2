package buildcraft.additionalpipes.gates;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;

public class GateProvider implements ITriggerProvider {

	@Override
	public LinkedList<ITriggerInternal> getInternalTriggers(IStatementContainer container)
	{
		
		Pipe<?> pipe = ((TileGenericPipe)container.getTile()).pipe;
		
		LinkedList<ITriggerInternal> list = new LinkedList<ITriggerInternal>();
		if(pipe instanceof PipeItemsClosed)
		{
			list.add(AdditionalPipes.instance.triggerPipeClosed);
		}
		if(pipe instanceof PipeTeleport)
		{
			
		}
		return list;
	}

	@Override
	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
		return null;
	}

}
