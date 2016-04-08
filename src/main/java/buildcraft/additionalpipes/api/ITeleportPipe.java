package buildcraft.additionalpipes.api;

import java.util.UUID;

import net.minecraft.util.BlockPos;
import buildcraft.transport.TileGenericPipe;

/**
 * Interface that describes the functions of a Teleport Pipe.
 * 
 * Mostly just getters and setters for the various properties.
 * @author Jamie
 *
 */
public interface ITeleportPipe 
{
	public void setFrequency(int freq);
	
	public int getFrequency();
	
	/**
	 * Gets the send-receive state of the teleport pipe.
	 * 
	 *  0b0 = none, 0b1 = send, 0b10 = receive, 0b11 = both
	 * @param state
	 */
	public byte getState();

	public void setState(byte state);


	public boolean isPublic();

	public void setPublic(boolean isPublic);

	public PipeType getType();
	
	public BlockPos getPosition();
	
	public boolean canReceive();
	
	public boolean canSend();

	public UUID getOwnerUUID();

	public String getOwnerName();

	public TileGenericPipe getContainer();
}