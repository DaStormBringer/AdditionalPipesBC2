package buildcraft.additionalpipes;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import buildcraft.additionalpipes.chunkloader.TileChunkLoader;
import buildcraft.api.core.LaserKind;
import buildcraft.core.Box;
import buildcraft.core.network.BuildCraftPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

public class MutiPlayerProxy {
	public boolean NeedsLoad = true;
	public File WorldDir;
	public boolean HDSet = false;
	public boolean HDFound = false;
	public boolean OFFound = false;

	public boolean isOnServer(World world) {
		return !world.isRemote;
	}

	public void registerRendering() {

	}

	public void registerPipeRendering(Item res) {		
	}

}
