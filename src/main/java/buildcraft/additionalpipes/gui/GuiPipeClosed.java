package buildcraft.additionalpipes.gui;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.pipes.PipeBehaviorClosed;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.lib.gui.GuiBC8;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPipeClosed extends GuiBC8<ContainerPipeClosed> {

	public GuiPipeClosed(EntityPlayer player, PipeBehaviorClosed pipe)
	{
		super(new ContainerPipeClosed(player, pipe));
	}

	@Override
	protected void drawForegroundLayer() 
	{
		fontRenderer.drawString(I18n.format("gui.closed_pipe.title"), guiLeft + 60, guiTop + 6, 4210752);
		fontRenderer.drawString(I18n.format("container.inventory"), guiLeft + 8, guiTop + ySize - 96 + 2, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawBackgroundLayer(float partialTicks)
	{

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(Textures.DISPENSER);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;
		drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
	}

}
