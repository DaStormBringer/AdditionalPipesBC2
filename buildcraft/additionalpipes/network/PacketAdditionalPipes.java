package buildcraft.additionalpipes.network;

import java.io.ByteArrayOutputStream;

import net.minecraft.src.Packet250CustomPayload;
import buildcraft.additionalpipes.AdditionalPipes;

public class PacketAdditionalPipes {

	private Packet250CustomPayload packet;
	private ByteArrayOutputStream data;

	public PacketAdditionalPipes(byte PacketId, boolean chunkPacket) {
		packet = new Packet250CustomPayload();
		packet.channel = AdditionalPipes.CHANNEL;
		packet.isChunkDataPacket = chunkPacket;
		data = new ByteArrayOutputStream();
		data.write(PacketId);
	}

	public void write(byte b) {
		data.write(b);
	}

	public void writeInt(int i) {
		data.write(i & 0xFF000000);
		data.write(i & 0xFF0000);
		data.write(i & 0xFF00);
		data.write(i & 0xFF);
	}

	public Packet250CustomPayload makePacket() {
		packet.data = data.toByteArray();
		packet.length = data.size();
		return packet;
	}
}