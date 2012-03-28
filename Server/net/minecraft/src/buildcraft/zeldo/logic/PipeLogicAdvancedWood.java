/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.zeldo.logic;

import net.minecraft.src.BuildCraftCore;
import net.minecraft.src.BuildCraftTransport;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogic;
import net.minecraft.src.buildcraft.transport.PipeLogicWood;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;

public class PipeLogicAdvancedWood extends PipeLogic {

    @TileNetworkData ItemStack [] items = new ItemStack [9];
    public @TileNetworkData boolean exclude = false;

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
               && (tile instanceof IInventory || tile instanceof ILiquidContainer)
               &&  Utils.checkPipesConnections(worldObj, xCoord, yCoord,
                                               zCoord, tile.xCoord, tile.yCoord, tile.zCoord);
    }


    @Override
    public boolean blockActivated(EntityPlayer entityplayer) {
        if (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() == BuildCraftCore.wrenchItem) {
            switchSource();
            return true;
        }

        if (entityplayer.getCurrentEquippedItem() != null && mod_zAdditionalPipes.ItemIsPipe(entityplayer.getCurrentEquippedItem().getItem().shiftedIndex))  {
            return false;
        }

        MutiPlayerProxy.displayGUIAdvancedWood(entityplayer, this.container);

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
    public void onNeighborBlockChange () {
        super.onNeighborBlockChange();

        switchSourceIfNeeded();
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return items [i];
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        ItemStack stack = items [i].copy();
        stack.stackSize = j;

        items [i].stackSize -= j;

        if (items [i].stackSize == 0) {
            items [i] = null;
        }

        return stack;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        items [i] = itemstack;

    }

    @Override
    public String getInvName() {
        return "Filters";
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
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
