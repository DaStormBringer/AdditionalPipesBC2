package buildcraft.additionalpipes.pipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.api.core.IIconProvider;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;

public abstract class APPipe<pipeType extends PipeTransport> extends Pipe<pipeType> 
{
	public APPipe(pipeType transport, Item item) {
		super(transport, item);
	}
	
	@Override
	public IIconProvider getIconProvider()
	{
		return Textures.pipeIconProvider;
	}
	
	/**
	 * Inject an item into the pipe.  Don't call this if the pipe isn't an item pipe!
	 * 
	 * @param toInject the ItemStack to inject
	 * @param fromSide the side that the item should come from.
	 */
	protected TravelingItem injectItem(ItemStack toInject, EnumFacing fromSide)
	{		
		Vec3 entPos = Utils.convertMiddle(container.getPos()).add(Utils.convert(fromSide.getOpposite(), -0.5));
		
		
		TravelingItem entity = TravelingItem.make(entPos, toInject);
		((PipeTransportItems) transport).injectItem(entity, fromSide.getOpposite());
		
		return entity;
	}
	
	/**
	 * Make a traveling item suitable for injecting into this pipe from the given side.
]	 * 
	 * @param toInject the ItemStack to inject
	 * @param fromSide the side of the pipe that the item appears on
	 */
	protected TravelingItem makeTravelingItem(ItemStack toInject, EnumFacing fromSide)
	{		
		Vec3 entPos = Utils.convertMiddle(container.getPos()).add(Utils.convert(fromSide.getOpposite(), -0.5));
		
		
		return TravelingItem.make(entPos, toInject);		
	}
	
	
	/**
	 * Inject an item into the pipe.  Don't call this if the pipe isn't an item pipe!
	 * 
	 * @param toInject the ItemStack to inject
	 * @param fromSide the side that the item should come from.
	 */
	protected TravelingItem injectItemAtCenter(ItemStack toInject, EnumFacing fromSide)
	{		
		Vec3 entPos = Utils.convertMiddle(container.getPos());
		
		TravelingItem entity = TravelingItem.make(entPos, toInject);
		((PipeTransportItems) transport).injectItem(entity, fromSide.getOpposite());
		
		return entity;
	}
}
