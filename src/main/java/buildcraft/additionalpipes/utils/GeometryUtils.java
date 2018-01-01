package buildcraft.additionalpipes.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GeometryUtils
{
	/**
	 * From a block position and a 3D point, gets the side of the block closest to that position.
	 * @param block
	 * @param point
	 * @return
	 */
	public static EnumFacing getNearestSide(BlockPos block, Vec3d point)
	{
		return EnumFacing.getFacingFromVector((float)(point.x - (block.getX() + 0.5)), (float)(point.y - (block.getY() + 0.5)), (float)(point.z - (block.getZ() + 0.5)));
	}
}
