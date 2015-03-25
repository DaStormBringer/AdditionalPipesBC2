package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.textures.Textures;
import buildcraft.transport.Pipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiJeweledPipe extends GuiContainer
{
	
	GuiButton[] directionButtons = new GuiButton[9];

	public GuiJeweledPipe(InventoryPlayer inventory, Pipe<?> pipe) {
		super(new ContainerJeweledPipe(inventory, pipe));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		directionButtons[0] = new GuiButton(0, 10, 20, 25, 20, StatCollector.translateToLocal("gui.button.up"));
		buttonList.add(directionButtons[0]);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.jeweled_pipe"), 150, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 146, ySize - 55 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Textures.DISPENSER);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
	}

}
