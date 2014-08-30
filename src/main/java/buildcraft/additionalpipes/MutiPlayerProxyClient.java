package buildcraft.additionalpipes;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.keyboard.Keybindings;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MutiPlayerProxyClient extends MutiPlayerProxy
{

	@Override
	public void registerKeyHandler()
	{
		Keybindings.lasers = new KeyBinding("key.lasers", AdditionalPipes.laserKeyCode, AdditionalPipes.NAME);

	}

	@Override
	public void registerPipeRendering(Item res)
	{
		MinecraftForgeClient.registerItemRenderer(res, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas)
	{
		try
		{
			Pipe<?> dummyPipe = clas.getConstructor(int.class).newInstance(item);
			if(dummyPipe != null)
			{
				item.setPipesIcons(dummyPipe.getIconProvider());
				// TODO look around
				item.setPipeIconIndex(dummyPipe.getIconIndex(ForgeDirection.VALID_DIRECTIONS[0]));
				// item.setTextureIndex(dummyPipe.getTextureIndexForItem());
			}
		} 
		catch(Exception e)
		{
			
		}
	}
}
