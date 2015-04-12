package buildcraft.additionalpipes.gates;

import java.util.ArrayList;

import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.transport.PipeWire;
import buildcraft.transport.TileGenericPipe;


public class TriggerPhasedSignal extends APTrigger implements ITriggerInternal {

	
	PipeWire colour;

	public TriggerPhasedSignal(PipeWire colour)
	{
		super("additionalpipes:trigger.phasedSignal" + colour.getColor());
		this.colour = colour;
	}

	@Override
	public String getDescription() {
		switch(colour) {
		case RED:
			return "Red Phased Signal";
		case BLUE:
			return "Blue Phased Signal";
		case GREEN:
			return "Green Phased Signal";
		default:
		case YELLOW:
			return "Yellow Phased Signal";
		}
	}

	@Override
	public int getIconIndex() 
	{
		switch(colour) {
		case RED:
			return 2;
		case BLUE:
			return 4;
		case GREEN:
			return 6;
		default:
		case YELLOW:
			return 8;
		}
	}

	@Override
	public boolean isTriggerActive(IStatementContainer statement, IStatementParameter[] parameters)
	{
		PipeTeleport<?> teleportPipe = null;
		//this much casting feels unsafe
		try
		{
			teleportPipe = (PipeTeleport<?>) ((TileGenericPipe)statement.getTile()).pipe;
		}
		catch(RuntimeException ex)
		{
			Log.error("Failed to get reference to Teleport Pipe:");
			ex.printStackTrace();
			return false;
		}
		
		ArrayList<PipeTeleport<?>> network = TeleportManager.instance.getConnectedPipes(teleportPipe, true);
		for(PipeTeleport<?> testingPipe : network)
		{
			if(testingPipe.signalStrength[colour.ordinal()] > 0)
			{
				return true;
			}
		}
		
		return false;
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
	public String getUniqueTag()
	{
		return id;
	}

}
