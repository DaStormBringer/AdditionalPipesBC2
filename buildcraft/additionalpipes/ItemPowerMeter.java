package buildcraft.additionalpipes;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;

public class ItemPowerMeter extends Item {

	public ItemPowerMeter(int id) {
		super(id);
		setMaxStackSize(1);
		setTextureFile(AdditionalPipes.TEXTURE_MASTER);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side,
			float par8, float par9, float par10) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		boolean isPowerTile = te instanceof IPowerReceptor;
		if(!world.isRemote && isPowerTile) {
			IPowerReceptor receptor = (IPowerReceptor) te;
			IPowerProvider provider = receptor.getPowerProvider();

			if(player.capabilities.isCreativeMode && ForgeDirection.VALID_DIRECTIONS[side] == ForgeDirection.UP) {
				receptor.getPowerProvider().receiveEnergy(1000, ForgeDirection.VALID_DIRECTIONS[side]);
			}

			player.sendChatToPlayer(String.format("R:%d L:%d m:%d M:%d A:%d S:%d",
					receptor.powerRequest(),
					provider.getLatency(),
					provider.getMinEnergyReceived(),
					provider.getMaxEnergyReceived(),
					provider.getMaxEnergyStored(),
					provider.getActivationEnergy(),
					provider.getEnergyStored()));
		}
		return isPowerTile;
	}

}
