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
import buildcraft.additionalpipes.pipes.logic.PipeLogicTeleport;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

public class PipePowerTeleport extends PipeTeleport implements IPipeTransportPowerHook {

	public class PowerReturn {
		public TileEntity tile;
		public Orientations orientation;

		public PowerReturn(TileEntity te, Orientations o) {
			tile = te;
			orientation = o;
		}
	}

	public PipePowerTeleport(int itemID) {
		super(new PipeTransportPower(), new PipeLogicTeleport(), itemID);

	}

	@Override
	public void requestEnergy(Orientations from, int is) {
		((PipeTransportPower)transport).step();

		if (!logic.canReceive) { //No need to waste CPU
			return;
		}

		List<PipeTeleport> pipeList = TeleportManager.instance.getConnectedPipes(this, true);

		if (pipeList.size() <= 0) {
			return;
		}

		for (PipeTeleport pipe : pipeList) {
			LinkedList<Orientations> possibleMovements = getRealPossibleMovements(pipe.getPosition());
			for (Orientations orientation : possibleMovements) {
				TileEntity tile = pipe.container.getTile(orientation);
				if (tile instanceof TileGenericPipe) {
					TileGenericPipe adjacentTile = (TileGenericPipe) tile;
					PipeTransportPower nearbyTransport = (PipeTransportPower) adjacentTile.pipe.transport;
					nearbyTransport.requestEnergy(orientation.reverse(), is);
				}
			}
		}
	}

	@Override
	public void receiveEnergy(Orientations from, double val) {
		((PipeTransportPower)transport).step();
		List<PipeTeleport> connectedPipes = TeleportManager.instance.getConnectedPipes(this, false);
		List<PipeTeleport> sendingToList = new LinkedList<PipeTeleport>();

		//no connected pipes, leave!
		if (connectedPipes.size() <= 0) {
			return;
		}

		for (PipeTeleport pipe : connectedPipes) {
			if (needsPower(pipe).size() > 0) {
				sendingToList.add(pipe);
			}
		}

		//no pipes need energy, leave!
		if (sendingToList.size() <= 0) {
			return;
		}

		//TODO proportional power relay
		double powerToSend = val / sendingToList.size();

		for (PipeTeleport receiver : sendingToList) {
			List<PowerReturn> needsPower = needsPower(receiver);

			if (needsPower.size() == 0) {
				return;
			}

			double powerToSendAfterLoss = AdditionalPipes.instance.powerLossCfg * powerToSend;
			//System.out.println("Power After Loss: " + powerToSendAfterLoss);
			double powerToSend2 = powerToSendAfterLoss / needsPower.size();

			//System.out.println("needsPower: " + needsPower.size() + " - PowerToSend2: " + powerToSend2);
			for (int b = 0; b < needsPower.size(); b++) {
				if (needsPower.get(b).tile instanceof TileGenericPipe) {
					TileGenericPipe nearbyTile = (TileGenericPipe) needsPower.get(b).tile;
					PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
					nearbyTransport.receiveEnergy(needsPower.get(b).orientation, powerToSend);
				}
				else if (needsPower.get(b).tile instanceof IPowerReceptor) {
					IPowerReceptor pow = (IPowerReceptor) needsPower.get(b);
					pow.getPowerProvider().receiveEnergy((int)powerToSend, Orientations.Unknown);
				}
			}

		}

	}

	private List<PowerReturn> needsPower(PipeTeleport a) {
		LinkedList<Orientations> theList = getRealPossibleMovements(a.getPosition());
		List<PowerReturn> needsPower = new LinkedList<PowerReturn>();

		if (theList.size() > 0) {
			for (int b = 0; b < theList.size(); b++) {
				Orientations newPos = theList.get(b);
				Position destPos = new Position(a.xCoord, a.yCoord, a.zCoord, newPos);
				destPos.moveForwards(1.0);
				TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);

				if (tileNeedsPower(tile)) {
					needsPower.add(new PowerReturn(tile, newPos.reverse()));
				}

			}
		}

		return needsPower;
	}

	//precondition: power pipe that isn't tp
	private boolean tileNeedsPower(TileEntity tile) {
		if (tile instanceof TileGenericPipe) {
			PipeTransportPower ttb = (PipeTransportPower) ((TileGenericPipe)tile).pipe.transport;
			for (int i = 0; i < ttb.powerQuery.length; i++)
				if (ttb.powerQuery[i] > 0) {
					return true;
				}
		}
		else if (tile instanceof IPowerReceptor) {
			return ((IPowerReceptor) tile).powerRequest() > 0;
		}

		return false;
	}

	//returns all adjacent pipes
	private LinkedList<Orientations> getRealPossibleMovements(Position pos) {
		LinkedList<Orientations> result = new LinkedList<Orientations>();

		for (int o = 0; o < 6; ++o) {
			if (Orientations.values()[o] != pos.orientation.reverse() && container.pipe.outputOpen(Orientations.values()[o])) {
				TileEntity te = container.getTile(Orientations.values()[o]);
				if ((te instanceof TileGenericPipe || te instanceof IPowerReceptor) &&
						Utils.checkPipesConnections(container, te)) {
					result.add(Orientations.values()[o]);
				}
			}
		}

		return result;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 3;
	}

}
