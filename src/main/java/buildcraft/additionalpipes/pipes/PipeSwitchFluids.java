package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.PipeTransportFluids;


public class PipeSwitchFluids extends PipeSwitch<PipeTransportFluids>
{

	public PipeSwitchFluids(Item item) {
		super(new PipeTransportFluids(), item, 22);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(getClass());
	}
	
	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) 
	{
		return tile instanceof IPipeTile && super.canPipeConnect(tile, side);
	}
}
