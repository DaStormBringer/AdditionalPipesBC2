package buildcraft.additionalpipes.gates;

import java.util.Collection;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerInternalSided;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class GateProvider implements ITriggerProvider {


	@Override
	public void addInternalTriggers(Collection<ITriggerInternal> triggers, IStatementContainer container)
	{
		PipeBehaviour behavior = ((TilePipeHolder)container.getTile()).getPipe().getBehaviour();
		
		if(behavior instanceof PipeBehaviorClosed)
		{
			triggers.add(AdditionalPipes.instance.triggerPipeClosed);
		}
	}

	@Override
	public void addInternalSidedTriggers(Collection<ITriggerInternalSided> triggers, IStatementContainer container,
			EnumFacing side)
	{
		// do nothing
		
	}

	@Override
	public void addExternalTriggers(Collection<ITriggerExternal> triggers, EnumFacing side, TileEntity tile)
	{
		// do nothing
		
	}

}
