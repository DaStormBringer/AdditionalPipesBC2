package buildcraft.additionalpipes.utils;

import java.util.HashSet;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.lib.registry.RegistrationHelper;
import buildcraft.transport.item.ItemPipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class PipeCreator
{

	// saves items during preInit, then registers them during the RegisterEvent
    private static final RegistrationHelper regHelper = new RegistrationHelper();
    
    private static final HashSet<IRecipe> recipesToRegister = new HashSet<>();

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
		try 
		{
			ResourceLocation registryName = new ResourceLocation(AdditionalPipes.MODID, "recipes/" + pipeDef.identifier.getResourcePath());
			
			IRecipe recipeToAdd;
			if(shapeless)
			{
				recipeToAdd = new ShapelessOreRecipe(registryName, new ItemStack(pipe, output), recipe);
			}
			else
			{
				recipeToAdd = new ShapedOreRecipe(registryName, new ItemStack(pipe, output), recipe);
			}
			
			recipeToAdd.setRegistryName(registryName);
			
			recipesToRegister.add(recipeToAdd);
		}
		catch(IllegalArgumentException ex)
		{
			Log.fatal("Failed to create recipe for " + pipeDef.identifier.getResourcePath() + ": " + ex.getMessage());
			ex.printStackTrace();
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
		ItemPipeHolder item = ItemPipeHolder.create(pipeDef);
		item.setRegistryName(pipeDef.identifier.getResourcePath());
		item.setUnlocalizedName("pipe.ap." + pipeDef.identifier.getResourcePath());
		item.registerWithPipeApi();
		
		regHelper.addItem(item);
		
		return item;
	}
	
	public static void onRecipeRegisterEvent(IForgeRegistry<IRecipe> registry)
	{
		registry.registerAll(recipesToRegister.toArray(new IRecipe[recipesToRegister.size()]));
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
