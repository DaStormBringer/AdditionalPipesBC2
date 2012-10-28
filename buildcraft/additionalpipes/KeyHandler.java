package buildcraft.additionalpipes;

import java.util.EnumSet;
import java.util.List;

import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.api.core.LaserKind;
import buildcraft.core.Box;

import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;

public class KeyHandler extends KeyBindingRegistry.KeyHandler{

	public KeyHandler(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return mod_AdditionalPipes.MODID + ": " + this.getClass().getSimpleName();
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		if (kb.keyCode == mod_AdditionalPipes.instance.showLaser.keyCode) {
			mod_AdditionalPipes.instance.chunkLoadViewer.toggleLasers();
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
