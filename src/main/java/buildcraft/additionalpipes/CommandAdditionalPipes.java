package buildcraft.additionalpipes;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;

public class CommandAdditionalPipes extends CommandBase {

	@Override
	public String getCommandName() {
		return AdditionalPipes.MODID.toLowerCase();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length > 0 && "teleport".equals(args[0])) 
		{
			StringBuffer sb = new StringBuffer();
			sb.append("Teleport pipes: ");
			for(PipeTeleport pipe : TeleportManager.instance.teleportPipes) {
				sb.append('[');
				sb.append(pipe.getClass().getSimpleName()).append(',');
				sb.append(pipe.getPosition().x).append(',');
				sb.append(pipe.getPosition().y).append(',');
				sb.append(pipe.getPosition().z).append(']');
			}
			sender.addChatMessage(new ChatComponentText(sb.toString()));

		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "";
	}

}
