package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.EntityList;
import net.minecraft.src.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TMIItemInfo
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private static final Map fallbackNames = new HashMap();
    private static final Map maxDamageExceptions = new HashMap();
    private static final Set hideItems = new HashSet();
    private static final Set showItems = new HashSet();
    private static final Set tools = new HashSet();
    private static final Set nonUnlimited = new HashSet();
    public static List potionValues = new ArrayList();
    public static int itemOffset = addItemOffset(0);
    public static final Set excludedSpawnerIds = new HashSet();

    public static void hideItem(int var0)
    {
        hideItems.add(Integer.valueOf(var0));
    }

    public static boolean isHidden(int var0)
    {
        return hideItems.contains(Integer.valueOf(var0));
    }

    public static void addFallbackName(int var0, int var1, String var2)
    {
        fallbackNames.put(Integer.valueOf(packItemIDAndDamage(var0, var1)), var2);
    }

    public static boolean hasFallbackName(int var0, int var1)
    {
        return fallbackNames.containsKey(Integer.valueOf(packItemIDAndDamage(var0, var1)));
    }

    public static String getFallbackName(int var0, int var1)
    {
        int var2 = packItemIDAndDamage(var0, var1);
        return fallbackNames.containsKey(Integer.valueOf(var2)) ? (String)fallbackNames.get(Integer.valueOf(var2)) : "Unnamed";
    }

    public static void setMaxDamageException(int var0, int var1)
    {
        maxDamageExceptions.put(Integer.valueOf(var0), Integer.valueOf(var1));
    }

    public static int getMaxDamageException(int var0)
    {
        return maxDamageExceptions.containsKey(Integer.valueOf(var0)) ? ((Integer)maxDamageExceptions.get(Integer.valueOf(var0))).intValue() : 0;
    }

    public static int packItemIDAndDamage(int var0, int var1)
    {
        return (var0 << 8) + var1;
    }

    public static int unpackItemID(int var0)
    {
        return var0 >> 8;
    }

    public static int unpackDamage(int var0)
    {
        return var0 & 255;
    }

    public static void showItemWithDamage(int var0, int var1)
    {
        showItems.add(Integer.valueOf(packItemIDAndDamage(var0, var1)));
    }

    public static void showItemWithDamageRange(int var0, int var1, int var2)
    {
        for (int var3 = var1; var3 <= var2; ++var3)
        {
            showItemWithDamage(var0, var3);
        }
    }

    public static boolean isShown(int var0, int var1)
    {
        return showItems.contains(Integer.valueOf(packItemIDAndDamage(var0, var1)));
    }

    public static int addItemOffset(int var0)
    {
        return var0 + Item.shovelIron.itemID;
    }

    static
    {
        int[] var0 = new int[] {0, 16, 32, 64, 8192, 8193, 8194, 8195, 8196, 8197, 8198, 8200, 8201, 8202, 8204, 8206, 8225, 8226, 8228, 8229, 8233, 8236, 8257, 8258, 8259, 8260, 8262, 8264, 8265, 8266, 8270, 16385, 16386, 16387, 16388, 16389, 16390, 16392, 16393, 16394, 16396, 16398, 16417, 16418, 16420, 16421, 16425, 16428, 16449, 16450, 16451, 16452, 16454, 16456, 16457, 16458, 16462};
        int[] var1 = var0;
        int var2 = var0.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            int var4 = var1[var3];
            potionValues.add(Integer.valueOf(var4));
        }

        showItemWithDamageRange(6, 0, 3);
        showItemWithDamageRange(18, 0, 3);
        showItemWithDamageRange(43, 0, 0);
        showItemWithDamageRange(addItemOffset(66), 0, 1);
        showItemWithDamageRange(78, 0, 7);
        showItemWithDamageRange(59, 7, 7);
        showItemWithDamageRange(104, 7, 7);
        showItemWithDamageRange(105, 7, 7);
        showItemWithDamageRange(115, 7, 7);
        addFallbackName(104, 0, "Pumpkin Stem");
        addFallbackName(105, 0, "Melon Stem");
        addFallbackName(104, 7, "Pumpkin Stem");
        addFallbackName(105, 7, "Melon Stem");
        addFallbackName(119, 0, "End Portal");
        addFallbackName(52, 0, "Pig Spawner");
        Iterator var5 = TMIPrivateFields.getSpawnerEntityIdSet().iterator();

        while (var5.hasNext())
        {
            var2 = ((Integer)var5.next()).intValue();
            addFallbackName(52, var2, EntityList.getStringFromID(var2) + " Spawner");
        }

        excludedSpawnerIds.add(Integer.valueOf(1));
        excludedSpawnerIds.add(Integer.valueOf(9));
        excludedSpawnerIds.add(Integer.valueOf(12));
        excludedSpawnerIds.add(Integer.valueOf(13));
        excludedSpawnerIds.add(Integer.valueOf(15));
        excludedSpawnerIds.add(Integer.valueOf(18));
        excludedSpawnerIds.add(Integer.valueOf(19));
        excludedSpawnerIds.add(Integer.valueOf(21));
        excludedSpawnerIds.add(Integer.valueOf(22));
        excludedSpawnerIds.add(Integer.valueOf(53));
        excludedSpawnerIds.add(Integer.valueOf(48));
        excludedSpawnerIds.add(Integer.valueOf(49));
        excludedSpawnerIds.add(Integer.valueOf(200));
        hideItem(26);
        hideItem(34);
        hideItem(36);
        hideItem(55);
        hideItem(63);
        hideItem(64);
        hideItem(68);
        hideItem(71);
        hideItem(74);
        hideItem(83);
        hideItem(93);
        hideItem(94);
        hideItem(95);
        hideItem(117);
        hideItem(118);
        hideItem(127);
        hideItem(132);
        hideItem(141);
        hideItem(142);
        hideItem(addItemOffset(117));
        hideItem(addItemOffset(147));
    }
}
