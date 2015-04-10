package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import buildcraft.additionalpipes.gui.ContainerJeweledPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Message that sets the properties of a Distribution Pipe from the GUI
 *
 */
public class MessageJeweledPipeGuiTab implements IMessage, IMessageHandler<MessageJeweledPipeGuiTab, IMessage>
{
	byte _guiTab;
	
    public MessageJeweledPipeGuiTab()
    {
    }

    public MessageJeweledPipeGuiTab(byte guiTab)
    {
    	_guiTab = guiTab;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        _guiTab = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(_guiTab);
    }

    @Override
    public IMessage onMessage(MessageJeweledPipeGuiTab message, MessageContext ctx)
    {
    	    	
    	((ContainerJeweledPipe)ctx.getServerHandler().playerEntity.openContainer).setFilterTab(_guiTab);
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageDistPipe";
    }
}
