package me.ayunami2000.ayuncraft.tmi;

import java.awt.Color;
import java.io.*;

import me.ayunami2000.ayuncraft.File;

import java.lang.reflect.Field;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.regex.Pattern;

import net.lax1dude.eaglercraft.LocalStorageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.lax1dude.eaglercraft.EaglerAdapter;

public class TMIUtils
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    public static final String CONFIG_FILENAME = "TooManyItems.txt";
    public static final String NBT_FILENAME = "TMI.nbt";
    public static final int SPAWNER_ID = 52;
    public static final int MODE_SURVIVAL = 0;
    public static final int MODE_CREATIVE = 1;
    public static final int MODE_ADVENTURE = 2;
    public static final List EMPTY_NAME = Arrays.asList("");
    public static boolean haveReplacedItems = false;
    public static final Random random = new Random();
    private static Map originalItems = new HashMap();
    public static List availableItems = new ArrayList();

    public static File configFile()
    {
        return new File("TooManyItems.txt");
    }

    public static File nbtFile()
    {
        return new File("TMI.nbt");
    }

    public static void loadPreferences(TMIConfig var0)
    {
        try
        {
            Map var1 = var0.getSettings();
            File var2 = configFile();

            if (var2.exists())
            {
                if (!nbtFile().exists())
                {
                    var1.put("favorites", "");

                    for (int var3 = 0; var3 < 7; ++var3)
                    {
                        var1.put("save" + (var3 + 1), "");
                    }
                }

                BufferedReader var7 = new BufferedReader(new StringReader(LocalStorageManager.gameSettingsStorage.getString(var2.getFileName())));
                String var4;

                while ((var4 = var7.readLine()) != null)
                {
                    String[] var5 = var4.split(":", 2);

                    if (var5.length > 1 && var1.containsKey(var5[0]))
                    {
                        var1.put(var5[0], var5[1]);
                    }
                }

                var7.close();

                if (!nbtFile().exists())
                {
                    for (int var8 = 0; var8 < var0.getNumSaves(); ++var8)
                    {
                        if (var1.containsKey("save" + (var8 + 1)))
                        {
                            var0.decodeState(var8, (String)var1.get("save" + (var8 + 1)));
                        }
                    }

                    System.out.println("Loading old favorites");
                    var0.decodeFavorites();
                }
            }

            loadNBTFile(var0);
        }
        catch (Exception var6)
        {
            var6.printStackTrace();
        }
    }

    public static void savePreferences(TMIConfig var0)
    {
        try
        {
            saveNBTFile(var0);
            Map var1 = var0.getSettings();
            File var2 = configFile();
            StringWriter stringWriter = new StringWriter();
            PrintWriter var3 = new PrintWriter(stringWriter);
            Iterator var4 = var1.keySet().iterator();

            while (var4.hasNext())
            {
                String var5 = (String)var4.next();

                if (!var5.matches("favorites|save\\d"))
                {
                    var3.println(var5 + ":" + (String)var1.get(var5));
                }
            }

            var3.close();
            LocalStorageManager.gameSettingsStorage.setString(var2.getFileName(),stringWriter.toString());
        }
        catch (Exception var6)
        {
            System.out.println(var6);
        }
    }

    public static void saveNBTFile(TMIConfig var0)
    {
        try
        {
            //DataOutputStream var1 = new DataOutputStream(new FileOutputStream(nbtFile()));
            NBTTagList var2 = new NBTTagList();
            NBTTagList var3 = new NBTTagList();
            List var4 = var0.getFavorites();
            NBTTagCompound var7;

            for (Iterator var5 = var4.iterator(); var5.hasNext(); var3.appendTag(var7))
            {
                ItemStack var6 = (ItemStack)var5.next();
                var7 = new NBTTagCompound();

                if (var6 != null)
                {
                    var6.writeToNBT(var7);
                }
            }

            var2.appendTag(var3);

            for (int var11 = 0; var11 < 7; ++var11)
            {
                if (TMIConfig.statesSaved[var11])
                {
                    ItemStack[] var12 = var0.getState(var11);
                    NBTTagList var14 = new NBTTagList();

                    for (int var8 = 0; var8 < var12.length; ++var8)
                    {
                        NBTTagCompound var9 = new NBTTagCompound();

                        if (var12[var8] != null)
                        {
                            var12[var8].writeToNBT(var9);
                        }

                        var14.appendTag(var9);
                    }

                    var2.appendTag(var14);
                }
                else
                {
                    NBTTagList var13 = new NBTTagList();
                    var2.appendTag(var13);
                }
            }

            LocalStorageManager.gameSettingsStorage.setTag(nbtFile().getFileName(),var2);

            //TMIPrivateFields.writeTagList.invoke(var2, new Object[] {var1});
        }
        catch (Exception var10)
        {
            System.out.println(var10);
        }
    }

    public static void loadNBTFile(TMIConfig var0)
    {
        try
        {
            File var1 = nbtFile();

            if (!var1.exists())
            {
                return;
            }

            //DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
            NBTTagList var3 = LocalStorageManager.gameSettingsStorage.getTagList(var1.getFileName());
            //TMIPrivateFields.readTagList.invoke(var3, new Object[] {var2});
            boolean var4 = false;

            if (var3.tagCount() > 0)
            {
                NBTTagList var5 = (NBTTagList)var3.tagAt(0);
                List var6 = var0.getFavorites();
                var6.clear();

                for (int var7 = 0; var7 < var5.tagCount(); ++var7)
                {
                    NBTTagCompound var8 = (NBTTagCompound)var5.tagAt(var7);
                    ItemStack var9 = new ItemStack(0, 1, 0);
                    var9.readFromNBT(var8);
                    var6.add(var9);
                }
            }

            for (int var14 = 1; var14 < var3.tagCount(); ++var14)
            {
                int var13 = var14 - 1;
                NBTBase var15 = var3.tagAt(var14);

                if (var15 instanceof NBTTagList)
                {
                    NBTTagList var16 = (NBTTagList)var15;

                    if (var16.tagCount() > 0 && var13 < 7)
                    {
                        ItemStack[] var17 = var0.getState(var13);

                        for (int var18 = 0; var18 < var16.tagCount(); ++var18)
                        {
                            NBTBase var10 = var16.tagAt(var18);

                            if (var10 instanceof NBTTagCompound)
                            {
                                NBTTagCompound var11 = (NBTTagCompound)var10;

                                if (var11.hasKey("id"))
                                {
                                    var17[var18] = new ItemStack(0, 1, 0);
                                    var17[var18].readFromNBT(var11);
                                }
                                else
                                {
                                    var17[var18] = null;
                                }
                            }
                        }

                        TMIConfig.statesSaved[var13] = true;
                    }
                }
            }
        }
        catch (Exception var12)
        {
            System.out.println(var12);
        }
    }

    public static void loadItems(TMIConfig var0)
    {
        List var1 = availableItems;
        List var2 = var0.getEnchantableItems();
        boolean var3 = TMIConfig.isMultiplayer();
        var1.clear();
        var2.clear();

        try
        {
            Class var4 = Class.forName("com.eloraam.redpower.RedPowerBase");
            Block var5 = (Block)var4.getDeclaredField("blockMicro").get((Object)null);
            Field var6 = Block.class.getDeclaredField("cz");
            TMIItemInfo.setMaxDamageException(var6.getInt(var5), 32000);
        }
        catch (ClassNotFoundException var19)
        {
            ;
        }
        catch (NoClassDefFoundError var20)
        {
            ;
        }
        catch (Exception var21)
        {
            System.out.println(var21);
        }

        ArrayList var26 = new ArrayList();
        Item[] var27 = Item.itemsList;
        int var30 = var27.length;
        ItemStack var37;
        label243:

        for (int var7 = 0; var7 < var30; ++var7)
        {
            Item var8 = var27[var7];

            if (var8 != null)
            {
                Iterator var34;

                if (var8.itemID == TMIItemInfo.addItemOffset(127))
                {
                    if (var0.areDamageVariantsShown())
                    {
                        var34 = EntityList.entityEggs.keySet().iterator();

                        while (var34.hasNext())
                        {
                            Object var40 = var34.next();
                            var1.add(new ItemStack(TMIItemInfo.addItemOffset(127), 64, ((Integer)var40).intValue()));
                        }
                    }
                }
                else
                {
                    int var10;
                    ItemStack var14;

                    if (var8.itemID == 52)
                    {
                        if (var3)
                        {
                            var1.add(new ItemStack(52, 64, 0));
                        }
                        else
                        {
                            try
                            {
                                var34 = TMIPrivateFields.getSpawnerEntityIdSet().iterator();

                                while (var34.hasNext())
                                {
                                    var10 = ((Integer)var34.next()).intValue();
                                    var1.add(new ItemStack(52, 64, var10));
                                }

                                ItemStack var35 = makeSingleSpawner(50, 64, "\u00a7r\u00a79Charged Creeper Spawner");
                                var35.stackTagCompound.getCompoundTag("SpawnData").setBoolean("powered", true);
                                var1.add(var35);
                                var37 = makeSingleSpawner(51, 64, "\u00a7r\u00a79Wither Skeleton Spawner");
                                var37.stackTagCompound.getCompoundTag("SpawnData").setTag("SkeletonType", new NBTTagByte("", (byte)1));
                                NBTTagList var38 = new NBTTagList();
                                ItemStack var41 = new ItemStack(TMIItemInfo.addItemOffset(16), 1, 0);
                                NBTTagCompound var42 = new NBTTagCompound();
                                var41.writeToNBT(var42);
                                var38.appendTag(var42);
                                var38.appendTag(new NBTTagCompound());
                                var38.appendTag(new NBTTagCompound());
                                var38.appendTag(new NBTTagCompound());
                                var38.appendTag(new NBTTagCompound());
                                var37.stackTagCompound.getCompoundTag("SpawnData").setTag("Equipment", var38);
                                var1.add(var37);
                                var14 = makeSingleSpawner(90, 64, "\u00a7r\u00a79Saddled Pig Spawner");
                                var14.stackTagCompound.getCompoundTag("SpawnData").setBoolean("Saddle", true);
                                var1.add(var14);
                                var1.add(makeRandomFireworkSpawner());
                                ItemStack var43 = makeSingleSpawner(21, 64, "\u00a7r\u00a79TMI Wide-Area Torch Spawner");
                                addLore(var43, "It\'s full of torches!");
                                var43.stackTagCompound.getCompoundTag("SpawnData").setByte("Tile", (byte)50);
                                var43.stackTagCompound.getCompoundTag("SpawnData").setByte("Time", (byte)2);
                                var43.stackTagCompound.getCompoundTag("SpawnData").setBoolean("DropItem", false);
                                var43.stackTagCompound.setShort("MinSpawnDelay", (short)15);
                                var43.stackTagCompound.setShort("MaxSpawnDelay", (short)15);
                                var43.stackTagCompound.setShort("SpawnCount", (short)10);
                                var43.stackTagCompound.setShort("MaxNearbyEntities", (short)15);
                                var43.stackTagCompound.setShort("RequiredPlayerRange", (short)16);
                                var43.stackTagCompound.setShort("SpawnRange", (short)136);
                            }
                            catch (Exception var22)
                            {
                                System.out.println(var22);
                                var1.add(new ItemStack(52, 64, 0));
                            }
                        }
                    }
                    else if (!TMIItemInfo.isHidden(var8.itemID))
                    {
                        if (var8.getItemEnchantability() > 0)
                        {
                            var2.add(var8);
                        }

                        if (var8.itemID < Block.blocksList.length && Block.blocksList[var8.itemID] != null)
                        {
                            var26.clear();
                            Block.blocksList[var8.itemID].getSubBlocks(var8.itemID, (CreativeTabs)null, var26);

                            if (var26.size() > 1)
                            {
                                var34 = var26.iterator();

                                while (true)
                                {
                                    if (!var34.hasNext())
                                    {
                                        continue label243;
                                    }

                                    var37 = (ItemStack)var34.next();
                                    var37.stackSize = Item.itemsList[var37.itemID].getItemStackLimit();
                                    var1.add(var37);
                                }
                            }
                        }

                        HashSet var9 = new HashSet();
                        var10 = var0.areDamageVariantsShown() ? 15 : 0;
                        int var11 = TMIItemInfo.getMaxDamageException(var8.itemID);

                        if (var11 > var10)
                        {
                            var10 = var11;
                        }

                        boolean var12 = false;

                        for (int var13 = 0; var13 <= var10; ++var13)
                        {
                            if ((var8.itemID != 43 || var13 <= 0) && (var8.itemID != 44 || var13 <= 5) && (var8.itemID != 59 || var13 == 7 && !var3) && (var8.itemID != 104 || var13 == 7 && !var3) && (var8.itemID != 105 || var13 == 7 && !var3) && (var8.itemID != 115 || var13 == 3 && !var3) && (var8.itemID != 78 || var13 <= 0 || !var3) && (var8.itemID != 125 || var13 <= 0) && (var8.itemID != 126 || var13 <= 3))
                            {
                                var14 = new ItemStack(var8, var8.getItemStackLimit(), var13);

                                try
                                {
                                    Icon var15 = var8.getIconIndex(var14);
                                    String var16 = var8.getUnlocalizedName(var14);

                                    if (itemDisplayName(var14).equals("Unnamed"))
                                    {
                                        if (var13 == 0)
                                        {
                                            break;
                                        }
                                    }
                                    else
                                    {
                                        if (var8.itemID < Block.blocksList.length && Block.blocksList[var8.itemID] != null)
                                        {
                                            try
                                            {
                                                Block.blocksList[var8.itemID].getIcon(1, var13);
                                            }
                                            catch (Exception var23)
                                            {
                                                continue;
                                            }
                                        }

                                        boolean var17 = !var3 && (var8.itemID == 99 || var8.itemID == 100) && var13 < 16;
                                        String var18 = var16 + "@" + var15.getIconName();

                                        if (!var9.contains(var18) || TMIItemInfo.isShown(var8.itemID, var13) || var17)
                                        {
                                            var1.add(var14);
                                            var9.add(var18);
                                        }
                                    }
                                }
                                catch (NullPointerException var24)
                                {
                                    ;
                                }
                                catch (IndexOutOfBoundsException var25)
                                {
                                    ;
                                }
                            }
                        }
                    }
                }
            }
        }

        ItemStack var32;

        if (var0.areDamageVariantsShown())
        {
            Iterator var28 = TMIItemInfo.potionValues.iterator();

            while (var28.hasNext())
            {
                var30 = ((Integer)var28.next()).intValue();
                var32 = new ItemStack(TMIItemInfo.addItemOffset(117), 64, var30);
                var1.add(var32);
            }
        }

        if (var0.areDamageVariantsShown() && !TMIConfig.isMultiplayer())
        {
            ItemStack var29 = new ItemStack(TMIItemInfo.addItemOffset(117), 64, 1);
            PotionEffect var31 = new PotionEffect(3, 0, 1);
            addEffectToPotion(var29, var31);
            var1.add(var29);
            var32 = new ItemStack(TMIItemInfo.addItemOffset(117), 64, 16384);
            PotionEffect var33 = new PotionEffect(7, 0, 3);
            addEffectToPotion(var32, var33);
            var1.add(var32);
            var1.add(makeFirework("Creepy Sparkler", 2, 3, new int[] {4312372}, new int[] {15435844}, true, false));
            var1.add(makeFirework("Star", 4, 2, new int[] {6719955}, (int[])null, false, false));
            var1.add(makeFirework("Big Red", 1, 1, new int[] {11743532}, (int[])null, false, false));
            NBTTagCompound var36 = makeExplosionTag(1, new int[] {11743532, 15790320, 2437522}, new int[] {15790320}, true, true);
            var37 = new ItemStack(TMIItemInfo.addItemOffset(145), 64, 0);
            var37.stackTagCompound = new NBTTagCompound();
            NBTTagCompound var39 = makeFireworksTag(2, new NBTTagCompound[] {var36, var36});
            var37.stackTagCompound.setCompoundTag("Fireworks", var39);
            nameStack(var37, "Old Glory");
            var1.add(var37);
        }

        filterItems((String)null, var0);
    }

    public static ItemStack makeRandomFireworkSpawner()
    {
        ItemStack var0 = new ItemStack(52, 1, 22);
        var0.stackTagCompound = new NBTTagCompound();
        nameStack(var0, "\u00a7r\u00a79TMI Random Firework Spawner");
        addLore(var0, "Every one is different");
        var0.stackTagCompound.setShort("MinSpawnDelay", (short)20);
        var0.stackTagCompound.setShort("MaxSpawnDelay", (short)20);
        var0.stackTagCompound.setShort("SpawnCount", (short)1);
        var0.stackTagCompound.setShort("MaxNearbyEntities", (short)5);
        var0.stackTagCompound.setShort("RequiredPlayerRange", (short)120);
        var0.stackTagCompound.setShort("SpawnRange", (short)2);
        NBTTagList var1 = new NBTTagList("SpawnPotentials");
        var0.stackTagCompound.setTag("SpawnPotentials", var1);

        for (int var2 = 0; var2 < 10; ++var2)
        {
            NBTTagCompound var3 = new NBTTagCompound("SpawnData");
            var3.setString("Type", "FireworksRocketEntity");
            var3.setInteger("Weight", 1);
            NBTTagCompound var4 = new NBTTagCompound("Properties");
            var3.setCompoundTag("Properties", var4);
            NBTTagCompound var5 = makeRandomFirework().writeToNBT(new NBTTagCompound());
            var4.setCompoundTag("FireworksItem", var5);
            var4.setInteger("LifeTime", random.nextInt(15) + random.nextInt(15) + 20);
            var1.appendTag(var3);
        }

        return var0;
    }

    public static ItemStack makeSingleSpawner(int var0, int var1, String var2)
    {
        String var3 = EntityList.getStringFromID(var0);
        ItemStack var4 = new ItemStack(52, var1, var0);
        var4.stackTagCompound = new NBTTagCompound();
        NBTTagCompound var5 = new NBTTagCompound("SpawnData");
        var4.stackTagCompound.setCompoundTag("SpawnData", var5);

        if (var2 != null)
        {
            nameStack(var4, var2);
        }

        return var4;
    }

    public static ItemStack makeFirework(String var0, int var1, int var2, int[] var3, int[] var4, boolean var5, boolean var6)
    {
        ItemStack var7 = new ItemStack(TMIItemInfo.addItemOffset(145), 64, 0);
        var7.stackTagCompound = new NBTTagCompound();
        NBTTagCompound var8 = makeExplosionTag(var2, var3, var4, var5, var6);
        NBTTagCompound var9 = makeFireworksTag(var1, new NBTTagCompound[] {var8});
        var7.stackTagCompound.setCompoundTag("Fireworks", var9);

        if (var0 != null && !var0.equals(""))
        {
            nameStack(var7, var0);
        }

        return var7;
    }

    public static NBTTagCompound makeExplosionTag(int var0, int[] var1, int[] var2, boolean var3, boolean var4)
    {
        NBTTagCompound var5 = new NBTTagCompound("Explosion");
        var5.setBoolean("Flicker", var3);
        var5.setBoolean("Trail", var4);
        var5.setByte("Type", (byte)(var0 & 15));

        if (var1 != null && var1.length > 0)
        {
            var5.setIntArray("Colors", var1);
        }

        if (var2 != null && var2.length > 0)
        {
            var5.setIntArray("FadeColors", var2);
        }

        return var5;
    }

    public static ItemStack makeRandomFirework()
    {
        int[] var0;

        if (random.nextBoolean())
        {
            var0 = new int[] {randomBrightColor(), randomBrightColor()};
        }
        else
        {
            var0 = new int[] {randomBrightColor()};
        }

        int[] var1;

        if (random.nextBoolean())
        {
            var1 = new int[] {randomBrightColor()};
        }
        else
        {
            var1 = null;
        }

        return makeFirework("Random Firework", random.nextInt(3) + 1, random.nextInt(4), var0, var1, random.nextBoolean(), random.nextBoolean());
    }

    public static int randomBrightColor()
    {
        return Color.HSBtoRGB(random.nextFloat(), random.nextFloat(), random.nextFloat() * 0.5F + 0.5F);
    }

    public static NBTTagCompound makeFireworksTag(int var0, NBTTagCompound ... var1)
    {
        NBTTagCompound var2 = new NBTTagCompound("Fireworks");
        var2.setByte("Flight", (byte)(var0 & 15));
        NBTTagList var3 = new NBTTagList();
        var2.setTag("Explosions", var3);
        NBTTagCompound[] var4 = var1;
        int var5 = var1.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            NBTTagCompound var7 = var4[var6];
            var3.appendTag(var7);
        }

        return var2;
    }

    public static String deaccent(String var0)
    {
        try
        {
            Class.forName("java.text.Normalizer");
        }
        catch (ClassNotFoundException var3)
        {
            return var0;
        }

        String var1 = Normalizer.normalize(var0, Form.NFD);
        Pattern var2 = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return var2.matcher(var1).replaceAll("");
    }

    public static void filterItems(String var0, TMIConfig var1)
    {
        List var2 = availableItems;
        List var3 = var1.getItems();
        var3.clear();
        Iterator var4;
        ItemStack var5;

        if (var0 != null && !var0.equals(""))
        {
            var0 = deaccent(var0.toLowerCase());
            var4 = var2.iterator();

            while (var4.hasNext())
            {
                var5 = (ItemStack)var4.next();

                if (var5 != null)
                {
                    String var6 = itemDisplayName(var5);

                    if (var6 != null && deaccent(var6.toLowerCase()).contains(var0))
                    {
                        var3.add(var5);
                    }
                }
            }
        }
        else
        {
            var4 = var2.iterator();

            while (var4.hasNext())
            {
                var5 = (ItemStack)var4.next();
                var3.add(var5);
            }
        }
    }

    public static void safeReportException(Exception var0)
    {
        try
        {
            SimpleDateFormat var1 = new SimpleDateFormat(".yyyyMMdd.HHmmss");
            StringBuffer var2 = new StringBuffer();
            var1.format(new Date(), var2, new FieldPosition(1));
            String var3 = "tmi" + var2.toString() + ".txt";
            StringWriter var4 = new StringWriter();
            PrintWriter var5 = new PrintWriter(var4);
            var5.print("[code]TMI Version: 1.5.2 2013-04-25\n");
            var0.printStackTrace(var5);
            var5.println("[/code]");
            var5.close();
            System.out.println(var4.toString());
        }
        catch (Exception var6)
        {
            System.out.println("Error during safeReportException:");
            var6.printStackTrace();
        }
    }

    public static List itemDisplayNameMultiline(ItemStack var0, boolean var1)
    {
        return itemDisplayNameMultiline(var0, var1, false);
    }

    public static List itemDisplayNameMultiline(ItemStack var0, boolean var1, boolean var2)
    {
        if (var0 == null)
        {
            return EMPTY_NAME;
        }
        else
        {
            if (var2)
            {
                var1 = false;
            }

            Object var3 = null;

            if (isValidItem(var0))
            {
                try
                {
                    List var4 = var0.getTooltip(Minecraft.getMinecraft().thePlayer, var2);
                    var3 = var4;
                }
                catch (Exception var5)
                {
                    ;
                }
            }

            if (var3 == null || ((List)var3).size() == 0 || var0.itemID == 52 && var0.stackTagCompound == null)
            {
                var3 = new ArrayList();
                ((List)var3).add(TMIItemInfo.getFallbackName(var0.itemID, var0.getItemDamageForDisplay()));

                if (var2)
                {
                    var1 = true;
                }
            }

            String var6 = ((String)((List)var3).get(0)).trim();

            if (var6.length() == 0)
            {
                var6 = TMIItemInfo.getFallbackName(var0.itemID, var0.getItemDamageForDisplay());
                ((List)var3).set(0, var6);
            }

            if (var1 && var0 != null)
            {
                var6 = var6 + " " + var0.itemID;

                if (var0.getItemDamageForDisplay() != 0)
                {
                    var6 = var6 + " : " + var0.getItemDamageForDisplay();
                }

                ((List)var3).set(0, var6);
            }

            return (List)var3;
        }
    }

    public static String itemDisplayName(ItemStack var0)
    {
        List var1 = itemDisplayNameMultiline(var0, false);
        return (String)var1.get(0);
    }

    public static boolean isValidItem(ItemStack var0)
    {
        return var0 == null || var0.itemID >= 0 && var0.itemID < Item.itemsList.length && Item.itemsList[var0.itemID] != null;
    }

    public static ItemStack getValidItem(ItemStack var0)
    {
        return isValidItem(var0) ? var0 : new ItemStack(Item.itemsList[52]);
    }

    public static String getValidItemDisplayName(ItemStack var0)
    {
        return isValidItem(var0) ? itemDisplayName(var0) : "Undefined Item";
    }

    public static void setHeldItem(ItemStack var0)
    {
        getPlayer().inventory.setItemStack(var0);
        Minecraft.getMinecraft().thePlayer.inventory.setItemStack(var0);
    }

    public static void deleteHeldItem()
    {
        setHeldItem((ItemStack)null);
    }

    public static ItemStack getHeldItem()
    {
        return Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
    }

    public static void giveStack(ItemStack var0, TMIConfig var1)
    {
        giveStack(var0, var1, var0.stackSize);
    }

    public static void giveStack(ItemStack var0, TMIConfig var1, int var2)
    {
        ItemStack var3 = copyStack(var0, var2);
        Minecraft var4 = Minecraft.getMinecraft();

        if (TMIConfig.isMultiplayer())
        {
            NumberFormat var5 = NumberFormat.getIntegerInstance();
            var5.setGroupingUsed(false);
            MessageFormat var6 = new MessageFormat((String)var1.getSettings().get("give-command"));
            var6.setFormatByArgumentIndex(1, var5);
            var6.setFormatByArgumentIndex(2, var5);
            var6.setFormatByArgumentIndex(3, var5);
            Object[] var7 = new Object[] {var4.thePlayer.username, Integer.valueOf(var3.itemID), Integer.valueOf(var3.stackSize), Integer.valueOf(var3.getItemDamageForDisplay())};
            StringBuilder var8 = new StringBuilder();
            var8.append(var6.format(var7));
            Iterator var9 = getEnchantments(var3).iterator();

            while (var9.hasNext())
            {
                int[] var10 = (int[])var9.next();
                var8.append(" ");
                var8.append(var10[0]);
                var8.append(":");
                var8.append(var10[1]);
            }

            var4.thePlayer.sendChatMessage(var8.toString());
        }
        else
        {
            getPlayer().inventory.addItemStackToInventory(var3);
        }
    }

    public static ItemStack copyStack(ItemStack var0, int var1)
    {
        if (var0 == null)
        {
            return null;
        }
        else
        {
            var0.stackSize += var1;
            return var0.splitStack(var1);
        }
    }

    public static ItemStack copyStack(ItemStack var0)
    {
        return var0 == null ? null : copyStack(var0, var0.stackSize);
    }

    public static void updateUnlimitedItems()
    {
        try
        {
            if (TMIConfig.isMultiplayer() || !TMIConfig.getInstance().isEnabled())
            {
                return;
            }

            ItemStack[] var0 = getPlayer().inventory.mainInventory;
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                ItemStack var3 = var0[var2];

                if (var3 != null)
                {
                    if (var3.stackTagCompound != null && var3.stackTagCompound.hasKey("TooManyItems"))
                    {
                        NBTTagCompound var4 = var3.stackTagCompound.getCompoundTag("TooManyItems");

                        if (var4.hasKey("Unlimited"))
                        {
                            int var5 = maxStackSize(var3.itemID);

                            if (var5 == 1)
                            {
                                var5 = 64;
                            }

                            var3.stackSize = var5;
                        }
                    }

                    if (var3.getItemDamageForDisplay() < 0)
                    {
                        setStackDamage(var3, -32000);
                    }
                }
            }

            ItemStack var7 = getHeldItem();

            if (var7 != null && var7.stackSize > 64)
            {
                var7.stackSize = -1;
            }
        }
        catch (NullPointerException var6)
        {
            ;
        }
    }

    public static void setStackDamage(ItemStack var0, int var1)
    {
        try
        {
            var0.itemDamage=var1;
        }
        catch (Exception var3)
        {
            System.out.println("[TMI] Error setting stack damage");
            var3.printStackTrace();
        }
    }

    public static void deleteInventory()
    {
        List var0 = getPlayer().openContainer.inventorySlots;

        for (int var1 = 0; var1 < var0.size(); ++var1)
        {
            Slot var2 = (Slot)var0.get(var1);

            if (var2 != null)
            {
                var2.putStack((ItemStack)null);
            }
        }
    }

    public static void deleteItemsOfType(ItemStack var0, GuiContainer var1)
    {
        List var2 = getPlayer().openContainer.inventorySlots;

        for (int var3 = 0; var3 < var2.size(); ++var3)
        {
            Slot var4 = (Slot)var2.get(var3);

            if (var4 != null)
            {
                ItemStack var5 = var4.getStack();

                if (var5 != null && var5.itemID == var0.itemID && var5.getItemDamageForDisplay() == var0.getItemDamageForDisplay())
                {
                    var4.putStack((ItemStack)null);
                }
            }
        }
    }

    public static boolean shiftKey()
    {
        return EaglerAdapter.isKeyDown(54) || EaglerAdapter.isKeyDown(42);
    }

    public static int getGameMode()
    {
        return Minecraft.getMinecraft().playerController.currentGameType.getID();
    }

    public static void setGameMode(int var0)
    {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/gamemode " + var0);
    }

    public static boolean isCreativeMode()
    {
        return Minecraft.getMinecraft().playerController.isInCreativeMode();
    }

    public static void setCreativeMode(boolean var0)
    {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/gamemode " + (var0 ? "1" : "0"));
    }

    public static boolean isCreativeSearchTab()
    {
        return false;
    }

    public static boolean isRaining()
    {
        return Minecraft.getMinecraft().theWorld.isRaining();
    }

    public static void setRaining(boolean var0)
    {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/toggledownfall");
    }

    public static long getTime()
    {
        return Minecraft.getMinecraft().theWorld.getWorldTime();
    }

    public static void setTime(long var0)
    {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/time set " + var0);
    }

    public static void setHourForward(int var0)
    {
        long var1 = getTime() / 24000L * 24000L;
        long var3 = var1 + 24000L + (long)(var0 * 1000);
        setTime(var3);
    }

    public static void logWithTrace(String var0)
    {
        System.out.println(var0);
        StackTraceElement[] var1 = Thread.currentThread().getStackTrace();

        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            System.out.println(var1[var2].toString());
        }
    }

    public static void logWithTrace(String var0, int var1)
    {
        System.out.println(var0);
        StackTraceElement[] var2 = Thread.currentThread().getStackTrace();

        for (int var3 = 0; var3 < var2.length && var3 < var1; ++var3)
        {
            System.out.println(var2[var3].toString());
        }
    }

    public static boolean playerIsAlive()
    {
        return !Minecraft.getMinecraft().thePlayer.isDead;
    }

    public static void setPlayerHealth(int var0)
    {
        if (playerIsAlive())
        {
            getPlayer().setEntityHealth(var0);
        }
    }

    public static void fillHunger()
    {
        if (playerIsAlive())
        {
            getPlayer().getFoodStats().setFoodLevel(20);
            getPlayer().getFoodStats().setFoodSaturationLevel(5.0F);
        }
    }

    public static void incrementDifficulty()
    {
        Minecraft.getMinecraft().gameSettings.setOptionValue(EnumOptions.DIFFICULTY, 1);
    }

    public static String getDifficultyString()
    {
        return Minecraft.getMinecraft().gameSettings.getKeyBinding(EnumOptions.DIFFICULTY);
    }

    public static void suppressAchievementNotice()
    {
        try
        {
            Field[] var0 = Minecraft.getMinecraft().guiAchievement.getClass().getDeclaredFields();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                Field var3 = var0[var2];

                if (var3.getName().equals("_gui_achievement_time"))
                {
                    var3.setAccessible(true);
                    var3.setInt(Minecraft.getMinecraft().guiAchievement, 0);
                }
            }
        }
        catch (Exception var4)
        {
            System.out.println(var4);
        }
    }

    public static int maxStackSize(int var0)
    {
        return Item.itemsList[var0].getItemStackLimit();
    }

    public static List getEnchantments(ItemStack var0)
    {
        ArrayList var1 = new ArrayList();

        if (var0 != null)
        {
            NBTTagList var2 = var0.getEnchantmentTagList();

            if (var2 != null)
            {
                for (int var3 = 0; var3 < var2.tagCount(); ++var3)
                {
                    short var4 = ((NBTTagCompound)var2.tagAt(var3)).getShort("id");
                    short var5 = ((NBTTagCompound)var2.tagAt(var3)).getShort("lvl");
                    int[] var6 = new int[] {var4, var5};
                    var1.add(var6);
                }
            }
        }

        return var1;
    }

    public static boolean addEnchantment(ItemStack var0, int var1, int var2)
    {
        if (var1 < Enchantment.enchantmentsList.length && Enchantment.enchantmentsList[var1] != null)
        {
            var0.addEnchantment(Enchantment.enchantmentsList[var1], var2);
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void addEffectToPotion(ItemStack var0, PotionEffect var1)
    {
        if (var0.itemID == TMIItemInfo.addItemOffset(117))
        {
            if (var0.stackTagCompound == null)
            {
                var0.stackTagCompound = new NBTTagCompound();
            }

            NBTTagList var2;

            if (var0.stackTagCompound.hasKey("CustomPotionEffects"))
            {
                var2 = var0.stackTagCompound.getTagList("CustomPotionEffects");
            }
            else
            {
                var2 = new NBTTagList();
                var0.stackTagCompound.setTag("CustomPotionEffects", var2);
            }

            NBTTagCompound var3 = new NBTTagCompound();
            var1.writeCustomPotionEffectToNBT(var3);
            var2.appendTag(var3);
        }
    }

    public static void nameStack(ItemStack var0, String var1)
    {
        if (var1 != null)
        {
            if (var0.stackTagCompound == null)
            {
                var0.stackTagCompound = new NBTTagCompound();
            }

            NBTTagCompound var2;

            if (var0.stackTagCompound.hasKey("display"))
            {
                var2 = var0.stackTagCompound.getCompoundTag("display");
            }
            else
            {
                var2 = new NBTTagCompound();
                var0.stackTagCompound.setCompoundTag("display", var2);
            }

            var2.setString("Name", var1);
        }
    }

    public static void addLore(ItemStack var0, String var1)
    {
        if (var1 != null)
        {
            if (var0.stackTagCompound == null)
            {
                var0.stackTagCompound = new NBTTagCompound();
            }

            NBTTagCompound var2;

            if (var0.stackTagCompound.hasKey("display"))
            {
                var2 = var0.stackTagCompound.getCompoundTag("display");
            }
            else
            {
                var2 = new NBTTagCompound();
                var0.stackTagCompound.setCompoundTag("display", var2);
            }

            NBTTagList var3;

            if (var2.hasKey("Lore"))
            {
                var3 = var2.getTagList("Lore");
            }
            else
            {
                var3 = new NBTTagList();
                var2.setTag("Lore", var3);
            }

            NBTTagString var4 = new NBTTagString("Lore", var1);
            var3.appendTag(var4);
        }
    }

    public static NBTTagCompound getTagCompoundWithCreate(ItemStack var0, String var1)
    {
        if (var0.stackTagCompound == null)
        {
            var0.stackTagCompound = new NBTTagCompound();
        }

        NBTTagCompound var2;

        if (!var0.stackTagCompound.hasKey(var1))
        {
            var2 = new NBTTagCompound();
            var0.stackTagCompound.setCompoundTag(var1, var2);
        }
        else
        {
            var2 = var0.stackTagCompound.getCompoundTag(var1);
        }

        return var2;
    }

    public static EntityClientPlayerMP getPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static void fastTransfer(int var0, int var1, Container var2)
    {
        Minecraft var3 = Minecraft.getMinecraft();
        ItemStack var4 = getHeldItem();
        var3.playerController.windowClick(var2.windowId, var0, var1, 0, var3.thePlayer);
        int var5 = var2.inventorySlots.size() - 36;
        boolean var6 = var0 < var5;
        int var7 = 0;
        int var8 = var5;
        int var9 = var5 - 1;
        int var10 = var2.inventorySlots.size();

        if (var6)
        {
            var7 = var5;
            var8 = var2.inventorySlots.size();
            var9 = -1;
            var10 = var5;
        }

        boolean var11 = true;

        while (var7 < var8 && var9 < var10)
        {
            Slot var12 = (Slot)var2.inventorySlots.get(var7);

            if (var12 != null)
            {
                ItemStack var13 = var12.getStack();

                if (var13 != null && var13.itemID == var4.itemID && var13.getItemDamageForDisplay() == var4.getItemDamageForDisplay())
                {
                    Slot var14;

                    if (var11)
                    {
                        var14 = (Slot)var2.inventorySlots.get(var0);
                    }
                    else
                    {
                        var14 = (Slot)var2.inventorySlots.get(var9);
                    }

                    if (var14 == null)
                    {
                        ++var9;
                        var11 = false;
                        continue;
                    }

                    ItemStack var15 = var14.getStack();

                    if (var15 == null)
                    {
                        var14.putStack(var13);
                        var12.putStack((ItemStack)null);
                    }
                    else
                    {
                        if (var15.itemID != var13.itemID || var15.getItemDamageForDisplay() != var13.getItemDamageForDisplay())
                        {
                            ++var9;
                            var11 = false;
                            continue;
                        }

                        int var16 = var13.stackSize + var15.stackSize;
                        int var17 = Item.itemsList[var13.itemID].getItemStackLimit();
                        int var18 = var16 - var17;

                        if (var18 > 0)
                        {
                            var15.stackSize = var17;
                            var13.stackSize = var18;
                            ++var9;
                            var11 = false;
                            continue;
                        }

                        var15.stackSize = var16;
                        var12.putStack((ItemStack)null);
                    }
                }
            }

            ++var7;
        }
    }

    public static boolean isEnchantmentNormallyPossible(Enchantment var0, Item var1)
    {
        return var0.type.canEnchantItem(var1) || var1.itemID == TMIItemInfo.addItemOffset(147);
    }

    public static boolean isEnchantmentNormallyPossible(Enchantment var0, ItemStack var1)
    {
        return var0.type.canEnchantItem(Item.itemsList[var1.itemID]) || var1.itemID == TMIItemInfo.addItemOffset(147);
    }

    public static List getPossibleEnchantments(Item var0)
    {
        if (var0.getItemEnchantability() > 0)
        {
            ArrayList var1 = new ArrayList();
            Enchantment[] var2 = Enchantment.enchantmentsList;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4)
            {
                Enchantment var5 = var2[var4];

                if (var5 != null && var5.type.canEnchantItem(var0))
                {
                    var1.add(var5);
                }
            }

            return var1;
        }
        else
        {
            return new ArrayList();
        }
    }

    public static void replaceCustomItems()
    {
        if (!haveReplacedItems)
        {
            TMIConfig.getInstance();

            if (!TMIConfig.isMultiplayer() && TMIConfig.getInstance().getBooleanSetting("replace-items"))
            {
                try
                {
                    Class var0 = Class.forName("cpw.mods.fml.common.registry.GameData");
                    System.out.println("[TMI] Please ignore Forge item allocation errors. The items are fine.");
                }
                catch (NoClassDefFoundError var1)
                {
                    ;
                }
                catch (Exception var2)
                {
                    ;
                }

                modMushroomBlock(true);
                modMobSpawner(true);
                modCrops(true);
                modSnowCover(true);
                haveReplacedItems = true;
                return;
            }
        }
    }

    public static void dumpItemList()
    {
        try
        {
            StringWriter var0 = new StringWriter();
            PrintWriter var1 = new PrintWriter(var0);
            Iterator var2 = TMIConfig.getInstance().getItems().iterator();

            while (var2.hasNext())
            {
                ItemStack var3 = (ItemStack)var2.next();
                var1.println("" + var3.itemID + ":" + var3.getItemDamageForDisplay() + " " + getValidItemDisplayName(var3));
            }

            var1.close();
            System.out.println(var0);
        }
        catch (Exception var4)
        {
            System.out.println(var4.toString());
        }
    }

    public static Item unsetItem(int var0)
    {
        Item var1 = Item.itemsList[var0];
        Item.itemsList[var0] = null;

        /*
        try
        {
            Class var2 = Class.forName("cpw.mods.fml.common.registry.GameData");
            Field var3 = var2.getDeclaredField("idMap");
            TMIPrivateFields.unsetFinalPrivate(var3);
            Map var4 = (Map)var3.get((Object)null);

            if (var4.containsKey(Integer.valueOf(var0)))
            {
                var4.remove(Integer.valueOf(var0));
            }
        }
        catch (NoClassDefFoundError var5)
        {
            ;
        }
        catch (Exception var6)
        {
            ;
        }
        */

        return var1;
    }

    public static void unsetAndSaveItem(int var0)
    {
        originalItems.put(Integer.valueOf(var0), unsetItem(var0));
    }

    public static void modMobSpawner(boolean var0)
    {
        unsetAndSaveItem(52);
        Item.itemsList[52] = (new TMIItemSpawner(52 - TMIItemInfo.itemOffset)).setUnlocalizedName("mobSpawner");
    }

    public static void modMushroomBlock(boolean var0)
    {
        unsetAndSaveItem(99);
        unsetAndSaveItem(100);
        Item.itemsList[99] = (new TMIItemMushroomCap(99 - TMIItemInfo.itemOffset)).setUnlocalizedName("mushroom");
        Item.itemsList[100] = (new TMIItemMushroomCap(100 - TMIItemInfo.itemOffset)).setUnlocalizedName("mushroom");
    }

    public static void modCrops(boolean var0)
    {
        unsetAndSaveItem(59);
        unsetAndSaveItem(104);
        unsetAndSaveItem(105);
        unsetAndSaveItem(115);
        Item.itemsList[59] = (new TMIItemCrop(59 - TMIItemInfo.itemOffset)).setUnlocalizedName("crops");
        Item.itemsList[104] = (new TMIItemCrop(104 - TMIItemInfo.itemOffset)).setUnlocalizedName("pumpkinStem");
        Item.itemsList[105] = (new TMIItemCrop(105 - TMIItemInfo.itemOffset)).setUnlocalizedName("melonStem");
        Item.itemsList[115] = (new TMIItemCrop(115 - TMIItemInfo.itemOffset)).setUnlocalizedName("netherStalk");
    }

    public static void modSnowCover(boolean var0) {}

    public static void resetItems()
    {
        int var1;

        for (Iterator var0 = originalItems.keySet().iterator(); var0.hasNext(); Item.itemsList[var1] = (Item)originalItems.get(Integer.valueOf(var1)))
        {
            var1 = ((Integer)var0.next()).intValue();
            unsetItem(var1);
        }

        originalItems.clear();
        haveReplacedItems = false;
    }
}
