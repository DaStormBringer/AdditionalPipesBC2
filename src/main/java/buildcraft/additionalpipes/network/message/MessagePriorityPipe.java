package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.transport.TileGenericPipe;


/**
 * Message that sets the properties of a Distribution Pipe from the GUI
 *
 */
public class MessagePriorityPipe implements IMessage, IMessageHandler<MessagePriorityPipe, IMessage>
{
	public BlockPos position;
	byte _index;
	int _newData;
	
    public MessagePriorityPipe()
    {
    }

    public MessagePriorityPipe(BlockPos position, byte index, int newData)
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
    public IMessage onMessage(MessagePriorityPipe message, MessageContext ctx)
    {
    	
    	World world = ctx.getServerHandler().playerEntity.worldObj;
    	TileEntity te = world.getTileEntity(message.position);
		if(te instanceof TileGenericPipe)
		{
			PipeItemsPriorityInsertion pipe = (PipeItemsPriorityInsertion) ((TileGenericPipe) te).pipe;

			if(message._newData >= 0 && message._index >= 0 && message._index < pipe.sidePriorities.length) {
				pipe.sidePriorities[message._index] = message._newData;
				boolean found = message._newData > 0;
				if(!found) {
					for(int i = 0; i < pipe.sidePriorities.length; i++) {
						if(pipe.sidePriorities[i] > 0) {
							found = true;
						}
					}
				}
				if(!found) {
					for(int i = 0; i < pipe.sidePriorities.length; i++) {
						pipe.sidePriorities[i] = 1;
					}
				}

			}
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessagePriorityPipe";
    }
}
