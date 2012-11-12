/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;

import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipedItem;
import buildcraft.core.network.TileNetworkData;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.PipeLogicStone;

public class PipeItemsRedstone extends APPipe implements IPipeTransportItemsHook {

	public @TileNetworkData boolean isPowering = false;
	public PipeItemsRedstone(int itemID) {
		super(new PipeTransportItems(), new PipeLogicStone(), itemID);
	}

	@Override
	public void readjustSpeed (IPipedItem item) {
		if (item.getSpeed() > Utils.pipeNormalSpeed) {
			item.setSpeed(item.getSpeed() - Utils.pipeNormalSpeed / 2.0F);
		}

		if (item.getSpeed() < Utils.pipeNormalSpeed) {
			item.setSpeed(Utils.pipeNormalSpeed);
		}
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(
			LinkedList<ForgeDirection> possibleOrientations, Position pos,
			IPipedItem item) {
		return possibleOrientations;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if ( ((PipeTransportItems)transport).travelingEntities.size() == 0 && isPowering) {
			isPowering = false;
			UpdateTiles(container.xCoord, container.yCoord, container.zCoord);
		}
		else if ( ((PipeTransportItems)transport).travelingEntities.size() > 0 && !isPowering) {
			isPowering = true;
			UpdateTiles(container.xCoord, container.yCoord, container.zCoord);
		}
	}

	private void UpdateTiles(int i, int j, int k) {
		worldObj.notifyBlocksOfNeighborChange(i, j, k, BuildCraftTransport.genericPipeBlock.blockID);
	}

	@Override
	public boolean isPoweringTo(int l) {
		//System.out.println("RedStoneIsPoweringTo");
		if (((PipeTransportItems)transport).travelingEntities.size() == 0) {
			isPowering = false;
			return false;
		}

		isPowering = true;
		int i1 = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if(i1 == 5 && l == 1) {
			return false;
		}

		if(i1 == 3 && l == 3) {
			return false;
		}

		if(i1 == 4 && l == 2) {
			return false;
		}

		if(i1 == 1 && l == 5) {
			return false;
		}

		return i1 != 2 || l != 4;
	}

	@Override
	public boolean isIndirectlyPoweringTo(int l) {
		return isPoweringTo(l);
	}

	@Override
	public void entityEntered(IPipedItem item, ForgeDirection orientation) {
	}

	@Override
	public int getTextureIndex(ForgeDirection direction) {
		return isPowering ? 5 : 4;
	}

}
