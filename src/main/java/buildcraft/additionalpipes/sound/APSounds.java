package buildcraft.additionalpipes.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Class to hold AdditionalPipes' sounds.  Currently just the Dog Deaggravator bell.
 * @author jamie
 *
 */
public class APSounds
{
	public static SoundEvent dogDeaggravatorBell;
	
	public static void register(IForgeRegistry<SoundEvent> registry)
	{
		ResourceLocation bellResourceLoc = new ResourceLocation("additionalpipes:bellRing");
		dogDeaggravatorBell = new SoundEvent(bellResourceLoc);
		dogDeaggravatorBell.setRegistryName(bellResourceLoc);
		registry.register(dogDeaggravatorBell);
	}
	
}
