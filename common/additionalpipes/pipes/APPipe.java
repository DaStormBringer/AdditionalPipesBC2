package additionalpipes.pipes;

import net.minecraftforge.common.ForgeDirection;
import additionalpipes.textures.Textures;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class APPipe extends Pipe
{

	public APPipe(PipeTransport transport, int itemID)
	{
		super(transport, itemID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIconProvider getIconProvider()
	{
		return Textures.pipeIconProvider;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public abstract int getIconIndex(ForgeDirection direction);
}
