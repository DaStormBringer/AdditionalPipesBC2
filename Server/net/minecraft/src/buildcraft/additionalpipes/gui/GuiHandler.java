package net.minecraft.src.buildcraft.additionalpipes.gui;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.buildcraft.additionalpipes.network.NetworkID;
import net.minecraft.src.buildcraft.additionalpipes.network.PacketAdditionalPipes;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemsAdvancedWood;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.*;
import net.minecraft.src.buildcraft.additionalpipes.gui.ContainerTeleportPipe;
import net.minecraft.src.buildcraft.additionalpipes.gui.CraftingAdvancedWoodPipe;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.core.network.PacketPayload;

public class GuiHandler implements IGuiHandler {
    
    private MinecraftServer mc = ModLoader.getMinecraftServerInstance();
    
    @Override
    public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        TileEntity tile = mc.getWorldManager(player.dimension).getBlockTileEntity(x, y, z);
        
        if (tile == null) {
            return null;
        }
        
        switch(ID) {
            case NetworkID.GUI_PIPE_TP:
            	sendPipeDesc( (TileGenericPipe) tile, (EntityPlayerMP) player);
                return new ContainerTeleportPipe();
                
            case NetworkID.GUI_PIPE_DIST:
            	sendPipeDesc( (TileGenericPipe) tile, (EntityPlayerMP) player);
                return null;
                
            case NetworkID.GUI_PIPE_WOODEN_ADV:
            	sendPipeDesc( (TileGenericPipe) tile, (EntityPlayerMP) player);
                return new CraftingAdvancedWoodPipe(player.inventory, (TileGenericPipe) tile);
        }
        
        return null;
    }
    
    private void sendPipeDesc(TileGenericPipe tile, EntityPlayerMP player) {
    	
    	PacketPayload payload = tile.pipe.getNetworkPacket();
        PacketAdditionalPipes packet = new PacketAdditionalPipes(1, payload);
        
        packet.posX = tile.pipe.xCoord;
        packet.posY = tile.pipe.yCoord;
        packet.posZ = tile.pipe.zCoord;      
        
        player.playerNetServerHandler.sendPacket(packet.getPacket());
    }
}
