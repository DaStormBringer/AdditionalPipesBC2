package buildcraft.additionalpipes.item;

import java.util.List;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.transport.item.ItemPipeHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//special pipe code
public class ItemPipeAP extends ItemPipeHolder
{
	String tooltip;
	
	/**
	 * 
	 * @param tooltip unlocalized key for the tooltip string
	 */
	public ItemPipeAP(PipeDefinition pipe, String tooltip) {
		super(pipe);
		this.tooltip = tooltip;
		
		setCreativeTab(AdditionalPipes.instance.creativeTab);
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		super.addInformation(stack, player, list, advanced);		
		list.add(I18n.format(tooltip));
	}
}