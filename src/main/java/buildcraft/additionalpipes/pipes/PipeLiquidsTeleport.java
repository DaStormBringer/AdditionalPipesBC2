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

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.transport.IPipeTransportFluidsHook;
import buildcraft.transport.PipeTransportFluids;

public class PipeLiquidsTeleport extends PipeTeleport<PipeTransportFluids> implements IPipeTransportFluidsHook {
	private static final int ICON = 2;

	public PipeLiquidsTeleport(Item item) {
		super(new PipeTransportFluids(), item);
		transport.flowRate = 220;
		transport.travelDelay = 3;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		List<PipeTeleport<?>> pipeList = TeleportManager.instance.getConnectedPipes(this, false);

		if(pipeList.size() == 0 || (state & 0x1) == 0) {
			return 0;
		}

		int i = getWorld().rand.nextInt(pipeList.size());
		List<IFluidHandler> possibleMovements = getPossibleLiquidMovements(pipeList.get(i));

		if(possibleMovements.size() <= 0) {
			return 0;
		}

		int used = 0;
		while(possibleMovements.size() > 0 && used <= 0) {
			int a = rand.nextInt(possibleMovements.size());
			used = possibleMovements.get(a).fill(EnumFacing.UP, resource, doFill);
			possibleMovements.remove(a);
		}

		return used;
	}

	private static List<IFluidHandler> getPossibleLiquidMovements(PipeTeleport<?> pipe) {
		List<IFluidHandler> result = new LinkedList<IFluidHandler>();

		for(EnumFacing o : EnumFacing.values()) {
			if(pipe.outputOpen(o)) {
				IFluidHandler te = (IFluidHandler) pipe.container.getTile(o);
				if (te != null) {
					result.add(te);
				}
			}
		}

		return result;
	}

	@Override
	public int getIconIndex(EnumFacing direction) {
		return ICON;
	}

}
