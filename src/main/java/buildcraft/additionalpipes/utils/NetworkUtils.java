package buildcraft.additionalpipes.utils;

import io.netty.buffer.ByteBuf;

public class NetworkUtils
{
	/**
	 * Write an array of booleans to a ByteBuf.  Compacts the booleans into bytes.
	 * 
	 * Use readBooleanArray() to deserialize.
	 * @param buf
	 * @param booleans
	 */
	public static void writeBooleanArray(ByteBuf buf, boolean[] booleans)
	{
		//transform to byte array
		byte[] bytes = new byte[(booleans.length / 8) + 1];
		
		Log.debug("Fitting " + booleans.length + " booleans in " + bytes.length + " byte" + (bytes.length > 1 ? "s" : ""));
		
		for(int index = 0; index < booleans.length; ++index)
		{
			bytes[index / 8] |= (booleans[index] ? 1 : 0) << (index % 8);
		}
		
		buf.writeBytes(bytes);
	}
	
	/**
	 * Reads an array of booleans read from the provided ByteBuf.
	 * @param buf
	 * @param len how many booleans you want to read.
	 * @return
	 */
	public static boolean[] readBooleanArray(ByteBuf buf, int len)
	{
		byte[] bytes = new byte[(len / 8) + 1];
		buf.readBytes(bytes);
		
		boolean[] booleans = new boolean[len];
		for(int index = 0; index < len; ++index)
		{
			booleans[index] = ((bytes[index / 8] >> index % 8) & 1) == 1;
		}
		return booleans;
	}
}
