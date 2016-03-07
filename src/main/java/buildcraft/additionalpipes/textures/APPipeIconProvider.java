package buildcraft.additionalpipes.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.api.core.IIconProvider;

public class APPipeIconProvider implements IIconProvider {
	private IIcon icons[];
	
	//1 more than highest-numbered icon
	private final int iconCount = 41;

	public APPipeIconProvider() {
		icons = new IIcon[iconCount];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int iconIndex) {
		if(iconIndex >= iconCount)
			return null;
		return icons[iconIndex];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for(int i = 0; i < iconCount; i++) {
			icons[i] = iconRegister.registerIcon("additionalpipes:" + i);
		}
	}

	@Override
	public TextureAtlasSprite getIcon(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerIcons(TextureMap iconRegister)
	{
		// TODO Auto-generated method stub
		
	}

}