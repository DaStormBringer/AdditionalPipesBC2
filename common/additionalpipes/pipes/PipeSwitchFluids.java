package additionalpipes.pipes;

import buildcraft.transport.PipeTransportFluids;

public class PipeSwitchFluids extends PipeSwitch
{

	public PipeSwitchFluids(int itemID)
	{
		super(new PipeTransportFluids(), itemID, 22);
	}

}
