package buildcraft.additionalpipes.textures;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import buildcraft.additionalpipes.gui.GuiJeweledPipe;

public class Textures {
	public static final APPipeIconProvider pipeIconProvider = new APPipeIconProvider();

	public static void registerIcons(TextureMap map)
	{
		pipeIconProvider.registerIcons(map);
	}
	
	// textures
	public static final ResourceLocation ITEMS = new ResourceLocation("textures/atlas/items.png");
	public static final ResourceLocation DISPENSER = new ResourceLocation("textures/gui/container/dispenser.png");
	
	public static final String TEXTURE_PATH = "textures";

	public static final ResourceLocation GUI_TELEPORT = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/blankSmallGui.png");
	public static final ResourceLocation GUI_ADVANCEDWOOD = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/advancedWoodGui.png");
	public static final ResourceLocation GUI_DISTRIBUTION = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/distributionGui.png");
	public static final ResourceLocation GUI_PRIORITY = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/priorityGui.png");
	public static final ResourceLocation GUI_JEWELED = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/jeweledGui.png");
	
	public static final ResourceLocation GUI_OUTLINE_JEWELED[] = new ResourceLocation[GuiJeweledPipe.NUM_TABS];
	
	static
	{
		for(int tabNumber = 1; tabNumber <= GuiJeweledPipe.NUM_TABS; ++tabNumber)
		{
			 GUI_OUTLINE_JEWELED[tabNumber - 1] = new ResourceLocation("additionalpipes", TEXTURE_PATH + "/gui/jeweledPipeGuiOutline" + tabNumber + ".png");
		}
	}



}
