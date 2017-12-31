package buildcraft.additionalpipes;

import java.util.HashSet;
import java.util.Set;

import buildcraft.additionalpipes.pipes.PipeBehaviorAddition;
import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.additionalpipes.pipes.PipeBehaviorDistribution;
import buildcraft.additionalpipes.pipes.PipeBehaviorGravityFeed;
import buildcraft.additionalpipes.pipes.PipeBehaviorJeweled;
import buildcraft.additionalpipes.pipes.PipeBehaviorPriorityInsertion;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleportItems;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleportFluids;
import buildcraft.additionalpipes.pipes.PipeBehaviorWaterPump;
import buildcraft.additionalpipes.pipes.PipeBehaviorTeleportPower;
import buildcraft.additionalpipes.pipes.PipeBehaviorSwitch;
import buildcraft.additionalpipes.utils.PipeCreator;
import buildcraft.api.recipes.AssemblyRecipe;
import buildcraft.api.recipes.StackDefinition;
import buildcraft.api.transport.pipe.PipeApi;
import buildcraft.api.transport.pipe.PipeDefinition;
import buildcraft.api.transport.pipe.PipeDefinition.PipeDefinitionBuilder;
import buildcraft.lib.inventory.filter.ArrayStackFilter;
import buildcraft.lib.recipe.AssemblyRecipeRegistry;
import buildcraft.silicon.BCSiliconItems;
import buildcraft.transport.BCTransportConfig;
import buildcraft.transport.BCTransportItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	
	// Item Teleport
	public static Item itemsTeleportPipeItem;
	public static PipeDefinition itemsTeleportPipeDef;
	
	// Liquid Teleport
	public static Item liquidsTeleportPipeItem;
	public static PipeDefinition liquidsTeleportPipeDef;
	
	// Power Teleport
	public static Item powerTeleportPipeItem;
	public static PipeDefinition powerTeleportPipeDef;
	
	// Switch pipes
	// Switch Transport Pipe
	public static Item itemsSwitchPipeItem;
	public static PipeDefinition itemsSwitchPipeDef;

	// Switch Fluid Pipe
	public static Item fluidsSwitchPipeItem;
	public static PipeDefinition fluidsSwitchPipeDef;
	
	// Switch Transport Pipe
	public static Item powerSwitchPipeItem;
	public static PipeDefinition powerSwitchPipeDef;
	
	// water pump pipe
	public static Item waterPumpPipeItem;
	public static PipeDefinition waterPumpPipeDef;
	
	
	public static void createPipes()
	{
		additionPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsAddition").logic(PipeBehaviorAddition::new, PipeBehaviorAddition::new).define();
		additionPipeItem = PipeCreator.createPipeItemAndRecipe(1, additionPipeDef, false, " R ", "RCR", " R ", 'C', BCTransportItems.pipeItemClay, 'R', "dustRedstone");
		
		advWoodPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsAdvancedWood").texSuffixes("_output", "_input").logic(PipeBehaviorAdvWood::new, PipeBehaviorAdvWood::new).define();
		advWoodPipeItem = PipeCreator.createPipeItemAndRecipe(8, advWoodPipeDef, false, "WgW", 'W', "gearWood", 'g', "blockGlass");
		
		closedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsClosed").texSuffixes("_open", "_closed").logic(PipeBehaviorClosed::new, PipeBehaviorClosed::new).define();
		closedPipeItem = PipeCreator.createPipeItemAndRecipe(1, closedPipeDef, true, BCTransportItems.pipeItemVoid, "gearIron");
		
		PipeDefinitionBuilder distPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsDistribution").logic(PipeBehaviorDistribution::new, PipeBehaviorDistribution::new);
		attachSidedSuffixes(distPipeDefBuilder);
		distributionPipeDef = distPipeDefBuilder.define();
		distributionPipeItem = PipeCreator.createPipeItemAndRecipe(1, distributionPipeDef, false, " r ", "IgI", 'r', "dustRedstone", 'I', "ingotIron", 'g', "blockGlass");
		
		gravityFeedPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsGravityFeed").texSuffixes("_up", "_sides").logic(PipeBehaviorGravityFeed::new, PipeBehaviorGravityFeed::new).define();
		gravityFeedPipeItem = PipeCreator.createPipeItemAndRecipe(1, gravityFeedPipeDef, false, "   ", "IgI", " I ", 'S', "stone", 'I', "ingotIron", 'g', "blockGlass");
		
		PipeDefinitionBuilder priorityPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsPriority").logic(PipeBehaviorPriorityInsertion::new, PipeBehaviorPriorityInsertion::new);
		attachSidedSuffixes(priorityPipeDefBuilder);
		priorityPipeDef = priorityPipeDefBuilder.define();
		priorityPipeItem = PipeCreator.createPipeItemAndRecipe(2, distributionPipeDef, true, distributionPipeItem, BCTransportItems.pipeItemClay);
		
		PipeDefinitionBuilder jeweledPipeDefBuilder = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsJeweled").logic(PipeBehaviorJeweled::new, PipeBehaviorJeweled::new);
		attachSidedSuffixes(jeweledPipeDefBuilder);
		jeweledPipeDef = jeweledPipeDefBuilder.define();
		jeweledPipeItem = PipeCreator.createPipeItemAndRecipe(2, jeweledPipeDef, false, " D ", "DGD", " D ", 'D', BCTransportItems.pipeItemDiamond, 'G', "gearGold");
		
		itemsTeleportPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsTeleport").logic(PipeBehaviorTeleportItems::new, PipeBehaviorTeleportItems::new).define();
		itemsTeleportPipeItem = PipeCreator.createPipeItem(itemsTeleportPipeDef);	
		
		// add assembly recipe for Item Teleport Pipe
		Set<StackDefinition> tpRecipeIngredients = new HashSet<StackDefinition>();
		tpRecipeIngredients.add(ArrayStackFilter.definition(new ItemStack(BCSiliconItems.redstoneChipset, 1, 4)));
		tpRecipeIngredients.add(ArrayStackFilter.definition(8, new ItemStack(BCTransportItems.pipeItemDiamond)));
		tpRecipeIngredients.add(ArrayStackFilter.definition(new ItemStack(BCSiliconItems.redstoneChipset, 1, 3)));
		AssemblyRecipeRegistry.INSTANCE.addRecipe(new AssemblyRecipe("teleportPipe", 10000, tpRecipeIngredients, new ItemStack(itemsTeleportPipeItem, 8)));
		
		liquidsTeleportPipeDef = new PipeDefinitionBuilder().flowFluid().idTexPrefix("pipeLiquidsTeleport").logic(PipeBehaviorTeleportFluids::new, PipeBehaviorTeleportFluids::new).define();
		liquidsTeleportPipeItem = PipeCreator.createPipeItemAndRecipe(1, liquidsTeleportPipeDef, true, new Object[] {BCTransportItems.waterproof, itemsTeleportPipeItem});
		
		powerTeleportPipeDef = new PipeDefinitionBuilder().flowPower().idTexPrefix("pipePowerTeleport").logic(PipeBehaviorTeleportPower::new, PipeBehaviorTeleportPower::new).define();
		powerTeleportPipeItem = PipeCreator.createPipeItemAndRecipe(1, powerTeleportPipeDef, true, new Object[] {"dustRedstone", itemsTeleportPipeItem});
		
		itemsSwitchPipeDef = new PipeDefinitionBuilder().flowItem().idTexPrefix("pipeItemsSwitch").texSuffixes("_closed", "_open").logic(PipeBehaviorSwitch::new, PipeBehaviorSwitch::new).define();
		itemsSwitchPipeItem = PipeCreator.createPipeItemAndRecipe(8, itemsSwitchPipeDef, false, "GgI", 'g', "blockGlass", 'G', "gearGold", 'I', "gearIron");
		
		fluidsSwitchPipeDef = new PipeDefinitionBuilder().flowFluid().idTexPrefix("pipeFluidsSwitch").texSuffixes("_closed", "_open").logic(PipeBehaviorSwitch::new, PipeBehaviorSwitch::new).define();
		fluidsSwitchPipeItem = PipeCreator.createPipeItemAndRecipe(1, fluidsSwitchPipeDef, true, new Object[] {BCTransportItems.waterproof, itemsSwitchPipeItem});
		
		powerSwitchPipeDef = new PipeDefinitionBuilder().flowPower().idTexPrefix("pipePowerSwitch").texSuffixes("_closed", "_open").logic(PipeBehaviorSwitch::new, PipeBehaviorSwitch::new).define();
		powerSwitchPipeItem = PipeCreator.createPipeItemAndRecipe(1, powerSwitchPipeDef, true, new Object[] {"dustRedstone", itemsSwitchPipeItem});
		
		waterPumpPipeDef = new PipeDefinitionBuilder().flowFluid().idTexPrefix("pipeFluidsWaterPump").logic(PipeBehaviorWaterPump::new, PipeBehaviorWaterPump::new).define();
		waterPumpPipeItem = PipeCreator.createPipeItemAndRecipe(1, waterPumpPipeDef, false, " L ", "rPr", " W ", 'r', "dustRedstone", 'P', "gearIron", 'L',
				BCTransportItems.pipeFluidGold, 'w', BCTransportItems.waterproof, 'W', BCTransportItems.pipeFluidWood);
	}
	
	public static void setFluidCapacities()
	{
		// set Liquids Teleport Pipe to be the same as the Diamond Fluid Pipe
		PipeApi.fluidTransferData.put(liquidsTeleportPipeDef, new PipeApi.FluidTransferInfo(BCTransportConfig.baseFlowRate * 8, 10));
		
		// set Switch Fluid Pipe to be halfway between the Iron and Gold FLuid Pipes
		PipeApi.fluidTransferData.put(fluidsSwitchPipeDef, new PipeApi.FluidTransferInfo(BCTransportConfig.baseFlowRate * 6, 10));
		
		// set Water Pump Pipe to match its output capacity
		PipeApi.fluidTransferData.put(waterPumpPipeDef, new PipeApi.FluidTransferInfo(APConfiguration.waterPumpWaterPerTick, 10));
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
