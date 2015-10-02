package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
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
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side)
	{
		return tile instanceof IPipeTile;
	}
}
