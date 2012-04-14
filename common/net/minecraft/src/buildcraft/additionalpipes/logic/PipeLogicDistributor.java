/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.additionalpipes.logic;

import net.minecraft.src.buildcraft.additionalpipes.gui.GuiHandler;
import net.minecraft.src.buildcraft.additionalpipes.network.NetworkID;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsDistributor;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.IPipeEntry;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.transport.PipeLogic;
import net.minecraft.src.buildcraft.transport.PipeLogicWood;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.*;

public class PipeLogicDistributor extends PipeLogic {
	
	@TileNetworkData
	public int nextTexture = mod_AdditionalPipes.DEFUALT_DISTRIBUTOR_TEXTURE_0;
	
    @TileNetworkData (staticSize = 6)
    public int distData[] = {1, 1, 1, 1, 1, 1};
    
    @TileNetworkData
    public int curTick = 0;

    public void switchPosition() {
    	
        int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        int nextMetadata = metadata;

        for (int l = 0; l < 6; ++l) {
            nextMetadata ++;

            if (nextMetadata > 5) {
                nextMetadata = 0;
            }

            Position pos = new Position(xCoord, yCoord, zCoord, Orientations.values()[nextMetadata]);
            pos.moveForwards(1.0);

            TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

            if (tile instanceof TileGenericPipe) {
                if (((TileGenericPipe) tile).pipe.logic instanceof PipeLogicWood || ((TileGenericPipe) tile).pipe.logic instanceof PipeLogicAdvancedWood) {
                    continue;
                }
            }

            if (tile instanceof IPipeEntry || tile instanceof IInventory || tile instanceof ILiquidContainer || tile instanceof TileGenericPipe) {
                if (distData[nextMetadata] > 0) {
                    worldObj.setBlockMetadata(xCoord, yCoord, zCoord, nextMetadata);
                    return;
                }
            }
        }
    }
    public void switchIfNeeded() {
        int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        int nextMetadata = metadata;

        for (int l = 0; l < 6; ++l) {
            if (nextMetadata > 5) {
                nextMetadata = 0;
            }

            Position pos = new Position(xCoord, yCoord, zCoord, Orientations.values()[nextMetadata]);
            pos.moveForwards(1.0);

            TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

            if (tile instanceof TileGenericPipe) {
                if (((TileGenericPipe) tile).pipe.logic instanceof PipeLogicWood || ((TileGenericPipe) tile).pipe.logic instanceof PipeLogicAdvancedWood) {
                    continue;
                }
            }

            if (tile instanceof IPipeEntry || tile instanceof IInventory || tile instanceof ILiquidContainer || tile instanceof TileGenericPipe) {
                if (distData[nextMetadata] > 0) {
                    worldObj.setBlockMetadata(xCoord, yCoord, zCoord, nextMetadata);
                    return;
                }
            }

            nextMetadata ++;
        }
    }


    @Override
    public void onBlockPlaced() {
        super.onBlockPlaced();

        worldObj.setBlockMetadata(xCoord, yCoord, zCoord, 1);
        switchPosition();
    }

    @Override
    public boolean blockActivated(EntityPlayer entityplayer) {

        ItemStack equippedItem = entityplayer.getCurrentEquippedItem();
        
        if (equippedItem != null) {
            
            if (equippedItem.getItem() == BuildCraftCore.wrenchItem) {

                switchPosition();
                worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);

                return true;
            }

            if (mod_AdditionalPipes.isPipe(equippedItem.getItem())) {
                return false;
            }
        }
        
        entityplayer.openGui(mod_AdditionalPipes.instance, NetworkID.GUI_PIPE_DIST, 
                container.worldObj, container.xCoord, container.yCoord, container.zCoord);

        return true;
    }

    @Override
    public boolean outputOpen(Orientations to) {
        return to.ordinal() == worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    }

}
