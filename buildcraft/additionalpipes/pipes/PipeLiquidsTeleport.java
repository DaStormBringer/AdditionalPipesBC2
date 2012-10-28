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

import net.minecraft.src.TileEntity;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.GuiHandler;
import buildcraft.additionalpipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.liquids.ITankContainer;
import buildcraft.api.liquids.LiquidStack;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportLiquidsHook;
import buildcraft.transport.PipeTransportLiquids;

public class PipeLiquidsTeleport extends PipeTeleport implements IPipeTransportLiquidsHook {

	class OilReturn {
		public Orientations theOrientation;
		public ITankContainer iliquid;
		public OilReturn(Orientations a, ITankContainer b) {
			theOrientation = a;
			iliquid = b;
		}
	}

	public PipeLiquidsTeleport(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogicTeleport(GuiHandler.PIPE_TP), itemID);

		((PipeTransportLiquids) transport).flowRate = 80;
		((PipeTransportLiquids) transport).travelDelay = 2;
	}

	/*
    @Override
    public void setPosition (int xCoord, int yCoord, int zCoord) {

        LinkedList <PipeLiquidsTeleport> toRemove = new LinkedList <PipeLiquidsTeleport> ();

        for (int i = 0; i < LiquidTeleportPipes.size(); i++) {
            if (LiquidTeleportPipes.get(i).xCoord == xCoord &&  LiquidTeleportPipes.get(i).yCoord == yCoord && LiquidTeleportPipes.get(i).zCoord == zCoord) {
                //System.out.println("Removed OldLoc: " + i);
                toRemove.add(LiquidTeleportPipes.get(i));
            }
        }

        LiquidTeleportPipes.removeAll(toRemove);
        LiquidTeleportPipes.add(this);

        super.setPosition(xCoord, yCoord, zCoord);
        //MutiPlayerProxy.AddChunkToList(xCoord, zCoord);
    }*/

	public LinkedList<OilReturn> getPossibleLiquidMovements(Position pos) {
		LinkedList<OilReturn> result = new LinkedList<OilReturn>();

		for (int o = 0; o <= 5; ++o) {
			Position newPos = new Position(pos);
			newPos.orientation = Orientations.values()[o];
			newPos.moveForwards(1.0);

			if (canReceiveLiquid2(newPos)) {

				//For better handling in future
				//int space = BuildCraftCore.OIL_BUCKET_QUANTITY / 4 - sideToCenter[((Orientations.values()[o]).reverse()).ordinal()] - centerToSide[((Orientations.values()[o]).reverse()).ordinal()] + flowRate;
				result.add(new OilReturn(Orientations.values()[o], (ITankContainer) Utils.getTile(worldObj, newPos, Orientations.Unknown)));
			}
		}

		return result;
	}

	public boolean canReceiveLiquid2(Position p) {
		TileEntity entity = worldObj.getBlockTileEntity((int) p.x, (int) p.y,
				(int) p.z);

		if (!Utils.checkLegacyPipesConnections(worldObj, (int) p.x, (int) p.y,
				(int) p.z, xCoord, yCoord, zCoord)) {
			return false;
		}

		if (entity instanceof IPipeEntry || entity instanceof ITankContainer) {
			return true;
		}

		return false;
	}

	@Override
	public Position getPosition() {
		return new Position (xCoord, yCoord, zCoord);
	}

	@Override
	public int fill(Orientations from, LiquidStack resource, boolean doFill) {
		List<PipeTeleport> pipeList = getConnectedPipes(false);

		if (pipeList.size() == 0) {
			return 0;
		}

		//System.out.println("PipeList Size: " + pipeList.size());
		int i = worldObj.rand.nextInt(pipeList.size());
		LinkedList<OilReturn> theList = getPossibleLiquidMovements(pipeList.get(i).getPosition());

		if (theList.size() <= 0) {
			return 0;
		}

		//System.out.println("theList Size: " + theList.size());
		int used = 0;
		int a = 0;

		while (theList.size() > 0 && used <= 0) {
			a = worldObj.rand.nextInt(theList.size());
			//System.out.println("A: " + a);
			used = theList.get(a).iliquid.fill(resource, doFill);
			theList.remove(a);
		}

		//System.out.println("Fill " + used);
		return used;
	}

	@Override
	public String getTextureFile() {
		return AdditionalPipes.TEXTURE_LIQUID_TELEPORT;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}

}
