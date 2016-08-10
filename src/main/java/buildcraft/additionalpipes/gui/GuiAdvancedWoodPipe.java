/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageAdvWoodPipe;
import buildcraft.additionalpipes.pipes.PipeTransportAdvancedWood;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.transport.TileGenericPipe;

@SideOnly(Side.CLIENT)
public class GuiAdvancedWoodPipe extends GuiContainer {

	int inventoryRows = 1;
	IInventory playerInventory;
	IInventory filterInventory;
	TileGenericPipe container;
	private GuiButton[] buttons = new GuiButton[1];

	public GuiAdvancedWoodPipe(EntityPlayer player, IInventory playerInventory, TileGenericPipe container) {
		super(new ContainerAdvancedWoodPipe(player, playerInventory, (PipeTransportAdvancedWood) container.pipe.transport));
		this.playerInventory = playerInventory;
		filterInventory = (PipeTransportAdvancedWood) container.pipe.transport;
		this.container = container;
		// container = theContainer;
		xSize = 175;
		ySize = 156;

	}

	@Override
	public void initGui() {
		super.initGui();
		int guiX = (width - xSize) / 2;
		int guiY = (height - ySize) / 2;
		buttons[0] = new GuiButton(1, guiX + 8, guiY + 40, 140, 20, "These items are required");
		buttonList.add(buttons[0]);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		if(((PipeTransportAdvancedWood) container.pipe.transport).exclude) {
			buttons[0].displayString = "These items are excluded";
		} else {
			buttons[0].displayString = "These items are required";
		}

		//fontRendererObj.drawString(StatCollector.translateToLocal(filterInventory.getInventoryName()), 8, 6, 0x404040);
		//fontRendererObj.drawString(StatCollector.translateToLocal(playerInventory.getInventoryName()), 8, 66, 0x404040);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(guibutton.id == 1) {
			MessageAdvWoodPipe packet = new MessageAdvWoodPipe(container.getPos());
			PacketHandler.INSTANCE.sendToServer(packet);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Textures.GUI_ADVANCEDWOOD);
		int j1 = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		drawTexturedModalRect(j1, k, 0, 0, xSize, ySize);
	}

}
