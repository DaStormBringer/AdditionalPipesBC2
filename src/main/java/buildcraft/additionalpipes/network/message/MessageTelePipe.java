package buildcraft.additionalpipes.network.message;

import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the given EEREExtebdedPlayer as the player data for that player on the server
 *
 */
public class MessageTelePipe implements IMessage, IMessageHandler<MessageTelePipe, IMessage>
{
	NBTTagCompound _telePipeData;
	
    public MessageTelePipe()
    {
    }

    public MessageTelePipe(NBTTagCompound telePipeData)
    {
    	_telePipeData = telePipeData;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	_telePipeData = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	ByteBufUtils.writeTag(buf, _telePipeData);
    }

    @Override
    public IMessage onMessage(MessageTelePipe message, MessageContext ctx)
    {
    	if(te instanceof TileGenericPipe) {
			PipeTeleport pipe = (PipeTeleport) ((TileGenericPipe) te).pipe;
			// only allow the owner to change pipe state
			EntityPlayerMP entityPlayer = (EntityPlayerMP) player;
			if(!PipeTeleport.canPlayerModifyPipe(entityPlayer, pipe)) {
				entityPlayer.addChatComponentMessage(new ChatComponentText("You may not change pipe state."));
				return;
			}
			int frequency = data.readInt();
			if(frequency < 0) {
				frequency = 0;
			}
			pipe.setFrequency(frequency);
			pipe.state = (byte) data.read();
			pipe.isPublic = (data.read() == 1);
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "EERExtendedPlayerUpdate";
    }
}
