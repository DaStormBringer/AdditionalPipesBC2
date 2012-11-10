package buildcraft.additionalpipes.gates;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.gates.Trigger;
import buildcraft.transport.ITriggerPipe;
import buildcraft.transport.Pipe;

public class TriggerPipeClosed extends Trigger implements ITriggerPipe {

	public TriggerPipeClosed(int id) {
		super(id);
	}

	@Override
	public String getTextureFile() {
		return AdditionalPipes.TEXTURE_TRIGGERS;
	}

	@Override
	public String getDescription() {
		return "Pipe Closed";
	}

	@Override
	public boolean isTriggerActive(Pipe pipe, ITriggerParameter parameter) {
		PipeItemsClosed closedPipe = (PipeItemsClosed) pipe;
		for(int i = 0; i < closedPipe.getSizeInventory(); i++) {
			if(closedPipe.getStackInSlot(i) != null) {
				return true;
			}
		}
		return false;
	}

}
