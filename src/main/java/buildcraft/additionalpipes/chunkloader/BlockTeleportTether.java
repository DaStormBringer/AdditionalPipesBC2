package buildcraft.additionalpipes.chunkloader;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.additionalpipes.AdditionalPipes;

public class BlockTeleportTether extends BlockContainer {

	public BlockTeleportTether()
	{
		super(Material.cloth);
		setCreativeTab(AdditionalPipes.instance.creativeTab);
		setUnlocalizedName("teleportTether");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileTeleportTether();
	}
}