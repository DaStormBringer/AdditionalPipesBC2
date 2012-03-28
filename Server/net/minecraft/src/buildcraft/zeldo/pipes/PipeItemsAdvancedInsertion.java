/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package net.minecraft.src.buildcraft.zeldo.pipes;

import java.util.LinkedList;
import java.util.Random;

import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.api.APIProxy;
import net.minecraft.src.buildcraft.api.EntityPassiveItem;
import net.minecraft.src.buildcraft.api.Orientations;
import net.minecraft.src.buildcraft.api.Position;
import net.minecraft.src.buildcraft.core.StackUtil;
import net.minecraft.src.buildcraft.core.Utils;
import net.minecraft.src.buildcraft.transport.IPipeTransportItemsHook;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogicStone;
import net.minecraft.src.buildcraft.transport.PipeTransportItems;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;

public class PipeItemsAdvancedInsertion extends Pipe implements IPipeTransportItemsHook {

    public PipeItemsAdvancedInsertion(int itemID) {
        super(new PipeTransportItems(), new PipeLogicStone (), itemID);

    }

    @Override
    public int getBlockTexture() {
        MutiPlayerProxy.bindTex();
        return mod_zAdditionalPipes.DEFUALT_Insertion_TEXTURE;
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
    public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, EntityPassiveItem item) {
        return filterPossibleMovements(possibleOrientations, pos, item, 0);
    }

    public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, EntityPassiveItem item, int Level) {
        LinkedList<Orientations> newOris = new LinkedList<Orientations>();
        LinkedList<Orientations> nullReturn = new LinkedList<Orientations>();
        nullReturn.add(Orientations.values()[0]);

        for (int o = 0; o < 6; ++o) {
            if (Orientations.values()[o] != pos.orientation.reverse()) {
                Position newPos = new Position(pos);
                newPos.orientation = Orientations.values()[o];
                newPos.moveForwards(1.0);

                TileEntity entity = worldObj.getBlockTileEntity((int) newPos.x, (int) newPos.y, (int) newPos.z);

                if (entity instanceof IInventory) {
                    if (new StackUtil(item.item).checkAvailableSlot((IInventory) entity, false, newPos.orientation.reverse())) {
                        newOris.add(newPos.orientation);
                    }
                }
            }
        }


        //System.out.println("NewOris Size: " + newOris.size() + " - PO Size: " + possibleOrientations.size() + " - Level: " + Level);
        if (newOris.size() > 0) {
            Position destPos =  new Position(pos.x, pos.y, pos.z, newOris.get( (new Random()) .nextInt(newOris.size()) ) );
            destPos.moveForwards(1.0);
            StackUtil utils = new StackUtil(item.item);
            TileEntity tile = worldObj.getBlockTileEntity((int) destPos.x, (int) destPos.y, (int) destPos.z);

            if (!APIProxy.isClient(worldObj)) {
                if (utils.checkAvailableSlot((IInventory) tile, true, destPos.orientation.reverse()) && utils.items.stackSize == 0) {
                    item.remove();
                    ((PipeTransportItems) this.transport).scheduleRemoval(item);
                }
                else {
                    item.item = utils.items;
                    return this.filterPossibleMovements(possibleOrientations, pos, item, (Level + 1));
                    //EntityItem dropped = item.toEntityItem(destPos.orientation);
                }
            }

            //System.out.println("Insertion Output 2 : " + destPos.orientation);
            return nullReturn;
        }

        if (Level == 0) {
            return possibleOrientations;
        }

        return ((PipeTransportItems)this.transport).getPossibleMovements(pos, item);
    }

    @Override
    public void entityEntered(EntityPassiveItem item, Orientations orientation) {

    }

}
