package me.ayunami2000.ayuncraft.tmi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Container;

public class TMICompatibility
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private static boolean prevZanEnabled = false;

    public static void disableCompetingMods()
    {
        prevZanEnabled = setZanMinimapEnabled(false);
    }

    public static void restoreCompetingMods()
    {
        setZanMinimapEnabled(prevZanEnabled);
    }

    public static boolean setZanMinimapEnabled(boolean var0)
    {
        try
        {
            boolean var1 = true;
            Class var2 = Class.forName("mod_ZanMinimap");
            Field var3 = var2.getField("instance");
            Object var4 = var3.get((Object)null);
            Field[] var5 = var2.getDeclaredFields();
            Field[] var6 = var5;
            int var7 = var5.length;

            for (int var8 = 0; var8 < var7; ++var8)
            {
                Field var9 = var6[var8];

                if (var9.getName().equals("hide"))
                {
                    var9.setAccessible(true);
                    var1 = !var9.getBoolean(var4);
                    var9.setBoolean(var4, !var0);
                }
            }

            return var1;
        }
        catch (ClassNotFoundException var10)
        {
            ;
        }
        catch (IllegalAccessException var11)
        {
            System.out.println("IllegalAccessException in setZanMinimapEnabled.");
        }
        catch (NoSuchFieldException var12)
        {
            System.out.println("NoSuchFieldException in setZanMinimapEnabled.");
        }

        return true;
    }

    public static boolean callConvenientInventoryHandler(int var0, int var1, boolean var2, Minecraft var3, Container var4)
    {
        if (TMIUtils.isCreativeMode())
        {
            return false;
        }
        else
        {
            try
            {
                Class var5 = Class.forName("ConvenientInventory");
                Class[] var6 = new Class[] {Integer.TYPE, Integer.TYPE, Boolean.TYPE, Minecraft.class, Container.class};
                Method var7 = var5.getMethod("mod_convenientInventory_handleClickOnSlot", var6);
                Object[] var8 = new Object[] {Integer.valueOf(var0), Integer.valueOf(var1), Boolean.valueOf(var2), var3, var4};
                var7.invoke((Object)null, var8);
                return true;
            }
            catch (ClassNotFoundException var9)
            {
                return false;
            }
            catch (NoSuchMethodException var10)
            {
                return false;
            }
            catch (InvocationTargetException var11)
            {
                System.out.println("callConvenientInventoryHandler: " + var11.getCause());
                return false;
            }
            catch (IllegalAccessException var12)
            {
                System.out.println("callConvenientInventoryHandler: " + var12);
                return false;
            }
        }
    }
}
