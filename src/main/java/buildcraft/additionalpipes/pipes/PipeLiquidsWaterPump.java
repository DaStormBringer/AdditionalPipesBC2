package buildcraft.additionalpipes.pipes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.additionalpipes.APConfiguration;
import buildcraft.transport.PipeTransportFluids;

public class PipeLiquidsWaterPump extends APPipe<PipeTransportFluids> {
	private static final int ICON = 24;
	private static final Block water = Blocks.water;

	public PipeLiquidsWaterPump(Item item)
	{
		super(new PipeTransportFluids(), item);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(getClass());
	}

	@Override
	public void updateEntity() 
	{
		super.updateEntity();
        if(getWorld().getBlockState(container.getPos().down()).getBlock() == water)
		{
			transport.fill(EnumFacing.DOWN, new FluidStack(FluidRegistry.WATER, APConfiguration.waterPumpWaterPerTick), true);

		}
	}

	@Override
	public int getIconIndex(EnumFacing direction) {

		return ICON;
	}
	
	@Override
	public boolean canPipeConnect(TileEntity tile, EnumFacing side)
	{
		return side != EnumFacing.DOWN && super.canPipeConnect(tile, side);
	}

}
