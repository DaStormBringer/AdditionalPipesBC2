/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.gui;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageAdvWoodPipe;
import buildcraft.additionalpipes.pipes.PipeBehaviorAdvWood;
import buildcraft.additionalpipes.textures.Textures;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedWoodPipe extends GuiContainer {

	int inventoryRows = 1;
	IInventory playerInventory;
	PipeBehaviorAdvWood pipe;
	private GuiButton[] buttons = new GuiButton[1];

	int guiX, guiY; 
	
	public GuiAdvancedWoodPipe(EntityPlayer player, IInventory playerInventory, PipeBehaviorAdvWood pipe)
	{
		super(new ContainerAdvancedWoodPipe(player, playerInventory, pipe));
		this.playerInventory = playerInventory;
		this.pipe = pipe;
		// container = theContainer;
		xSize = 175;
		ySize = 156;

	}

	@Override
	public void initGui() 
	{
		super.initGui();
		guiX = (width - xSize) / 2;
		guiY = (height - ySize) / 2;
		buttons[0] = new GuiButton(1, guiX + 8, guiY + 40, 140, 20, "");
		buttonList.add(buttons[0]);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) 
	{
		if(pipe.exclude) 
		{
			buttons[0].displayString = I18n.format("gui.advwood_pipe.blacklist");
		}
		else
		{
			buttons[0].displayString = I18n.format("gui.advwood_pipe.whitelist");
		}
		
		fontRendererObj.drawString(I18n.format("gui.advwood_pipe.title"), guiX + 42, guiY + 22, 4210752);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if(guibutton.id == 1) 
		{
			pipe.exclude = !pipe.exclude;
			MessageAdvWoodPipe packet = new MessageAdvWoodPipe(pipe.pipe.getHolder().getPipePos(), pipe.exclude);
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
