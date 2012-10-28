package buildcraft.additionalpipes.gui;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;

public class ContainerTeleportPipe extends Container {

    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return true;
    }
    @Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }
}
