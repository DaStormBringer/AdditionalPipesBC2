package buildcraft.additionalpipes.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Class to hold AdditionalPipes' sounds.  Currently just the Dog Deaggravator bell.
 * @author jamie
 *
 */
public class APSounds
{
	public static SoundEvent dogDeaggravatorBell;
	
	public static void init()
	{
		ResourceLocation bellResourceLoc = new ResourceLocation("additionalpipes:bellRing");
		dogDeaggravatorBell = new SoundEvent(bellResourceLoc);
		SoundEvent.REGISTRY.register(0, bellResourceLoc, dogDeaggravatorBell);
	}
	
}
