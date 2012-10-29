package buildcraft.additionalpipes;

import net.minecraft.src.Item;
import net.minecraft.src.KeyBinding;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class MutiPlayerProxyClient extends MutiPlayerProxy {
	@Override
	public void registerKeyHandler() {
		KeyBinding[] bindings = new KeyBinding[] { AdditionalPipes.laserKey };
		boolean[] repeatableBindings = new boolean[] { false };
		KeyBindingRegistry.registerKeyBinding(new KeyHandler(bindings, repeatableBindings));
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
