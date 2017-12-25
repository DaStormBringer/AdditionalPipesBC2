package buildcraft.additionalpipes.network.message;

import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.SideFilterData;
import buildcraft.transport.tile.TilePipeHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the three option booleans of a Jeweled Pipe on the client for a single side
 *
 */
public class MessageJeweledPipeOptionsServer implements IMessage, IMessageHandler<MessageJeweledPipeOptionsServer, IMessage>
{
	public BlockPos position;
	byte index; //1-indexed index of filter data that we are updating

	boolean acceptUnsorted;
	boolean matchNBT;
	boolean matchMeta;
    public MessageJeweledPipeOptionsServer()
    {
    }

    public MessageJeweledPipeOptionsServer(BlockPos position, byte index, SideFilterData filterData)
    {
    	this.position = position;
    	this.index = index;

    	acceptUnsorted = filterData.acceptsUnsortedItems();
    	matchMeta = filterData.matchMetadata();
    	matchNBT = filterData.matchNBT();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        position = BlockPos.fromLong(buf.readLong());
        index = buf.readByte();
        acceptUnsorted = buf.readBoolean();
        matchNBT = buf.readBoolean();
        matchMeta = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(position.toLong());
        buf.writeByte(index);
        buf.writeBoolean(acceptUnsorted);
        buf.writeBoolean(matchNBT);
        buf.writeBoolean(matchMeta);
    }

    @Override
    public IMessage onMessage(MessageJeweledPipeOptionsServer message, MessageContext ctx)
    {
    	
    	World world = ctx.getServerHandler().playerEntity.getEntityWorld();
    	TileEntity te = world.getTileEntity(message.position);
		if(te instanceof TilePipeHolder)
		{
			PipeItemsJeweled pipe = (PipeItemsJeweled) ((TilePipeHolder) te).getPipe().getBehaviour();

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
        return "MessageJeweledPipeOptionsServer";
    }
}
