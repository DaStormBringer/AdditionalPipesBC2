package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.api.core.Orientations;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.pipes.PipeLogic;

public abstract class APPipe extends Pipe {

	public APPipe(PipeTransport transport, PipeLogic logic, int itemID) {
		super(transport, logic, itemID);
	}

	@Override
	public String getTextureFile() {
		return AdditionalPipes.TEXTURE_PIPES;
	}

	@Override
	public abstract int getTextureIndex(Orientations direction);
}
