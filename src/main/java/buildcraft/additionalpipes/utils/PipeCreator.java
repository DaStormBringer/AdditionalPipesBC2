package buildcraft.additionalpipes.utils;

import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.transport.item.ItemPipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class PipeCreator
{

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
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(pipe, output), recipe));
		}
		else
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(pipe, output), recipe));
		}
		return pipe;
	}

	/**
	 * Creates and registers a buildcraft pipe from the provided class.
	 * @param clas
	 * @return
	 */
	public static ItemPipeHolder createPipeItem(PipeDefinition pipeDef)
	{
		return new ItemPipeHolder(pipeDef);
	}

	/*public static Item createPipeTooltip(Class<? extends APPipe<?>> clas, String tooltip)
	{
		//we need to use our own version of ItemPipe with tooltip support
		ItemPipeHolder item = new ItemPipeAP(tooltip);
		item.setUnlocalizedName(clas.getSimpleName());
		BlockGenericPipe.pipes.put(item, clas);
		
		GameRegistry.registerItem(item, item.getUnlocalizedName());
		
		AdditionalPipes.proxy.createPipeSpecial(item, clas);
	
		return item;
	}*/

}
