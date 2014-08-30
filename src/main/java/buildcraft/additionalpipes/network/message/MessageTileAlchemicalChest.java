package net.multiplemonomials.eer.network.message;

import net.multiplemonomials.eer.tileentity.TileEntityAlchemicalChest;
import net.multiplemonomials.eer.tileentity.TileEntityEE;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class MessageTileAlchemicalChest implements IMessage, IMessageHandler<MessageTileAlchemicalChest, IMessage>
{
    public int x, y, z;
    public byte orientation, state;
    public String customName, owner;

    public MessageTileAlchemicalChest()
    {
    }

    public MessageTileAlchemicalChest(TileEntityAlchemicalChest tileEntityAlchemicalChest)
    {
        this.x = tileEntityAlchemicalChest.xCoord;
        this.y = tileEntityAlchemicalChest.yCoord;
        this.z = tileEntityAlchemicalChest.zCoord;
        this.orientation = (byte) tileEntityAlchemicalChest.getOrientation().ordinal();
        this.state = (byte) tileEntityAlchemicalChest.getState();
        this.customName = tileEntityAlchemicalChest.getCustomName();
        this.owner = tileEntityAlchemicalChest.getOwner();
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
    }

    @Override
    public IMessage onMessage(MessageTileAlchemicalChest message, MessageContext ctx)
    {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);

        if (tileEntity instanceof TileEntityEE)
        {
            ((TileEntityEE) tileEntity).setOrientation(message.orientation);
            ((TileEntityEE) tileEntity).setState(message.state);
            ((TileEntityEE) tileEntity).setCustomName(message.customName);
            ((TileEntityEE) tileEntity).setOwner(message.owner);
        }

        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MessageTileEntityAlchemicalChest - x:%s, y:%s, z:%s, orientation:%s, state:%s, customName:%s, owner:%s", x, y, z, orientation, state, customName, owner);
    }
}
