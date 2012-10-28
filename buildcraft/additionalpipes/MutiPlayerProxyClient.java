package buildcraft.additionalpipes;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.api.core.LaserKind;
import buildcraft.core.Box;
import buildcraft.transport.TransportProxyClient;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeChunkManager;

public class MutiPlayerProxyClient extends MutiPlayerProxy {
	public void registerRendering() {
		KeyBinding[] bindings = new KeyBinding[] { mod_AdditionalPipes.instance.showLaser };
		boolean[] repeatableBindings = new boolean[] { false };
		KeyBindingRegistry.registerKeyBinding(new KeyHandler(bindings, repeatableBindings));

		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_ITEM_TELEPORT_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStoneLiquid_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_POWER_TELEPORT_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStone_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStone_FILE_POWERED);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE_CLOSED);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_ADVANCEDWOOD_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_Insertion_FILE);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "0.png");
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "1.png");
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "2.png");
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "3.png");
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "4.png");
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_FILE_BASE + "5.png");
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.DEFUALT_RedStoneLiquid_FILE_POWERED);
		MinecraftForgeClient.preloadTexture(mod_AdditionalPipes.PATH + "gui/chunkloader.png");
	}

	public void registerPipeRendering(Item res){
		MinecraftForgeClient.registerItemRenderer(res.shiftedIndex, TransportProxyClient.pipeItemRenderer);
	}

}
