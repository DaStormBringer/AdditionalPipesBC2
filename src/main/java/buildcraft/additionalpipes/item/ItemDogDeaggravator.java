package buildcraft.additionalpipes.item;

import java.util.Iterator;
import java.util.List;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.utils.Log;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDogDeaggravator extends Item
{	
	public static final String NAME = "dogDeaggravator";
	
	public ItemDogDeaggravator()
	{
		setRegistryName(NAME);
		setCreativeTab(AdditionalPipes.instance.creativeTab);
		setMaxStackSize(1);
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		//this code adapted from EntityAIHurtByTarget.startExecuting()
		double horizontalRange = 16;
        List<EntityWolf> list = world.getEntitiesWithinAABB(EntityWolf.class, new AxisAlignedBB(player.posX, player.posY, player.posZ,
        		player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D).expand(horizontalRange, 10.0D, horizontalRange));
        Iterator<EntityWolf> iterator = list.iterator();
        int wolfCounter = 0;

        while(iterator.hasNext())
        {
            EntityWolf wolf = iterator.next();

            if(wolf.isTamed() && wolf.isOnSameTeam(player))
            {
            	++wolfCounter;
                wolf.setAttackTarget(null);
            }
        }
        
        world.playSound(player, player.posX, player.posY, player.posZ, "additionalpipes:bellRing", 1, 1);
        Log.debug("Cleared attack target on " + wolfCounter + " wolves.");
        
        return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, );

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		list.add(I18n.format("tooltip.dogDeaggravator"));
	}
	


    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item parItem, CreativeTabs parTab, NonNullList parListSubItems)
    {
        parListSubItems.add(new ItemStack(this, 1));
    }


}