package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.SideFilterData;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the three option booleans of a Jeweled Pipe on the client for all six sides
 *
 */
public class MessageJeweledPipeOptionsServer implements IMessage, IMessageHandler<MessageJeweledPipeOptionsServer, IMessage>
{
	public int x, y, z;
	byte index; //1-indexed index of filter data that we are updating

	boolean acceptUnsorted;
	boolean matchNBT;
	boolean matchMeta;
    public MessageJeweledPipeOptionsServer()
    {
    }

    public MessageJeweledPipeOptionsServer(int x, int y,int z, byte index, SideFilterData filterData)
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	this.index = index;

    	acceptUnsorted = filterData.acceptsUnsortedItems();
    	matchNBT = filterData.matchMetadata();
    	matchMeta = filterData.matchNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        index = buf.readByte();
        acceptUnsorted = buf.readBoolean();
        matchNBT = buf.readBoolean();
        matchMeta = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(index);
        buf.writeBoolean(acceptUnsorted);
        buf.writeBoolean(matchNBT);
        buf.writeBoolean(matchMeta);
    }

    @Override
    public IMessage onMessage(MessageJeweledPipeOptionsServer message, MessageContext ctx)
    {
    	
    	World world = ctx.getServerHandler().playerEntity.worldObj;
    	TileEntity te = world.getTileEntity(message.x, message.y, message.z);
		if(te instanceof TileGenericPipe)
		{
			PipeItemsJeweled pipe = (PipeItemsJeweled) ((TileGenericPipe) te).pipe;

			SideFilterData dataToUpdate = pipe.filterData[message.index - 1];
			dataToUpdate.setAcceptUnsortedItems(message.acceptUnsorted);
			dataToUpdate.setMatchNBT(message.matchNBT);
			dataToUpdate.setMatchMetadata(message.matchMeta);
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageJeweledPipe";
    }
}
