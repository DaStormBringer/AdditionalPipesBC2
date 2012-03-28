package net.minecraft.src.buildcraft.zeldo.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ModLoaderMp;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;
import net.minecraft.src.buildcraft.zeldo.pipes.PipePowerTeleport;

import org.lwjgl.opengl.GL11;

public class GuiPowerTeleportPipe extends GuiContainer {

    private PipePowerTeleport actualPipe;
    private GuiButton[] buttons = new GuiButton[7];


    public GuiPowerTeleportPipe(TileGenericPipe thisPipe) {
        super(new ContainerTeleportPipe());
        actualPipe = (PipePowerTeleport)thisPipe.pipe;
        xSize = 228;
        ySize = 117;
    }
    @SuppressWarnings("unchecked")
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

        fontRenderer.drawString("Frequency: " + actualPipe.myFreq, 8, 6, 0x404040);

        if (MutiPlayerProxy.isOnServer()) {
            fontRenderer.drawString("Connected Pipes: " + mod_zAdditionalPipes.CurrentGUICount, 100, 6, 0x404040);
        }
        else {
            fontRenderer.drawString("Connected Pipes: " + actualPipe.getConnectedPipes(true).size(), 100, 6, 0x404040);
        }

        fontRenderer.drawString("Can Receive: " + actualPipe.canReceive, 8, 42, 0x404040);
        fontRenderer.drawString("Owner: " + actualPipe.Owner, 8, 75, 0x404040);

        //fontRenderer.drawString(filterInventory.getInvName(), 8, 6, 0x404040);
        //fontRenderer.drawString(playerInventory.getInvName(), 8, ySize - 97, 0x404040);
    }
    protected void actionPerformed(GuiButton guibutton) {
        switch(guibutton.id) {
            case 1:
                actualPipe.myFreq -= 100;
                break;

            case 2:
                actualPipe.myFreq -= 10;
                break;

            case 3:
                actualPipe.myFreq -= 1;
                break;

            case 4:
                actualPipe.myFreq += 1;
                break;

            case 5:
                actualPipe.myFreq += 10;
                break;

            case 6:
                actualPipe.myFreq += 100;
                break;

            case 7:
                actualPipe.canReceive = !actualPipe.canReceive;
                break;
        }

        if (actualPipe.myFreq < 0) {
            actualPipe.myFreq = 0;
        }

        ModLoaderMp.sendPacket(mod_zAdditionalPipes.instance, actualPipe.getDescPipe());
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int i = mc.renderEngine
                .getTexture("/net/minecraft/src/buildcraft/zeldo/gui/gui.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

    }

}
