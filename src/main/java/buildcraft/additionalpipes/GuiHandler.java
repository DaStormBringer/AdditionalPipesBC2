package buildcraft.additionalpipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import buildcraft.additionalpipes.gui.ContainerAdvancedWoodPipe;
import buildcraft.additionalpipes.gui.ContainerDistributionPipe;
import buildcraft.additionalpipes.gui.ContainerTeleportPipe;
import buildcraft.additionalpipes.gui.GuiAdvancedWoodPipe;
import buildcraft.additionalpipes.gui.GuiDistributionPipe;
import buildcraft.additionalpipes.gui.GuiTeleportPipe;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.PipeTransportAdvancedWood;
import buildcraft.transport.TileGenericPipe;

public class GuiHandler implements IGuiHandler {
	// Gui IDs
	public static final int PIPE_TP = 1;
	public static final int PIPE_DIST = 2;
	public static final int PIPE_WOODEN_ADV = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile == null) {
			return null;
		}
		switch(ID) {
		case PIPE_TP:
			return new ContainerTeleportPipe(player, (PipeTeleport<?>) ((TileGenericPipe) tile).pipe);
		case PIPE_DIST:
			return new ContainerDistributionPipe((TileGenericPipe) tile);
		case PIPE_WOODEN_ADV:
			return new ContainerAdvancedWoodPipe(player.inventory, (PipeTransportAdvancedWood) ((TileGenericPipe) tile).pipe.transport);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile == null) {
			return null;
		}
		switch(ID) {
		case PIPE_TP:
			return new GuiTeleportPipe(player, (PipeTeleport<?>) ((TileGenericPipe) tile).pipe);
		case PIPE_DIST:
			return new GuiDistributionPipe((TileGenericPipe) tile);
		case PIPE_WOODEN_ADV:
			return new GuiAdvancedWoodPipe(player.inventory, (TileGenericPipe) tile);
		default:
			return null;
		}
	}
}
