/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.IPipeEntry;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.transport.IPipeTransportLiquidsHook;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeTransportLiquids;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.additionalpipes.MutiPlayerProxy;
import net.minecraft.src.buildcraft.additionalpipes.logic.PipeLogicLiquidTeleport;

public class PipeLiquidsTeleport extends Pipe implements IPipeTransportLiquidsHook {

    class OilReturn {
        public Orientations theOrientation;
        public ILiquidContainer iliquid;
        public OilReturn(Orientations a, ILiquidContainer b) {
            theOrientation = a;
            iliquid = b;
        }
    }

    public @TileNetworkData int myFreq = 0;
    public @TileNetworkData boolean canReceive = false;
    public @TileNetworkData String Owner = "";
    public @TileNetworkData static List<PipeLiquidsTeleport> LiquidTeleportPipes = new LinkedList<PipeLiquidsTeleport>();
    LinkedList <Integer> idsToRemove = new LinkedList <Integer> ();

    public PipeLiquidsTeleport(int itemID) {
        super(new PipeTransportLiquids(), new PipeLogicLiquidTeleport(), itemID);
    }

    public void updateEntity() {
        if (!LiquidTeleportPipes.contains(this)) {
            LiquidTeleportPipes.add(this);
        }
    }
    @Override
    public int getBlockTexture() {
        return mod_zAdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE;
    }

    public void removeOldPipes() {
        LinkedList <PipeLiquidsTeleport> toRemove = new LinkedList <PipeLiquidsTeleport> ();

        for (int i = 0; i < LiquidTeleportPipes.size(); i++) {
            if (!(worldObj.getBlockTileEntity(LiquidTeleportPipes.get(i).xCoord, LiquidTeleportPipes.get(i).yCoord, LiquidTeleportPipes.get(i).zCoord) instanceof TileGenericPipe)) {
                //System.out.println("Removed: " + i);
                toRemove.add(LiquidTeleportPipes.get(i));
                //MutiPlayerProxy.DeleteChunkFromList(LiquidTeleportPipes.get(i).xCoord, LiquidTeleportPipes.get(i).zCoord);
            }
        }

        LiquidTeleportPipes.removeAll(toRemove);

    }

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
    }

    public List<PipeLiquidsTeleport> getConnectedPipes(boolean ignoreReceive) {
        List<PipeLiquidsTeleport> Temp = new LinkedList<PipeLiquidsTeleport>();
        removeOldPipes();

        for (int i = 0; i < LiquidTeleportPipes.size(); i++) {
            if (LiquidTeleportPipes.get(i).Owner.equalsIgnoreCase(Owner) || MutiPlayerProxy.isOnServer() == false) {
                if (LiquidTeleportPipes.get(i).canReceive || ignoreReceive) {
                    if (LiquidTeleportPipes.get(i).myFreq == myFreq) {
                        if (xCoord != LiquidTeleportPipes.get(i).xCoord || yCoord != LiquidTeleportPipes.get(i).yCoord || zCoord != LiquidTeleportPipes.get(i).zCoord ) {
                            ////System.out.print("MyPos: " + getPosition().toString() + " ++ Pos: " + teleportPipes.get(i).getPosition().toString() + "\n");
                            //System.out.println("aExists: " + (worldObj.getBlockTileEntity(ItemTeleportPipes.get(i).xCoord, ItemTeleportPipes.get(i).yCoord, ItemTeleportPipes.get(i).zCoord) instanceof TileGenericPipe));
                            Temp.add(LiquidTeleportPipes.get(i));
                        }
                    }
                }
            }
        }

        return Temp;
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

    @Override
    public int fill(Orientations from, int quantity, int id, boolean doFill) {
        List<PipeLiquidsTeleport> pipeList = getConnectedPipes(false);

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
            used = theList.get(a).iliquid.fill(theList.get(a).theOrientation.reverse(), quantity, id, doFill);
            theList.remove(a);
        }

        //System.out.println("Fill " + used);
        return used;

    }

    public LinkedList<OilReturn> getPossibleLiquidMovements(Position pos) {
        LinkedList<OilReturn> result = new LinkedList<OilReturn>();

        for (int o = 0; o <= 5; ++o) {
            Position newPos = new Position(pos);
            newPos.orientation = Orientations.values()[o];
            newPos.moveForwards(1.0);

            if (canReceiveLiquid2(newPos)) {

                //For better handling in future
                //int space = BuildCraftCore.OIL_BUCKET_QUANTITY / 4 - sideToCenter[((Orientations.values()[o]).reverse()).ordinal()] - centerToSide[((Orientations.values()[o]).reverse()).ordinal()] + flowRate;
                result.add(new OilReturn(Orientations.values()[o], (ILiquidContainer) Utils.getTile(worldObj, newPos, Orientations.Unknown)));
            }
        }

        return result;
    }
    public boolean canReceiveLiquid2(Position p) {
        TileEntity entity = worldObj.getBlockTileEntity((int) p.x, (int) p.y,
                            (int) p.z);

        if (!Utils.checkPipesConnections(worldObj, (int) p.x, (int) p.y,
                                         (int) p.z, xCoord, yCoord, zCoord)) {
            return false;
        }

        if (entity instanceof IPipeEntry || entity instanceof ILiquidContainer) {
            return true;
        }

        return false;
    }
    public Position getPosition() {
        return new Position (xCoord, yCoord, zCoord);
    }
    /*
    public Packet230ModLoader getDescPipe() {
        Packet230ModLoader packet = new Packet230ModLoader();

        packet.modId = mod_zAdditionalPipes.instance.getId();
        packet.packetType = mod_zAdditionalPipes.PACKET_SET_LIQUID;
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
    } */

}
