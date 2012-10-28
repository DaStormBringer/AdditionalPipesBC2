/**
 * BuildCraft is open-source. It is distributed under the terms of the
 * BuildCraft Open Source License. It grants rights to read, modify, compile
 * or run the code. It does *NOT* grant the right to redistribute this software
 * or its modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package buildcraft.additionalpipes.logic;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.GuiHandler;
import buildcraft.api.core.Orientations;
import buildcraft.api.core.Position;
import buildcraft.api.liquids.ITankContainer;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogic;
import buildcraft.transport.pipes.PipeLogicWood;

public class PipeLogicDistributor extends PipeLogic {

	public int distData[] = {1, 1, 1, 1, 1, 1};
	public int curTick = 0;

	public void switchPosition() {

		int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		int nextMetadata = metadata;

		for (int l = 0; l < 6; ++l) {
			nextMetadata ++;

			if (nextMetadata > 5) {
				nextMetadata = 0;
			}

			Position pos = new Position(xCoord, yCoord, zCoord, Orientations.values()[nextMetadata]);
			pos.moveForwards(1.0);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (tile instanceof TileGenericPipe) {
				if (((TileGenericPipe) tile).pipe.logic instanceof PipeLogicWood || ((TileGenericPipe) tile).pipe.logic instanceof PipeLogicAdvancedWood) {
					continue;
				}
			}

			if (tile instanceof IPipeEntry || tile instanceof IInventory || tile instanceof ITankContainer || tile instanceof TileGenericPipe) {
				if (distData[nextMetadata] > 0) {
					worldObj.setBlockMetadata(xCoord, yCoord, zCoord, nextMetadata);
					return;
				}
			}
		}
	}
	public void switchIfNeeded() {
		int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		int nextMetadata = metadata;

		for (int l = 0; l < 6; ++l) {
			if (nextMetadata > 5) {
				nextMetadata = 0;
			}

			Position pos = new Position(xCoord, yCoord, zCoord, Orientations.values()[nextMetadata]);
			pos.moveForwards(1.0);

			TileEntity tile = worldObj.getBlockTileEntity((int) pos.x, (int) pos.y, (int) pos.z);

			if (tile instanceof TileGenericPipe) {
				if (((TileGenericPipe) tile).pipe.logic instanceof PipeLogicWood || ((TileGenericPipe) tile).pipe.logic instanceof PipeLogicAdvancedWood) {
					continue;
				}
			}

			if (tile instanceof IPipeEntry || tile instanceof IInventory || tile instanceof ITankContainer || tile instanceof TileGenericPipe) {
				if (distData[nextMetadata] > 0) {
					worldObj.setBlockMetadata(xCoord, yCoord, zCoord, nextMetadata);
					return;
				}
			}

			nextMetadata ++;
		}
	}


	@Override
	public void onBlockPlaced() {
		super.onBlockPlaced();

		worldObj.setBlockMetadata(xCoord, yCoord, zCoord, 1);
		switchPosition();
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {

		ItemStack equippedItem = entityplayer.getCurrentEquippedItem();

		if (equippedItem != null) {

			if (equippedItem.getItem() instanceof IToolWrench) {

				switchPosition();
				worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);

				return true;
			}

			if (AdditionalPipes.isPipe(equippedItem.getItem())) {
				return false;
			}
		}

		entityplayer.openGui(AdditionalPipes.instance, GuiHandler.PIPE_DIST,
				container.worldObj, container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public boolean outputOpen(Orientations to) {
		return to.ordinal() == worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setInteger("curTick", curTick);
		for (int i = 0; i < distData.length; i++) {
			nbt.setInteger("distData" + i, distData[i]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		curTick = nbt.getInteger("curTick");

		boolean found = false;
		for (int i = 0; i < distData.length; i++) {
			int d = nbt.getInteger("distData" + i);
			if (d > 0) found = true;
			distData[i] = d;
		}

		if (!found) {
			for (int i = 0; i < distData.length; i++) {
				distData[i] = 1;
			}
		}
	}

}
