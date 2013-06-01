package buildcraft.additionalpipes.pipes;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.pipes.PipeLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class APPipe extends Pipe {

	public APPipe(PipeTransport transport, PipeLogic logic, int itemID) {
		super(transport, logic, itemID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIconProvider getIconProvider() {
		return Textures.pipeIconProvider;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public abstract int getIconIndex(ForgeDirection direction);
}
