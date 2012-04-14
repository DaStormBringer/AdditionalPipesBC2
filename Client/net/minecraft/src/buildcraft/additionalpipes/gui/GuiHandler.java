package net.minecraft.src.buildcraft.additionalpipes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.buildcraft.additionalpipes.gui.*;
import net.minecraft.src.buildcraft.additionalpipes.network.NetworkID;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.*;

public class GuiHandler implements IGuiHandler {
    
    private Minecraft mc = ModLoader.getMinecraftInstance();
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntity tile = mc.theWorld.getBlockTileEntity(x, y, z);
        
        if (tile == null) {
            return null;
        }
        
        switch(ID) {
            case NetworkID.GUI_PIPE_TP_ITEM:
                return new GuiTeleportPipe((TileGenericPipe)tile);
                
            case NetworkID.GUI_PIPE_TP_LIQUID:
                return new GuiTeleportPipe((TileGenericPipe)tile);
                
            case NetworkID.GUI_PIPE_TP_POWER:
                return new GuiTeleportPipe((TileGenericPipe)tile); 
                
            case NetworkID.GUI_PIPE_DIST:
                return new GuiDistributionPipe((TileGenericPipe)tile);
                
            case NetworkID.GUI_PIPE_WOODEN_ADV:
                
                TileGenericPipe pipe = new TileGenericPipe();
                pipe.pipe = new PipeItemsAdvancedWood(mod_AdditionalPipes.pipeAdvancedWood.shiftedIndex);
                return new GuiAdvancedWoodPipe(player.inventory, pipe, (TileGenericPipe)tile);
        }
        
        return null;
    }
}
