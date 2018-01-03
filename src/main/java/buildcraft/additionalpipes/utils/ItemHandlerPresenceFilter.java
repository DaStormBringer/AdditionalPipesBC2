package buildcraft.additionalpipes.utils;

import javax.annotation.Nonnull;

import buildcraft.api.core.IStackFilter;
import buildcraft.lib.misc.StackUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * StackFilter that puses an ItemHandler as a whitelist or blacklist
 * @author jamie
 *
 */
public class ItemHandlerPresenceFilter implements IStackFilter
{
	private IItemHandler handler;
	private boolean exclude;
	
	/**
	 * 
	 * @param handler
	 * @param exclude If true, the filter will accept any item but those contained in handler.  If false, the filter will accept any item that is contained in handler.
	 */
	public ItemHandlerPresenceFilter(IItemHandler handler, boolean exclude)
	{
		this.handler = handler;
		this.exclude = exclude;
	}
	
    @Override
    public boolean matches(@Nonnull ItemStack stack)
    {
        for (int slot = 0; slot < handler.getSlots(); slot++) 
        {
            if (StackUtil.isMatchingItem(handler.getStackInSlot(slot), stack)) 
            {
            	// if we are excluding, and we found a match, then return false
            	// if we are not excluding, and we found a match, return true
                return !exclude;
            }
        }
        
        return exclude;
    }
}
