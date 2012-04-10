/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.additionalpipes.pipes;

import net.minecraft.src.Block;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.api.EntityPassiveItem;
import net.minecraft.src.buildcraft.api.ILiquidContainer;
import net.minecraft.src.buildcraft.api.IPowerReceptor;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.api.PowerFramework;
import net.minecraft.src.buildcraft.api.PowerProvider;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogicWood;
import net.minecraft.src.buildcraft.transport.PipeTransportItems;
import net.minecraft.src.buildcraft.additionalpipes.MutiPlayerProxy;
import net.minecraft.src.buildcraft.additionalpipes.logic.PipeLogicAdvancedWood;

public class PipeItemsAdvancedWood extends Pipe implements IPowerReceptor {

    private PowerProvider powerProvider;

    private int baseTexture = mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_TEXTURE;
    private int plainTexture = mod_zAdditionalPipes.DEFUALT_ADVANCEDWOOD_TEXTURE_CLOSED;
    private @TileNetworkData int nextTexture = baseTexture;

    public PipeItemsAdvancedWood(int itemID) {
        super(new PipeTransportItems(), new PipeLogicAdvancedWood(), itemID);

        powerProvider = PowerFramework.currentFramework.createPowerProvider();
        powerProvider.configure(50, 1, 64, 1, 64);
        powerProvider.configurePowerPerdition(64, 1);
    }

    @Override
    public int getBlockTexture() {
        MutiPlayerProxy.bindTex();
        return nextTexture;
    }

    @Override
    public void prepareTextureFor(Orientations connection) {
        if (connection == Orientations.Unknown) {
            nextTexture = baseTexture;
        }
        else {
            int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

            if (metadata == connection.ordinal()) {
                nextTexture = plainTexture;
            }
            else {
                nextTexture = baseTexture;
            }
        }

    }

    @Override
    public void setPowerProvider(PowerProvider provider) {
        provider = powerProvider;
    }

    @Override
    public PowerProvider getPowerProvider() {
        return powerProvider;
    }

    @Override
    public void doWork() {
        if (powerProvider.energyStored <= 0) {
            return;
        }

        World w = worldObj;

        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        if (meta > 5) {
            return;
        }

        Position pos = new Position(xCoord, yCoord, zCoord,
                                    Orientations.values()[meta]);
        pos.moveForwards(1);
        int blockId = w.getBlockId((int) pos.x, (int) pos.y, (int) pos.z);
        TileEntity tile = w.getBlockTileEntity((int) pos.x, (int) pos.y,
                                               (int) pos.z);

        if (tile == null
                || !(tile instanceof IInventory || tile instanceof ILiquidContainer)
                || PipeLogicWood
                .isExcludedFromExtraction(Block.blocksList[blockId])) {
            return;
        }

        if (tile instanceof IInventory) {
            IInventory inventory = (IInventory) tile;

            ItemStack stack = checkExtract(inventory, true,
                                           pos.orientation.reverse());

            if (stack == null || stack.stackSize == 0) {
                powerProvider.useEnergy(1, 1, false);
                return;
            }

            Position entityPos = new Position(pos.x + 0.5, pos.y
                                              + Utils.getPipeFloorOf(stack), pos.z + 0.5,
                                              pos.orientation.reverse());

            entityPos.moveForwards(0.5);

            EntityPassiveItem entity = new EntityPassiveItem(w, entityPos.x,
                    entityPos.y, entityPos.z, stack);

            ((PipeTransportItems) transport).entityEntering(entity,
                    entityPos.orientation);
        }
    }

    /**
     * Return the itemstack that can be if something can be extracted from this
     * inventory, null if none. On certain cases, the extractable slot depends
     * on the position of the pipe.
     */
    public ItemStack checkExtract(IInventory inventory, boolean doRemove,
                                  Orientations from) {
        //		if (inventory instanceof ISpecialInventory) {
        //			//At the moment we are going to let special inventorys handle there own. Might change if popular demand
        //			return ((ISpecialInventory) inventory).extractItem(doRemove, from);
        //		}
        IInventory inv = Utils.getInventory(inventory);
        ItemStack result = checkExtractGeneric(inv, doRemove, from);
        return result;
    }

    public ItemStack checkExtractGeneric(IInventory inventory,
                                         boolean doRemove, Orientations from) {
        for (int k = 0; k < inventory.getSizeInventory(); ++k) {
            if (inventory.getStackInSlot(k) != null
                    && inventory.getStackInSlot(k).stackSize > 0) {

                ItemStack slot = inventory.getStackInSlot(k);

                if (slot != null && slot.stackSize > 0 && CanExtract(slot)) {
                    if (doRemove) {
                        return inventory.decrStackSize(k, powerProvider.useEnergy(1, slot.stackSize, true));
                    }
                    else {
                        return slot;
                    }
                }
            }
        }

        return null;
    }
    public boolean CanExtract(ItemStack item) {
        for (int i = 0; i < logic.getSizeInventory(); i++) {
            ItemStack stack = logic.getStackInSlot(i);

            if (stack != null && stack.itemID == item.itemID) {
                if ((Item.itemsList[item.itemID].isDamageable())) {
                    return !((PipeLogicAdvancedWood)this.logic).exclude;
                }
                else if (stack.getItemDamage() == item.getItemDamage()) {
                    return !((PipeLogicAdvancedWood)this.logic).exclude;
                }
            }
        }

        return ((PipeLogicAdvancedWood)this.logic).exclude;
    }

    @Override
    public int powerRequest() {
        return getPowerProvider().maxEnergyReceived;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);

    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);

    }

    public Packet230ModLoader getDescPacket() {
        Packet230ModLoader packet = new Packet230ModLoader();

        packet.modId = mod_zAdditionalPipes.instance.getId();
        packet.packetType = mod_zAdditionalPipes.PACKET_SET_AW;
        packet.isChunkDataPacket = true;

        packet.dataInt = new int [5];

        packet.dataInt [0] = xCoord;
        packet.dataInt [1] = yCoord;
        packet.dataInt [2] = zCoord;
        packet.dataInt [3] = mod_zAdditionalPipes.boolToInt(((PipeLogicAdvancedWood)this.logic).exclude);

        return packet;
    }

}
