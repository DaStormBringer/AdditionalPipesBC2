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
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_zAdditionalPipes;
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

    private @TileNetworkData int nextTexture = mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0;
    public @TileNetworkData int distData[] = {1, 1, 1, 1, 1, 1};
    public @TileNetworkData int curTick = 0;

    public PipeItemsDistributor(int itemID) {
        super(new PipeTransportItems(), new PipeLogicDistributor(), itemID);
    }

    @Override
    public void prepareTextureFor(Orientations connection) {
        if (connection == Orientations.Unknown) {
            nextTexture = mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0;
        }
        else {
            nextTexture = mod_zAdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0 + connection.ordinal();
        }

    }

    @Override
    public int getBlockTexture() {
        MutiPlayerProxy.bindTex();
        return nextTexture;
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

        curTick++;

        if (curTick >= distData[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)]) {
            ((PipeLogicDistributor)this.logic).switchPosition();
            curTick = 0;
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
        nbttagcompound.setInteger("curTick", curTick);

        for (int i = 0; i < distData.length; i++) {
            nbttagcompound.setInteger("Dist" + i, distData[i]);
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        curTick = nbttagcompound.getInteger("curTick");

        for (int i = 0; i < distData.length; i++) {
            distData[i] = nbttagcompound.getInteger("Dist" + i);
        }

        boolean found = false;

        for (int i = 0; i < distData.length; i++)
            if (distData[i] > 0) {
                found = true;
            }

        if (!found)
            for (int i = 0; i < distData.length; i++) {
                distData[i] = 1;
            }

    }
    public Packet230ModLoader getDescPipe() {
        Packet230ModLoader packet = new Packet230ModLoader();

        packet.modId = mod_zAdditionalPipes.instance.getId();
        packet.packetType = mod_zAdditionalPipes.PACKET_SET_DIST;
        packet.isChunkDataPacket = true;

        packet.dataInt = new int [9];

        packet.dataInt [0] = xCoord;
        packet.dataInt [1] = yCoord;
        packet.dataInt [2] = zCoord;

        for (int i = 0; i < distData.length; i++) {
            packet.dataInt [3 + i] = distData[i];
        }

        return packet;
    }

}
