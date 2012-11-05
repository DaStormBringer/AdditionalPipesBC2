package buildcraft.additionalpipes;

import net.minecraft.src.Item;
import net.minecraft.src.KeyBinding;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class MutiPlayerProxyClient extends MutiPlayerProxy {
	
	@Override
	public void registerKeyHandler() {
		KeyHandler.laserKey = new KeyBinding("Toggle chunk loading boundries", 
				AdditionalPipes.instance.laserKeyCode);
		
		KeyBinding[] bindings = new KeyBinding[] { KeyHandler.laserKey };
		boolean[] repeatableBindings = new boolean[] { false };
		
		KeyHandler keyHandler = new KeyHandler(bindings, repeatableBindings);
		KeyBindingRegistry.registerKeyBinding(keyHandler);
	}

	@Override
	public void registerRendering() {
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_MASTER);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_PIPES);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_BLOCKS);
	}

	@Override
	public void registerPipeRendering(Item res){
		MinecraftForgeClient.registerItemRenderer(res.shiftedIndex, TransportProxyClient.pipeItemRenderer);
	}

}
