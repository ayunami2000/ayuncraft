package me.ayunami2000.ayuncraft.tmi;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.src.*;
import net.lax1dude.eaglercraft.EaglerAdapter;

public class TMIPrivateFields
{
    public static Field lwjglMouseDWheel;
    public static Field lwjglMouseEventDWheel;
    public static Field textFieldX;
    public static Field textFieldY;
    public static Field textFieldWidth;
    public static Field textFieldHeight;
    public static Field threadTarget;
    public static Field creativeTab;
    public static Field gameMode;
    public static Method setCreativeTab;
    public static Field creativeSlot;
    public static Field blockSpawner;
    public static Field blockSnow;
    public static Field entityIdClassMap;
    public static Field creativeSearchBox;
    public static Method setBlockBounds;
    public static Field spawnerProps;
    public static Field stackDamage;
    public static Method readTagList;
    public static Method writeTagList;
    public static Method drawGradientRect;

    public static void printFieldsAndMethods(Class var0)
    {
        Field[] var1 = var0.getDeclaredFields();
        int var2 = var1.length;
        int var3;

        for (var3 = 0; var3 < var2; ++var3)
        {
            Field var4 = var1[var3];
            System.out.println(var0.getName() + " field " + var4.getName());
        }

        Method[] var5 = var0.getDeclaredMethods();
        var2 = var5.length;

        for (var3 = 0; var3 < var2; ++var3)
        {
            Method var6 = var5[var3];
            System.out.println(var0.getName() + " method " + var6.getName());
        }
    }

    public static Field getPrivateField(Class var0, String var1, String var2)
    {
        Field var3 = null;

        try
        {
            var3 = var0.getDeclaredField(var1);
        }
        catch (NoSuchFieldException var7)
        {
            try
            {
                var3 = var0.getDeclaredField(var2);
            }
            catch (NoSuchFieldException var6)
            {
                System.out.println("[TMI] Disabling TMI, cannot access " + var0.getName() + "." + var1 + ", " + var0.getName() + "." + var2);
                TMIConfig.getInstance().setEnabled(false);
            }
        }

        if (var3 != null)
        {
            unsetFinalPrivate(var3);
        }

        return var3;
    }

    public static Method getPrivateMethod(Class var0, String var1, String var2, Class[] var3)
    {
        Method var4 = null;

        try
        {
            var4 = var0.getDeclaredMethod(var1, var3);
        }
        catch (NoSuchMethodException var8)
        {
            try
            {
                var4 = var0.getDeclaredMethod(var2, var3);
            }
            catch (NoSuchMethodException var7)
            {
                System.out.println("[TMI] Disabling TMI, cannot access " + var0.getName() + "." + var1 + ", " + var0.getName() + "." + var2);
                TMIConfig.getInstance().setEnabled(false);
            }
        }

        if (var4 != null)
        {
            var4.setAccessible(true);
        }

        return var4;
    }

    public static SortedSet getSpawnerEntityIdSet()
    {
        try
        {
            TreeSet var0 = new TreeSet(((Map)entityIdClassMap.get((Object)null)).keySet());
            Iterator var1 = TMIItemInfo.excludedSpawnerIds.iterator();

            while (var1.hasNext())
            {
                Integer var2 = (Integer)var1.next();

                if (var0.contains(var2))
                {
                    var0.remove(var2);
                }
            }

            return var0;
        }
        catch (Exception var3)
        {
            System.out.println(var3);
            TreeSet retval=new TreeSet<>();
            retval.add(0);
            return retval;
        }
    }

    public static void unsetFinalPrivate(Class var0, String var1)
    {
        try
        {
            unsetFinalPrivate(var0.getDeclaredField(var1));
        }
        catch (NoSuchFieldException var3)
        {
            System.out.println(var3);
        }
    }

    public static void unsetFinalPrivate(Field var0)
    {
        try
        {
            var0.setAccessible(true);
            Field var1 = Field.class.getDeclaredField("modifiers");
            var1.setAccessible(true);
            var1.setInt(var0, var0.getModifiers() & -17);
        }
        catch (Exception var2)
        {
            System.out.println(var2);
        }
    }

    static
    {
        try
        {
            threadTarget = Thread.class.getDeclaredField("target");
            threadTarget.setAccessible(true);
            lwjglMouseEventDWheel = EaglerAdapter.class.getDeclaredField("event_dwheel");
            unsetFinalPrivate(lwjglMouseEventDWheel);
            lwjglMouseDWheel = EaglerAdapter.class.getDeclaredField("dwheel");
            unsetFinalPrivate(lwjglMouseDWheel);
            textFieldX = getPrivateField(GuiTextField.class, "b", "xPos");
            textFieldY = getPrivateField(GuiTextField.class, "c", "yPos");
            textFieldWidth = getPrivateField(GuiTextField.class, "d", "width");
            textFieldHeight = getPrivateField(GuiTextField.class, "e", "height");
            creativeTab = getPrivateField(GuiContainerCreative.class, "s", "selectedTabIndex");
            setCreativeTab = getPrivateMethod(GuiContainerCreative.class, "b", "setCurrentCreativeTab", new Class[] {CreativeTabs.class});
            creativeSearchBox = getPrivateField(GuiContainerCreative.class, "w", "searchField");
            gameMode = getPrivateField(PlayerControllerMP.class, "k", "currentGameType");
            creativeSlot = getPrivateField(SlotCreativeInventory.class, "b", "theSlot");
            stackDamage = getPrivateField(ItemStack.class, "e", "itemDamage");
            blockSpawner = getPrivateField(Block.class, "aw", "mobSpawner");
            blockSnow = getPrivateField(Block.class, "aW", "snow");
            entityIdClassMap = getPrivateField(EntityList.class, "d", "IDtoClassMapping");
            setBlockBounds = getPrivateMethod(Block.class, "a", "setBlockBounds", new Class[] {Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE});
            readTagList = getPrivateMethod(NBTTagList.class, "a", "load", new Class[] {DataInput.class});
            writeTagList = getPrivateMethod(NBTTagList.class, "a", "write", new Class[] {DataOutput.class});
            drawGradientRect = getPrivateMethod(Gui.class, "a", "drawGradientRect", new Class[] {Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE});
        }
        catch (Exception var1)
        {
            System.out.println("[TMI] Critical error in TMIPrivateFields");
            var1.printStackTrace();
        }
    }
}
