package buildcraft.additionalpipes.network;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.message.MessageAdvWoodPipe;
import buildcraft.additionalpipes.network.message.MessageChunkloadData;
import buildcraft.additionalpipes.network.message.MessageChunkloadRequest;
import buildcraft.additionalpipes.network.message.MessageDistPipe;
import buildcraft.additionalpipes.network.message.MessageJeweledPipeOptionsClient;
import buildcraft.additionalpipes.network.message.MessageJeweledPipeOptionsServer;
import buildcraft.additionalpipes.network.message.MessagePriorityPipe;
import buildcraft.additionalpipes.network.message.MessageTelePipeData;
import buildcraft.additionalpipes.network.message.MessageTelePipeUpdate;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AdditionalPipes.MODID.toLowerCase());

    public static void init()
    {
        INSTANCE.registerMessage(MessageDistPipe.class, MessageDistPipe.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageTelePipeUpdate.class, MessageTelePipeUpdate.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessageAdvWoodPipe.class, MessageAdvWoodPipe.class, 2, Side.SERVER);
        INSTANCE.registerMessage(MessageTelePipeData.class, MessageTelePipeData.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(MessageChunkloadRequest.class, MessageChunkloadRequest.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MessageChunkloadData.class, MessageChunkloadData.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(MessagePriorityPipe.class, MessagePriorityPipe.class, 6, Side.SERVER);
        INSTANCE.registerMessage(MessageJeweledPipeOptionsServer.class, MessageJeweledPipeOptionsServer.class, 7, Side.SERVER);
        INSTANCE.registerMessage(MessageJeweledPipeOptionsClient.class, MessageJeweledPipeOptionsClient.class, 8, Side.CLIENT);

    }
}
