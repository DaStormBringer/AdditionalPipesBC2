package additionalpipes;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import additionalpipes.pipes.PipeTeleport;
import additionalpipes.pipes.TeleportManager;

public class CommandAdditionalPipes extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return AdditionalPipes.MODID.toLowerCase();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if ((args.length > 0) && "teleport".equals(args[0]))
		{

			if ((args.length > 1) && "info".equals(args[1]))
			{
				if (args.length == 3)
				{
					try
					{
						final int index = Integer.parseInt(args[2]);
					} catch (final Exception e)
					{
					}

				}
				else if (args.length == 5)
				{
					try
					{
						final int x = Integer.parseInt(args[2]);
						final int y = Integer.parseInt(args[3]);
						final int z = Integer.parseInt(args[4]);
						final TileEntity te = ((EntityPlayer) sender).worldObj.getBlockTileEntity(x, y, z);
						/*
						 * if(te instanceof TileGenericPipe) { Pipe pipe =
						 * ((TileGenericPipe) te).pipe; ifpipe instanceof
						 * PipeTeleport) { PipeLogicTeleport logicTeleport =
						 * (PipeLogicTeleport) logic; StringBuffer sb = new
						 * StringBuffer(); sb.append('[');
						 * sb.append(logic.container
						 * .pipe.getClass().getSimpleName());
						 * sb.append(", Freq: "
						 * ).append(logicTeleport.getFrequency());
						 * sb.append(", State: ").append(logicTeleport.state);
						 * sb.append(", Owner: ").append(logicTeleport.owner);
						 * sb.append(']');
						 * sender.sendChatToPlayer(ChatMessageComponent
						 * .func_111066_d(sb.toString())); } }
						 */
					} catch (final Exception e)
					{
					}
				}
			}
			else
			{
				final StringBuffer sb = new StringBuffer();
				sb.append("Teleport pipes: ");
				for (final PipeTeleport pipe : TeleportManager.instance.teleportPipes)
				{
					sb.append('[');
					sb.append(pipe.getClass().getSimpleName()).append(',');
					sb.append(pipe.getPosition().x).append(',');
					sb.append(pipe.getPosition().y).append(',');
					sb.append(pipe.getPosition().z).append(']');
				}
				sender.sendChatToPlayer(ChatMessageComponent.createFromText(sb.toString()));
			}

		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
