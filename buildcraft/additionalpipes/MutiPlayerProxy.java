package buildcraft.additionalpipes;

import net.minecraft.src.Item;
import net.minecraft.src.World;

public class MutiPlayerProxy {
	public boolean isServer(World world) {
		return !world.isRemote;
	}

	public void registerKeyHandler() {}

	public void registerRendering() {}

	public void registerPipeRendering(Item res) {}

}
