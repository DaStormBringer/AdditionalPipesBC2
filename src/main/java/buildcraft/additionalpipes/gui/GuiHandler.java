package buildcraft.additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.pipes.PipeItemsJeweled;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.PipeTransportAdvancedWood;
import buildcraft.additionalpipes.utils.Log;
import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	// Gui IDs
	public static final int PIPE_TP = 1;
	public static final int PIPE_DIST = 2;
	public static final int PIPE_WOODEN_ADV = 3;
	public static final int PIPE_CLOSED = 4;
	public static final int PIPE_PRIORITY = 5;
	public static final int PIPE_JEWELED = 6;


	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile == null)
		{
			return null;
		}
		
		Log.debug("Opening Container " + ID + " on the server");
		
		switch(ID) {
		case PIPE_TP:
			return new ContainerTeleportPipe(player, (PipeTeleport<?>) ((TileGenericPipe) tile).pipe);
		case PIPE_DIST:
			return new ContainerDistributionPipe((TileGenericPipe) tile);
		case PIPE_WOODEN_ADV:
			return new ContainerAdvancedWoodPipe(player.inventory, (PipeTransportAdvancedWood) ((TileGenericPipe) tile).pipe.transport);
		case PIPE_CLOSED:
			return new ContainerPipeClosed(player.inventory, ((TileGenericPipe) tile).pipe);
		case PIPE_PRIORITY:
			return new ContainerPriorityInsertionPipe((TileGenericPipe) tile);
		case PIPE_JEWELED:
			return new ContainerJeweledPipe(player.inventory, ((PipeItemsJeweled)((TileGenericPipe) tile).pipe));
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile == null)
		{
			return null;
		}
		
		Log.debug("Opening GUI " + ID + " on the client");
		
		switch(ID)
		{
		case PIPE_TP:
			return new GuiTeleportPipe(player, (PipeTeleport<?>) ((TileGenericPipe) tile).pipe);
		case PIPE_DIST:
			return new GuiDistributionPipe((TileGenericPipe) tile);
		case PIPE_WOODEN_ADV:
			return new GuiAdvancedWoodPipe(player.inventory, (TileGenericPipe) tile);
		case PIPE_CLOSED:
			return new GuiPipeClosed(player.inventory, ((TileGenericPipe) tile).pipe);
		case PIPE_PRIORITY:
			return new GuiPriorityInsertionPipe((TileGenericPipe) tile);
		case PIPE_JEWELED:
			return new GuiJeweledPipe(player.inventory, ((PipeItemsJeweled)((TileGenericPipe) tile).pipe));
		default:
			return null;
		}
	}
}
