package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import buildcraft.additionalpipes.api.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTelePipeData implements IMessage, IMessageHandler<MessageTelePipeData, IMessage>
{
    public int x, y, z;
    public int[] locations;
    public String ownerUUID;
    public String ownerName;

    public MessageTelePipeData()
    {
    }

    public MessageTelePipeData(int x, int y, int z, int[] locations, UUID ownerUUID, String ownerName)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.locations = locations;
        this.ownerUUID = ownerUUID.toString();
        this.ownerName = ownerName;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
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
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
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
        TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

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
