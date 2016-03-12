package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;

public abstract class APPipe<pipeType extends PipeTransport> extends Pipe<pipeType> 
{
	public APPipe(pipeType transport, Item item) {
		super(transport, item);
	}
	
	@Override
	public IIconProvider getIconProvider()
	{
		return Textures.pipeIconProvider;
	}
}
