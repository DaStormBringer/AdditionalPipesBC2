package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipePowerDiamond;

public class PipeSwitchPower extends PipeSwitch {

	public PipeSwitchPower(Item item) {
		super(new PipeTransportPower(), item, 16);
		((PipeTransportPower) transport).initFromPipe(PipePowerDiamond.class);
	}

}
