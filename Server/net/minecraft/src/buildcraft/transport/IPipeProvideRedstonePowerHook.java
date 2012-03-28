// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package net.minecraft.src.buildcraft.transport;

import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

public interface IPipeProvideRedstonePowerHook {

    public abstract boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l);

    public abstract boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l);
}
