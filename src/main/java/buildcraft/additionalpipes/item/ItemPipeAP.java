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
	public ItemPipeAP() {
		super(AdditionalPipes.instance.creativeTab);
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
		String key = "tip." + stack.getItem().getClass().getSimpleName();
		
		list.add(StatCollector.translateToLocal(key));
	}
}