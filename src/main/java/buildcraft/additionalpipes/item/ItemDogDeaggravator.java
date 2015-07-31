package buildcraft.additionalpipes.item;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.utils.Log;

public class ItemDogDeaggravator extends Item
{	
	public static final String NAME = "dogDeaggravator";
	
	public ItemDogDeaggravator()
	{
		setUnlocalizedName(NAME);
		setTextureName(AdditionalPipes.MODID + ':' + NAME);
		setCreativeTab(AdditionalPipes.instance.creativeTab);
		setMaxStackSize(1);
	}
	
	@Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
		//this code adapted from EntityAIHurtByTarget.startExecuting()
		double horizontalRange = 16;
        List<?> list = world.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
        		player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D).expand(horizontalRange, 10.0D, horizontalRange));
        Iterator<?> iterator = list.iterator();
        int wolfCounter = 0;

        while(iterator.hasNext())
        {
            EntityWolf wolf = (EntityWolf)iterator.next();

            if(wolf.isTamed() && wolf.isOnSameTeam(player))
            {
            	++wolfCounter;
                wolf.setAttackTarget(null);
            }
        }
        
        world.playSoundAtEntity(player, "additionalpipes:bellRing", 1, 1);
        Log.debug("Cleared attack target on " + wolfCounter + " wolves.");
        
        return itemStack;

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		list.add(StatCollector.translateToLocal("tooltip.dogDeaggravator"));
	}
}