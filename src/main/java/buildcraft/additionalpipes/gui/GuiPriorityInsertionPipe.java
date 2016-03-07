package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessagePriorityPipe;
import buildcraft.additionalpipes.pipes.PipeItemsPriorityInsertion;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPriorityInsertionPipe extends GuiContainer {

	protected int xSize;
	protected int ySize;
	private GuiButton[] buttons = new GuiButton[18];
	public int guiX = 0;
	public int guiY = 0;
	private final PipeItemsPriorityInsertion pipe;

	public GuiPriorityInsertionPipe(TileGenericPipe container) {
		super(new ContainerPriorityInsertionPipe(container));
		pipe = (PipeItemsPriorityInsertion) container.pipe;
		xSize = 132;
		ySize = 130;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		// int bw = this.xSize - 20;
		int guiX = (width - xSize) / 2 + 30;
		int guiY = (height - ySize) / 2 - 10;

		buttonList.add(buttons[0] = new GuiButton(1, guiX + 6, guiY + 24, 20, 17, "-"));
		buttonList.add(buttons[1] = new GuiButton(2, guiX + 8 + 20, guiY + 24, 30, 17, "0"));
		buttonList.add(buttons[2] = new GuiButton(3, guiX + 10 + 50, guiY + 24, 20, 17, "+"));

		buttonList.add(buttons[3] = new GuiButton(4, guiX + 6, guiY + 25 + 17, 20, 17, "-"));
		buttonList.add(buttons[4] = new GuiButton(5, guiX + 8 + 20, guiY + 25 + 17, 30, 17, "0"));
		buttonList.add(buttons[5] = new GuiButton(6, guiX + 10 + 50, guiY + 25 + 17, 20, 17, "+"));

		buttonList.add(buttons[6] = new GuiButton(7, guiX + 6, guiY + 26 + 17 * 2, 20, 17, "-"));
		buttonList.add(buttons[7] = new GuiButton(8, guiX + 8 + 20, guiY + 26 + 17 * 2, 30, 17, "0"));
		buttonList.add(buttons[8] = new GuiButton(9, guiX + 10 + 50, guiY + 26 + 17 * 2, 20, 17, "+"));

		buttonList.add(buttons[9] = new GuiButton(10, guiX + 6, guiY + 27 + 17 * 3, 20, 17, "-"));
		buttonList.add(buttons[10] = new GuiButton(11, guiX + 8 + 20, guiY + 27 + 17 * 3, 30, 17, "0"));
		buttonList.add(buttons[11] = new GuiButton(12, guiX + 10 + 50, guiY + 27 + 17 * 3, 20, 17, "+"));

		buttonList.add(buttons[12] = new GuiButton(13, guiX + 6, guiY + 28 + 17 * 4, 20, 17, "-"));
		buttonList.add(buttons[13] = new GuiButton(14, guiX + 8 + 20, guiY + 28 + 17 * 4, 30, 17, "0"));
		buttonList.add(buttons[14] = new GuiButton(15, guiX + 10 + 50, guiY + 28 + 17 * 4, 20, 17, "+"));

		buttonList.add(buttons[15] = new GuiButton(16, guiX + 6, guiY + 29 + 17 * 5, 20, 17, "-"));
		buttonList.add(buttons[16] = new GuiButton(17, guiX + 8 + 20, guiY + 29 + 17 * 5, 30, 17, "0"));
		buttonList.add(buttons[17] = new GuiButton(18, guiX + 10 + 50, guiY + 29 + 17 * 5, 20, 17, "+"));

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		buttons[1].displayString = "" + pipe.sidePriorities[0];
		buttons[4].displayString = "" + pipe.sidePriorities[1];
		buttons[7].displayString = "" + pipe.sidePriorities[2];
		buttons[10].displayString = "" + pipe.sidePriorities[3];
		buttons[13].displayString = "" + pipe.sidePriorities[4];
		buttons[16].displayString = "" + pipe.sidePriorities[5];
		
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.pipeItemsPriorityInsertion"), guiX + 33, guiY + 22, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int index = (guibutton.id - 1) / 3;
		int newData = pipe.sidePriorities[index];
		if((guibutton.id - 1) % 3 == 0) {
			newData--;
		} else {
			newData++;
		}
		/*
		 * //Old code switch (guibutton.id) { case 1: index = 0;
		 * pipeLogic.distData[0] -= 1; break; case 3: index = 0;
		 * pipeLogic.distData[0] += 1; break; case 4: pipeLogic.distData[1] -=
		 * 1; break; case 6: pipeLogic.distData[1] += 1; break; case 7:
		 * pipeLogic.distData[2] -= 1; break; case 9: pipeLogic.distData[2] +=
		 * 1; break; case 10: pipeLogic.distData[3] -= 1; break; case 12:
		 * pipeLogic.distData[3] += 1; break; case 13: pipeLogic.distData[4] -=
		 * 1; break; case 15: pipeLogic.distData[4] += 1; break; case 16:
		 * pipeLogic.distData[5] -= 1; break; case 18: pipeLogic.distData[5] +=
		 * 1; break; }
		 */

		if(newData < 0 || newData > 6)
			return;

		MessagePriorityPipe message = new MessagePriorityPipe(pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord, (byte) index, newData);
		PacketHandler.INSTANCE.sendToServer(message);	
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Textures.GUI_PRIORITY);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

	}

}
