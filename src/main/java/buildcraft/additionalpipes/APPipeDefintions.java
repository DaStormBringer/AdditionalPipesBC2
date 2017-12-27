package buildcraft.additionalpipes;

import buildcraft.additionalpipes.pipes.PipeItemsAddition;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.utils.PipeCreator;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.api.transport.pipe.PipeDefinition.PipeDefinitionBuilder;
import buildcraft.transport.BCTransportItems;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;

public class APPipeDefintions
{
	// Addition
	public static Item additionPipeItem;
	public static PipeDefinition additionPipeDef;
	
	// Advanced Wooden Pipe
	public static Item advWoodPipeItem;
	public static PipeDefinition advWoodPipeDef;
	
	// Closed Pipe
	public static Item closedPipeItem;
	public static PipeDefinition closedPipeDef;
	
	// Distribution Pipe
	public static Item distributionPipeItem;
	public static PipeDefinition distributionPipeDef;
	
	public static void createPipes()
	{
		additionPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsAddition").logic(PipeItemsAddition::new, PipeItemsAddition::new).define();
		additionPipeItem = PipeCreator.createPipeItemAndRecipe(1, additionPipeDef, false, " R ", "RCR", " R ", 'C', BCTransportItems.pipeItemClay, 'R', "dustRedstone");
		
		advWoodPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsAdvancedWood").texSuffixes("_output", "_input").logic(PipeItemsAdvancedWood::new, PipeItemsAdvancedWood::new).define();
		advWoodPipeItem = PipeCreator.createPipeItemAndRecipe(8, advWoodPipeDef, false, "WgW", 'W', "gearWood", 'g', "blockGlass");
		
		closedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsClosed").texSuffixes("_open", "_closed").logic(PipeItemsClosed::new, PipeItemsClosed::new).define();
		closedPipeItem = PipeCreator.createPipeItemAndRecipe(1, closedPipeDef, true, BCTransportItems.pipeItemVoid, "gearIron");
		
		PipeDefinitionBuilder distPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsDistribution").logic(PipeItemsDistributor::new, PipeItemsClosed::new);
		attachSidedSuffixes(distPipeDefBuilder);
		distributionPipeDef = distPipeDefBuilder.define();
	}
	
	/**
	 * Attach "_down" through "_east" suffixes to a definition, with their indices corresponding to EnumFacing.values()[] ordering
	 * @param builder
	 */
	private static void attachSidedSuffixes(PipeDefinitionBuilder builder)
	{
		String[] suffixes = new String[6];
		
		for(int dirIndex = 0; dirIndex < EnumFacing.VALUES.length; ++dirIndex)
		{
			suffixes[dirIndex] = "_" + EnumFacing.VALUES[dirIndex].getName();
		}
		
		builder.texSuffixes(suffixes);
	}
}
