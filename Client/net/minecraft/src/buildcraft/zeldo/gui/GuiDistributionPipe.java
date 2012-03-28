package net.minecraft.src.buildcraft.zeldo.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.ModLoaderMp;
import net.minecraft.src.mod_zAdditionalPipes;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;
import net.minecraft.src.buildcraft.zeldo.pipes.PipeItemsDistributor;

import org.lwjgl.opengl.GL11;

public class GuiDistributionPipe extends GuiContainer {

    protected int xSize;
    protected int ySize;
    private GuiButton[] buttons = new GuiButton[18];
    public int guiX = 0;
    public int guiY = 0;
    TileGenericPipe a;
    PipeItemsDistributor actualPipe;

    public GuiDistributionPipe(TileGenericPipe container) {
        super(new ContainerTeleportPipe());
        a = container;
        actualPipe = (PipeItemsDistributor) container.pipe;
        xSize = 175;
        ySize = 130;

    }
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        //int bw = this.xSize - 20;
        int guiX = (width - this.xSize) / 2 + 30;
        int guiY = (height - this.ySize) / 2 - 7;

        controlList.add(this.buttons[0] =  new GuiButton(1, guiX + 1,           guiY + 24, 20, 17, "-"));
        controlList.add(this.buttons[1] =  new GuiButton(2, guiX + 3 + 20,      guiY + 24, 30, 17, "1000"));
        controlList.add(this.buttons[2] =  new GuiButton(3, guiX + 5 + 50,      guiY + 24, 20, 17, "+"));

        controlList.add(this.buttons[3] =  new GuiButton(4, guiX + 1,           guiY + 25 + 17, 20, 17, "-"));
        controlList.add(this.buttons[4] =  new GuiButton(5, guiX + 3 + 20,      guiY + 25 + 17, 30, 17, "1000"));
        controlList.add(this.buttons[5] =  new GuiButton(6, guiX + 5 + 50,      guiY + 25 + 17, 20, 17, "+"));

        controlList.add(this.buttons[6] =  new GuiButton(7, guiX + 1,           guiY + 26 + 17 * 2, 20, 17, "-"));
        controlList.add(this.buttons[7] =  new GuiButton(8, guiX + 3 + 20,      guiY + 26 + 17 * 2, 30, 17, "1000"));
        controlList.add(this.buttons[8] =  new GuiButton(9, guiX + 5 + 50,      guiY + 26 + 17 * 2, 20, 17, "+"));

        controlList.add(this.buttons[9] =  new GuiButton(10, guiX + 1,          guiY + 27 + 17 * 3, 20, 17, "-"));
        controlList.add(this.buttons[10] =  new GuiButton(11, guiX + 3 + 20,     guiY + 27 + 17 * 3, 30, 17, "1000"));
        controlList.add(this.buttons[11] =  new GuiButton(12, guiX + 5 + 50,     guiY + 27 + 17 * 3, 20, 17, "+"));

        controlList.add(this.buttons[12] =  new GuiButton(13, guiX + 1,          guiY + 28 + 17 * 4, 20, 17, "-"));
        controlList.add(this.buttons[13] =  new GuiButton(14, guiX + 3 + 20,     guiY + 28 + 17 * 4, 30, 17, "1000"));
        controlList.add(this.buttons[14] =  new GuiButton(15, guiX + 5 + 50,     guiY + 28 + 17 * 4, 20, 17, "+"));

        controlList.add(this.buttons[15] =  new GuiButton(16, guiX + 1,          guiY + 29 + 17 * 5, 20, 17, "-"));
        controlList.add(this.buttons[16] =  new GuiButton(17, guiX + 3 + 20,     guiY + 29 + 17 * 5, 30, 17, "1000"));
        controlList.add(this.buttons[17] =  new GuiButton(18, guiX + 5 + 50,     guiY + 29 + 17 * 5, 20, 17, "+"));

    }
    @Override
    protected void drawGuiContainerForegroundLayer() {
        this.buttons[1].displayString = "" + actualPipe.distData[0];
        this.buttons[4].displayString = "" + actualPipe.distData[1];
        this.buttons[7].displayString = "" + actualPipe.distData[2];
        this.buttons[10].displayString = "" + actualPipe.distData[3];
        this.buttons[13].displayString = "" + actualPipe.distData[4];
        this.buttons[16].displayString = "" + actualPipe.distData[5];

        //super.drawGuiContainerForegroundLayer();
        //fontRenderer.drawString(filterInventory.getInvName(), 8, 6, 0x404040);
        //fontRenderer.drawString(playerInventory.getInvName(), 8, ySize - 97, 0x404040);
    }
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        switch (guibutton.id) {
            case 1:
                actualPipe.distData[0] -= 1;
                break;

            case 3:
                actualPipe.distData[0] += 1;
                break;

            case 4:
                actualPipe.distData[1] -= 1;
                break;

            case 6:
                actualPipe.distData[1] += 1;
                break;

            case 7:
                actualPipe.distData[2] -= 1;
                break;

            case 9:
                actualPipe.distData[2] += 1;
                break;

            case 10:
                actualPipe.distData[3] -= 1;
                break;

            case 12:
                actualPipe.distData[3] += 1;
                break;

            case 13:
                actualPipe.distData[4] -= 1;
                break;

            case 15:
                actualPipe.distData[4] += 1;
                break;

            case 16:
                actualPipe.distData[5] -= 1;
                break;

            case 18:
                actualPipe.distData[5] += 1;
                break;
        }

        boolean found = false;

        for (int i = 0; i < actualPipe.distData.length; i++) {
            if (actualPipe.distData[i] < 0) {
                actualPipe.distData[i] = 0;
            }

            if (actualPipe.distData[i] > 0) {
                found = true;
            }
        }

        if (!found)
            for (int i = 0; i < actualPipe.distData.length; i++) {
                actualPipe.distData[i] = 1;
            }

        ModLoaderMp.sendPacket(mod_zAdditionalPipes.instance, actualPipe.getDescPipe());
    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        int i = mc.renderEngine.getTexture("/net/minecraft/src/buildcraft/zeldo/gui/DistGUI.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

    }

}
