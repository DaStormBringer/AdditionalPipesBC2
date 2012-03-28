package net.minecraft.src.buildcraft.zeldo.ChunkLoader;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.buildcraft.zeldo.MutiPlayerProxy;


// Referenced classes of package net.minecraft.src:
//            Block, Material

public class BlockChunkLoader extends BlockContainer
{

	public BlockChunkLoader(int BlockID)
	{
		super(BlockID, 12 * 16 + 1, Material.cloth);
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k)
	{
		MutiPlayerProxy.AddChunkToList(i, k);
	}

	@Override
	public void onBlockRemoval(World world, int i, int j, int k)
	{
		MutiPlayerProxy.DeleteChunkFromList(i, k);
	}

	@Override
	public TileEntity getBlockEntity()
	{
		try
		{
			return new TileChunkLoader();
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}

}