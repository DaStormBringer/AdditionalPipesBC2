package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.textures.Textures;
import buildcraft.transport.Pipe;

@SideOnly(Side.CLIENT)
public class GuiPipeClosed extends GuiContainer {

	public GuiPipeClosed(InventoryPlayer inventory, Pipe<?> pipe) {
		super(new ContainerPipeClosed(inventory, pipe));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(StatCollector.translateToLocal("Closed Pipe"), 60, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
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
