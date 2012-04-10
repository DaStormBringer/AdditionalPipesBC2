package net.minecraft.src.buildcraft.additionalpipes;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.*;
import net.minecraft.src.buildcraft.additionalpipes.gui.ContainerTeleportPipe;
import net.minecraft.src.buildcraft.additionalpipes.gui.CraftingAdvancedWoodPipe;

public class GuiHandler implements IGuiHandler {

    public static final int PIPE_TP_ITEM = 1;
    public static final int PIPE_TP_LIQUID = 2;
    public static final int PIPE_TP_POWER = 3;
    public static final int PIPE_DIST = 4;
    public static final int PIPE_WOODEN_ADV = 5;
    
    private MinecraftServer mc = ModLoader.getMinecraftServerInstance();
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntity tile = mc.getWorldManager(player.dimension).getBlockTileEntity(x, y, z);
        
        if (tile == null) {
            return null;
        }
        
        switch(ID) {
            case PIPE_TP_ITEM:
                return new ContainerTeleportPipe();
                
            case PIPE_TP_LIQUID:
                return new ContainerTeleportPipe();
                
            case PIPE_TP_POWER:
                return new ContainerTeleportPipe(); 
                
            case PIPE_DIST:
                return new ContainerTeleportPipe();
                
            case PIPE_WOODEN_ADV:
                
                TileGenericPipe pipe = new TileGenericPipe();
                pipe.pipe = new PipeItemsAdvancedWood(mod_AdditionalPipes.pipeAdvancedWood.shiftedIndex);
                return new CraftingAdvancedWoodPipe(player.inventory, pipe);
        }
        
        return null;
    }
}
