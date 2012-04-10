/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.additionalpipes.pipes;

import java.util.LinkedList;

import net.minecraft.src.BuildCraftTransport;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;
import net.minecraft.src.mod_AdditionalPipes;
import net.minecraft.src.buildcraft.api.EntityPassiveItem;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.additionalpipes.transport.IPipeProvideRedstonePowerHook;
import net.minecraft.src.buildcraft.transport.IPipeTransportItemsHook;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogicStone;
import net.minecraft.src.buildcraft.transport.PipeTransportItems;

public class PipeItemsRedstone extends Pipe implements IPipeTransportItemsHook, IPipeProvideRedstonePowerHook {

    private @TileNetworkData int nextTexture = mod_AdditionalPipes.DEFUALT_RedStone_TEXTURE;
    public @TileNetworkData boolean isPowering = false;
    public PipeItemsRedstone(int itemID) {
        super(new PipeTransportItems(), new PipeLogicStone (), itemID);
    }

    @Override
    public void prepareTextureFor(Orientations connection) {
        if (!isPowering) {
            nextTexture = mod_AdditionalPipes.DEFUALT_RedStone_TEXTURE;
        }
        else {
            nextTexture = mod_AdditionalPipes.DEFUALT_RedStone_TEXTURE_POWERED;
        }
    }
    @Override
    public int getBlockTexture() {
        return nextTexture;
    }

    @Override
    public void readjustSpeed (EntityPassiveItem item) {
        if (item.speed > Utils.pipeNormalSpeed) {
            item.speed = item.speed - Utils.pipeNormalSpeed / 2.0F;
        }

        if (item.speed < Utils.pipeNormalSpeed) {
            item.speed = Utils.pipeNormalSpeed;
        }
    }

    @Override
    public LinkedList<Orientations> filterPossibleMovements(
        LinkedList<Orientations> possibleOrientations, Position pos,
        EntityPassiveItem item) {
        return possibleOrientations;
    }

    @Override
    public void entityEntered(EntityPassiveItem item, Orientations orientation) {
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if ( ((PipeTransportItems)this.transport).travelingEntities.size() == 0 && isPowering) {
            isPowering = false;
            UpdateTiles(this.container.xCoord, this.container.yCoord, this.container.zCoord);
        }
        else if ( ((PipeTransportItems)this.transport).travelingEntities.size() > 0 && !isPowering) {
            isPowering = true;
            UpdateTiles(this.container.xCoord, this.container.yCoord, this.container.zCoord);
        }
    }

    private void UpdateTiles(int i, int j, int k) {
        worldObj.notifyBlocksOfNeighborChange(i, j, k, BuildCraftTransport.genericPipeBlock.blockID);
    }

    @Override
    public boolean isPoweringTo(int l) {
        //System.out.println("RedStoneIsPoweringTo");
        if (((PipeTransportItems)this.transport).travelingEntities.size() == 0) {
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
    public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k,
                                int l) {
        return isPoweringTo(l);
    }

    @Override
    public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
        return isIndirectlyPoweringTo(l);
    }

}
