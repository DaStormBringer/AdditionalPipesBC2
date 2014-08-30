package buildcraft.additionalpipes.network;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.message.MessageDistPipe;
import buildcraft.additionalpipes.network.message.MessageTelePipe;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AdditionalPipes.MODID.toLowerCase());

    public static void init()
    {
        INSTANCE.registerMessage(MessageDistPipe.class, MessageDistPipe.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageTelePipe.class, MessageTelePipe.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MessageTileEntityAludel.class, MessageTileEntityAludel.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(MessageTileEntityGlassBell.class, MessageTileEntityGlassBell.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(MessageKeyPressed.class, MessageKeyPressed.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MessageDistPipe.class, MessageDistPipe.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(MessageTelePipe.class, MessageTelePipe.class, 6, Side.SERVER);
        INSTANCE.registerMessage(MessageTileAlchemicalChest.class, MessageTileAlchemicalChest.class, 7, Side.CLIENT);
        INSTANCE.registerMessage(MessageTileCondenser.class, MessageTileCondenser.class, 8, Side.CLIENT);

        
    }
}
