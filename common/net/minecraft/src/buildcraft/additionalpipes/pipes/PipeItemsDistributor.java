/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.additionalpipes.pipes;

import java.util.LinkedList;

import net.minecraft.src.IInventory;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_AdditionalPipes;
import net.minecraft.src.buildcraft.api.EntityPassiveItem;
import net.minecraft.src.buildcraft.api.IPipeEntry;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.StackUtil;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.energy.TileEngine;
import net.minecraft.src.buildcraft.transport.IPipeTransportItemsHook;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeTransportItems;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.additionalpipes.MutiPlayerProxy;
import net.minecraft.src.buildcraft.additionalpipes.logic.PipeLogicDistributor;

public class PipeItemsDistributor extends Pipe implements IPipeTransportItemsHook {

	public final PipeLogicDistributor logic;
	
    public PipeItemsDistributor(int itemID) {
        super(new PipeTransportItems(), new PipeLogicDistributor(), itemID);
        logic = (PipeLogicDistributor) super.logic;
    }

    @Override
    public void prepareTextureFor(Orientations connection) {
    	
        if (connection == Orientations.Unknown) {
            logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0;
        }
        else {
            switch(connection) {
                case YNeg:
                	logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0;
                    break;

                case YPos:
                	logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_1;
                    break;

                case ZNeg:
                	logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_2;
                    break;

                case ZPos:
                	logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_3;
                    break;

                case XNeg:
                	logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_4;
                    break;

                case XPos:
                	logic.nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_5;
                    break;

            }

            //nextTexture = mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0 + connection.ordinal();
        }

    }

    @Override
    public int getBlockTexture() {
          return logic.nextTexture;
    }

    @Override
    public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, EntityPassiveItem item) {

        ((PipeLogicDistributor)this.logic).switchIfNeeded();

        LinkedList<Orientations> result = new LinkedList<Orientations>();

        for (int o = 0; o < 6; ++o) {
            if (container.pipe.outputOpen(Orientations.values()[o])) {
                Position newPos = new Position(pos);
                newPos.orientation = Orientations.values()[o];
                newPos.moveForwards(1.0);

                if (canReceivePipeObjects(newPos, item)) {
                    result.add(newPos.orientation);
                }
            }
        }

        logic.curTick++;

        if (logic.curTick >= logic.distData[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)]) {
            ((PipeLogicDistributor)this.logic).switchPosition();
            logic.curTick = 0;
        }


        worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
        return result;
    }

    public boolean canReceivePipeObjects(Position p,
                                         EntityPassiveItem item) {
        TileEntity entity = worldObj.getBlockTileEntity((int) p.x, (int) p.y,
                            (int) p.z);

        if (!Utils.checkPipesConnections(worldObj, (int) p.x, (int) p.y,
                                         (int) p.z, xCoord, yCoord, zCoord)) {
            return false;
        }

        if (entity instanceof IPipeEntry) {
            return true;
        }
        else if (entity instanceof TileEngine) {
            return false;
        }
        else if (entity instanceof TileGenericPipe) {
            TileGenericPipe pipe = (TileGenericPipe) entity;
            return pipe.pipe.transport instanceof PipeTransportItems;
        }
        else if (entity instanceof IInventory) {
            if (new StackUtil(item.item).checkAvailableSlot((IInventory) entity,
                    false, p.orientation.reverse())) {
                return true;
            }
        }

        return false;
    }
    @Override
    public void entityEntered(EntityPassiveItem item, Orientations orientation) {

    }

    @Override
    public void readjustSpeed(EntityPassiveItem item) {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("curTick", logic.curTick);

        for (int i = 0; i < logic.distData.length; i++) {
            nbttagcompound.setInteger("Dist" + i, logic.distData[i]);
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        logic.curTick = nbttagcompound.getInteger("curTick");

        for (int i = 0; i < logic.distData.length; i++) {
        	logic.distData[i] = nbttagcompound.getInteger("Dist" + i);
        }

        boolean found = false;

        for (int i = 0; i < logic.distData.length; i++)
            if (logic.distData[i] > 0) {
                found = true;
            }

        if (!found)
            for (int i = 0; i < logic.distData.length; i++) {
            	logic.distData[i] = 1;
            }

    }
}
