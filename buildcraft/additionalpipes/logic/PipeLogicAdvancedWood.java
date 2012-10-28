/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.logic;

import buildcraft.BuildCraftTransport;
import buildcraft.additionalpipes.mod_AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkID;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.core.network.TileNetworkData;
import buildcraft.core.utils.Utils;
import buildcraft.transport.*;
import buildcraft.transport.pipes.PipeLogic;
import buildcraft.transport.pipes.PipeLogicWood;
import net.minecraft.src.*;
import buildcraft.api.liquids.*;
import buildcraft.api.tools.*;

public class PipeLogicAdvancedWood extends PipeLogic {

    @TileNetworkData (staticSize = 9)
    public ItemStack [] items = new ItemStack [9];
    
    @TileNetworkData
    public boolean exclude = false;
    
    @TileNetworkData
    public int nextTexture;

    public void switchSource () {
    	
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        int newMeta = 6;

        for (int i = meta + 1; i <= meta + 6; ++i) {
        	
            Orientations o = Orientations.values() [i % 6];

            Position pos = new Position (xCoord, yCoord, zCoord, o);

            pos.moveForwards(1);

            TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y,
                              (int) pos.z);

            if (isInput (tile)) {
                newMeta = o.ordinal();
                break;
            }
        }

        if (newMeta != meta) {
            worldObj.setBlockMetadata(xCoord, yCoord, zCoord, newMeta);
            worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
        }
    }

    public boolean isInput(TileEntity tile) {
        return !(tile instanceof TileGenericPipe)
               && (tile instanceof IInventory || tile instanceof ITankContainer)
               &&  Utils.checkLegacyPipesConnections(worldObj, xCoord, yCoord,
                                               zCoord, tile.xCoord, tile.yCoord, tile.zCoord);
    }


    @Override
    public boolean blockActivated(EntityPlayer entityplayer) {
        
        ItemStack equippedItem = entityplayer.getCurrentEquippedItem();
        
        if (equippedItem != null) {

            if (equippedItem.getItem() instanceof IToolWrench) {
                switchSource();
                return true;
            }

            if (mod_AdditionalPipes.isPipe(equippedItem.getItem())) {
                return false;
            }
        }

        entityplayer.openGui(mod_AdditionalPipes.instance, NetworkID.GUI_PIPE_WOODEN_ADV, 
                container.worldObj, container.xCoord, container.yCoord, container.zCoord);

        return true;
    }

    @Override
    public boolean isPipeConnected(TileEntity tile) {
        Pipe pipe2 = null;

        if (tile instanceof TileGenericPipe) {
            pipe2 = ((TileGenericPipe) tile).pipe;
        }

        if (BuildCraftTransport.alwaysConnectPipes) {
            return super.isPipeConnected(tile);
        }
        else {
            return (pipe2 == null || (!(pipe2.logic instanceof PipeLogicWood) && !(pipe2.logic instanceof PipeLogicAdvancedWood))) && super.isPipeConnected(tile);
        }
    }

    @Override
    public void initialize () {
        super.initialize();

        switchSourceIfNeeded();
    }

    private void switchSourceIfNeeded () {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        if (meta > 5) {
            switchSource();
        }
        else {
            Position pos = new Position(xCoord, yCoord, zCoord,
                                        Orientations.values()[meta]);
            pos.moveForwards(1);

            TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y,
                              (int) pos.z);

            if (!isInput(tile)) {
                switchSource();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        exclude = nbttagcompound.getBoolean("exclude");

        NBTTagList nbttaglist = nbttagcompound.getTagList("items");

        for (int j = 0; j < nbttaglist.tagCount(); ++j) {
            NBTTagCompound nbttagcompound2 = (NBTTagCompound) nbttaglist.tagAt(j);
            int index = nbttagcompound2.getInteger("index");
            items [index] = ItemStack.loadItemStackFromNBT(nbttagcompound2);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("exclude", exclude);

        NBTTagList nbttaglist = new NBTTagList();

        for (int j = 0; j < items.length; ++j) {
            if (items [j] != null && items [j].stackSize > 0) {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound ();
                nbttaglist.appendTag(nbttagcompound2);
                nbttagcompound2.setInteger("index", j);
                items [j].writeToNBT(nbttagcompound2);
            }
        }

        nbttagcompound.setTag("items", nbttaglist);
    }

}
