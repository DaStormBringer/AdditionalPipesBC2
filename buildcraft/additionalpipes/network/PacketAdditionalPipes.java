package net.minecraft.src.buildcraft.additionalpipes.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.buildcraft.core.network.PacketPayload;
import net.minecraft.src.buildcraft.core.network.PacketUpdate;
import net.minecraft.src.forge.packets.ForgePacket;

public class PacketAdditionalPipes extends PacketUpdate {
    
    public PacketAdditionalPipes(int PacketId) {
    	super(PacketId);
    	this.channel = NetworkHandler.CHANNEL;
	}
    
    public PacketAdditionalPipes(int packetId, PacketPayload payload) {
    	super(packetId, payload);
    	this.channel = NetworkHandler.CHANNEL;
    }
}