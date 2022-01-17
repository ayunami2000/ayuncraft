package me.ayunami2000.ayuncraft.tmi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.GuiContainerCreative;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.lwjgl.input.Keyboard;

public class TMIConfig
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    public static final String VERSION = "1.5.2 2013-04-25";
    public static final int NUM_SAVES = 7;
    public static final int INVENTORY_SIZE = 44;
    public static boolean isModloaderEnabled = false;
    private static TMIConfig instance;
    private static List items = new ArrayList();
    private static List enchantableItems = new ArrayList();
    private static List favorites = new ArrayList();
    private static HashSet toolIds = new HashSet();
    private static HashSet nonUnlimitedIds;
    private Map settings = new LinkedHashMap();
    private static ItemStack[][] states;
    public static boolean[] statesSaved;

    public TMIConfig()
    {
        this.settings.put("enable", "true");
        this.settings.put("enablemp", "true");
        this.settings.put("itemsonly", "false");
        this.settings.put("give-command", "/give {0} {1} {2} {3}");
        this.settings.put("key", "o");

        for (int var1 = 0; var1 < this.getNumSaves(); ++var1)
        {
            this.settings.put("save-name" + (var1 + 1), "" + (var1 + 1));
        }

        this.settings.put("replace-items", "true");
        instance = this;
    }

    public static boolean isMultiplayer()
    {
        ThreadGroup var0 = Thread.currentThread().getThreadGroup();
        Thread[] var1 = new Thread[var0.activeCount()];
        var0.enumerate(var1);

        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            if (var1[var2] != null)
            {
                String var3 = var1[var2].getName();

                if (var3 != null && var3.equals("Client read thread"))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static TMIConfig getInstance()
    {
        if (instance == null)
        {
            new TMIConfig();
        }

        return instance;
    }

    public Map getSettings()
    {
        return this.settings;
    }

    public List getItems()
    {
        return items;
    }

    public List getEnchantableItems()
    {
        return enchantableItems;
    }

    public int getHotkey()
    {
        String var1 = (String)this.settings.get("key");
        boolean var2 = false;
        int var3 = EaglerAdapter.getKeyIndex(var1.toUpperCase());

        if (var3 == 0)
        {
            var3 = 24;
        }

        return var3;
    }

    public int getNumSaves()
    {
        return 7;
    }

    public boolean isStateSaved(int var1)
    {
        return statesSaved[var1];
    }

    public ItemStack[] getState(int var1)
    {
        return states[var1];
    }

    public boolean getBooleanSetting(String var1)
    {
        return Boolean.parseBoolean((String)this.settings.get(var1));
    }

    public boolean isEnabled()
    {
        return isMultiplayer() && this.getBooleanSetting("enablemp") || !isMultiplayer() && this.getBooleanSetting("enable");
    }

    public void toggleEnabled()
    {
        String var1 = isMultiplayer() ? "enablemp" : "enable";
        this.settings.put(var1, Boolean.toString(!this.getBooleanSetting(var1)));
    }

    public void setEnabled(boolean var1)
    {
        String var2 = isMultiplayer() ? "enablemp" : "enable";
        this.settings.put(var2, Boolean.toString(var1));
    }

    public static boolean isTool(Item var0)
    {
        return toolIds.contains(Integer.valueOf(var0.itemID));
    }

    public static boolean canItemBeUnlimited(Item var0)
    {
        return !nonUnlimitedIds.contains(Integer.valueOf(var0.itemID));
    }

    public boolean areDamageVariantsShown()
    {
        if (isMultiplayer())
        {
            String var1 = (String)this.getSettings().get("give-command");
            return var1.contains("{3}");
        }
        else
        {
            return true;
        }
    }

    public void clearState(int var1)
    {
        for (int var2 = 0; var2 < 44; ++var2)
        {
            states[var1][var2] = null;
            statesSaved[var1] = false;
        }

        this.settings.put("save" + (var1 + 1), "");
    }

    public void loadState(int var1)
    {
        if (statesSaved[var1])
        {
            try
            {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative)
                {
                    TMIPrivateFields.setCreativeTab.invoke((GuiContainerCreative)Minecraft.getMinecraft().currentScreen, new Object[] {CreativeTabs.tabInventory});
                }
            }
            catch (Exception var5)
            {
                System.out.println(var5);
            }

            ItemStack[] var2 = TMIUtils.getPlayer().inventory.mainInventory;
            ItemStack[] var3 = TMIUtils.getPlayer().inventory.armorInventory;
            int var4;

            for (var4 = 0; var4 < 4; ++var4)
            {
                var3[var4] = TMIUtils.copyStack(states[var1][var4 + 4]);
            }

            for (var4 = 0; var4 < 27; ++var4)
            {
                var2[var4 + 9] = TMIUtils.copyStack(states[var1][var4 + 8]);
            }

            for (var4 = 0; var4 < 9; ++var4)
            {
                var2[var4] = TMIUtils.copyStack(states[var1][var4 + 8 + 27]);
            }
        }
    }

    public void saveState(int var1)
    {
        ItemStack[] var2 = TMIUtils.getPlayer().inventory.mainInventory;
        ItemStack[] var3 = TMIUtils.getPlayer().inventory.armorInventory;
        int var4;

        for (var4 = 0; var4 < 4; ++var4)
        {
            states[var1][var4 + 4] = TMIUtils.copyStack(var3[var4]);
        }

        for (var4 = 0; var4 < 27; ++var4)
        {
            states[var1][var4 + 8] = TMIUtils.copyStack(var2[var4 + 9]);
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            states[var1][var4 + 8 + 27] = TMIUtils.copyStack(var2[var4]);
        }

        this.settings.put("save" + (var1 + 1), this.encodeState(var1));
        statesSaved[var1] = true;
    }

    public String encodeState(int var1)
    {
        StringBuilder var2 = new StringBuilder();

        for (int var3 = 0; var3 < 44; ++var3)
        {
            if (states[var1][var3] != null)
            {
                var2.append(states[var1][var3].itemID);
                var2.append(":");
                var2.append(states[var1][var3].stackSize);
                var2.append(":");
                var2.append(states[var1][var3].getItemDamageForDisplay());
                List var4 = TMIUtils.getEnchantments(states[var1][var3]);
                Iterator var5 = var4.iterator();

                while (var5.hasNext())
                {
                    int[] var6 = (int[])var5.next();
                    int var7 = var6[0];
                    int var8 = var6[1];
                    var2.append(":" + var7 + ":" + var8);
                }
            }

            var2.append(",");
        }

        return var2.toString();
    }

    public void decodeState(int var1, String var2)
    {
        if (var2.trim().equals(""))
        {
            statesSaved[var1] = false;
        }
        else
        {
            String[] var3 = var2.split(",", 0);

            for (int var4 = 0; var4 < var3.length && var4 < states[var1].length; ++var4)
            {
                String[] var5 = var3[var4].split(":");

                if (var5.length >= 3)
                {
                    try
                    {
                        states[var1][var4] = new ItemStack(Integer.parseInt(var5[0]), Integer.parseInt(var5[1]), Integer.parseInt(var5[2]));

                        for (int var6 = 3; var6 < var5.length - 1; var6 += 2)
                        {
                            int var7 = Integer.parseInt(var5[var6]);
                            int var8 = Integer.parseInt(var5[var6 + 1]);
                            TMIUtils.addEnchantment(states[var1][var4], var7, var8);
                        }
                    }
                    catch (Exception var9)
                    {
                        System.out.println(var9);
                    }
                }
            }

            statesSaved[var1] = true;
        }
    }

    public List getFavorites()
    {
        return favorites;
    }

    public void decodeFavorites()
    {
        favorites.clear();
        String[] var1 = ((String)this.settings.get("favorites")).trim().split(",", 0);
        String[] var2 = var1;
        int var3 = var1.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            String var5 = var2[var4];
            String[] var6 = var5.split(":");

            if (var6.length >= 2)
            {
                try
                {
                    int var7 = Integer.parseInt(var6[0]);
                    int var8 = Integer.parseInt(var6[1]);
                    ItemStack var9 = new ItemStack(var7, TMIUtils.maxStackSize(var7), var8);

                    for (int var10 = 2; var10 < var6.length - 1; var10 += 2)
                    {
                        int var11 = Integer.parseInt(var6[var10]);
                        int var12 = Integer.parseInt(var6[var10 + 1]);
                        TMIUtils.addEnchantment(var9, var11, var12);
                    }

                    favorites.add(var9);
                }
                catch (Exception var13)
                {
                    System.out.println(var13);
                }
            }
        }
    }

    public void encodeFavorites()
    {
        StringBuilder var1 = new StringBuilder();
        Iterator var2 = favorites.iterator();

        while (var2.hasNext())
        {
            ItemStack var3 = (ItemStack)var2.next();
            var1.append(var3.itemID);
            var1.append(":");
            var1.append(var3.getItemDamageForDisplay());
            List var4 = TMIUtils.getEnchantments(var3);
            Iterator var5 = var4.iterator();

            while (var5.hasNext())
            {
                int[] var6 = (int[])var5.next();
                int var7 = var6[0];
                int var8 = var6[1];
                var1.append(":" + var7 + ":" + var8);
            }

            var1.append(",");
        }

        this.settings.put("favorites", var1.toString());
    }

    public static boolean canDelete()
    {
        return !isMultiplayer();
    }

    public static boolean canChangeWeather()
    {
        return !getInstance().getBooleanSetting("itemsonly");
    }

    public static boolean canChangeCreativeMode()
    {
        return !getInstance().getBooleanSetting("itemsonly");
    }

    public static boolean canChangeTime()
    {
        return !getInstance().getBooleanSetting("itemsonly");
    }

    public static boolean canChangeHealth()
    {
        return !isMultiplayer() && !getInstance().getBooleanSetting("itemsonly");
    }

    public static boolean canRestoreSaves()
    {
        return !isMultiplayer();
    }

    public static boolean canChangeDifficulty()
    {
        return !isMultiplayer();
    }

    static
    {
        int var0;

        for (var0 = 0; var0 <= 3; ++var0)
        {
            toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(var0)));
        }

        for (var0 = 11; var0 <= 23; ++var0)
        {
            toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(var0)));
        }

        for (var0 = 27; var0 <= 30; ++var0)
        {
            toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(var0)));
        }

        for (var0 = 34; var0 <= 38; ++var0)
        {
            toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(var0)));
        }

        for (var0 = 42; var0 <= 61; ++var0)
        {
            toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(var0)));
        }

        toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(103)));
        toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(90)));
        toolIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(5)));
        nonUnlimitedIds = new HashSet();
        nonUnlimitedIds.add(Integer.valueOf(TMIItemInfo.addItemOffset(102)));
        states = new ItemStack[7][44];
        statesSaved = new boolean[7];
    }
}
