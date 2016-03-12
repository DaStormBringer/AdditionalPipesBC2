package buildcraft.additionalpipes.gates;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.StatementManager;

public abstract class APTrigger implements IStatement {

	protected String id;
    protected ResourceLocation texture;

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprite;

    /**
     * 
     * @param id the name of the trigger, without the "additionalpipes:" prefix.  Also the name of the texture.
     */
	public APTrigger(String idWithoutPrefix)
	{
		this.id = "additionalpipes:" + idWithoutPrefix;
		StatementManager.statements.put(this.id, this);
		texture = new ResourceLocation("additionalpipes:textures/items/" + idWithoutPrefix);
		
        MinecraftForge.EVENT_BUS.register(this);
	}

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void stitchTextures(TextureStitchEvent.Pre event) {
        sprite = event.map.getTextureExtry(texture.toString());
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

}
