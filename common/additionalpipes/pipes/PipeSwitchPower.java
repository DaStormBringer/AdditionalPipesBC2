package additionalpipes.pipes;

import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipePowerDiamond;

public class PipeSwitchPower extends PipeSwitch
{

	public PipeSwitchPower(int itemID)
	{
		super(new PipeTransportPower(), itemID, 16);
		((PipeTransportPower) transport).initFromPipe(PipePowerDiamond.class);
	}

}
