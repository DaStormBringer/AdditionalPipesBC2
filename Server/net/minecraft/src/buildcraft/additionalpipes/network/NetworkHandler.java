package net.minecraft.src.buildcraft.additionalpipes.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.src.*;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.core.network.PacketUpdate;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.forge.IConnectionHandler;
import net.minecraft.src.forge.IPacketHandler;
import net.minecraft.src.forge.MessageManager;

public class NetworkHandler implements IConnectionHandler, IPacketHandler {

    public static final String CHANNEL = "AdditionalPipes";
    
    @Override
    public void onConnect(NetworkManager network) {
    }

    @Override
    public void onLogin(NetworkManager network, Packet1Login login) {
        
        MessageManager messageManager = MessageManager.getInstance();
        messageManager.registerChannel(network, this, CHANNEL);
        messageManager.addActiveChannel(network, CHANNEL);
    }

    @Override
    public void onDisconnect(NetworkManager network, String message, Object[] args) {
        
        MessageManager messageManager = MessageManager.getInstance();
        messageManager.unregisterChannel(network, this, CHANNEL);
        messageManager.removeActiveChannel(network, CHANNEL);
    }

    @Override
    public void onPacketData(NetworkManager network, String channel, byte[] rawData) {
        
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(rawData));
        PacketAdditionalPipes packet = null;
        
        try {
            
            NetServerHandler net = (NetServerHandler)network.getNetHandler();
            
            int packetID = data.read();
            switch(packetID) {
                
                case 1:
                    packet = new PacketAdditionalPipes(1);
                    packet.readData(data);
                    onTelePipeDesc(packet, net.getPlayerEntity());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onTelePipeDesc(PacketAdditionalPipes packet, EntityPlayer player) {
        
        TileGenericPipe tile = (TileGenericPipe) ModLoader.getMinecraftServerInstance().getWorldManager(player.dimension)
                .getBlockTileEntity(packet.posX, packet.posY, packet.posZ);
        
        tile.pipe.handlePacket(packet);
    }

}