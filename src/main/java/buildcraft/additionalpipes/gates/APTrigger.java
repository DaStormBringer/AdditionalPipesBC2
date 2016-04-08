package buildcraft.additionalpipes.gates;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.StatementManager;

public abstract class APTrigger implements IStatement {

	protected String id; //used as unique key in BC registry
	protected String descriptionKey; //used for description translations
    protected ResourceLocation texture;

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprite;

    /**
     * 
     * @param id the name of the trigger, without the "additionalpipes:" prefix.  Also the name of the texture.
     */
	public APTrigger(String idWithoutPrefix)
	{
		descriptionKey = "trigger." + idWithoutPrefix;
		
		this.id = "additionalpipes:" + descriptionKey;
		StatementManager.statements.put(this.id, this);
		texture = new ResourceLocation(AdditionalPipes.MODID, "textures/items/triggers/" + idWithoutPrefix);
		
        MinecraftForge.EVENT_BUS.register(this);
	}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void stitchTextures(TextureStitchEvent.Pre event) {
        sprite = event.map.getTextureExtry(texture.toString());
        
        Log.debug("resource location: " + texture.toString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getGuiSprite() {
        return sprite;
    }
    
    @Override
    public String getUniqueTag()
    {
    	return id;
    }
    
	@Override
	public String getDescription()
	{
		return StatCollector.translateToLocal(descriptionKey);
	}

}
