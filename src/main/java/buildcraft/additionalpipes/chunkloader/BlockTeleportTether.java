package buildcraft.additionalpipes.chunkloader;

import buildcraft.additionalpipes.AdditionalPipes;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTeleportTether extends BlockContainer {

	public BlockTeleportTether()
	{
		super(Material.CLOTH);
		setCreativeTab(AdditionalPipes.instance.creativeTab);
		setUnlocalizedName("teleport_tether");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileTeleportTether();
	}
	
	public boolean isFullCube()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }
}