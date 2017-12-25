package buildcraft.additionalpipes.network.message;

import buildcraft.additionalpipes.gui.GuiJeweledPipe;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.SideFilterData;
import buildcraft.additionalpipes.utils.NetworkUtils;
import buildcraft.transport.tile.TilePipeHolder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


/**
 * Message that sets the three option booleans of a Jeweled Pipe
 *
 */
public class MessageJeweledPipeOptionsClient implements IMessage, IMessageHandler<MessageJeweledPipeOptionsClient, IMessage>
{
	BlockPos position;
	boolean[] acceptUnsorted;
	boolean[] matchNBT;
	boolean[] matchMeta;

    public MessageJeweledPipeOptionsClient()
    {
    }

    public MessageJeweledPipeOptionsClient(BlockPos position, SideFilterData[] sideFilters)
    {
    	this.position = position;
    	
    	acceptUnsorted = new boolean[GuiJeweledPipe.NUM_TABS];
    	matchNBT = new boolean[GuiJeweledPipe.NUM_TABS];
    	matchMeta = new boolean[GuiJeweledPipe.NUM_TABS];
    	
    	for(int sideNumber = 0; sideNumber < GuiJeweledPipe.NUM_TABS; ++sideNumber)
    	{
    		acceptUnsorted[sideNumber] = sideFilters[sideNumber].acceptsUnsortedItems();
    		matchNBT[sideNumber] = sideFilters[sideNumber].matchNBT();
    		matchMeta[sideNumber] = sideFilters[sideNumber].matchMetadata();
    	}
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	position = BlockPos.fromLong(buf.readLong());
        acceptUnsorted = NetworkUtils.readBooleanArray(buf, GuiJeweledPipe.NUM_TABS); 
        matchNBT = NetworkUtils.readBooleanArray(buf, GuiJeweledPipe.NUM_TABS); 
        matchMeta = NetworkUtils.readBooleanArray(buf, GuiJeweledPipe.NUM_TABS); 
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(position.toLong());
        NetworkUtils.writeBooleanArray(buf, acceptUnsorted);
        NetworkUtils.writeBooleanArray(buf, matchNBT);
        NetworkUtils.writeBooleanArray(buf, matchMeta);
    }

    @Override
    public IMessage onMessage(MessageJeweledPipeOptionsClient message, MessageContext ctx)
    {
    	
        TileEntity te = FMLClientHandler.instance().getClient().world.getTileEntity(message.position);
		if(te instanceof TilePipeHolder)
		{
			PipeItemsJeweled pipe = (PipeItemsJeweled) ((TilePipeHolder) te).getPipe().getBehaviour();

	    	for(int sideNumber = 0; sideNumber < GuiJeweledPipe.NUM_TABS; ++sideNumber)
	    	{
	    		pipe.filterData[sideNumber].setAcceptUnsortedItems(message.acceptUnsorted[sideNumber]);
	    		pipe.filterData[sideNumber].setMatchNBT(message.matchNBT[sideNumber]);
	    		pipe.filterData[sideNumber].setMatchMetadata(message.matchMeta[sideNumber]);
	    	}
	    	
	    	//update client GUI to reflect the new options
	    	GuiScreen currentScreen = FMLClientHandler.instance().getClient().currentScreen;
	    	if(currentScreen != null && currentScreen instanceof GuiJeweledPipe)
	    	{
	    		((GuiJeweledPipe)currentScreen).updateButtonsForTab();
	    	}
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageJeweledPipe";
    }
}
