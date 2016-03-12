package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import buildcraft.transport.PipeTransportFluids;


public class PipeSwitchFluids extends PipeSwitch<PipeTransportFluids>
{

	public PipeSwitchFluids(Item item) {
		super(new PipeTransportFluids(), item, 22);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(getClass());
	}
}
