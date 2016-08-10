package buildcraft.additionalpipes.utils;


import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import buildcraft.additionalpipes.APConfiguration;
import buildcraft.additionalpipes.AdditionalPipes;

public class Log
{
    public static void log(Level logLevel, Object object)
    {
        FMLLog.log(AdditionalPipes.NAME, logLevel, String.valueOf(object));
    }

    public static void all(Object object)
    {
        log(Level.ALL, object);
    }

    public static void debug(Object object)
    {
    	if(APConfiguration.enableDebugLog)
    	{
    		log(Level.INFO, object);
    	}
    }

    public static void error(Object object)
    {
        log(Level.ERROR, object);
    }
    
    public static void unexpected(Object object)
    {
        log(Level.ERROR, "...what? " + object);
    }

    public static void fatal(Object object)
    {
        log(Level.FATAL, object);
    }

    public static void info(Object object)
    {
        log(Level.INFO, object);
    }

    public static void off(Object object)
    {
        log(Level.OFF, object);
    }

    public static void trace(Object object)
    {
        log(Level.TRACE, object);
    }

    public static void warn(Object object)
    {
        log(Level.WARN, object);
    }
}
