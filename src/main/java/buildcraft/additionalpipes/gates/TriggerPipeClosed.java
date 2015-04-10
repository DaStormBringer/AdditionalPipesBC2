package buildcraft.additionalpipes.gates;

import net.minecraft.util.StatCollector;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.transport.TileGenericPipe;

public class TriggerPipeClosed extends APTrigger implements ITriggerInternal {

	public TriggerPipeClosed()
	{
		super("additionalpipes:trigger.pipeclosed");
	}

	@Override
	public String getDescription()
	{
		return StatCollector.translateToLocal("gate.pipeClosed");
	}

	@Override
	public boolean isTriggerActive(IStatementContainer statement, IStatementParameter[] parameters)
	{
		PipeItemsClosed closedPipe = null;
		//this much casting feels unsafe
		try
		{
			closedPipe = (PipeItemsClosed) ((TileGenericPipe)statement.getTile()).pipe;
		}
		catch(RuntimeException ex)
		{
			Log.error("Failed to get reference to Closed Pipe:");
			ex.printStackTrace();
			return false;
		}
		
		for(int i = 0; i < closedPipe.getSizeInventory(); i++) 
		{
			if(closedPipe.getStackInSlot(i) != null)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getIconIndex() {
		return 0;
	}

	@Override
	public String getUniqueTag() 
	{
		return id;
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

}
