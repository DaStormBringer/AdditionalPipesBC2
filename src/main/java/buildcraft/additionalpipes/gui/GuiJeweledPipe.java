package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;
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
	//note: the first two ff's are the alpha value.
	final static int[] tabColorsMain = {0xFFFFFFFF, 0xff3fc2ff, 0xff3e3ee0, 0xff44be4e, 0xfffffa00, 0xffff2323};
	
	final static int[] tabColorsOutline = {0xFFF0F0F0, 0xff30b2f0, 0xff2121d0, 0xff35af3f, 0xfff0db00, 0xfff01414};

	
	//set in initGui()
	int tabStartX;
	
	final int tabY = 14;
	final int tabHeight = 11;
	final int totalSpaceBetweenTabs = 7; //total space between tab strings
	
	int[] tabEndX = new int[6];
		
    public GuiJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
        super(new ContainerJeweledPipe(inventoryPlayer, pipe));
        xSize = 184;
        ySize = 211;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String containerName = StatCollector.translateToLocal("gui.jeweled_pipe");
        fontRendererObj.drawString(containerName, xSize / 2 - fontRendererObj.getStringWidth(containerName) / 2, 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 93, 4210752);
		
        //draw the actual tabs
        for(int tabNumber = 0; tabNumber < 6; ++tabNumber)
        {
        	//make the selected tab taller
        	int startY = ((ContainerJeweledPipe)inventorySlots).currentSide == tabNumber ? tabY - 3 : tabY;
        	
        	//draw outer rectangle
        	drawRect((tabNumber == 0 ? tabStartX - 3 : tabEndX[tabNumber- 1] + 3), startY, tabEndX[tabNumber] + 2, tabY + tabHeight, tabColorsMain[tabNumber]);
        	//draw inner rectangle
        	drawRect((tabNumber == 0 ? tabStartX - 1 : tabEndX[tabNumber- 1] + 5), startY + 2, tabEndX[tabNumber], tabY + tabHeight, tabColorsOutline[tabNumber] - 0x000f0f0f);
        }
        
		//add the tab labels
		for(int tabNumber = 0; tabNumber < 6; ++tabNumber)
		{
			String tabName = StatCollector.translateToLocal("gui.tab." + ForgeDirection.VALID_DIRECTIONS[tabNumber].toString().toLowerCase());
			
			int thisTabStartX = (tabNumber == 0 ? tabStartX: tabEndX[tabNumber- 1] + 6);
			
			fontRendererObj.drawString(tabName, thisTabStartX, tabY + 3, 4210752);
		}
	
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Textures.GUI_JEWELED);
        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
        
    }
    
	@SuppressWarnings("unchecked")
	public void initGui() 
	{
		super.initGui();
		
		tabStartX = 15;
		
		//calculate the tab widths
		for(int tabNumber = 0; tabNumber < 6; ++tabNumber)
		{
			String tabName = StatCollector.translateToLocal("gui.tab." + ForgeDirection.VALID_DIRECTIONS[tabNumber].toString().toLowerCase());
			
			int thisTabStartX = (tabNumber == 0 ? tabStartX: tabEndX[tabNumber- 1]);
			
			//record the width of this tab
			tabEndX[tabNumber] = thisTabStartX + fontRendererObj.getStringWidth(tabName) + (tabNumber == 0 ? 0 : totalSpaceBetweenTabs);
			
			//Log.debug("End x coordinate of tab number " + tabNumber + " is " + tabEndX[tabNumber]);
		}
	}
	
    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int x, int y, int type)
    {
        if (type == 0)
        {
            int hitboxShiftDistance = MathHelper.floor_double(totalSpaceBetweenTabs / 2.0);

            int xDistance = x - (this.guiLeft + tabStartX - hitboxShiftDistance);
            int yDistance = y - (this.guiTop + tabY);
            
            //check if click was on a tab box
            if(xDistance >= 0 && xDistance <= tabEndX[tabEndX.length - 1])
            {
            	if(yDistance >= 0 && yDistance <= tabHeight)
            	{
            		//find the tab the user clicked on
            		for(int tabNumber = 0; tabNumber < 6; ++tabNumber)
            		{
            			if(tabEndX[tabNumber] + hitboxShiftDistance > x - guiLeft)
            			{
            				((ContainerJeweledPipe)inventorySlots).setFilterTab((byte) tabNumber);
            				break;
            			}
            		}
            	}
            }
        }

        super.mouseMovedOrUp(x, y, type);
    }

}
