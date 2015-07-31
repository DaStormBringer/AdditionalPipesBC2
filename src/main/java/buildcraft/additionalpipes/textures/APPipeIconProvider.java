package buildcraft.additionalpipes.textures;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class APPipeIconProvider implements IIconProvider {
	private IIcon icons[];
	
	//1 more than highest-numbered icon
	private final int iconCount = 40;

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

}