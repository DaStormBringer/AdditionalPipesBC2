package additionalpipes.gates;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import additionalpipes.textures.Textures;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.gates.TriggerParameter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class APTrigger implements ITrigger
{

	private final int oldid;
	protected String id;

	public APTrigger(int oldid, String id)
	{
		this.oldid = oldid;
		this.id = id;
		ActionManager.registerTrigger(this);
	}

	@Override
	public String getUniqueTag()
	{
		return this.id;
	}

	@Override
	public int getLegacyId()
	{
		return oldid;
	}

	protected abstract int getIconIndex();

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon()
	{
		return Textures.actionIconProvider.getIcon(getIconIndex());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
	}

	@Override
	public boolean hasParameter()
	{
		return false;
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		return false;
	}

	@Override
	public final ITriggerParameter createParameter()
	{
		return new TriggerParameter();
	}
}
