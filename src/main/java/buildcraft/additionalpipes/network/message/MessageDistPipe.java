package buildcraft.additionalpipes.network.message;

import buildcraft.additionalpipes.pipes.PipeBehaviorDistribution;
import buildcraft.transport.tile.TilePipeHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the properties of a Distribution Pipe from the GUI
 *
 */
public class MessageDistPipe implements IMessage, IMessageHandler<MessageDistPipe, IMessage>
{
	public BlockPos position;
	byte _index;
	int _newData;
	
    public MessageDistPipe()
    {
    }

    public MessageDistPipe(BlockPos position, byte index, int newData)
    {
    	this.position = position;
    	_index = index;
    	_newData = newData;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	position = BlockPos.fromLong(buf.readLong());
        _index = buf.readByte();
        _newData = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeLong(position.toLong());
        buf.writeByte(_index);
        buf.writeInt(_newData);
    }

    @Override
    public IMessage onMessage(MessageDistPipe message, MessageContext ctx)
    {
    	
    	World world = ctx.getServerHandler().playerEntity.getEntityWorld();
    	TileEntity te = world.getTileEntity(message.position);
		if(te instanceof TilePipeHolder)
		{
			PipeBehaviorDistribution pipe = (PipeBehaviorDistribution) ((TilePipeHolder) te).getPipe().getBehaviour();

			if(message._newData >= 0 && message._index >= 0 && message._index < pipe.distData.length) 
			{
				pipe.distData[message._index] = message._newData;
				boolean found = message._newData > 0;
				if(!found) {
					for(int i = 0; i < pipe.distData.length; i++) {
						if(pipe.distData[i] > 0) {
							found = true;
						}
					}
				}
				if(!found) {
					for(int i = 0; i < pipe.distData.length; i++) {
						pipe.distData[i] = 1;
					}
				}

			}
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageDistPipe";
    }
}
