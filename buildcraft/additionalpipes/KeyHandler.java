package buildcraft.additionalpipes;

import java.util.EnumSet;

import net.minecraft.src.KeyBinding;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class KeyHandler extends KeyBindingRegistry.KeyHandler{

	public KeyHandler(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return AdditionalPipes.MODID + ": " + this.getClass().getSimpleName();
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		if (tickEnd && kb.keyCode == AdditionalPipes.laserKey.keyCode) {
			AdditionalPipes.instance.chunkLoadViewer.toggleLasers();
			if(AdditionalPipes.instance.chunkLoadViewer.lasersActive()) {
				PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.CHUNKLOAD_REQUEST, false);
				PacketDispatcher.sendPacketToServer(packet.makePacket());
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
