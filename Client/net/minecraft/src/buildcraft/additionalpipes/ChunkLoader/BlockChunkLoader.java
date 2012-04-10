package net.minecraft.src.buildcraft.additionalpipes.ChunkLoader;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;


// Referenced classes of package net.minecraft.src:
//            Block, Material

public class BlockChunkLoader extends BlockContainer {

    public BlockChunkLoader(int BlockID) {
        super(BlockID, 12 * 16 + 1, Material.cloth);
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
    }

    @Override
    public void onBlockRemoval(World world, int i, int j, int k) {
    }

    @Override
    public TileEntity getBlockEntity() {
        return new TileChunkLoader();
    }

}