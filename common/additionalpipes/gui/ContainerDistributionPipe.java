package additionalpipes.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import additionalpipes.pipes.PipeItemsDistributor;
import buildcraft.transport.TileGenericPipe;

public class ContainerDistributionPipe extends Container
{
	private final PipeItemsDistributor pipe;
	public int[] lastDistData;

	public ContainerDistributionPipe(TileGenericPipe container)
	{
		pipe = (PipeItemsDistributor) container.pipe;
		lastDistData = new int[pipe.distData.length];
		for (int i = 0; i < lastDistData.length; i++)
		{
			lastDistData[i] = -1;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for (final Object obj : crafters)
		{
			final ICrafting crafter = (ICrafting) obj;
			for (int i = 0; i < lastDistData.length; i++)
			{
				if (lastDistData[i] != pipe.distData[i])
				{
					crafter.sendProgressBarUpdate(this, i, pipe.distData[i]);
				}
			}
		}
		for (int i = 0; i < lastDistData.length; i++)
		{
			lastDistData[i] = pipe.distData[i];
		}
	}

	@Override
	public void updateProgressBar(int i, int j)
	{
		if ((i >= 0) && (i < pipe.distData.length))
		{
			pipe.distData[i] = j;
		}
	}
}
