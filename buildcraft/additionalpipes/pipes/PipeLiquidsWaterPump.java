package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.transport.PipeTransportLiquids;
import buildcraft.transport.pipes.PipeLogic;
import net.minecraft.src.Block;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;

public class PipeLiquidsWaterPump extends APPipe {
	private static final Block water = Block.waterStill;

	private PipeTransportLiquids transport;

	public PipeLiquidsWaterPump(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogic(), itemID);
		transport = (PipeTransportLiquids) super.transport;
		transport.flowRate = 80;
		transport.travelDelay = 4;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(AdditionalPipes.proxy.isServer(worldObj) &&
				worldObj.getBlockId(xCoord, yCoord - 1, zCoord) == water.blockID) {
			transport.fill(ForgeDirection.UNKNOWN, new LiquidStack(water, 100), true);
		}
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		return 24;
	}

}
