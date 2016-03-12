package buildcraft.additionalpipes.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.item.ItemPipeAP;
import buildcraft.additionalpipes.pipes.APPipe;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;

public class PipeCreator
{

	/**
	 * Creates and registers a buildcraft pipe from the provided class.
	 * Also sets a recipe for it from the provided Object[].
	 * @param output how many of the pipe should be output from the recipe
	 * @param clas
	 * @param recipe
	 * @param shapeless whether or not the recipe is shapeless
	 * @return
	 */
	public static Item createPipeAndRecipe(int output, Class<? extends Pipe<?>> clas, Object[] recipe, boolean shapeless) 
	{
	
		Item pipe = createPipe(clas);
		for(Object obj : recipe) {
			if(obj == null)
				return pipe;
		}
		if(shapeless)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(pipe, output), recipe);
		}
		else
		{
			GameRegistry.addRecipe(new ItemStack(pipe, output), recipe);
		}
		return pipe;
	}

	/**
	 * Creates and registers a buildcraft pipe from the provided class.
	 * @param clas
	 * @return
	 */
	public static Item createPipe(Class<? extends Pipe<?>> clas)
	{
		Item res = BlockGenericPipe.registerPipe(clas, AdditionalPipes.instance.creativeTab);
		res.setUnlocalizedName(clas.getSimpleName());
		AdditionalPipes.proxy.registerPipeRendering(res);
		return res;
	}

	public static Item createPipeTooltip(Class<? extends APPipe<?>> clas, String tooltip)
	{
		ItemPipe item = new ItemPipeAP(tooltip);
		item.setUnlocalizedName(clas.getSimpleName());
		AdditionalPipes.proxy.registerPipeRendering(item);
		BlockGenericPipe.pipes.put(item, clas);
		
		GameRegistry.registerItem(item, item.getUnlocalizedName());
		
		AdditionalPipes.proxy.createPipeSpecial(item, clas);
	
		return item;
	}

}
