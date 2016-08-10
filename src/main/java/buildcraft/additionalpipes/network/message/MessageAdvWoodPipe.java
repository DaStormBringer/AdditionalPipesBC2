package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.transport.TileGenericPipe;

/**
 * Message that signals an AdvWoodenPipe to switch its state
 *
 */
public class MessageAdvWoodPipe implements IMessage, IMessageHandler<MessageAdvWoodPipe, IMessage>
{
	public BlockPos position;
	
    public MessageAdvWoodPipe()
    {
    }

    public MessageAdvWoodPipe(BlockPos position)
    {
    	this.position = position;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        position = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(position.toLong());
    }

    @Override
    public IMessage onMessage(MessageAdvWoodPipe message, MessageContext ctx)
    {
    	World world = ctx.getServerHandler().playerEntity.worldObj;
    	TileEntity te = world.getTileEntity(message.position);
    	if(te instanceof TileGenericPipe) {
			PipeItemsAdvancedWood pipe = (PipeItemsAdvancedWood) ((TileGenericPipe) te).pipe;
			pipe.transport.exclude = !pipe.transport.exclude;
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageDistPipe";
    }
}
