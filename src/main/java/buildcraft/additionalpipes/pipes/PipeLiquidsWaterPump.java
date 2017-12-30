package buildcraft.additionalpipes.pipes;

import buildcraft.additionalpipes.APConfiguration;
import buildcraft.api.transport.pipe.IPipe;
import buildcraft.api.transport.pipe.PipeBehaviour;
import buildcraft.transport.pipe.flow.PipeFlowFluids;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class PipeLiquidsWaterPump extends APPipe
{

	public PipeLiquidsWaterPump(IPipe pipe, NBTTagCompound nbt)
	{
		super(pipe, nbt);
	}

	public PipeLiquidsWaterPump(IPipe pipe)
	{
		super(pipe);
	}

	@Override
	public void onTick()
	{
        if(pipe.getHolder().getPipeWorld().getBlockState(getPos().down()).getBlock() == Blocks.WATER)
		{
			((PipeFlowFluids)pipe.getFlow()).insertFluidsForce(new FluidStack(FluidRegistry.WATER, APConfiguration.waterPumpWaterPerTick), EnumFacing.DOWN, false);
		}
	}
	
	@Override
	public boolean canConnect(EnumFacing side, TileEntity tile)
	{
		return side != EnumFacing.DOWN;
	}
	
	@Override
	public boolean canConnect(EnumFacing side, PipeBehaviour tile)
	{
		return side != EnumFacing.DOWN;
	}

}
