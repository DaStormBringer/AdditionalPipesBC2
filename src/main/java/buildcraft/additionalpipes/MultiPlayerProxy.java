package buildcraft.additionalpipes;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;

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
