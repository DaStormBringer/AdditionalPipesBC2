package buildcraft.additionalpipes.chunkloader;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockChunkLoader extends BlockContainer {

	public BlockChunkLoader()
	{
		super(Material.cloth);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileChunkLoader();
	}
}