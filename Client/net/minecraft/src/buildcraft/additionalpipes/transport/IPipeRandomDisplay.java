//   Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package net.minecraft.src.buildcraft.additionalpipes.transport;

import java.util.Random;

import net.minecraft.src.World;

public interface IPipeRandomDisplay {

    public abstract void randomDisplayTick(World world, int i, int j, int k, Random random);
}
