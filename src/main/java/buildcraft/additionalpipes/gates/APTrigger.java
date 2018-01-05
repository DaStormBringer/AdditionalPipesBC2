package buildcraft.additionalpipes.gates;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.StatementManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public abstract class APTrigger implements IStatement {

	protected String id; //used as unique key in BC registry
	protected String descriptionKey; //used for description translations
    protected ResourceLocation texture;
    
    /**
     * 
     * @param id the name of the trigger, without the "additionalpipes:" prefix.  Also the name of the texture.
     */
	public APTrigger(String idWithoutPrefix)
	{
		descriptionKey = "trigger." + idWithoutPrefix;
		
		this.id = AdditionalPipes.MODID + ":" + descriptionKey;
		StatementManager.statements.put(this.id, this);			}
    
    @Override
    public String getUniqueTag()
    {
    	return id;
    }
    
	@Override
	public String getDescription()
	{
		return I18n.translateToLocal(descriptionKey);
	}
	

}
