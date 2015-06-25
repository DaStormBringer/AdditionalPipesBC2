package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import buildcraft.additionalpipes.gui.GuiJeweledPipe;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.SideFilterData;
import buildcraft.additionalpipes.utils.NetworkUtils;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the three option booleans of a Jeweled Pipe
 *
 */
public class MessageJeweledPipeOptionsClient implements IMessage, IMessageHandler<MessageJeweledPipeOptionsClient, IMessage>
{
	public int x, y, z;
	boolean[] acceptUnsorted;
	boolean[] matchNBT;
	boolean[] matchMeta;

    public MessageJeweledPipeOptionsClient()
    {
    }

    public MessageJeweledPipeOptionsClient(int x, int y,int z, SideFilterData[] sideFilters)
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	
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
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        acceptUnsorted = NetworkUtils.readBooleanArray(buf, GuiJeweledPipe.NUM_TABS); 
        matchNBT = NetworkUtils.readBooleanArray(buf, GuiJeweledPipe.NUM_TABS); 
        matchMeta = NetworkUtils.readBooleanArray(buf, GuiJeweledPipe.NUM_TABS); 
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        NetworkUtils.writeBooleanArray(buf, acceptUnsorted);
        NetworkUtils.writeBooleanArray(buf, matchNBT);
        NetworkUtils.writeBooleanArray(buf, matchMeta);
    }

    @Override
    public IMessage onMessage(MessageJeweledPipeOptionsClient message, MessageContext ctx)
    {
    	
        TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
		if(te instanceof TileGenericPipe)
		{
			PipeItemsJeweled pipe = (PipeItemsJeweled) ((TileGenericPipe) te).pipe;

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
