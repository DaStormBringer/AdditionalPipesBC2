package net.minecraft.src.buildcraft.additionalpipes;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.additionalpipes.gui.GuiItemTeleportPipe;
import net.minecraft.src.buildcraft.additionalpipes.gui.GuiLiquidTeleportPipe;
import net.minecraft.src.buildcraft.additionalpipes.gui.GuiPowerTeleportPipe;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.forge.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int PIPE_TP_ITEM = 1;
    public static final int PIPE_TP_LIQUID = 2;
    public static final int PIPE_TP_POWER = 3;
    
    private Minecraft mc = ModLoader.getMinecraftInstance();
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntity tile = mc.theWorld.getBlockTileEntity(x, y, z);
        
        if (tile == null) {
            return null;
        }
        
        switch(ID) {
            case PIPE_TP_ITEM:
                return new GuiItemTeleportPipe( (TileGenericPipe)tile );
            case PIPE_TP_LIQUID:
                return new GuiLiquidTeleportPipe( (TileGenericPipe)tile );
            case PIPE_TP_POWER:
                return new GuiPowerTeleportPipe( (TileGenericPipe)tile );              
        }
        
        return null;
    }
}
