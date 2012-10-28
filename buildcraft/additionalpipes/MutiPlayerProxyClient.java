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
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_ITEM_TELEPORT);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_LIQUID_REDSTONE);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_LIQUID_TELEPORT);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_POWER_TELEPORT);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_REDSTONE);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_REDSTONE_POWERED);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_ADVANCEDWOOD_CLOSED);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_ADVANCEDWOOD);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_INSERTION);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_DISTRIBUTOR_BASE + "0.png");
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_DISTRIBUTOR_BASE + "1.png");
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_DISTRIBUTOR_BASE + "2.png");
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_DISTRIBUTOR_BASE + "3.png");
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_DISTRIBUTOR_BASE + "4.png");
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_DISTRIBUTOR_BASE + "5.png");
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_LIQUID_REDSTONE_POWERED);
		MinecraftForgeClient.preloadTexture(AdditionalPipes.TEXTURE_CHUNKLOADER);
	}

	@Override
	public void registerPipeRendering(Item res){
		MinecraftForgeClient.registerItemRenderer(res.shiftedIndex, TransportProxyClient.pipeItemRenderer);
	}

}
