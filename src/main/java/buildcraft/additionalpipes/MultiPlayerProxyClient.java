package buildcraft.additionalpipes;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.additionalpipes.keyboard.KeyInputEventHandler;
import buildcraft.additionalpipes.keyboard.Keybindings;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;

@SideOnly(Side.CLIENT)
public class MultiPlayerProxyClient extends MultiPlayerProxy
{

	@Override
	public void registerKeyHandler()
	{
		
		Log.info("Registering key handler(s)");

		Keybindings.lasers = new KeyBinding("key.lasers", APConfiguration.laserKeyCode, AdditionalPipes.NAME);
		ClientRegistry.registerKeyBinding(Keybindings.lasers);
		
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
	}

	@Override
	public void registerPipeRendering(Item res)
	{
		//MinecraftForgeClient.registerItemRenderer(res, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas)
	{
		try
		{
			Pipe<?> dummyPipe = clas.getConstructor(Item.class).newInstance(item);
			if(dummyPipe != null)
			{
				//item.setPipesIcons(dummyPipe.getIconProvider());
				// TODO look around
				item.setPipeIconIndex(dummyPipe.getIconIndex(EnumFacing.DOWN));
				// item.setTextureIndex(dummyPipe.getTextureIndexForItem());
			}
		} 
		catch(Exception e)
		{
			Log.error("MultiPlayerProxyClient.createPipeSpecial() failed with exception!");
			
			e.printStackTrace();
		}
	}
}
