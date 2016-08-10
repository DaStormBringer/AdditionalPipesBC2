package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import logisticspipes.interfaces.routing.ISpecialPipedConnection;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.buildcraft.BCPipeInformationProvider;
import logisticspipes.proxy.specialconnection.SpecialPipeConnection.ConnectionInformation;
import logisticspipes.routing.PipeRoutingConnectionType;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.transport.IPipeTile;

/**
 * Another Logistics Pipes interface class.
 * @author Jamie
 *
 */
public class APSpecialPipedConnection implements ISpecialPipedConnection
{

	EnumSet<PipeRoutingConnectionType> flags = EnumSet.<PipeRoutingConnectionType>of(PipeRoutingConnectionType.canRequestFrom, PipeRoutingConnectionType.canRouteTo);

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public boolean isType(IPipeInformationProvider startPipe)
	{
		return startPipe instanceof LogisticsTileGenericPipe || startPipe instanceof BCPipeInformationProvider;
	}

	@Override
	public List<ConnectionInformation> getConnections(
			IPipeInformationProvider startPipeInfoGeneric,
			EnumSet<PipeRoutingConnectionType> connection,
			ForgeDirection side)
	{
		ArrayList<ConnectionInformation> connectionList = new ArrayList<ConnectionInformation>();

		TileEntity pipeTile = startPipeInfoGeneric.getWorld().getTileEntity(startPipeInfoGeneric.getX(), startPipeInfoGeneric.getY(), startPipeInfoGeneric.getZ());
		if(pipeTile instanceof IPipeTile)
		{
			if(((IPipeTile)pipeTile).getPipe() instanceof PipeLogisticsTeleport)
			{
				PipeLogisticsTeleport nearPipe = (PipeLogisticsTeleport) ((IPipeTile)pipeTile).getPipe();
				PipeLogisticsTeleport farPipe = nearPipe.getConnectedPipe();				
				
				if(farPipe != null)
				{
					ConnectionInformation connectionInfo = new ConnectionInformation(new PipeTeleportInformationProvider(farPipe),
							flags,
							nearPipe.getOpenOrientation().getOpposite(),
							farPipe.getOpenOrientation().getOpposite(),
							1
							); 
					connectionList.add(connectionInfo);
					
				}
			}
		}
		
		
		
		return connectionList;
	}

}
