package buildcraft.additionalpipes.pipes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import logisticspipes.interfaces.routing.ISpecialPipedConnection;
import logisticspipes.proxy.specialconnection.SpecialPipeConnection.ConnectionInformation;
import logisticspipes.routing.PipeRoutingConnectionType;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import net.minecraftforge.common.util.ForgeDirection;

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
		return startPipe instanceof PipeLogisticsTeleport;
	}

	@Override
	public List<ConnectionInformation> getConnections(
			IPipeInformationProvider startPipeInfoGeneric,
			EnumSet<PipeRoutingConnectionType> connection,
			ForgeDirection side)
	{
		PipeTeleportInformationProvider startPipeInfo = ((PipeTeleportInformationProvider)startPipeInfoGeneric);
		PipeLogisticsTeleport connectedPipe = startPipeInfo.pipe.getConnectedPipe();				
						
		ArrayList<ConnectionInformation> connectionList = new ArrayList<ConnectionInformation>();
		
		if(connectedPipe != null)
		{
			ConnectionInformation connectionInfo = new ConnectionInformation(new PipeTeleportInformationProvider(connectedPipe),
					flags,
					connectedPipe.getOpenOrientation().getOpposite(),
					connectedPipe.getOpenOrientation().getOpposite(),
					1
					); 
			connectionList.add(connectionInfo);
			
		}
		
		return connectionList;
	}

}
