package buildcraft.additionalpipes.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class Textures {
	public static final APActionTriggerIconProvider actionIconProvider = new APActionTriggerIconProvider();
	public static final APPipeIconProvider pipeIconProvider = new APPipeIconProvider();
	public static Icon tetherTexture;

	public static void registerIcons(IconRegister iconRegister) {
		actionIconProvider.registerIcons(iconRegister);
		pipeIconProvider.registerIcons(iconRegister);
		tetherTexture = iconRegister.registerIcon("additionalpipes:tether");
	}
	
	// textures
	public static final ResourceLocation ITEMS = new ResourceLocation("/gui/items.png");
	public static final ResourceLocation DISPENSER = new ResourceLocation("gui/trap.png");
	
	public static final String BASE_PATH = "/mods/additionalpipes";
	public static final String TEXTURE_PATH = BASE_PATH + "/textures";

	public static final ResourceLocation GUI_TELEPORT = new ResourceLocation(TEXTURE_PATH + "/gui/blankSmallGui.png");
	public static final ResourceLocation GUI_ADVANCEDWOOD = new ResourceLocation(TEXTURE_PATH + "/gui/advancedWoodGui.png");
	public static final ResourceLocation GUI_DISTRIBUTION = new ResourceLocation(TEXTURE_PATH + "/gui/distributionGui.png");
}
