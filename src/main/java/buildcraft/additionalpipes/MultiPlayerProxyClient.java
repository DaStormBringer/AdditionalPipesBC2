package buildcraft.additionalpipes;

import buildcraft.additionalpipes.item.ItemDogDeaggravator;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.lib.client.sprite.SpriteHolderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultiPlayerProxyClient extends MultiPlayerProxy
{

	@Override
	public void registerKeyHandler()
	{
		// disabled since I cannot get this to work in 1.8
		//Log.info("Registering key handler(s)");

		//Keybindings.lasers = new KeyBinding("key.lasers", APConfiguration.laserKeyCode, AdditionalPipes.NAME);
		//ClientRegistry.registerKeyBinding(Keybindings.lasers);
		
		//MinecraftForge.EVENT_BUS.register(new KeyInputEventHandler());
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

	     
	}
	
	@Override
	public void registerSprites()
	{
		Textures.TRIGGER_PIPE_CLOSED = SpriteHolderRegistry.getHolder("additionalpipes:items/triggers/pipe_closed");
	}
}
