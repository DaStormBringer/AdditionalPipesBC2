package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipePowerIron;

public class PipeSwitchPower extends PipeSwitch<PipeTransportPower> {

	public PipeSwitchPower(Item item) {
		super(new PipeTransportPower(), item, 16);
		((PipeTransportPower) transport).initFromPipe(PipePowerIron.class);
	}

}
