package buildcraft.additionalpipes.network.message;

import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.transport.tile.TilePipeHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that signals an AdvWoodenPipe to change its include/exclude state
 *
 */
public class MessageAdvWoodPipe implements IMessage, IMessageHandler<MessageAdvWoodPipe, IMessage>
{
	public BlockPos position;
	public boolean exclude;
	
    public MessageAdvWoodPipe()
    {
    }

    public MessageAdvWoodPipe(BlockPos position, boolean exclude)
    {
    	this.position = position;
    	this.exclude = exclude;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        position = BlockPos.fromLong(buf.readLong());
        exclude = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(position.toLong());
        buf.writeBoolean(exclude);
    }

    @Override
    public IMessage onMessage(MessageAdvWoodPipe message, MessageContext ctx)
    {
    	World world = ctx.getServerHandler().player.getEntityWorld();
    	TileEntity te = world.getTileEntity(message.position);
    	
    	if(te instanceof TilePipeHolder) {
			PipeBehaviorAdvWood pipe = (PipeBehaviorAdvWood) ((TilePipeHolder) te).getPipe().getBehaviour();
			pipe.exclude = exclude;
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageAdvWoodPipe";
    }
}
