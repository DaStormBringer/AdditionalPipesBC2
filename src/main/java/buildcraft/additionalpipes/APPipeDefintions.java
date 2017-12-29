package buildcraft.additionalpipes;

import buildcraft.additionalpipes.pipes.PipeItemsAddition;
import buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import buildcraft.additionalpipes.pipes.PipeItemsClosed;
import buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.additionalpipes.pipes.PipeItemsGravityFeed;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.additionalpipes.utils.PipeCreator;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.api.transport.pipe.PipeDefinition.PipeDefinitionBuilder;
import buildcraft.transport.BCTransportItems;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;

public class APPipeDefintions
{
	// Addition Pipe
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
	
	// Gravity Feed Pipe
	public static Item gravityFeedPipeItem;
	public static PipeDefinition gravityFeedPipeDef;
	
	// Priority Insertion Pipe
	public static Item priorityPipeItem;
	public static PipeDefinition priorityPipeDef;
	
	// Jeweled Pipe
	public static Item jeweledPipeItem;
	public static PipeDefinition jeweledPipeDef;
	
	public static void createPipes()
	{
		additionPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsAddition").logic(PipeItemsAddition::new, PipeItemsAddition::new).define();
		additionPipeItem = PipeCreator.createPipeItemAndRecipe(1, additionPipeDef, false, " R ", "RCR", " R ", 'C', BCTransportItems.pipeItemClay, 'R', "dustRedstone");
		
		advWoodPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsAdvancedWood").texSuffixes("_output", "_input").logic(PipeItemsAdvancedWood::new, PipeItemsAdvancedWood::new).define();
		advWoodPipeItem = PipeCreator.createPipeItemAndRecipe(8, advWoodPipeDef, false, "WgW", 'W', "gearWood", 'g', "blockGlass");
		
		closedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsClosed").texSuffixes("_open", "_closed").logic(PipeItemsClosed::new, PipeItemsClosed::new).define();
		closedPipeItem = PipeCreator.createPipeItemAndRecipe(1, closedPipeDef, true, BCTransportItems.pipeItemVoid, "gearIron");
		
		PipeDefinitionBuilder distPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsDistribution").logic(PipeItemsDistributor::new, PipeItemsDistributor::new);
		attachSidedSuffixes(distPipeDefBuilder);
		distributionPipeDef = distPipeDefBuilder.define();
		distributionPipeItem = PipeCreator.createPipeItemAndRecipe(1, distributionPipeDef, false, " r ", "IgI", 'r', "dustRedstone", 'I', "ingotIron", 'g', "blockGlass");
		
		gravityFeedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsGravityFeed").texSuffixes("_up", "_sides").logic(PipeItemsGravityFeed::new, PipeItemsGravityFeed::new).define();
		gravityFeedPipeItem = PipeCreator.createPipeItemAndRecipe(1, gravityFeedPipeDef, false, "   ", "IgI", " I ", 'S', "stone", 'I', "ingotIron", 'g', "blockGlass");
		
		PipeDefinitionBuilder priorityPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsPriority").logic(PipeItemsPriorityInsertion::new, PipeItemsPriorityInsertion::new);
		attachSidedSuffixes(priorityPipeDefBuilder);
		priorityPipeDef = priorityPipeDefBuilder.define();
		priorityPipeItem = PipeCreator.createPipeItemAndRecipe(2, distributionPipeDef, true, distributionPipeItem, BCTransportItems.pipeItemClay);
		
		PipeDefinitionBuilder jeweledPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsJeweled").logic(PipeItemsJeweled::new, PipeItemsJeweled::new);
		attachSidedSuffixes(jeweledPipeDefBuilder);
		jeweledPipeDef = jeweledPipeDefBuilder.define();
		jeweledPipeItem = PipeCreator.createPipeItemAndRecipe(2, jeweledPipeDef, false, " D ", "DGD", " D ", 'D', BCTransportItems.pipeItemDiamond, 'G', "gearGold");
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
