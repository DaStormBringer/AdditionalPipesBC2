package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the properties of a Teleport Pipe from the GUI
 *
 */
public class MessageTelePipe implements IMessage, IMessageHandler<MessageTelePipe, IMessage>
{
	public int x, y, z;
	int _freq;
	boolean _isPublic;
	byte _state;
	int _newData;
	
    public MessageTelePipe()
    {
    }

    public MessageTelePipe(int x, int y,int z, int freq, boolean isPublic, byte index)
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	_freq = freq;
    	_isPublic = isPublic;
    	_state = index;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        _state = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(_freq);
        buf.writeBoolean(_isPublic);
        buf.writeByte(_state);
    }

    @Override
    public IMessage onMessage(MessageTelePipe message, MessageContext ctx)
    {
    	TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
    	if(te instanceof TileGenericPipe) {
			PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
			// only allow the owner to change pipe state
			EntityPlayerMP entityPlayer = (EntityPlayerMP) ctx.getServerHandler().playerEntity;
			if(!PipeTeleport.canPlayerModifyPipe(entityPlayer, pipe)) {
				entityPlayer.addChatComponentMessage(new ChatComponentText("Sorry, You may not change pipe state."));
				return null;
			}
			int frequency = message._freq;
			if(frequency < 0) {
				frequency = 0;
			}
			pipe.setFrequency(frequency);
			pipe.state = (byte) message._state;
			pipe.isPublic = message._isPublic;
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageTelePipe";
    }
}
