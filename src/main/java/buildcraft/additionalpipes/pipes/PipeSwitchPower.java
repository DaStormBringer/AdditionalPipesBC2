package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import buildcraft.transport.PipeTransportPower;

public class PipeSwitchPower extends PipeSwitch<PipeTransportPower> {

	public PipeSwitchPower(Item item) {
		super(new PipeTransportPower(), item, 16);
		transport.initFromPipe(getClass());
	}

}
