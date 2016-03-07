package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the properties of a Distribution Pipe from the GUI
 *
 */
public class MessagePriorityPipe implements IMessage, IMessageHandler<MessagePriorityPipe, IMessage>
{
	public int x, y, z;
	byte _index;
	int _newData;
	
    public MessagePriorityPipe()
    {
    }

    public MessagePriorityPipe(int x, int y,int z, byte index, int newData)
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	_index = index;
    	_newData = newData;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        _index = buf.readByte();
        _newData = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(_index);
        buf.writeInt(_newData);
    }

    @Override
    public IMessage onMessage(MessagePriorityPipe message, MessageContext ctx)
    {
    	
    	World world = ctx.getServerHandler().playerEntity.worldObj;
    	TileEntity te = world.getTileEntity(message.x, message.y, message.z);
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
