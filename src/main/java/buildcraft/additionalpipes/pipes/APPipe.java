package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class APPipe extends Pipe<PipeTransport> {

	public APPipe(PipeTransport transport, Item item) {
		super(transport, item);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIconProvider getIconProvider() {
		return Textures.pipeIconProvider;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public abstract int getIconIndex(net.minecraftforge.common.util.ForgeDirection direction);
}
