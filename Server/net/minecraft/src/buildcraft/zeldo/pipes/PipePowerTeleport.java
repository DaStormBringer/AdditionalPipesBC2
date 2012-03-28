/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.zeldo.pipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.api.IPowerReceptor;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.transport.IPipeTransportPowerHook;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeTransportPower;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;
import net.minecraft.src.buildcraft.zeldo.logic.PipeLogicPowerTeleport;

public class PipePowerTeleport extends Pipe implements IPipeTransportPowerHook {
	public class PowerReturn
	{
		public TileEntity tile;
		public Orientations ori;

		public PowerReturn(TileEntity te, Orientations o)
		{
			tile = te; ori = o;
		}
	}
	public @TileNetworkData static final double PowerLoss = .0595;
	// Changed the PowerLoss to a configurable Variable
	//public @TileNetworkData static final double PowerLoss = mod_zAdditionalPipes.PowerLossCfg;
	public @TileNetworkData int myFreq = 0;
	public @TileNetworkData boolean canReceive = false;
	public @TileNetworkData String Owner = "";
	public @TileNetworkData static List<PipePowerTeleport> PowerTeleportPipes = new LinkedList<PipePowerTeleport>();
	public PipePowerTeleport(int itemID) {
		super(new PipeTransportPower(), new PipeLogicPowerTeleport(), itemID);

	}
	
	public void updateEntity()
    {
    	if (!PowerTeleportPipes.contains(this)) {
    		PowerTeleportPipes.add(this);
    	}
    }
	
	@Override
	public int getBlockTexture() {
		MutiPlayerProxy.bindTex();
		return mod_zAdditionalPipes.DEFUALT_POWER_TELEPORT_TEXTURE;
	}

	public double calculateLoss(int distance, double power)
	{
		power = power * Math.pow(PowerLoss, distance);
		return power;
	}

	@Override
	public void receiveEnergy(Orientations from, double val) {
		((PipeTransportPower)this.transport).step();
		List<PipePowerTeleport> pipeList = getConnectedPipes(false);
		List<PipePowerTeleport> sendingToList = new LinkedList<PipePowerTeleport>();
		if (pipeList.size() == 0)
			return;

		for (int a=0; a<pipeList.size(); a++)
		{
			if (TeleportNeedsPower(pipeList.get(a)).size() > 0)
				sendingToList.add(pipeList.get(a));
		}
		if (sendingToList.size() == 0)
			return;
		double powerToSend = val / sendingToList.size();
		//System.out.println("SendingToList: " + sendingToList.size() + " - PowerToSend: " + powerToSend);
		for (int a=0; a<sendingToList.size(); a++)
		{
			List<PowerReturn> needsPower = TeleportNeedsPower(sendingToList.get(a));
			if (needsPower.size() == 0)
				return;
			double powerToSendAfterLoss = calculateLoss(getDistance(sendingToList.get(a).xCoord, sendingToList.get(a).yCoord, sendingToList.get(a).zCoord), powerToSend);
			//System.out.println("Power After Loss: " + powerToSendAfterLoss);
			double powerToSend2 = powerToSendAfterLoss / needsPower.size();
			//System.out.println("needsPower: " + needsPower.size() + " - PowerToSend2: " + powerToSend2);
			for (int b=0; b<needsPower.size(); b++)
			{
				if (needsPower.get(b).tile instanceof TileGenericPipe) {
					TileGenericPipe nearbyTile = (TileGenericPipe) needsPower.get(b).tile;
					PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
					nearbyTransport.receiveEnergy(needsPower.get(b).ori, powerToSend);
				} else if (needsPower.get(b).tile instanceof IPowerReceptor) {
					IPowerReceptor pow = (IPowerReceptor) needsPower.get(b);
					pow.getPowerProvider().receiveEnergy((int)powerToSend);
				}
			}

		}










		//		//System.out.println("PipeList Size: " + pipeList.size());
		//		int i = worldObj.rand.nextInt(pipeList.size());
		//		LinkedList<Orientations> theList = getRealPossibleMovements(pipeList.get(i).getPosition());
		//		if (theList.size() <= 0)
		//			return;
		//
		//		Orientations newPos = theList.get(worldObj.rand.nextInt(theList.size()));
		//		Position destPos = new Position((int) pipeList.get(i).xCoord, pipeList.get(i).yCoord, pipeList.get(i).zCoord, newPos);
		//		destPos.moveForwards(1.0);
		//
		//
		//
		//
		//		TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);
		//		if (tile instanceof TileGenericPipe) {
		//			TileGenericPipe nearbyTile = (TileGenericPipe) tile;
		//			PipeTransportPower nearbyTransport = (PipeTransportPower) nearbyTile.pipe.transport;
		//			nearbyTransport.receiveEnergy(newPos.reverse(), val);
		//		} else if (tile instanceof IPowerReceptor) {
		//			IPowerReceptor pow = (IPowerReceptor) tile;
		//			pow.getPowerProvider().receiveEnergy((int)val);
		//		}

	}
	public List<PowerReturn> TeleportNeedsPower(PipePowerTeleport a)
	{
		LinkedList<Orientations> theList = getRealPossibleMovements(a.getPosition());
		List<PowerReturn> needsPower = new LinkedList<PowerReturn>();
		if (theList.size() > 0)
		{
			for (int b=0; b<theList.size(); b++)
			{
				Orientations newPos = theList.get(b);
				Position destPos = new Position(a.xCoord, a.yCoord, a.zCoord, newPos);
				destPos.moveForwards(1.0);
				TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);
				if (TileNeedsPower(tile))
					needsPower.add(new PowerReturn(tile, newPos.reverse()));

			}
		}
		return needsPower;
	}
	public boolean TileNeedsPower(TileEntity tile)
	{

		if (tile instanceof TileGenericPipe) {
			PipeTransportPower ttb = (PipeTransportPower) ((TileGenericPipe)tile).pipe.transport;
			for (int i=0; i<ttb.powerQuery.length; i++)
				if (ttb.powerQuery[i] > 0)
					return true;
		} else if (tile instanceof IPowerReceptor) {
		}

		return false;
	}
	public List<PipePowerTeleport> getConnectedPipes(boolean ignoreReceive) {
		List<PipePowerTeleport> Temp = new LinkedList<PipePowerTeleport>();
		removeOldPipes();
		for (int i=0; i< PowerTeleportPipes.size(); i++) {
			if (PowerTeleportPipes.get(i).Owner.equalsIgnoreCase(Owner) || MutiPlayerProxy.isOnServer() == false) {
				if (PowerTeleportPipes.get(i).canReceive || ignoreReceive) {
					if (PowerTeleportPipes.get(i).myFreq == myFreq) {
						if (xCoord != PowerTeleportPipes.get(i).xCoord || yCoord != PowerTeleportPipes.get(i).yCoord || zCoord != PowerTeleportPipes.get(i).zCoord ) {
							////System.out.print("MyPos: " + getPosition().toString() + " ++ Pos: " + teleportPipes.get(i).getPosition().toString() + "\n");
							//System.out.println("aExists: " + (worldObj.getBlockTileEntity(ItemTeleportPipes.get(i).xCoord, ItemTeleportPipes.get(i).yCoord, ItemTeleportPipes.get(i).zCoord) instanceof TileGenericPipe));
							Temp.add(PowerTeleportPipes.get(i));
						}
					}
				}
			}
		}

		return Temp;
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
			if (Utils.checkPipesConnections(worldObj, (int) p.x, (int) p.y, (int) p.z, xCoord, yCoord, zCoord))
			{
				return true;
			}
		}
		return false;
	}
	public void removeOldPipes()
	{
		LinkedList <PipePowerTeleport> toRemove = new LinkedList <PipePowerTeleport> ();
		for (int i=0; i< PowerTeleportPipes.size(); i++) {
			if (!(worldObj.getBlockTileEntity(PowerTeleportPipes.get(i).xCoord, PowerTeleportPipes.get(i).yCoord, PowerTeleportPipes.get(i).zCoord) instanceof TileGenericPipe)) {
				//System.out.println("Removed: " + i);
				toRemove.add(PowerTeleportPipes.get(i));
				//MutiPlayerProxy.DeleteChunkFromList(PowerTeleportPipes.get(i).xCoord, PowerTeleportPipes.get(i).zCoord);
			}
		}
		PowerTeleportPipes.removeAll(toRemove);
		//MutiPlayerProxy.AddChunkToList(xCoord, zCoord);
	}
	@Override
	public void setPosition (int xCoord, int yCoord, int zCoord) {
		LinkedList <PipePowerTeleport> toRemove = new LinkedList <PipePowerTeleport> ();
		for (int i=0; i< PowerTeleportPipes.size(); i++) {
			if (PowerTeleportPipes.get(i).xCoord == xCoord &&  PowerTeleportPipes.get(i).yCoord == yCoord && PowerTeleportPipes.get(i).zCoord == zCoord) {
				//System.out.println("Removed OldLoc: " + i);
				toRemove.add(PowerTeleportPipes.get(i));
			}
		}
		PowerTeleportPipes.removeAll(toRemove);
		PowerTeleportPipes.add(this);
		super.setPosition(xCoord, yCoord, zCoord);
		//MutiPlayerProxy.AddChunkToList(xCoord, zCoord);
	}
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		//MutiPlayerProxy.AddChunkToList(xCoord, zCoord);
		nbttagcompound.setInteger("Freq", myFreq);
		nbttagcompound.setBoolean("Rec", canReceive);
		nbttagcompound.setString("Owner", Owner);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		//MutiPlayerProxy.AddChunkToList(xCoord, zCoord);
		myFreq = nbttagcompound.getInteger("Freq");
		canReceive = nbttagcompound.getBoolean("Rec");
		Owner = nbttagcompound.getString("Owner");
	}

	public int getDistance(int x, int y, int z) {
		return (int) Math.sqrt(((xCoord - x) * (xCoord - x)) + ((yCoord - y) * (yCoord - y)) + ((zCoord - z) * (zCoord - z)));
	}
	public Position getPosition() {
		return new Position (xCoord, yCoord, zCoord);
	}

	@Override
	public void requestEnergy(Orientations from, int is) {
		((PipeTransportPower)this.transport).step();
		if (!canReceive) //No need to waste CPU
			return;
		List<PipePowerTeleport> pipeList = getConnectedPipes(true);
		if (pipeList.size() == 0)
			return;

		for (int a=0; a<pipeList.size(); a++) {
			LinkedList<Orientations> theList = getRealPossibleMovements(pipeList.get(a).getPosition());
			if (theList.size() > 0)
			{
				for (int b=0; b<theList.size(); b++)
				{
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
	public Packet230ModLoader getDescPipe() {
		Packet230ModLoader packet = new Packet230ModLoader();

		packet.modId = mod_zAdditionalPipes.instance.getId();
		packet.packetType = mod_zAdditionalPipes.PACKET_SET_POWER;
		packet.isChunkDataPacket = true;

		packet.dataInt = new int [5];

		packet.dataInt [0] = xCoord;
		packet.dataInt [1] = yCoord;
		packet.dataInt [2] = zCoord;
		packet.dataInt [3] = myFreq;
		packet.dataInt [4] = mod_zAdditionalPipes.boolToInt(canReceive);

		packet.dataString = new String[1];
		packet.dataString[0] = Owner;


		return packet;
	}


}
