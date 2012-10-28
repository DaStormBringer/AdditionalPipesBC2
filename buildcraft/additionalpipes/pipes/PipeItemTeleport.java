/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.pipes;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.core.EntityPassiveItem;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import net.minecraft.src.IInventory;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class PipeItemTeleport extends PipeTeleport implements IPipeTransportItemsHook {
    
    LinkedList <Integer> idsToRemove = new LinkedList <Integer> ();
    
    public PipeItemTeleport(int itemID) {
        super(new PipeTransportItems(), new PipeLogicTeleport(NetworkID.GUI_PIPE_TP), itemID);
    }

    @Override
    public int getBlockTexture() {
        return mod_AdditionalPipes.DEFUALT_ITEM_TELEPORT_TEXTURE;
    }

    @Override
    public void readjustSpeed(EntityPassiveItem item) {
        ((PipeTransportItems) transport).defaultReajustSpeed(item);
    }
    
    /*
    @Override
    public void setPosition (int xCoord, int yCoord, int zCoord) {
    	
        LinkedList <PipeItemTeleport> toRemove = new LinkedList <PipeItemTeleport> ();

        for (int i = 0; i < ItemTeleportPipes.size(); i++) {
        	
            if (ItemTeleportPipes.get(i).xCoord == xCoord &&  ItemTeleportPipes.get(i).yCoord == yCoord && ItemTeleportPipes.get(i).zCoord == zCoord) {

                toRemove.add(ItemTeleportPipes.get(i));
            }
        }

        ItemTeleportPipes.removeAll(toRemove);
        ItemTeleportPipes.add(this);
        
        super.setPosition(xCoord, yCoord, zCoord);
    }*/

    @Override
    public void updateEntity() {

    	super.updateEntity();
    	
        for (int theID : idsToRemove) {
            ((PipeTransportItems)transport).travelingEntities.remove(theID);
        }

        idsToRemove.clear();
    }


    @Override
    public LinkedList<Orientations> filterPossibleMovements(LinkedList<Orientations> possibleOrientations, Position pos, EntityPassiveItem item) {
        
    	List<PipeTeleport> TempTeleport = getConnectedPipes(false);
        LinkedList<Orientations> result = new LinkedList<Orientations>();

        ////System.out.print("Pos: " + pos.toString() + "\n");
        if (TempTeleport.size() <= 0) {
            result.add(pos.orientation.reverse());
            return result;
        }

        Random pipeRand = new Random();
        int i = pipeRand.nextInt(TempTeleport.size());

        LinkedList<Orientations> temp = new LinkedList<Orientations>();
        
        Position pos1 = TempTeleport.get(i).getPosition();
        
        for (int o = 0; o < 6; ++o) {
            if (Orientations.values()[o] != pos1.orientation.reverse()
                    && container.pipe.outputOpen(Orientations.values()[o])) {
                Position newPos = new Position(pos1);
                newPos.orientation = Orientations.values()[o];
                newPos.moveForwards(1.0);

                if (((PipeTransportItems)transport).canReceivePipeObjects(newPos, item)) {
                    temp.add(newPos.orientation);
                }
            }
        }

        ////System.out.println("Temp: " + Temp.size());
        if (temp.size() <= 0) {
            result.add(pos.orientation.reverse());
            return result;
        }

        Orientations newPos = temp.get(worldObj.rand.nextInt(temp.size()));
        ////System.out.println(newPos.toString());
        Position destPos = new Position(TempTeleport.get(i).xCoord, TempTeleport.get(i).yCoord, TempTeleport.get(i).zCoord, newPos);
        destPos.moveForwards(1.0);

        TileEntity tile = worldObj.getBlockTileEntity((int)destPos.x, (int)destPos.y, (int)destPos.z);

        if (tile instanceof TileGenericPipe) {
            TileGenericPipe pipe = (TileGenericPipe)tile;

            if (pipe.pipe.transport instanceof PipeTransportItems) {
                //This pipe can actually receive items
                idsToRemove.add(item.getEntityId());
                ((PipeTransportItems) this.transport).scheduleRemoval(item);
                Position newItemPos = getNewItemPos(destPos, newPos, Utils.getPipeFloorOf(item.item));
                item.setPosition(newItemPos.x, newItemPos.y, newItemPos.z);
                ((PipeTransportItems)pipe.pipe.transport).entityEntering(item, newPos);
            }
        }
        else if (tile instanceof IPipeEntry) {
            idsToRemove.add(item.getEntityId());
            ((PipeTransportItems) this.transport).scheduleRemoval(item);
            Position newItemPos = getNewItemPos(destPos, newPos, Utils.getPipeFloorOf(item.item));
            item.setPosition(newItemPos.x, newItemPos.y, newItemPos.z);
            ((IPipeEntry) tile).entityEntering(item, newPos);
        }
        else if (tile instanceof IInventory) {
            StackUtil utils = new StackUtil(item.item);

            if (!APIProxy.isClient(worldObj)) {
                if (utils.checkAvailableSlot((IInventory) tile, true, destPos.orientation.reverse()) && utils.items.stackSize == 0) {
                    idsToRemove.add(item.getEntityId());
                    ((PipeTransportItems) this.transport).scheduleRemoval(item);
                    // Do nothing, we're adding the object to the world
                }
                else {
                    //Wont accept it return...
                    newPos = pos.orientation.reverse();
                }
            }
        }

        result.add(newPos);

        return result;
    }
    
    public Position getNewItemPos(Position Old, Orientations newPos, float f) {
        //Utils.getPipeFloorOf(data.item.item)
        double x = Old.x;
        double y = Old.y;
        double z = Old.z;

        if (newPos == Orientations.XNeg) {
            x += 1;
            y += .5;
            z += .5;
        }
        else if (newPos == Orientations.XPos) {
            //x += .6;
            y += f;
            z += .5;
        }
        else if (newPos == Orientations.YNeg) {
            x += .5;
            y += 1;
            z += .5;
        }
        else if (newPos == Orientations.YPos) {
            x += .5;
            //y += .6;
            z += .5;
        }
        else if (newPos == Orientations.ZNeg) {
            x += .5;
            y += f;
            z += 1;
        }
        else if (newPos == Orientations.ZPos) {
            x += .5;
            y += f;
            //z += .6;
        }

        return new Position(x, y, z);
    }

    @Override
    public void entityEntered(EntityPassiveItem item, Orientations orientation) {}

}
