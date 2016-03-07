package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.utils.DataUtils;
import buildcraft.transport.TileGenericPipe;

/**
 * Message that sets the properties of a Teleport Pipe from the GUI
 *
 */
public class MessageTelePipeUpdate implements IMessage, IMessageHandler<MessageTelePipeUpdate, IMessage>
{
	public BlockPos position;
	int _freq;
	boolean _isPublic;
	byte _state;
	int _newData;
	
    public MessageTelePipeUpdate()
    {
    }

    public MessageTelePipeUpdate(BlockPos position, int freq, boolean isPublic, byte index)
    {
    	this.position = position;
    	_freq = freq;
    	_isPublic = isPublic;
    	_state = index;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	position = DataUtils.readPosition(buf);
        _freq = buf.readInt();
        _isPublic = buf.readBoolean();
        _state = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	DataUtils.writePosition(position, buf);
        buf.writeInt(_freq);
        buf.writeBoolean(_isPublic);
        buf.writeByte(_state);
    }

    @Override
    public IMessage onMessage(MessageTelePipeUpdate message, MessageContext ctx)
    {
    	TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.position);
    	if(te instanceof TileGenericPipe) {
			PipeTeleport<?> pipe = (PipeTeleport<?>) ((TileGenericPipe) te).pipe;
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
        return "MessageTelePipeUpdate";
    }
}
