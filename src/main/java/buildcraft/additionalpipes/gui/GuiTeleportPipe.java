package buildcraft.additionalpipes.gui;

import org.lwjgl.opengl.GL11;

import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageTelePipeUpdate;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.lib.BCLibSprites;
import buildcraft.lib.gui.GuiBC8;
import buildcraft.lib.gui.GuiIcon;
import buildcraft.lib.gui.ledger.Ledger_Neptune;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleportPipe extends GuiBC8<ContainerTeleportPipe> {
		
	protected class TeleportPipeLedger extends Ledger_Neptune
	{

		int headerColour = 0xe1c92f;
		int subheaderColour = 0xaaafb8;
		int textColour = 0x000000;
		
		final static int OVERLAY_COLOR = 0xd46c1f;

		String networkTitle;
		
		public TeleportPipeLedger() {
			super(GuiTeleportPipe.this, OVERLAY_COLOR, true);
			maxHeight = 99;
			shownElements.add(new TeleportPipeLedger());
			
			this.title = "gui.teleport.ledger.title";
		}

		@Override
		public void drawForeground(float partialTicks)
		{
			//we have to initialize this here since pipe is not yet set when the constructor is run
			if(networkTitle == null)
			{
				 appendText(((pipe.state & 0x1) >= 1) ? I18n.format("gui.teleport.ledger.outputs") : I18n.format("gui.teleport.ledger.inputs"), headerColour);
			}

			appendText(I18n.format("gui.teleport.ledger.owner"), subheaderColour);
			appendText(pipe.ownerName, textColour);
			appendText(networkTitle, subheaderColour);
			appendText(String.valueOf(container.connectedPipes), textColour);
			int[] net = pipe.network;
			if(net.length > 0) 
			{
				appendText(new StringBuilder("(").append(net[0]).append(", ").append(net[1]).append(", ").append(net[2]).append(")").toString(), textColour);
			}
			if(net.length > 3) {
				appendText(new StringBuilder("(").append(net[3]).append(", ").append(net[4]).append(", ").append(net[5]).append(")").toString(), textColour);
			}
			if(net.length > 6) {
				appendText(new StringBuilder("(").append(net[6]).append(", ").append(net[7]).append(", ").append(net[8]).append(")").toString(), textColour);
			}
			
			super.drawForeground(partialTicks);
		}

		@Override
		protected void drawIcon(double x, double y)
		{
	        GuiIcon.draw(BCLibSprites.ENGINE_ACTIVE, x, y, x + 16, y + 16);
		}
	
	}

	private final PipeTeleport pipe;
	private final ContainerTeleportPipe container;
	private final GuiButton[] buttons = new GuiButton[8];

	public GuiTeleportPipe(EntityPlayer player, PipeTeleport pipe) {
		super(new ContainerTeleportPipe(player, pipe));
		this.pipe = pipe;
		container = (ContainerTeleportPipe) inventorySlots;
		xSize = 228;
		ySize = 117;
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2, y = (height - ySize) / 2 + 16;
		int bw = xSize - 24;
		buttonList.add(buttons[0] = new GuiButton(1, x + 12, y + 32, bw / 6, 20, "-100"));
		buttonList.add(buttons[1] = new GuiButton(2, x + 12 + bw / 6, y + 32, bw / 6, 20, "-10"));
		buttonList.add(buttons[2] = new GuiButton(3, x + 12 + bw * 2 / 6, y + 32, bw / 6, 20, "-1"));
		buttonList.add(buttons[3] = new GuiButton(4, x + 12 + bw * 3 / 6, y + 32, bw / 6, 20, "+1"));
		buttonList.add(buttons[4] = new GuiButton(5, x + 12 + bw * 4 / 6, y + 32, bw / 6, 20, "+10"));
		buttonList.add(buttons[5] = new GuiButton(6, x + 12 + bw * 5 / 6, y + 32, bw / 6, 20, "+100"));

		buttonList.add(buttons[6] = new GuiButton(7, x + 12, y + 10, bw / 2, 20, ""));
		buttonList.add(buttons[7] = new GuiButton(8, x + 12 + bw * 3 / 6, y + 10, bw / 2, 20, ""));
	}

	@Override
	protected void drawForegroundLayer() 
	{
		fontRendererObj.drawString(I18n.format("gui.teleport.frequency", pipe.getFrequency()), 16, 12, 0x404040);
		fontRendererObj.drawString(new StringBuilder("(")
			.append(pipe.getPos().getX()).append(", ")
			.append(pipe.getPos().getY()).append(", ")
			.append(pipe.getPos().getZ()).append(")").toString(), 128, 12, 0x404040);
		switch(pipe.state) {
		case 3:
			buttons[6].displayString = I18n.format("gui.teleport.send_and_receive");
			break;
		case 2:
			buttons[6].displayString = I18n.format("gui.teleport.receive_only");
			break;
		case 1:
			buttons[6].displayString = I18n.format("gui.teleport.send_only");
			break;
		default:
			buttons[6].displayString = I18n.format("gui.teleport.disabled");
			break;
		}
		if(pipe.isPublic) {
			buttons[7].displayString = I18n.format("gui.teleport.public");
		} else {
			buttons[7].displayString = I18n.format("gui.teleport.private");
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int freq = pipe.getFrequency();
		byte state = pipe.state;
		boolean isPublic = pipe.isPublic;
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
			state = (byte) ((state + 1) % 4);
			break;
		case 8:
			isPublic = !isPublic;
			break;
		}
		if(freq < 0) {
			freq = 0;
		}

		MessageTelePipeUpdate packet = new MessageTelePipeUpdate(pipe.getPos(), freq, isPublic, state);
		PacketHandler.INSTANCE.sendToServer(packet);
	}

	@Override
	protected void drawBackgroundLayer(float partialTicks)
	{
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(Textures.GUI_TELEPORT);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
