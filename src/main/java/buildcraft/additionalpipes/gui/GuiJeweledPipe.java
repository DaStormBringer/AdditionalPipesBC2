package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.textures.Textures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiJeweledPipe extends GuiContainer
{
	//set in initGui()
	int tabStartX;
	
	final int tabY = 18;
	final int tabHeight = 9;
	final int tabWidth = 30;
		
    public GuiJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
        super(new ContainerJeweledPipe(inventoryPlayer, pipe));
        xSize = 175;
        ySize = 211;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String containerName = StatCollector.translateToLocal("gui.jeweled_pipe");
        fontRendererObj.drawString(containerName, xSize / 2 - fontRendererObj.getStringWidth(containerName) / 2, 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 93, 4210752);
		
		//add the tab labels
		for(int tabNumber = 0; tabNumber < 6; ++tabNumber)
		{
			String tabName = StatCollector.translateToLocal("gui.tab." + ForgeDirection.VALID_DIRECTIONS[tabNumber].toString().toLowerCase());
			fontRendererObj.drawString(tabName, tabStartX + (tabWidth * tabNumber), tabY, 4210752);
		}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Textures.GUI_JEWELED[((ContainerJeweledPipe)inventorySlots).currentSide]);
        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
    }
    
	@SuppressWarnings("unchecked")
	public void initGui() 
	{
		super.initGui();
		
		tabStartX = ((width - xSize) / 2) + 30;
	}
	
    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int x, int y, int type)
    {
        if (type == 0)
        {
            int xDistance = x - (this.guiLeft + tabStartX);
            int yDistance = y - (this.guiTop + tabY);

            //check if click was on a tab box
            if(xDistance >= 0 && xDistance <= 6 * tabWidth)
            {
            	if(yDistance >= 0 && yDistance <= tabHeight)
            	{
            		((ContainerJeweledPipe)inventorySlots).setFilterTab((byte) (xDistance / tabWidth));
            	}
            }
        }

        super.mouseMovedOrUp(x, y, type);
    }

}
