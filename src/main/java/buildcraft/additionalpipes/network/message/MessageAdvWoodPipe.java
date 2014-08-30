package buildcraft.additionalpipes.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Message that signals an AdvWoodenPipe to switch its state
 *
 */
public class MessageAdvWoodPipe implements IMessage, IMessageHandler<MessageAdvWoodPipe, IMessage>
{
	public int x, y, z;
	
    public MessageAdvWoodPipe()
    {
    }

    public MessageAdvWoodPipe(int x, int y,int z)
    {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(MessageAdvWoodPipe message, MessageContext ctx)
    {
    	World world = ctx.getServerHandler().playerEntity.worldObj;
    	TileEntity te = world.getTileEntity(message.x, message.y, message.z);
    	if(te instanceof TileGenericPipe) {
			PipeItemsAdvancedWood pipe = (PipeItemsAdvancedWood) ((TileGenericPipe) te).pipe;
			pipe.transport.exclude = !pipe.transport.exclude;
		}
    	
    	return null;
    }

    @Override
    public String toString()
    {
        return "MessageDistPipe";
    }
}
