package net.minecraft.src.buildcraft.additionalpipes.gui;

import net.minecraft.src.*;
import net.minecraft.src.buildcraft.additionalpipes.logic.PipeLogicItemTeleport;
import net.minecraft.src.buildcraft.additionalpipes.network.PacketAdditionalPipes;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.additionalpipes.MutiPlayerProxy;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeItemTeleport;
import net.minecraft.src.buildcraft.core.CoreProxy;
import net.minecraft.src.buildcraft.core.network.PacketPayload;
import net.minecraft.src.buildcraft.core.network.PacketUpdate;

import org.lwjgl.opengl.GL11;

public class GuiItemTeleportPipe extends GuiContainer {

    private PipeItemTeleport actualPipe;
    private PipeLogicItemTeleport logic;
    private GuiButton[] buttons = new GuiButton[7];

    public GuiItemTeleportPipe(TileGenericPipe thisPipe) {
        
        super(new ContainerTeleportPipe());
        
        actualPipe = (PipeItemTeleport)thisPipe.pipe;
        logic = (PipeLogicItemTeleport) actualPipe.logic;
        
        xSize = 228;
        ySize = 117;
    }
    
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        int bw = this.xSize - 20;

        controlList.add(this.buttons[0] =  new GuiButton(1, (width - xSize) / 2 + 10, (height - ySize) / 2 + 20, bw / 6, 20, "-100"));
        controlList.add(this.buttons[1] =  new GuiButton(2, (width - xSize) / 2 + 12 + bw / 6, (height - ySize) / 2 + 20, bw / 6, 20, "-10"));
        controlList.add(this.buttons[2] =  new GuiButton(3, (width - xSize) / 2 + 12 + bw * 2 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "-1"));
        controlList.add(this.buttons[3] =  new GuiButton(4, (width - xSize) / 2 + 12 + bw * 3 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "+1"));
        controlList.add(this.buttons[4] =  new GuiButton(5, (width - xSize) / 2 + 12 + bw * 4 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "+10"));
        controlList.add(this.buttons[5] =  new GuiButton(6, (width - xSize) / 2 + 16 + bw * 5 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "+100"));
        controlList.add(this.buttons[6] =  new GuiButton(7, (width - xSize) / 2 + 16, (height - ySize) / 2 + 52, bw / 6, 20, "Switch"));


    }
    @Override
    protected void drawGuiContainerForegroundLayer() {

        fontRenderer.drawString("Frequency: " + logic.myFreq, 8, 7, 0x404040);

        if (MutiPlayerProxy.isOnServer()) {
            fontRenderer.drawString("Connected Pipes: " + mod_AdditionalPipes.CurrentGUICount, 100, 6, 0x404040);
        }
        else {
            fontRenderer.drawString("Connected Pipes: " + actualPipe.getConnectedPipes(true).size(), 100, 6, 0x404040);
        }

        fontRenderer.drawString("Can Receive: " + logic.canReceive, 8, 42, 0x404040);
        fontRenderer.drawString("Owner: " + logic.owner, 8, 75, 0x404040);

        //fontRenderer.drawString(filterInventory.getInvName(), 8, 6, 0x404040);
        //fontRenderer.drawString(playerInventory.getInvName(), 8, ySize - 97, 0x404040);
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        
        switch(guibutton.id) {
            case 1:
                logic.myFreq -= 100;
                break;

            case 2:
                logic.myFreq -= 10;
                break;

            case 3:
                logic.myFreq -= 1;
                break;

            case 4:
                logic.myFreq += 1;
                break;

            case 5:
                logic.myFreq += 10;
                break;

            case 6:
                logic.myFreq += 100;
                break;

            case 7:
                logic.canReceive = !logic.canReceive;
                break;
        }

        if (logic.myFreq < 0) {
            logic.myFreq = 0;
        }
        
        PacketPayload payload = actualPipe.getNetworkPacket();
        PacketAdditionalPipes packet = new PacketAdditionalPipes(1, payload);
        
        packet.posX = actualPipe.xCoord;
        packet.posY = actualPipe.yCoord;
        packet.posZ = actualPipe.zCoord;      
        
        ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(packet.getPacket());
        
   //     ModLoaderMp.sendPacket(mod_zAdditionalPipes.instance, actualPipe.getDescPipe());
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        int k = mc.renderEngine
                .getTexture("/net/minecraft/src/buildcraft/additionalpipes/gui/gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(k);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);

    }

}
