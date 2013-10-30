package additionalpipes.pipes;

import buildcraft.transport.PipeTransportFluids;

public class PipeSwitchLiquids extends PipeSwitch
{

	public PipeSwitchLiquids(int itemID)
	{
		super(new PipeTransportFluids(), itemID, 22);
	}

}
