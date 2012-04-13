/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */
package net.minecraft.src.buildcraft.additionalpipes.logic;

import net.minecraft.src.buildcraft.additionalpipes.gui.GuiHandler;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemTeleport;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogic;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.*;

public class PipeLogicItemTeleport extends PipeLogic {

    @Override
    public boolean blockActivated(EntityPlayer entityplayer) {
        
        PipeItemTeleport a = (PipeItemTeleport) this.container.pipe;
        if (a.Owner == null || a.Owner.equalsIgnoreCase("")) {
            a.Owner = entityplayer.username;
        }
        
        ItemStack equippedItem = entityplayer.getCurrentEquippedItem();
        
        if (equippedItem != null) {
            
            if (mod_AdditionalPipes.isPipe(equippedItem.getItem()))  {
                return false;
            }

            if (equippedItem.getItem() == BuildCraftCore.wrenchItem && !mod_AdditionalPipes.wrenchOpensGui) {
                return false;
            }
        }

        entityplayer.openGui(mod_AdditionalPipes.instance, GuiHandler.PIPE_TP_ITEM, 
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
            return (pipe2 == null || !(pipe2.logic instanceof PipeLogicItemTeleport))
                   && super.isPipeConnected(tile);
        }
    }

}
