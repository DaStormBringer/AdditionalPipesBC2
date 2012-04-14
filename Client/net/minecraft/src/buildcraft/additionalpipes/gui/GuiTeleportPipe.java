package net.minecraft.src.buildcraft.additionalpipes.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Container;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_AdditionalPipes;
import net.minecraft.src.buildcraft.additionalpipes.MutiPlayerProxy;
import net.minecraft.src.buildcraft.additionalpipes.logic.PipeLogicTeleport;
import net.minecraft.src.buildcraft.additionalpipes.network.NetworkID;
import net.minecraft.src.buildcraft.additionalpipes.network.PacketAdditionalPipes;
import net.minecraft.src.buildcraft.additionalpipes.pipes.PipeTeleport;
import net.minecraft.src.buildcraft.core.network.PacketPayload;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;

public class GuiTeleportPipe extends GuiContainer {

	private PipeTeleport pipe;
    private GuiButton[] buttons = new GuiButton[7];

    public GuiTeleportPipe(TileGenericPipe thisPipe) {
        super(new ContainerTeleportPipe());
        
        pipe = (PipeTeleport) thisPipe.pipe;
        
        xSize = 228;
        ySize = 117;
    }
    
    public void initGui() {
    	
        super.initGui();
        int bw = this.xSize - 20;

        controlList.add(this.buttons[0] =  new GuiButton(1, (width - this.xSize) / 2 + 10, (height - this.ySize) / 2 + 20, bw / 6, 20, "-100"));
        controlList.add(this.buttons[1] =  new GuiButton(2, (width - this.xSize) / 2 + 12 + bw / 6, (height - this.ySize) / 2 + 20, bw / 6, 20, "-10"));
        controlList.add(this.buttons[2] =  new GuiButton(3, (width - this.xSize) / 2 + 12 + bw * 2 / 6, (height - this.ySize) / 2 + 20, bw / 6, 20, "-1"));
        controlList.add(this.buttons[3] =  new GuiButton(4, (width - this.xSize) / 2 + 12 + bw * 3 / 6, (height - this.ySize) / 2 + 20, bw / 6, 20, "+1"));
        controlList.add(this.buttons[4] =  new GuiButton(5, (width - this.xSize) / 2 + 12 + bw * 4 / 6, (height - this.ySize) / 2 + 20, bw / 6, 20, "+10"));
        controlList.add(this.buttons[5] =  new GuiButton(6, (width - this.xSize) / 2 + 16 + bw * 5 / 6, (height - this.ySize) / 2 + 20, bw / 6, 20, "+100"));
        controlList.add(this.buttons[6] =  new GuiButton(7, (width - this.xSize) / 2 + 16, (height - this.ySize) / 2 + 52, bw / 6, 20, "Switch"));
    }
    protected void drawGuiContainerForegroundLayer() {

        fontRenderer.drawString("Frequency: " + pipe.logic.freq, 8, 6, 0x404040);

        fontRenderer.drawString("Connected Pipes: " + pipe.getConnectedPipes(true).size(), 100, 6, 0x404040);

        fontRenderer.drawString("Can Receive: " + pipe.logic.canReceive, 8, 42, 0x404040);
        fontRenderer.drawString("Owner: " + pipe.logic.owner, 8, 75, 0x404040);

        //fontRenderer.drawString(filterInventory.getInvName(), 8, 6, 0x404040);
        //fontRenderer.drawString(playerInventory.getInvName(), 8, ySize - 97, 0x404040);
    }
    protected void actionPerformed(GuiButton guibutton) {
    	
        switch(guibutton.id) {
            case 1:
                pipe.logic.freq -= 100;
                break;

            case 2:
            	pipe.logic.freq -= 10;
                break;

            case 3:
            	pipe.logic.freq -= 1;
                break;

            case 4:
            	pipe.logic.freq += 1;
                break;

            case 5:
            	pipe.logic.freq += 10;
                break;

            case 6:
            	pipe.logic.freq += 100;
                break;

            case 7:
                pipe.logic.canReceive = !pipe.logic.canReceive;
                break;
        }

        if (pipe.logic.freq < 0) {
        	pipe.logic.freq = 0;
        }
        
        if (mc.theWorld.isRemote) {
        	
        	PacketPayload payload = pipe.getNetworkPacket();

    		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkID.PACKET_PIPE_DESC, payload);
    		packet.posX = pipe.xCoord;
    		packet.posY = pipe.yCoord;
    		packet.posZ = pipe.zCoord;      
      
            ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(packet.getPacket());
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int i = mc.renderEngine.getTexture("/net/minecraft/src/buildcraft/additionalpipes/gui/gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

    }

}
