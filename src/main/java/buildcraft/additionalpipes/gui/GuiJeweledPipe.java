package buildcraft.additionalpipes.gui;

import net.minecraft.client.gui.GuiButton;
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
	//stores the six buttons which control which side of the filter is active
	GuiButton[] directionButtons;
	
	final static String[] buttonNames = {
		"gui.button.down",
		"gui.button.up",
		"gui.button.north",
		"gui.button.south",
		"gui.button.west",
		"gui.button.east",
	};
	
	int centerX;
	int centerY;
	
	ContainerJeweledPipe container;
		
    public GuiJeweledPipe(InventoryPlayer inventoryPlayer, PipeItemsJeweled pipe)
    {
        super(new ContainerJeweledPipe(inventoryPlayer, pipe));

        //create a pre-casted reference to the container
        container = (ContainerJeweledPipe) inventorySlots;
        
        xSize = 176;
        ySize = 140;
        
		centerX = (width - xSize) / 2 + 30;
		centerY = (height - ySize) / 2 - 10;
        
        directionButtons = new GuiButton[ForgeDirection.VALID_DIRECTIONS.length];
    }
    
    @Override
    public void initGui()
    {
    	
    	for(int side = 0; side < ForgeDirection.VALID_DIRECTIONS.length; ++side)
    	{
    		String text = StatCollector.translateToLocal(buttonNames[side]);
    		directionButtons[side] = new GuiButton(side, 50 + (30 * side), 8, 30, 17, text);
    	}
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String containerName = StatCollector.translateToLocal("gui.jeweled_pipe");
        fontRendererObj.drawString(containerName, xSize / 2 - fontRendererObj.getStringWidth(containerName) / 2, 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 93, 4210752);
        
        
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Textures.GUI_JEWELED[container.guiTab]);
        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
    }
}
