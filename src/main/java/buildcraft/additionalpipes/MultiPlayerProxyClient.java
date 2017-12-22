package buildcraft.additionalpipes;

import buildcraft.additionalpipes.item.ItemDogDeaggravator;
import buildcraft.additionalpipes.keyboard.KeyInputEventHandler;
import buildcraft.additionalpipes.keyboard.Keybindings;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultiPlayerProxyClient extends MultiPlayerProxy
{

	@Override
	public void registerKeyHandler()
	{
		
		Log.info("Registering key handler(s)");

		Keybindings.lasers = new KeyBinding("key.lasers", APConfiguration.laserKeyCode, AdditionalPipes.NAME);
		ClientRegistry.registerKeyBinding(Keybindings.lasers);
		
		MinecraftForge.EVENT_BUS.register(new KeyInputEventHandler());
	}

	@Override
	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas)
	{
		try
		{
			Pipe<?> dummyPipe = clas.getConstructor(Item.class).newInstance(item);
			if(dummyPipe != null)
			{
				item.setPipesIcons(dummyPipe.getIconProvider());
				// TODO look around
				item.setPipeIconIndex(dummyPipe.getIconIndex(EnumFacing.DOWN));
			}
			
			CoreProxy.proxy.postRegisterItem(item);
		} 
		catch(Exception e)
		{
			Log.error("MultiPlayerProxyClient.createPipeSpecial() failed with exception!");
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void setPipeTextureProvider(ItemPipe pipeItem)
	{
		pipeItem.setPipesIcons(Textures.pipeIconProvider);
	}
	
	@Override
	public void registerRendering()
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		
		if(APConfiguration.enableChunkloader)
		{
			renderItem.getItemModelMesher().register(Item.getItemFromBlock(AdditionalPipes.instance.blockTeleportTether), 0, 
					new ModelResourceLocation(AdditionalPipes.instance.blockTeleportTether.getRegistryName(), "inventory"));
		}
		
	     renderItem.getItemModelMesher().register(AdditionalPipes.instance.dogDeaggravator, 0, 
	    		 new ModelResourceLocation(AdditionalPipes.MODID + ":" + ItemDogDeaggravator.NAME, "inventory"));
	     
	     Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(AdditionalPipes.instance.blockTutorial), 0,
	    		 new ModelResourceLocation("additionalpipes:tutorial_block", "inventory"));
	     
	}
}
