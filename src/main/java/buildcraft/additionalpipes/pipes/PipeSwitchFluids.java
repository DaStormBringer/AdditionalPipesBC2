package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.IPipeTransportFluidsHook;
import buildcraft.transport.PipeTransportFluids;


public class PipeSwitchFluids extends PipeSwitch<PipeTransportFluids> implements IPipeTransportFluidsHook
{

	public PipeSwitchFluids(Item item) {
		super(new PipeTransportFluids(), item, 22);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(getClass());
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) 
	{
		if (!(container.getTile(from) instanceof IPipeTile)) {
			return 0;
		} else {
			return transport.fill(from, resource, doFill);
		}
	}

}
