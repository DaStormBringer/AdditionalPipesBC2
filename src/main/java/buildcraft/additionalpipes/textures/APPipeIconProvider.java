package buildcraft.additionalpipes.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.api.core.IIconProvider;

public class APPipeIconProvider implements IIconProvider {
	private TextureAtlasSprite icons[];
	
	//1 more than highest-numbered icon
	private final int iconCount = 41;

	public APPipeIconProvider() {
		icons = new TextureAtlasSprite[iconCount];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon(int iconIndex) {
		if(iconIndex >= iconCount)
			return null;
		return icons[iconIndex];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(TextureMap iconRegister) {
		for(int i = 0; i < iconCount; i++) {
			icons[i] = iconRegister.registerSprite(new ResourceLocation("additionalpipes:pipes/" + i));
		}
	}


}