package buildcraft.additionalpipes.chunkloader;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import buildcraft.additionalpipes.AdditionalPipes;

public class BlockChunkLoader extends BlockContainer {

	public BlockChunkLoader(int BlockID, int i) {
		super(BlockID, i, Material.cloth);
		setTextureFile(AdditionalPipes.TEXTURE_CHUNKLOADER);
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		//TODO implement?
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int p1, int p2) {
		//TODO implement?
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new TileChunkLoader();
	}

}