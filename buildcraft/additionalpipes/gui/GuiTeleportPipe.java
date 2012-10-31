package buildcraft.additionalpipes.gui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.NetworkHandler;
import buildcraft.additionalpipes.network.PacketAdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GuiTeleportPipe extends GuiContainer {

	private PipeTeleport pipe;
	private ContainerTeleportPipe container;
	private GuiButton[] buttons = new GuiButton[7];

	public GuiTeleportPipe(TileGenericPipe tile) {
		super(new ContainerTeleportPipe(tile));

		container = (ContainerTeleportPipe) inventorySlots;
		pipe = (PipeTeleport) tile.pipe;

		xSize = 228;
		ySize = 117;
	}

	@Override
	public void initGui() {
		super.initGui();
		int bw = xSize - 22;
		controlList.add(buttons[0] = new GuiButton(1, (width - xSize) / 2 + 10, (height - ySize) / 2 + 20, bw / 6, 20, "-100"));
		controlList.add(buttons[1] = new GuiButton(2, (width - xSize) / 2 + 12 + bw / 6, (height - ySize) / 2 + 20, bw / 6, 20, "-10"));
		controlList.add(buttons[2] = new GuiButton(3, (width - xSize) / 2 + 12 + bw * 2 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "-1"));
		controlList.add(buttons[3] = new GuiButton(4, (width - xSize) / 2 + 12 + bw * 3 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "+1"));
		controlList.add(buttons[4] = new GuiButton(5, (width - xSize) / 2 + 12 + bw * 4 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "+10"));
		controlList.add(buttons[5] = new GuiButton(6, (width - xSize) / 2 + 16 + bw * 5 / 6, (height - ySize) / 2 + 20, bw / 6, 20, "+100"));
		controlList.add(buttons[6] = new GuiButton(7, (width - xSize) / 2 + 16, (height - ySize) / 2 + 52, bw / 6, 20, "Switch"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		fontRenderer.drawString("Frequency: " + pipe.logic.freq, 8, 6, 0x404040);
		fontRenderer.drawString("Connected Pipes: " + container.connectedPipes, 100, 6, 0x404040);
		fontRenderer.drawString("Can Receive: " + pipe.logic.canReceive, 8, 42, 0x404040);
		//fontRenderer.drawString("Owner: " + pipe.logic.owner, 8, 75, 0x404040);
	}
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int freq = pipe.logic.freq;
		boolean canReceive = pipe.logic.canReceive;
		switch(guibutton.id) {
		case 1:
			freq -= 100;
			break;
		case 2:
			freq -= 10;
			break;
		case 3:
			freq -= 1;
			break;
		case 4:
			freq += 1;
			break;
		case 5:
			freq += 10;
			break;
		case 6:
			freq += 100;
			break;
		case 7:
			canReceive = !pipe.logic.canReceive;
			break;
		}
		if (freq < 0) {
			freq = 0;
		}

		PacketAdditionalPipes packet = new PacketAdditionalPipes(NetworkHandler.TELE_PIPE_DATA, false);
		packet.writeInt(pipe.xCoord);
		packet.writeInt(pipe.yCoord);
		packet.writeInt(pipe.zCoord);
		packet.writeInt(freq);
		packet.write((byte) (canReceive ? 1 : 0));
		PacketDispatcher.sendPacketToServer(packet.makePacket());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		int i = mc.renderEngine.getTexture(AdditionalPipes.TEXTURE_GUI_TELEPORT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(i);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, xSize, ySize);

	}

}
