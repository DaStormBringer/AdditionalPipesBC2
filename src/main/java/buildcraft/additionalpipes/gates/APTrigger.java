package buildcraft.additionalpipes.gates;

import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.StatementManager;

public abstract class APTrigger implements IStatement {

	protected String id;

	public APTrigger(String id)
	{
		this.id = id;
		StatementManager.statements.put(id, this);
	}

	protected abstract int getIconIndex();
}
