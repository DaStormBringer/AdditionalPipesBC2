package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import buildcraft.additionalpipes.AdditionalPipes;
import net.minecraft.world.ChunkCoordIntPair;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageChunkloadData implements IMessage, IMessageHandler<MessageChunkloadData, IMessage>
{
	List<ChunkCoordIntPair> _chunksInRange;
	
    public MessageChunkloadData()
    {
    }
    
    public MessageChunkloadData(List<ChunkCoordIntPair> chunksInRange)
    {
    	_chunksInRange = chunksInRange;
    }

    @Override
    public IMessage onMessage(MessageChunkloadData message, MessageContext ctx)
    {
    	AdditionalPipes.instance.chunkLoadViewer.receivePersistentChunks(message._chunksInRange);
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageChunkloadData";
    }

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int _chunksInRangeLength = buf.readInt();
		
		_chunksInRange = new ArrayList<ChunkCoordIntPair>(_chunksInRangeLength);
		
		for(int counter = 0; counter < _chunksInRangeLength; ++counter)
		{
			_chunksInRange.add(new ChunkCoordIntPair(buf.readInt(), buf.readInt()));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(_chunksInRange.size());
		
		for(ChunkCoordIntPair pair : _chunksInRange)
		{
			buf.writeInt(pair.chunkXPos);
			buf.writeInt(pair.chunkZPos);
		}
	}
}
