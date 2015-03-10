package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.utils.DataUtils;
import buildcraft.transport.TileGenericPipe;

public class MessageTelePipeData implements IMessage, IMessageHandler<MessageTelePipeData, IMessage>
{
	public BlockPos position;
    public int[] locations;
    public String ownerUUID;
    public String ownerName;

    public MessageTelePipeData()
    {
    }

    public MessageTelePipeData(BlockPos position, int[] locations, UUID ownerUUID, String ownerName)
    {
    	this.position = position;
        this.locations = locations;
        this.ownerUUID = ownerUUID.toString();
        this.ownerName = ownerName;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	position = DataUtils.readPosition(buf);
        int locationsLength = buf.readInt();
        	
        this.locations = new int[locationsLength];
        
        for(int counter = 0; counter < locationsLength; ++counter)
        {
        	locations[counter] = buf.readInt();
        }
        		
        ownerUUID = ByteBufUtils.readUTF8String(buf);
        ownerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	DataUtils.writePosition(position, buf);
        buf.writeInt(locations.length);
        for(int location : locations)
        {
        	buf.writeInt(location);
        }
        ByteBufUtils.writeUTF8String(buf, ownerUUID);
        ByteBufUtils.writeUTF8String(buf, ownerName);
    }

    @Override
    public IMessage onMessage(MessageTelePipeData message, MessageContext ctx)
    {
        TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.position);

        PipeTeleport<?> pipe = (PipeTeleport<?>) ((TileGenericPipe) te).pipe;
		pipe.ownerUUID = UUID.fromString(message.ownerUUID);
		pipe.ownerName = message.ownerName;
		pipe.network = message.locations;

        return null;
    }

    @Override
    public String toString()
    {
        return "MessageTelePipeData";
    }
}
