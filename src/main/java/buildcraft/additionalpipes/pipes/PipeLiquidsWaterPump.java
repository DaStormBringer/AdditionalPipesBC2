package buildcraft.additionalpipes.pipes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.transport.PipeTransportFluids;

public class PipeLiquidsWaterPump extends APPipe<PipeTransportFluids> {
	private static final int ICON = 24;
	private static final Block water = Blocks.water;

	private PipeTransportFluids transport;

	public PipeLiquidsWaterPump(Item item) {
		super(new PipeTransportFluids(), item);
		transport = (PipeTransportFluids) super.transport;
		transport.flowRate = 80;
		transport.travelDelay = 4;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(AdditionalPipes.proxy.isServer(getWorld()) && getWorld().getBlockState(container.getPos().offsetDown()).getBlock() == water)
		{
			transport.fill(EnumFacing.DOWN, new FluidStack(FluidRegistry.WATER, 100), true);
		}
	}

	@Override
	public int getIconIndex(EnumFacing direction) {
		return ICON;
	}

}
