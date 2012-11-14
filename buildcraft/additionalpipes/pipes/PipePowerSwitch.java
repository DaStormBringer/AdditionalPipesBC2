package buildcraft.additionalpipes.pipes;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.pipes.logic.PipeLogicPowerSwitch;
import buildcraft.transport.PipeTransportPower;

public class PipePowerSwitch extends APPipe {

	public PipePowerSwitch(int itemID) {
		super(new PipeTransportPower(), new PipeLogicPowerSwitch(), itemID);
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		return 16 + (logic.isPipeConnected(null) ? 0 : 1);
	}

	@Override
	public boolean canConnectRedstone() {
		return true;
	}

}
