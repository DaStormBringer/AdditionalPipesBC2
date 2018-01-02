package buildcraft.additionalpipes.gates;

import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraftforge.items.CapabilityItemHandler;

public class TriggerPipeClosed extends APTrigger implements ITriggerInternal {

	public TriggerPipeClosed()
	{
		super("pipe_closed");
	}



	@Override
	public boolean isTriggerActive(IStatementContainer statement, IStatementParameter[] parameters)
	{
		PipeBehaviorClosed closedPipe = null;
		//this much casting feels unsafe
		try
		{
			closedPipe = (PipeBehaviorClosed) ((TilePipeHolder)statement.getTile()).getPipe().getBehaviour();
		}
		catch(RuntimeException ex)
		{
			Log.error("Failed to get reference to Closed Pipe:");
			ex.printStackTrace();
			return false;
		}
		
		// if the first ItemStack is null, then there are no items in the pipe and the trigger should be inactive
		return closedPipe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0) != null;
	}

	@Override
	public int maxParameters() 
	{
		return 0;
	}

	@Override
	public int minParameters() 
	{
		return 0;
	}

	@Override
	public IStatementParameter createParameter(int index) 
	{
		return null;
	}

	@Override
	public IStatement rotateLeft() 
	{
		return this;
	}



	@Override
	public IStatement[] getPossible()
	{
		return null;
	}


}
