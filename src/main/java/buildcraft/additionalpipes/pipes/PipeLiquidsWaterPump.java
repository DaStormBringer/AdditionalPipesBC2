package buildcraft.additionalpipes.pipes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
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
		if(AdditionalPipes.proxy.isServer(getWorld()) && getWorld().getBlock(container.xCoord, container.yCoord - 1, container.zCoord) == water) {
			transport.fill(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.WATER, 100), true);
		}
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}

}
