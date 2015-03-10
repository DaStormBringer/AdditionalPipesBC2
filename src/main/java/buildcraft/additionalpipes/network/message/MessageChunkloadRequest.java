package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import buildcraft.additionalpipes.AdditionalPipes;

public class MessageChunkloadRequest implements IMessage, IMessageHandler<MessageChunkloadRequest, IMessage>
{
    public MessageChunkloadRequest()
    {
    }

    @Override
    public IMessage onMessage(MessageChunkloadRequest message, MessageContext ctx)
    {
    	AdditionalPipes.instance.chunkLoadViewer.sendPersistentChunksToPlayer(ctx.getServerHandler().playerEntity);
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageChunkloadRequest";
    }

	@Override
	public void fromBytes(ByteBuf buf) {
		//Empty
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//Empty
		
	}
}
