package buildcraft.additionalpipes.gates;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.api.gates.IGate;
import buildcraft.api.gates.IStatement;
import buildcraft.api.gates.IStatementParameter;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.transport.IPipeTile;

public class TriggerPipeClosed extends APTrigger implements ITrigger {

	public TriggerPipeClosed(String id) {
		super(id);
	}

	@Override
	public String getDescription() {
		return "Pipe Closed";
	}

	@Override
	public boolean isTriggerActive(IGate gate, ITriggerParameter[] parameters) {
		PipeItemsClosed closedPipe = (PipeItemsClosed) gate.getPipe();
		for(int i = 0; i < closedPipe.getSizeInventory(); i++) {
			if(closedPipe.getStackInSlot(i) != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getIconIndex() {
		return 0;
	}

	@Override
	public String getUniqueTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int maxParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int minParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatement rotateLeft() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ITrigger> getPipeTriggers(IPipeTile pipe) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		// TODO Auto-generated method stub
		return null;
	}
}
