package buildcraft.additionalpipes;

import java.util.logging.Level;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.additionalpipes.keyboard.KeyInputEventHandler;
import buildcraft.additionalpipes.keyboard.Keybindings;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;

@SideOnly(Side.CLIENT)
public class MutiPlayerProxyClient extends MultiPlayerProxy
{

	@Override
	public void registerKeyHandler()
	{
		
		AdditionalPipes.instance.logger.info("Registering key handler(s)");

		Keybindings.lasers = new KeyBinding("key.lasers", AdditionalPipes.laserKeyCode, AdditionalPipes.NAME);
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
			AdditionalPipes.instance.logger.log(Level.SEVERE, "MultiPlayerProxyClient.createPipeSpecial() failed with exception!");
			
			e.printStackTrace();
		}
	}
}
