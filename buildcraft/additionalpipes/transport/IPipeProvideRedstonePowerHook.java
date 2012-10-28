package buildcraft.additionalpipes.transport;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public interface IPipeProvideRedstonePowerHook {

	public abstract boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l);

	public abstract boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l);
}
