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

import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Orientations;
import buildcraft.api.liquids.ITankContainer;
import buildcraft.api.liquids.LiquidStack;
import buildcraft.transport.IPipeTransportLiquidsHook;
import buildcraft.transport.PipeTransportLiquids;

public class PipeLiquidsTeleport extends PipeTeleport implements IPipeTransportLiquidsHook {

	public PipeLiquidsTeleport(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogicTeleport(), itemID);
		((PipeTransportLiquids) transport).flowRate = 80;
		((PipeTransportLiquids) transport).travelDelay = 2;
	}

	private List<ITankContainer> getPossibleLiquidMovements(PipeTeleport pipe) {
		List<ITankContainer> result = new LinkedList<ITankContainer>();

		for (int o = 0; o < 6; ++o) {
			if (pipe.logic.outputOpen((Orientations.values()[o]))) {
				//For (possibly) better handling in future
				//int space = BuildCraftCore.OIL_BUCKET_QUANTITY / 4 - sideToCenter[((Orientations.values()[o]).reverse()).ordinal()] - centerToSide[((Orientations.values()[o]).reverse()).ordinal()] + flowRate;
				ITankContainer te = (ITankContainer) pipe.container.getTile(Orientations.values()[o]);
				result.add(te);
			}
		}

		return result;
	}

	@Override
	public int fill(Orientations from, LiquidStack resource, boolean doFill) {
		List<PipeTeleport> pipeList = TeleportManager.instance.getConnectedPipes(this, false);

		if (pipeList.size() == 0) {
			return 0;
		}

		int i = worldObj.rand.nextInt(pipeList.size());
		List<ITankContainer> possibleMovements = getPossibleLiquidMovements(pipeList.get(i));

		if (possibleMovements.size() <= 0) {
			return 0;
		}

		int used = 0;
		while (possibleMovements.size() > 0 && used <= 0) {
			int a = rand.nextInt(possibleMovements.size());
			used = possibleMovements.get(a).fill(Orientations.Unknown, resource, doFill);
			possibleMovements.remove(a);
		}

		return used;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 2;
	}

}
