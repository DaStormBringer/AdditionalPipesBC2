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
import buildcraft.api.power.IPowerReceptor;
import buildcraft.core.utils.Utils;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

public class PipePowerTeleport extends PipeTeleport implements IPipeTransportPowerHook {

	public class PowerReturn {
		public TileEntity tile;
		public Orientations ori;

		public PowerReturn(TileEntity te, Orientations o) {
			tile = te;
			ori = o;
		}
	}

	public PipePowerTeleport(int itemID) {
		super(new PipeTransportPower(), new PipeLogicTeleport(GuiHandler.PIPE_TP), itemID);

	}

	public double calculateLoss(int distance, double power) {

		return power;
	}

	@Override
	public void receiveEnergy(Orientations from, double val) {

		((PipeTransportPower)transport).step();
		List<PipeTeleport> pipeList = getConnectedPipes(false);
		List<PipeTeleport> sendingToList = new LinkedList<PipeTeleport>();

		if (pipeList.size() == 0) {
			return;
		}

		for (int a = 0; a < pipeList.size(); a++) {
			if (TeleportNeedsPower(pipeList.get(a)).size() > 0) {
				sendingToList.add(pipeList.get(a));
			}
		}

		if (sendingToList.size() == 0) {
			return;
		}

		double powerToSend = val / sendingToList.size();

		//System.out.println("SendingToList: " + sendingToList.size() + " - PowerToSend: " + powerToSend);
		for (int a = 0; a < sendingToList.size(); a++) {
			List<PowerReturn> needsPower = TeleportNeedsPower(sendingToList.get(a));

			if (needsPower.size() == 0) {
				return;
			}

			double powerToSendAfterLoss = calculateLoss(getDistance(sendingToList.get(a).xCoord, sendingToList.get(a).yCoord, sendingToList.get(a).zCoord), powerToSend);
			//System.out.println("Power After Loss: " + powerToSendAfterLoss);
			double powerToSend2 = powerToSendAfterLoss / needsPower.size();

			//System.out.println("needsPower: " + needsPower.size() + " - PowerToSend2: " + powerToSend2);
			for (int b = 0; b < needsPower.size(); b++) {
				if (needsPower.get(b).tile instanceof TileGenericPipe) {
					TileGenericPipe nearbyTile = (TileGenericPipe) needsPower.get(b).tile;
					PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
					nearbyTransport.receiveEnergy(needsPower.get(b).ori, powerToSend);
				}
				else if (needsPower.get(b).tile instanceof IPowerReceptor) {
					IPowerReceptor pow = (IPowerReceptor) needsPower.get(b);
					pow.getPowerProvider().receiveEnergy((int)powerToSend, Orientations.Unknown);
				}
			}

		}

	}

	public List<PowerReturn> TeleportNeedsPower(PipeTeleport a) {

		LinkedList<Orientations> theList = getRealPossibleMovements(a.getPosition());
		List<PowerReturn> needsPower = new LinkedList<PowerReturn>();

		if (theList.size() > 0) {
			for (int b = 0; b < theList.size(); b++) {
				Orientations newPos = theList.get(b);
				Position destPos = new Position(a.xCoord, a.yCoord, a.zCoord, newPos);
				destPos.moveForwards(1.0);
				TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);

				if (TileNeedsPower(tile)) {
					needsPower.add(new PowerReturn(tile, newPos.reverse()));
				}

			}
		}

		return needsPower;
	}

	public boolean TileNeedsPower(TileEntity tile) {

		if (tile instanceof TileGenericPipe) {
			PipeTransportPower ttb = (PipeTransportPower) ((TileGenericPipe)tile).pipe.transport;

			for (int i = 0; i < ttb.powerQuery.length; i++)
				if (ttb.powerQuery[i] > 0) {
					return true;
				}
		}
		else if (tile instanceof IPowerReceptor) {
		}

		return false;
	}

	public LinkedList<Orientations> getRealPossibleMovements(Position pos) {
		LinkedList<Orientations> result = new LinkedList<Orientations>();

		for (int o = 0; o < 6; ++o) {
			if (Orientations.values()[o] != pos.orientation.reverse() && container.pipe.outputOpen(Orientations.values()[o])) {
				Position newPos = new Position(pos);
				newPos.orientation = Orientations.values()[o];
				newPos.moveForwards(1.0);

				if (canReceivePower(newPos)) {
					result.add(newPos.orientation);
				}
			}
		}

		return result;
	}

	public boolean canReceivePower(Position p) {
		TileEntity entity = worldObj.getBlockTileEntity((int) p.x, (int) p.y, (int) p.z);

		if (entity instanceof TileGenericPipe || entity instanceof IPowerReceptor) {
			if (Utils.checkLegacyPipesConnections(worldObj, (int) p.x, (int) p.y, (int) p.z, xCoord, yCoord, zCoord)) {
				return true;
			}
		}

		return false;
	}
	/*
    @Override
    public void setPosition (int xCoord, int yCoord, int zCoord) {

        LinkedList <PipePowerTeleport> toRemove = new LinkedList <PipePowerTeleport> ();

        for (int i = 0; i < PowerTeleportPipes.size(); i++) {
            if (PowerTeleportPipes.get(i).xCoord == xCoord &&  PowerTeleportPipes.get(i).yCoord == yCoord && PowerTeleportPipes.get(i).zCoord == zCoord) {
                //System.out.println("Removed OldLoc: " + i);
                toRemove.add(PowerTeleportPipes.get(i));
            }
        }

        PowerTeleportPipes.removeAll(toRemove);
        PowerTeleportPipes.add(this);
        super.setPosition(xCoord, yCoord, zCoord);
    }*/

	public int getDistance(int x, int y, int z) {
		return (int) Math.sqrt(((xCoord - x) * (xCoord - x)) + ((yCoord - y) * (yCoord - y)) + ((zCoord - z) * (zCoord - z)));
	}

	@Override
	public Position getPosition() {
		return new Position (xCoord, yCoord, zCoord);
	}

	@Override
	public void requestEnergy(Orientations from, int is) {

		((PipeTransportPower)transport).step();

		if (!logic.canReceive) { //No need to waste CPU
			return;
		}

		List<PipeTeleport> pipeList = getConnectedPipes(true);

		if (pipeList.size() == 0) {
			return;
		}

		for (int a = 0; a < pipeList.size(); a++) {
			LinkedList<Orientations> theList = getRealPossibleMovements(pipeList.get(a).getPosition());

			if (theList.size() > 0) {
				for (int b = 0; b < theList.size(); b++) {
					Orientations newPos = theList.get(b);
					Position destPos = new Position(pipeList.get(a).xCoord, pipeList.get(a).yCoord, pipeList.get(a).zCoord, newPos);
					destPos.moveForwards(1.0);

					//System.out.println(getPosition().toString() + " RequestEnergy: " + from.toString() + " - Val: " + is + " - Dest: " + destPos.toString());

					TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);

					if (tile instanceof TileGenericPipe) {
						TileGenericPipe nearbyTile = (TileGenericPipe) tile;
						PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
						nearbyTransport.requestEnergy(newPos.reverse(), is);
					}
				}
			}
		}
	}

	@Override
	public String getTextureFile() {
		return AdditionalPipes.TEXTURE_POWER_TELEPORT;
	}

	@Override
	public int getTextureIndex(Orientations direction) {
		return 0;
	}

}
