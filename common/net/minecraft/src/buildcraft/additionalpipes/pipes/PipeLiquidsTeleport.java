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
import net.minecraft.src.mod_AdditionalPipes;
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
import net.minecraft.src.buildcraft.additionalpipes.logic.PipeLogicTeleport;
import net.minecraft.src.buildcraft.additionalpipes.network.NetworkID;

public class PipeLiquidsTeleport extends Pipe implements IPipeTransportLiquidsHook {

    class OilReturn {
        public Orientations theOrientation;
        public ILiquidContainer iliquid;
        public OilReturn(Orientations a, ILiquidContainer b) {
            theOrientation = a;
            iliquid = b;
        }
    }

    public @TileNetworkData static List<PipeLiquidsTeleport> LiquidTeleportPipes = new LinkedList<PipeLiquidsTeleport>();
    LinkedList <Integer> idsToRemove = new LinkedList <Integer> ();

    public PipeLiquidsTeleport(int itemID) {
        super(new PipeTransportLiquids(), new PipeLogicTeleport(NetworkID.GUI_PIPE_TP_LIQUID), itemID);
    }

    public void updateEntity() {
        if (!LiquidTeleportPipes.contains(this)) {
            LiquidTeleportPipes.add(this);
        }
    }
    
    @Override
    public int getBlockTexture() {
        return mod_AdditionalPipes.DEFUALT_LIQUID_TELEPORT_TEXTURE;
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
    	
        List<PipeLiquidsTeleport> temp = new LinkedList<PipeLiquidsTeleport>();
        removeOldPipes();
        
        PipeLogicTeleport logic = (PipeLogicTeleport) this.logic;

        for (PipeLiquidsTeleport pipe : LiquidTeleportPipes) {
        	
        	PipeLogicTeleport pipeLogic = (PipeLogicTeleport) pipe.logic;
        	
        	if (pipeLogic.owner.equalsIgnoreCase(logic.owner) || MutiPlayerProxy.isOnServer() == false) {
        		
                if (pipeLogic.canReceive || ignoreReceive) {
                	
                    if (pipeLogic.freq == logic.freq) {
                    	
                        if (xCoord != pipe.xCoord || yCoord != pipe.yCoord || zCoord != pipe.zCoord ) {
                            temp.add(pipe);
                        }
                    }
                }
            }
        	
        }

        for (int i = 0; i < LiquidTeleportPipes.size(); i++) {
            
        }

        return temp;
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

}
