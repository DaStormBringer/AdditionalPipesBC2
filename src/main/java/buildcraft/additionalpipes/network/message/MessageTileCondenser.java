package net.multiplemonomials.eer.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.multiplemonomials.eer.tileentity.TileEntityCondenser;
import net.multiplemonomials.eer.tileentity.TileEntityEE;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTileCondenser implements IMessage, IMessageHandler<MessageTileCondenser, IMessage>
{
    public int x, y, z;
    public byte orientation, state;
    public String customName, owner;
    public double leftoverEMC;

    public MessageTileCondenser()
    {
    }

    public MessageTileCondenser(TileEntityCondenser tileEntityCondenser)
    {
        this.x = tileEntityCondenser.xCoord;
        this.y = tileEntityCondenser.yCoord;
        this.z = tileEntityCondenser.zCoord;
        this.orientation = (byte) tileEntityCondenser.getOrientation().ordinal();
        this.state = (byte) tileEntityCondenser.getState();
        this.customName = tileEntityCondenser.getCustomName();
        this.owner = tileEntityCondenser.getOwner();
        this.leftoverEMC = tileEntityCondenser.getLeftoverEMC();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.orientation = buf.readByte();
        this.state = buf.readByte();
        int customNameLength = buf.readInt();
        this.customName = new String(buf.readBytes(customNameLength).array());
        int ownerLength = buf.readInt();
        this.owner = new String(buf.readBytes(ownerLength).array());
        this.leftoverEMC = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeByte(orientation);
        buf.writeByte(state);
        buf.writeInt(customName.length());
        buf.writeBytes(customName.getBytes());
        buf.writeInt(owner.length());
        buf.writeBytes(owner.getBytes());
        buf.writeDouble(leftoverEMC);
    }

    @Override
    public IMessage onMessage(MessageTileCondenser message, MessageContext ctx)
    {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

        if (tileEntity instanceof TileEntityEE)
        {
        	TileEntityCondenser tileCondenser = ((TileEntityCondenser) tileEntity);
        	tileCondenser.setOrientation(message.orientation);
        	tileCondenser.setState(message.state);
        	tileCondenser.setCustomName(message.customName);
        	tileCondenser.setOwner(message.owner);
        	tileCondenser.setLeftoverEMC(message.leftoverEMC);
        }

        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MessageTileEntityAlchemicalChest - x:%s, y:%s, z:%s, orientation:%s, state:%s, customName:%s, owner:%s", x, y, z, orientation, state, customName, owner);
    }
}
