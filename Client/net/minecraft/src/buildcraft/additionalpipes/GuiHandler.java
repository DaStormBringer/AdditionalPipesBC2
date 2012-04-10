package net.minecraft.src.buildcraft.additionalpipes;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.minecraft.src.buildcraft.additionalpipes.gui.*;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.forge.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int PIPE_TP_ITEM = 1;
    public static final int PIPE_TP_LIQUID = 2;
    public static final int PIPE_TP_POWER = 3;
    public static final int PIPE_DIST = 4;
    public static final int PIPE_WOODEN_ADV = 5;
    
    private Minecraft mc = ModLoader.getMinecraftInstance();
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntity tile = mc.theWorld.getBlockTileEntity(x, y, z);
        
        if (tile == null) {
            return null;
        }
        
        switch(ID) {
            case PIPE_TP_ITEM:
                return new GuiItemTeleportPipe((TileGenericPipe)tile);
                
            case PIPE_TP_LIQUID:
                return new GuiLiquidTeleportPipe((TileGenericPipe)tile);
                
            case PIPE_TP_POWER:
                return new GuiPowerTeleportPipe((TileGenericPipe)tile); 
                
            case PIPE_DIST:
                return new GuiDistributionPipe((TileGenericPipe)tile);
                
            case PIPE_WOODEN_ADV:
                
                TileGenericPipe pipe = new TileGenericPipe();
                pipe.pipe = new PipeItemsAdvancedWood(mod_AdditionalPipes.pipeAdvancedWood.shiftedIndex);
                return new GuiAdvancedWoodPipe(player.inventory, pipe, (TileGenericPipe)tile);
        }
        
        return null;
    }
}
