/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.additionalpipes.pipes;

import net.minecraft.src.BuildCraftTransport;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_AdditionalPipes;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.IPipeEntry;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.additionalpipes.transport.IPipeProvideRedstonePowerHook;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogicGold;
import net.minecraft.src.buildcraft.transport.PipeTransportLiquids;

public class PipeLiquidsRedstone extends Pipe implements IPipeProvideRedstonePowerHook {
    private @TileNetworkData int nextTexture = mod_AdditionalPipes.DEFUALT_RedStoneLiquid_TEXTURE;
    public @TileNetworkData boolean isPowering = false;
    public PipeLiquidsRedstone(int itemID) {
        super(new PipeTransportLiquids(), new PipeLogicGold(), itemID);

        ((PipeTransportLiquids) transport).flowRate = 80;
        ((PipeTransportLiquids) transport).travelDelay = 2;
    }

    @Override
    public void prepareTextureFor(Orientations connection) {
        if (!isPowering) {
            nextTexture = mod_AdditionalPipes.DEFUALT_RedStoneLiquid_TEXTURE;
        }
        else {
            nextTexture = mod_AdditionalPipes.DEFUALT_RedStoneLiquid_TEXTURE_POWERED;
        }
    }
    @Override
    public int getBlockTexture() {
        return nextTexture;
    }

    private void UpdateTiles(int i, int j, int k) {
        worldObj.notifyBlocksOfNeighborChange(i, j, k, BuildCraftTransport.genericPipeBlock.blockID);
    }

    @Override
    public boolean isPoweringTo(int l) {
        //System.out.println("RedStoneIsPoweringTo");
        if (((PipeTransportLiquids)this.transport).getCenter() < 250) {
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
        if ( ((PipeTransportLiquids)this.transport).getCenter() < 250 && isPowering) {
            isPowering = false;
            UpdateTiles(this.container.xCoord, this.container.yCoord, this.container.zCoord);
        }
        else if (!isPowering) {
            isPowering = true;
            UpdateTiles(this.container.xCoord, this.container.yCoord, this.container.zCoord);
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
                ret[1] += ((PipeTransportLiquids)this.transport).side[i].average;
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

        if (!Utils.checkPipesConnections(worldObj, (int) p.x, (int) p.y,
                                         (int) p.z, xCoord, yCoord, zCoord)) {
            return false;
        }

        if (entity instanceof IPipeEntry || entity instanceof ILiquidContainer) {
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
}
