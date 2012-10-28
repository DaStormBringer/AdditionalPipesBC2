package buildcraft.additionalpipes.transport;

import java.util.Random;

import net.minecraft.src.World;

public interface IPipeRandomDisplay {

	public abstract void randomDisplayTick(World world, int i, int j, int k, Random random);
}
