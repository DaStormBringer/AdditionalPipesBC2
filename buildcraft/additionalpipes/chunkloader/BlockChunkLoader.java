package buildcraft.additionalpipes.chunkloader;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.AdditionalPipes;

public class BlockChunkLoader extends BlockContainer {

	public BlockChunkLoader(int BlockID, int i) {
		super(BlockID, i, Material.cloth);
		setTextureFile(AdditionalPipes.TEXTURE_MASTER);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		//TODO implement?
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, int p1, int p2) {
		super.breakBlock(world, i, j, k, p2, p2);
		//TODO implement?
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileChunkLoader();
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}

}