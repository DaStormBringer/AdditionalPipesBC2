package buildcraft.additionalpipes.pipes;

import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.transport.PipeTransportFluids;

public class PipeLiquidsWaterPump extends APPipe {
	private static final int ICON = 24;
	private static final Block water = Block.waterStill;

	private PipeTransportFluids transport;

	public PipeLiquidsWaterPump(int itemID) {
		super(new PipeTransportFluids(), itemID);
		transport = (PipeTransportFluids) super.transport;
		transport.flowRate = 80;
		transport.travelDelay = 4;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(AdditionalPipes.proxy.isServer(getWorld()) && getWorld().getBlockId(container.xCoord, container.yCoord - 1, container.zCoord) == water.blockID) {
			transport.fill(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.WATER, 100), true);
		}
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}

}
