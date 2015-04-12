package buildcraft.additionalpipes;

import java.util.Collection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;

public class CommandAdditionalPipes extends CommandBase {

	@Override
	public String getCommandName() {
		return AdditionalPipes.MODID.toLowerCase();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length > 0 && "teleport".equals(args[0])) 
		{
			//let's be a bit lenient with plurals
			
			Collection pipes = null;
			
			if(args[1].equals("items") || args[1].equals("item"))
			{
				pipes = TeleportManager.instance.getAllItemPipesInNetwork();
			}
			else if(args[1].equals("fluids") || args[1].equals("fluid"))
			{
				pipes = TeleportManager.instance.getAllFluidPipesInNetwork();
			}
			else if(args[1].equals("power"))
			{
				pipes = TeleportManager.instance.getAllFluidPipesInNetwork();
			}
			else
			{
				return;
			}
			StringBuffer sb = new StringBuffer();
			sb.append("Teleport pipes: ");
			for(Object pipeObject : pipes)
			{
				PipeTeleport<?> pipe = (PipeTeleport<?>)pipeObject;
				sb.append('[');
				sb.append(pipe.type.toString()).append(',');
				sb.append(pipe.getPosition().x).append(',');
				sb.append(pipe.getPosition().y).append(',');
				sb.append(pipe.getPosition().z).append(']');
			}
			sender.addChatMessage(new ChatComponentText(sb.toString()));

		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return StatCollector.translateToLocal("command.ap.usage");
	}

}
