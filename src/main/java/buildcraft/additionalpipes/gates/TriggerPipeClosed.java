package buildcraft.additionalpipes.gates;

import java.util.Collection;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.transport.TileGenericPipe;

public class TriggerPipeClosed extends APTrigger implements ITriggerInternal {

	public TriggerPipeClosed(String id) {
		super(id);
	}

	@Override
	public String getDescription() {
		return "Pipe Closed";
	}

	@Override
	public boolean isTriggerActive(IStatementContainer gate, IStatementParameter[] parameters) {
		PipeItemsClosed closedPipe = (PipeItemsClosed) ((TileGenericPipe)gate.getTile()).pipe;
		for(int i = 0; i < closedPipe.getSizeInventory(); i++) {
			if(closedPipe.getStackInSlot(i) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * MultipleMonomials here.  These functions below were added at some point between BC 4 and 6, and there's
	 * almost zero documentation, so I have no idea what most of them do. Help on implementing them correctly
	 * would be very much appreciated.
	 */
	
	@Override
	public int getIconIndex() {
		return 0;
	}

	@Override
	public String getUniqueTag() 
	{
		return "additionalpipes: trigger.pipeclosed";
	}

	@Override
	public int maxParameters() 
	{
		return 0;
	}

	@Override
	public int minParameters() 
	{
		return 0;
	}

	@Override
	public IStatementParameter createParameter(int index) 
	{
		return null;
	}

	@Override
	public IStatement rotateLeft() 
	{
		return this;
	}

}
