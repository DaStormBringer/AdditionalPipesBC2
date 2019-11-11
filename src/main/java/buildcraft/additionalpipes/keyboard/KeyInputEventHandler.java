package buildcraft.additionalpipes.keyboard;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


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
            		/*
	            	ChunkLoadViewDataProxy viewer = AdditionalPipes.instance.chunkLoadViewer;
	    			if(!viewer.lasersActive()) 
	    			{
	    				viewer.requestPersistentChunks();
	    			}
	    			viewer.toggleLasers();
	    			*/
            	}
            }
        }
    }
}
