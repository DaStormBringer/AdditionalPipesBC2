package net.minecraft.src.buildcraft.additionalpipes.logic;

import net.minecraft.src.BuildCraftCore;
import net.minecraft.src.BuildCraftTransport;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_AdditionalPipes;
import net.minecraft.src.buildcraft.additionalpipes.gui.GuiHandler;
import net.minecraft.src.buildcraft.api.TileNetworkData;
import net.minecraft.src.buildcraft.transport.Pipe;
import net.minecraft.src.buildcraft.transport.PipeLogic;
import net.minecraft.src.buildcraft.transport.TileGenericPipe;

public class PipeLogicTeleport extends PipeLogic {

	@TileNetworkData public int freq = 0;
	@TileNetworkData public boolean canReceive = false;
	@TileNetworkData public String owner = "";
	
	protected int guiId;
	
	public PipeLogicTeleport(int guiId) {
		super();
		this.guiId = guiId;
	}
	
	@Override
    public boolean blockActivated(EntityPlayer entityplayer) {
        
        if (owner == null || owner.equalsIgnoreCase("")) {
            owner = entityplayer.username;
        }
        
        ItemStack equippedItem = entityplayer.getCurrentEquippedItem();
        
        if (equippedItem != null) {
            
            if (mod_AdditionalPipes.isPipe(equippedItem.getItem()))  {
                return false;
            }

            if (equippedItem.getItem() == BuildCraftCore.wrenchItem && !mod_AdditionalPipes.wrenchOpensGui) {
                return false;
            }
        }

        entityplayer.openGui(mod_AdditionalPipes.instance, guiId, 
                container.worldObj, container.xCoord, container.yCoord, container.zCoord);

        return true;
    }
	
	@Override
    public boolean isPipeConnected(TileEntity tile) {
		
        Pipe pipe = null;

        if (tile instanceof TileGenericPipe) {
            pipe = ((TileGenericPipe) tile).pipe;
        }

        if (BuildCraftTransport.alwaysConnectPipes) {
            return super.isPipeConnected(tile);
        }
        else {
        	
        	if (pipe == null) {
        		return false;
        	}
        	
        	if (this.container.pipe.getClass().equals(pipe.getClass()) && super.isPipeConnected(tile)) {
        		return true;
        	}
        	
        	
        	return true;
        }
    }
	
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		
		nbttagcompound.setInteger("freq", freq);
		nbttagcompound.setBoolean("canReceive", canReceive);
		nbttagcompound.setString("owner", owner);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		
		freq = nbttagcompound.getInteger("freq");
		canReceive = nbttagcompound.getBoolean("canReceive");
		owner = nbttagcompound.getString("owner");
	}

}
