package net.minecraft.src.buildcraft.zeldo.ChunkLoader;

import net.minecraft.src.TileEntity;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;

public class TileChunkLoader extends TileEntity {

	public TileChunkLoader()
	{
		//System.out.println("Tile");
	}
	@Override
	public void updateEntity()
	{
		MutiPlayerProxy.AddChunkToList(xCoord, zCoord);
	}
}

