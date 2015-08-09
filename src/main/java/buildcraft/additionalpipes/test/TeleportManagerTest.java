package buildcraft.additionalpipes.test;

import java.util.ArrayList;

import buildcraft.additionalpipes.pipes.PipeItemsTeleport;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.additionalpipes.utils.Log;

/**
 * This class is used to make sure the new teleport manager is working properly.
 * @author Jamie
 *
 */
public class TeleportManagerTest
{
	
	public static void runAllTests()
	{
		//set the name of the current thread so that FMLCommonHandler.instance().getEfectiveSide returns the server side.
		String oldThreadName = Thread.currentThread().getName();
		
		Thread.currentThread().setName("Server thread");
		
		Log.info("[TeleportManagerTest] Testing Basic Adding and Removing");
		
		if(testBasicAddRemove())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing basic getConnectedPipes()");
		if(testGetConnectedPipesBasic())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing proper behavior of getConnectedPipes() on send-receive pipes");
		if(testGetConnectedPipesSendReceive())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		Log.info("[TeleportManagerTest] Testing a real use case for getConnectedPipes()");
		if(testGetConnectedPipesRealUseCase())
		{
			Log.info(">> Passed!");
		}
		else
		{
			Log.info(">> Failed!");
		}
		
		//clean up
		TeleportManager.instance.reset();
		Thread.currentThread().setName(oldThreadName);
	}
	
	public static boolean testBasicAddRemove()
	{
		//just test to make sure no exceptions are thrown
		try
		{
			TeleportManager.instance.reset();
			PipeItemsTeleport pipe = new PipeItemsTeleport(null);
			
			pipe.setFrequency(3);
			TeleportManager.instance.add(pipe, pipe.getFrequency());
			
			TeleportManager.instance.remove(pipe, pipe.getFrequency());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
			
		return true;
	}
	
	public static boolean testGetConnectedPipesBasic()
	{
		TeleportManager.instance.reset();
		PipeItemsTeleport pipe1 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe2 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe3 = new PipeItemsTeleport(null);

		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		
		pipe1.state = 0x3;
		pipe2.state = 0x3;
		pipe3.state = 0x3;
		
		pipe1.isPublic = true;
		pipe2.isPublic = true;
		pipe3.isPublic = true;

		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe2, 3);
		TeleportManager.instance.add(pipe3, 3);
		
		ArrayList<PipeTeleport<?>> pipesList = TeleportManager.instance.getConnectedPipes(pipe1, true, true);
		
		if(!(pipesList.size() == 2 && (pipesList.contains(pipe2) && pipesList.contains(pipe3))))
		{
			return false;
		}

		TeleportManager.instance.remove(pipe1, 3);
		TeleportManager.instance.remove(pipe2, 3);
		TeleportManager.instance.remove(pipe3, 3);		
		return true;
	}
	
	public static boolean testGetConnectedPipesSendReceive()
	{
		TeleportManager.instance.reset();
		PipeItemsTeleport pipe1 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe2 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe3 = new PipeItemsTeleport(null);

		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		
		pipe1.state = 0x3;
		pipe2.state = 0x1;
		pipe3.state = 0x2;
		
		pipe1.isPublic = true;
		pipe2.isPublic = true;
		pipe3.isPublic = true;

		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe2, 3);
		TeleportManager.instance.add(pipe3, 3);
		
		//get a list of pipes which can send to pipe1
		ArrayList<PipeTeleport<?>> sendablePipes = TeleportManager.instance.getConnectedPipes(pipe1, true, false);
		
		if(!(sendablePipes.size() == 1 && (sendablePipes.contains(pipe2) && !(sendablePipes.contains(pipe1) && sendablePipes.contains(pipe3)))))
		{
			return false;
		}
		
		//get a list of pipes which pipe1 can send to
		ArrayList<PipeTeleport<?>> receivingPipes = TeleportManager.instance.getConnectedPipes(pipe1, false, true);
		
		if(!(receivingPipes.size() == 1 && (receivingPipes.contains(pipe3) && !(receivingPipes.contains(pipe1) && receivingPipes.contains(pipe2)))))
		{
			return false;
		}

		TeleportManager.instance.remove(pipe1, 3);
		TeleportManager.instance.remove(pipe2, 3);
		TeleportManager.instance.remove(pipe3, 3);		
		return true;
	}
	
	public static boolean testGetConnectedPipesRealUseCase()
	{
		TeleportManager.instance.reset();
		PipeItemsTeleport pipe1 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe2 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe3 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe4 = new PipeItemsTeleport(null);
		PipeItemsTeleport pipe5 = new PipeItemsTeleport(null);

		pipe1.setFrequency(3);
		pipe2.setFrequency(3);
		pipe3.setFrequency(3);
		pipe4.setFrequency(3);
		pipe5.setFrequency(7);
		
		pipe1.state = 0x1;  //send only
		pipe2.state = 0x3;  //send and receive
		pipe3.state = 0x2;  //receive only
		pipe4.state = 0x1;  //send only
		pipe5.state = 0x3;  //send and receive
		
		pipe1.isPublic = true;
		pipe2.isPublic = true;
		pipe3.isPublic = true;
		pipe4.isPublic = true;
		pipe5.isPublic = true;
		
		TeleportManager.instance.add(pipe1, 3);
		TeleportManager.instance.add(pipe2, 3);
		TeleportManager.instance.add(pipe3, 3);
		TeleportManager.instance.add(pipe4, 3);
		TeleportManager.instance.add(pipe5, 7);

		ArrayList<PipeTeleport<?>> pipesList = TeleportManager.instance.getConnectedPipes(pipe1, false, true);
		
		Log.info(">> getConnectedPipes() returned " + pipesList.size() + " pipes");

		TeleportManager.instance.remove(pipe1, 3);
		TeleportManager.instance.remove(pipe2, 3);
		TeleportManager.instance.remove(pipe3, 3);	
		
		if(pipesList.size() == 2)
		{
			if(pipesList.contains(pipe2) && pipesList.contains(pipe3))
			{
				if(!pipesList.contains(pipe4))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	
}
