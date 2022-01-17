package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.*;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class TMIEnchanting
{
    public static ItemStack currentItem;
    public static LinkedHashMap currentEnchantmentLevels = new LinkedHashMap();

    public static void setItem(ItemStack var0)
    {
        currentItem = TMIUtils.copyStack(var0);

        if (currentItem.stackTagCompound != null && currentItem.stackTagCompound.hasKey("ench"))
        {
            currentItem.stackTagCompound.removeTag("ench");
        }

        Item var1 = Item.itemsList[currentItem.itemID];
        LinkedHashMap var2 = new LinkedHashMap(currentEnchantmentLevels);
        currentEnchantmentLevels.clear();
        Iterator var3 = var2.keySet().iterator();
        Enchantment var4;

        while (var3.hasNext())
        {
            var4 = (Enchantment)var3.next();

            if (var4 != null)
            {
                if (var4.type == null)
                {
                    System.out.println("ERROR: enchantment lacks type (" + var4 + ")");
                }
                else if (var4.type.canEnchantItem(var1))
                {
                    currentEnchantmentLevels.put(var4, Integer.valueOf(0));
                }
            }
        }

        var3 = var2.keySet().iterator();

        while (var3.hasNext())
        {
            var4 = (Enchantment)var3.next();

            if (var4 != null)
            {
                if (var4.type == null)
                {
                    System.out.println("ERROR: enchantment lacks type (" + var4 + ")");
                }
                else if (!var4.type.canEnchantItem(var1))
                {
                    currentEnchantmentLevels.put(var4, Integer.valueOf(0));
                }
            }
        }
    }

    public static ItemStack createStack()
    {
        ItemStack var0 = TMIUtils.copyStack(currentItem);

        if (var0.itemID == TMIItemInfo.addItemOffset(147))
        {
            NBTTagList var1 = new NBTTagList();

            for (Iterator var2 = currentEnchantmentLevels.keySet().iterator(); var2.hasNext(); var0.stackTagCompound.setTag("StoredEnchantments", var1))
            {
                Enchantment var3 = (Enchantment)var2.next();
                int var4 = ((Integer)currentEnchantmentLevels.get(var3)).intValue();

                if (var4 > 0)
                {
                    NBTTagCompound var5 = new NBTTagCompound();
                    var5.setShort("id", (short)var3.effectId);
                    var5.setShort("lvl", (short)var4);
                    var1.appendTag(var5);
                }

                if (var0.stackTagCompound == null)
                {
                    var0.stackTagCompound = new NBTTagCompound();
                }
            }
        }
        else
        {
            Iterator var6 = currentEnchantmentLevels.keySet().iterator();

            while (var6.hasNext())
            {
                Enchantment var7 = (Enchantment)var6.next();
                int var8 = ((Integer)currentEnchantmentLevels.get(var7)).intValue();

                if (var8 > 0)
                {
                    TMIUtils.addEnchantment(var0, var7.effectId, var8);
                }
            }
        }

        return var0;
    }

    public static void adjustEnchantmentLevel(Enchantment var0, int var1)
    {
        int var2 = ((Integer)currentEnchantmentLevels.get(var0)).intValue() + var1;

        if (var2 < 0)
        {
            var2 = 0;
        }
        else if (var2 > 10)
        {
            var2 = 10;
        }

        currentEnchantmentLevels.put(var0, Integer.valueOf(var2));
    }

    public static int getEnchantmentLevel(Enchantment var0)
    {
        return ((Integer)currentEnchantmentLevels.get(var0)).intValue();
    }

    static
    {
        Enchantment[] var0 = Enchantment.enchantmentsList;
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2)
        {
            Enchantment var3 = var0[var2];

            if (var3 != null)
            {
                currentEnchantmentLevels.put(var3, Integer.valueOf(0));
            }
        }

        setItem(new ItemStack(TMIItemInfo.addItemOffset(22), 1, 0));
    }
}
