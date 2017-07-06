package buildcraft.additionalpipes;

import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;

public class MultiPlayerProxy {

	//none of these methods do anything on the server

	public void registerKeyHandler() {
	}

	public void registerRendering() 
	{
		// do nothing
	}

	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas) {
	}
	
	/**
	 * Sets the pipe item's icon provider to the Additional Pipes one
	 * @param pipeItem
	 */
	public void setPipeTextureProvider(ItemPipe pipeItem)
	{
		//do nothing
	}

}
