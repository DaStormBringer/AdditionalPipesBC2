package buildcraft.additionalpipes;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class MultiPlayerProxy {
	public boolean isServer(World world) {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}

	public void registerKeyHandler() {
	}

	public void registerRendering() {
	}

	public void registerPipeRendering(Item res) {
	}

	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas) {
	}
}
