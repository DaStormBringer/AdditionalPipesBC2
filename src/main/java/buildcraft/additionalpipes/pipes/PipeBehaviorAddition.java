/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import buildcraft.api.inventory.IItemTransactor;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.IPipe.ConnectedType;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.lib.inventory.ItemTransactorHelper;
import buildcraft.lib.inventory.filter.ArrayStackFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PipeBehaviorAddition extends APPipe
{

	public PipeBehaviorAddition(IPipe pipe) {
		super(pipe);
	}
	
    public PipeBehaviorAddition(IPipe pipe, NBTTagCompound nbt) {
        super(pipe, nbt);
    }
	
    @PipeEventHandler
    public void orderSides(PipeEventItem.SideCheck ordering) {
        for (EnumFacing face : EnumFacing.VALUES) 
        {
        	if(face != ordering.from)
        	{
                ConnectedType type = pipe.getConnectedType(face);
                if (type == ConnectedType.TILE) 
                {
                	IItemTransactor trans = ItemTransactorHelper.getTransactor(pipe.getConnectedTile(face), face.getOpposite());
                    ItemStack possible = trans.extract(new ArrayStackFilter(ordering.stack), 1, 1, true);
                	
                    if(!possible.isEmpty())
                    {
                    	// cause the pipe to prefer this face above all others
                        ordering.increasePriority(face, 100);
                    }
                    

                }
        	}

        }
    }

}
