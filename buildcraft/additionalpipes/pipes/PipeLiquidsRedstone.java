/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.transport.IPipeProvideRedstonePowerHook;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.liquids.ITankContainer;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.core.network.TileNetworkData;
import buildcraft.core.utils.Utils;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportLiquids;
import buildcraft.transport.pipes.PipeLogicGold;

public class PipeLiquidsRedstone extends Pipe implements IPipeProvideRedstonePowerHook {
	public @TileNetworkData boolean isPowering = false;

	public PipeLiquidsRedstone(int itemID) {
		super(new PipeTransportLiquids(), new PipeLogicGold(), itemID);

		((PipeTransportLiquids) transport).flowRate = 80;
		((PipeTransportLiquids) transport).travelDelay = 2;
	}

	private void UpdateTiles(int i, int j, int k) {
		worldObj.notifyBlocksOfNeighborChange(i, j, k, BuildCraftTransport.genericPipeBlock.blockID);
	}

	@Override
	public boolean isPoweringTo(int l) {
		//System.out.println("RedStoneIsPoweringTo");
		if (((PipeTransportLiquids)transport).getCenter() < 250) {
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
	public void updateEntity() {
		super.updateEntity();

		//System.out.println("Quantity: " + (((PipeTransportLiquids)this.transport).getLiquidQuantity()) + " - Wanted: " + computeMaxLiquid() + " - Qua2: " + computeEnds()[1]);
		//System.out.println("Quantity: " + ((PipeTransportLiquids)this.transport).getCenter());
		if ( ((PipeTransportLiquids)transport).getCenter() < 250 && isPowering) {
			isPowering = false;
			UpdateTiles(container.xCoord, container.yCoord, container.zCoord);
		}
		else if (!isPowering) {
			isPowering = true;
			UpdateTiles(container.xCoord, container.yCoord, container.zCoord);
		}
	}

	private int[] computeEnds() {
		int outputNumber = 0;
		int total = 0;
		int ret[] = new int[2];

		for (int i = 0; i < 6; ++i) {
			Position p = new Position(xCoord, yCoord, zCoord, Orientations.values()[i]);
			p.moveForwards(1);

			if (canRec(p)) {
				ret[0]++;
				ret[1] += ((PipeTransportLiquids)transport).side[i].average;
			}
		}

		return ret;
	}

	public int computeMaxLiquid() {
		return ((computeEnds()[0] + 1) * PipeTransportLiquids.LIQUID_IN_PIPE);
	}

	public boolean canRec(Position p) {
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
	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k,
			int l) {
		return isPoweringTo(l);
	}

	@Override
	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return isIndirectlyPoweringTo(l);
	}

	@Override
	public String getTextureFile() {
		if (!isPowering) {
			return AdditionalPipes.TEXTURE_REDSTONE;
		} else {
			return AdditionalPipes.TEXTURE_REDSTONE_POWERED;
		}
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}
}
