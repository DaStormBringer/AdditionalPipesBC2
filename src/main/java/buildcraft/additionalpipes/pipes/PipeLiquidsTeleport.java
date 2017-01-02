/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import buildcraft.additionalpipes.api.PipeType;
import buildcraft.transport.IPipeTransportFluidsHook;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.pipes.PipeFluidsDiamond;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class PipeLiquidsTeleport extends PipeTeleport<PipeTransportFluids> implements IPipeTransportFluidsHook {
	private static final int ICON = 2;

	public PipeLiquidsTeleport(Item item)
	{
		super(new PipeTransportFluids(), item, PipeType.FLUIDS);
		
		//load the fluid capacities set in mod init
		transport.initFromPipe(PipeFluidsDiamond.class);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		List<PipeLiquidsTeleport> pipeList = TeleportManager.instance.getConnectedPipes(this, false, true);

		if(pipeList.size() == 0 || (state & 0x1) == 0) {
			return 0;
		}

		int i = getWorld().rand.nextInt(pipeList.size());
		List<Pair<ForgeDirection, IFluidHandler>> possibleMovements = getPossibleLiquidMovements(pipeList.get(i));

		if(possibleMovements.size() <= 0) {
			return 0;
		}

		int used = 0;
		while(possibleMovements.size() > 0 && used <= 0) {
			int a = rand.nextInt(possibleMovements.size());
			Pair<ForgeDirection, IFluidHandler> outputData = possibleMovements.get(a);
			
			used = outputData.getRight().fill(outputData.getLeft().getOpposite(), resource, doFill);
			possibleMovements.remove(a);
		}

		return used;
	}

	private static List<Pair<ForgeDirection, IFluidHandler>> getPossibleLiquidMovements(PipeTeleport<?> pipe) {
		List<Pair<ForgeDirection, IFluidHandler>> result = new LinkedList<Pair<ForgeDirection, IFluidHandler>>();

		for(ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			if(pipe.outputOpen(o) && pipe.container.getTile(o) instanceof IFluidHandler) {
				IFluidHandler te = (IFluidHandler) pipe.container.getTile(o);
				if (te != null) {
					result.add(Pair.<ForgeDirection, IFluidHandler>of(o, te));
				}
			}
		}

		return result;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return ICON;
	}

}
