package net.minecraft.src.buildcraft.additionalpipes;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.additionalpipes.gui.GuiItemTeleportPipe;
import net.minecraft.src.forge.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    private Minecraft mc = ModLoader.getMinecraftInstance();
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntity tile = mc.theWorld.getBlockTileEntity(x, y, z);
        
        if (tile == null) {
            return null;
        }
        
        return new GuiItemTeleportPipe((TileGenericPipe)tile);
    }
}
