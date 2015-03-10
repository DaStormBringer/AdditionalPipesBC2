package buildcraft.additionalpipes.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;

public class DataUtils
{
	/**
	 * Write a BlockPos to a ByteBuf for network transmission
	 * @param position
	 * @param to
	 */
	public static void writePosition(BlockPos position, ByteBuf to)
	{
		to.writeInt(position.getX());
		to.writeInt(position.getY());
		to.writeInt(position.getZ());
	}
	
	/**
	 * Read a BlockPos from a ByteBuf
	 */
	public static BlockPos readPosition(ByteBuf from)
	{
		int x = from.readInt();
		int y = from.readInt();
		int z = from.readInt();
		
		return new BlockPos(x, y, z);
	}
}
