package buildcraft.additionalpipes.pipes.logic;

import net.minecraft.tileentity.TileEntity;
import buildcraft.BuildCraftTransport;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;

public class PipeLogicWaterPump extends PipeLogic {

	@Override
	public boolean isPipeConnected(TileEntity tile) {
		if (BuildCraftTransport.alwaysConnectPipes)
			return super.isPipeConnected(tile);
		Pipe pipe = null;
		if (tile instanceof TileGenericPipe) {
			pipe = ((TileGenericPipe) tile).pipe;
		}
		if(pipe != null && pipe.logic instanceof PipeLogicWaterPump) {
			return false;
		}
		return pipe != null;
	}

}
