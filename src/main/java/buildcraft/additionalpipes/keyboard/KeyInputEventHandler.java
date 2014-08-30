package buildcraft.additionalpipes.keyboard;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.ChunkLoadViewDataProxy;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyInputEventHandler
{

    @SubscribeEvent
    public void handleKeyInputEvent(InputEvent.KeyInputEvent event)
    {
        if (FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            if (FMLClientHandler.instance().getClientPlayerEntity() != null)
            {
            	if(Keybindings.lasers.isPressed())
            	{
	            	ChunkLoadViewDataProxy viewer = AdditionalPipes.instance.chunkLoadViewer;
	    			viewer.toggleLasers();
	    			if(viewer.lasersActive()) 
	    			{
	    				viewer.requestPersistentChunks();
	    			}
            	}
            }
        }
    }
}
