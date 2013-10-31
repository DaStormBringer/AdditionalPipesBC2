/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import additionalpipes.AdditionalPipes;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipePowerDiamond;

public class PipePowerTeleport extends PipeTeleport implements IPipeTransportPowerHook
{
	private static final int ICON = 3;

	private static class PowerRequest
	{
		public final TileGenericPipe tile;
		public final ForgeDirection orientation;

		public PowerRequest(TileGenericPipe te, ForgeDirection o)
		{
			tile = te;
			orientation = o;
		}
	}

	public PipePowerTeleport(int itemID)
	{
		super(new PipeTransportPower(), itemID);
		((PipeTransportPower) transport).initFromPipe(PipePowerDiamond.class);
	}

	@Override
	public float requestEnergy(ForgeDirection from, float is)
	{
		float requested = 0;

		if ((state & 0x2) == 0) return requested;

		final List<PipeTeleport> pipeList = TeleportManager.instance.getConnectedPipes(this, true);

		if (pipeList.size() <= 0) return requested;

		for (final PipeTeleport pipe : pipeList)
		{
			final LinkedList<ForgeDirection> possibleMovements = getRealPossibleMovements(pipe);
			for (final ForgeDirection orientation : possibleMovements)
			{
				final TileEntity tile = pipe.container.getTile(orientation);
				if (tile instanceof TileGenericPipe)
				{
					final TileGenericPipe adjacentTile = (TileGenericPipe) tile;
					final PipeTransportPower nearbyTransport = (PipeTransportPower) adjacentTile.pipe.transport;
					nearbyTransport.requestEnergy(orientation.getOpposite(), is);
					// TODO does this work??
					requested += nearbyTransport.nextPowerQuery[orientation.getOpposite().ordinal()];
				}
			}
		}
		return requested;
	}

	@Override
	public float receiveEnergy(ForgeDirection from, float energy)
	{
		final List<PipeTeleport> connectedPipes = TeleportManager.instance.getConnectedPipes(this, false);
		final List<PipeTeleport> sendingToList = new LinkedList<PipeTeleport>();

		// no connected pipes, leave!
		if ((connectedPipes.size() <= 0) || ((state & 0x1) == 0)) return 0;

		for (final PipeTeleport pipe : connectedPipes)
		{
			if (getPipesNeedsPower(pipe).size() > 0)
			{
				sendingToList.add(pipe);
			}
		}

		// no pipes need energy, leave!
		if (sendingToList.size() <= 0) return 0;

		// TODO proportional power relay
		final float powerToSend = (AdditionalPipes.instance.powerLossCfg * energy) / sendingToList.size();

		for (final PipeTeleport receiver : sendingToList)
		{
			final List<PowerRequest> needsPower = getPipesNeedsPower(receiver);

			if (needsPower.size() <= 0)
			{
				continue;
			}

			final float dividedPowerToSend = powerToSend / needsPower.size();

			for (final PowerRequest powerEntry : needsPower)
			{
				final PipeTransportPower nearbyTransport = (PipeTransportPower) powerEntry.tile.pipe.transport;
				nearbyTransport.receiveEnergy(powerEntry.orientation, dividedPowerToSend);
			}
		}
		return energy;
	}

	private List<PowerRequest> getPipesNeedsPower(PipeTeleport pipe)
	{
		final LinkedList<ForgeDirection> possibleMovements = getRealPossibleMovements(pipe);
		final List<PowerRequest> needsPower = new LinkedList<PowerRequest>();

		if (possibleMovements.size() > 0)
		{
			for (final ForgeDirection orientation : possibleMovements)
			{
				final TileEntity tile = pipe.container.getTile(orientation);
				if (tile instanceof TileGenericPipe)
				{
					final TileGenericPipe adjacentPipe = (TileGenericPipe) tile;
					if (pipeNeedsPower(adjacentPipe))
					{
						needsPower.add(new PowerRequest(adjacentPipe, orientation.getOpposite()));
					}
				}
			}
		}

		return needsPower;
	}

	// precondition: power pipe that isn't tp
	private static boolean pipeNeedsPower(TileGenericPipe tile)
	{
		if (tile instanceof TileGenericPipe)
		{
			final PipeTransportPower ttb = (PipeTransportPower) tile.pipe.transport;
			for (final int element : ttb.nextPowerQuery)
				if (element > 0) return true;
		}
		return false;
	}

	// returns all adjacent pipes
	private static LinkedList<ForgeDirection> getRealPossibleMovements(PipeTeleport pipe)
	{
		final LinkedList<ForgeDirection> result = new LinkedList<ForgeDirection>();

		for (final ForgeDirection orientation : ForgeDirection.VALID_DIRECTIONS)
		{
			if (pipe.outputOpen(orientation))
			{
				final TileEntity te = pipe.container.getTile(orientation);
				if ((te instanceof TileGenericPipe) && Utils.checkPipesConnections(pipe.container, te))
				{
					result.add(orientation);
				}
			}
		}

		return result;
	}

	@Override
	public int getIconIndex(ForgeDirection direction)
	{
		return ICON;
	}

}
