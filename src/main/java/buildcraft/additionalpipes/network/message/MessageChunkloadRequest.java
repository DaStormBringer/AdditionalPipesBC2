package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import buildcraft.additionalpipes.AdditionalPipes;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
