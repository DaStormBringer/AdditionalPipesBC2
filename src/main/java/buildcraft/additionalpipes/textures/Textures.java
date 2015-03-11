package buildcraft.additionalpipes.textures;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class Textures {
	//public static final APActionTriggerIconProvider actionIconProvider = new APActionTriggerIconProvider();
	//public static final APPipeIconProvider pipeIconProvider = new APPipeIconProvider();

	public static void registerIcons(TextureMap map)
	{
	}
	
	// textures
	public static final ResourceLocation ITEMS = new ResourceLocation("textures/atlas/items.png");
	public static final ResourceLocation DISPENSER = new ResourceLocation("textures/gui/container/dispenser.png");
	
	public static final String TEXTURE_PATH = "textures";

	public static final ResourceLocation GUI_TELEPORT = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/blankSmallGui.png");
	public static final ResourceLocation GUI_ADVANCEDWOOD = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/advancedWoodGui.png");
	public static final ResourceLocation GUI_DISTRIBUTION = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/distributionGui.png");
}
