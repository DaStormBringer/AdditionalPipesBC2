package buildcraft.additionalpipes.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.transport.ItemPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//special pipe code
public class ItemPipeAP extends ItemPipe
{
	String tooltip;
	
	/**
	 * 
	 * @param tooltip unlocalized key for the tooltip string
	 */
	public ItemPipeAP(String tooltip) {
		super(AdditionalPipes.instance.creativeTab);
		this.tooltip = tooltip;
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		super.addInformation(stack, player, list, advanced);		
		list.add(StatCollector.translateToLocal(tooltip));
	}
}