package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Message that sets the properties of a Distribution Pipe from the GUI
 *
 */
public class MessageDistPipe implements IMessage, IMessageHandler<MessageDistPipe, IMessage>
{
	public int x, y, z;
	byte _index;
	int _newData;
	
    public MessageDistPipe()
    {
    }

    public MessageDistPipe(int x, int y,int z, byte index, int newData)
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

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageDistPipe message, MessageContext ctx)
    {
    	
    	World world = ctx.getServerHandler().playerEntity.worldObj;
    	TileEntity te = world.getTileEntity(message.x, message.y, message.z);
		if(te instanceof TileGenericPipe)
		{
			PipeItemsDistributor pipe = (PipeItemsDistributor) ((TileGenericPipe) te).pipe;

			if(message._newData >= 0 && message._index >= 0 && message._index < pipe.distData.length) {
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
