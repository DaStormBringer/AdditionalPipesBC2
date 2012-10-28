package buildcraft.additionalpipes;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import buildcraft.additionalpipes.gui.ContainerTeleportPipe;
import buildcraft.additionalpipes.gui.CraftingAdvancedWoodPipe;
import buildcraft.additionalpipes.gui.GuiAdvancedWoodPipe;
import buildcraft.additionalpipes.gui.GuiDistributionPipe;
import buildcraft.additionalpipes.gui.GuiTeleportPipe;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	//Gui IDs
	public static final int PIPE_TP = 1;
	public static final int PIPE_DIST = 2;
	public static final int PIPE_WOODEN_ADV = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile == null) {
			return null;
		}
		switch(ID) {
		case PIPE_TP:
			return new ContainerTeleportPipe((TileGenericPipe) tile);
		case PIPE_DIST:
			return null;
		case PIPE_WOODEN_ADV:
			return new CraftingAdvancedWoodPipe(player.inventory, (IInventory) tile);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile == null) {
			return null;
		}
		switch(ID) {
		case PIPE_TP:
			return new GuiTeleportPipe((TileGenericPipe) tile);
		case PIPE_DIST:
			return new GuiDistributionPipe((TileGenericPipe) tile);
		case PIPE_WOODEN_ADV:
			return new GuiAdvancedWoodPipe(player.inventory, (IInventory) tile, (TileGenericPipe)tile);
		default:
			return null;
		}
	}
}
