package buildcraft.additionalpipes.utils;

import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.lib.registry.RegistrationHelper;
import buildcraft.lib.registry.TagManager;
import buildcraft.transport.item.ItemPipeHolder;

public class PipeCreator
{

	// saves items during preInit, then registers them during the RegisterEvent
    private static final RegistrationHelper regHelper = new RegistrationHelper();

	/**
	 * Creates and registers a buildcraft pipe from the provided class.
	 * Also sets a recipe for it from the provided Object[].
	 * @param output how many of the pipe should be output from the recipe
	 * @param pipeDef
	 * @param recipe
	 * @param shapeless whether or not the recipe is shapeless
	 * @return
	 */
	public static ItemPipeHolder createPipeItemAndRecipe(int output, PipeDefinition pipeDef, boolean shapeless, Object... recipe) 
	{
	
		ItemPipeHolder pipe = createPipeItem(pipeDef);
		for(Object obj : recipe) {
			if(obj == null)
				return pipe;
		}
		if(shapeless)
		{
			//ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(new ResourceLocation(AdditionalPipes.MODID, "recipes/" + pipeDef.identifier.getResourcePath()), new ItemStack(pipe, output), recipe));
		}
		else
		{
			//ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(AdditionalPipes.MODID, "recipes/" + pipeDef.identifier.getResourcePath()), new ItemStack(pipe, output), recipe));
		}
		return pipe;
	}

	/**
	 * Creates and registers a buildcraft pipe from the provided definition.
	 * @param clas
	 * @return
	 */
	public static ItemPipeHolder createPipeItem(PipeDefinition pipeDef)
	{
		TagManager.registerTag("item.pipe.additionalpipes." + pipeDef.identifier.getResourcePath())
			.reg(pipeDef.identifier.getResourcePath())
			.locale("pipe.ap." + pipeDef.identifier.getResourcePath())
			.tab("apcreativetab");		
		
		ItemPipeHolder item = new ItemPipeHolder(pipeDef);
		item.registerWithPipeApi();
		
		regHelper.addItem(item);
		
		return item;
	}

	/*public static Item createPipeTooltip(Class<? extends APPipe<?>> clas, String tooltip)
	{	
	
	item.pipe.additionalpipes.pipeitemsaddition
	item.pipe.additionalpipes.pipeitemsaddition
	
		//we need to use our own version of ItemPipe with tooltip support
		ItemPipeHolder item = new ItemPipeAP(tooltip);
		item.setUnlocalizedName(clas.getSimpleName());
		BlockGenericPipe.pipes.put(item, clas);
		
		GameRegistry.registerItem(item, item.getUnlocalizedName());
		
		AdditionalPipes.proxy.createPipeSpecial(item, clas);
	
		return item;
	}*/

}
