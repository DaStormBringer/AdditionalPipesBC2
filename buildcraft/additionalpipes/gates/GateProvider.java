package buildcraft.additionalpipes.gates;

import java.util.LinkedList;

import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipe;

public class GateProvider implements ITriggerProvider {

	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipe pipe) {
		LinkedList <ITrigger> list = new LinkedList<ITrigger>();
		if(pipe instanceof PipeItemsClosed) {
			list.add(AdditionalPipes.instance.triggerPipeClosed);
		}
		return list;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		return null;
	}

}
