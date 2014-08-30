package buildcraft.additionalpipes.gates;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.gates.StatementManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class APTrigger implements ITriggerProvider {

	protected String id;

	public APTrigger(String id)
	{
		this.id = id;
		StatementManager.registerTriggerProvider(this);
	}

	protected abstract int getIconIndex();

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return Textures.actionIconProvider.getIcon(getIconIndex());
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
	}
}
