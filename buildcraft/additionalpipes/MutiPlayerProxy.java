package buildcraft.additionalpipes;

import java.io.File;

import net.minecraft.src.Item;
import net.minecraft.src.World;

public class MutiPlayerProxy {
	public boolean NeedsLoad = true;
	public File WorldDir;
	public boolean HDSet = false;
	public boolean HDFound = false;
	public boolean OFFound = false;

	public boolean isOnServer(World world) {
		return !world.isRemote;
	}

	public void registerKeyHandler() {}

	public void registerRendering() {}

	public void registerPipeRendering(Item res) {}

}
