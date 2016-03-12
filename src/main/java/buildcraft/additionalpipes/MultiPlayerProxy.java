package buildcraft.additionalpipes;

import net.minecraft.item.Item;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;

public class MultiPlayerProxy {

	//none of these methods do anything on the server

	public void registerKeyHandler() {
	}

	public void registerRendering() {
	}

	public void registerPipeRendering(Item res) {
	}

	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas) {
	}
}
