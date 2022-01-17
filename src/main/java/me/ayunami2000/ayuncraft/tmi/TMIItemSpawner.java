package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.*;

import java.util.Iterator;

public class TMIItemSpawner extends ItemBlock
{
    public TMIItemSpawner(int var1)
    {
        super(var1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10)
    {
        int var11 = var3.getBlockId(var4, var5, var6);

        if (var11 != Block.vine.blockID && var11 != Block.tallGrass.blockID && var11 != Block.deadBush.blockID)
        {
            var4 += var7 == 4 ? -1 : (var7 == 5 ? 1 : 0);
            var5 += var7 == 0 ? -1 : (var7 == 1 ? 1 : 0);
            var6 += var7 == 2 ? -1 : (var7 == 3 ? 1 : 0);
        }

        if (!var2.canPlayerEdit(var4, var5, var6, var7, var1))
        {
            return false;
        }
        else if (!Block.mobSpawner.canPlaceBlockAt(var3, var4, var5, var6))
        {
            return false;
        }
        else
        {
            var3.setBlock(var4, var5, var6, 52, 0, 3);
            --var1.stackSize;
            TileEntityMobSpawner var12 = (TileEntityMobSpawner)var3.getBlockTileEntity(var4, var5, var6);

            if (var12 == null)
            {
                System.out.println("[TMI] Failed to access TileEntity for spawner.");
                return false;
            }
            else
            {
                MobSpawnerBaseLogic var13 = var12.func_98049_a();

                if (var13 == null)
                {
                    System.out.println("[TMI] Failed to access spawner data from TileEntity.");
                    return false;
                }
                else
                {
                    int var14 = var1.getItemDamageForDisplay();
                    String var15 = null;

                    if (!TMIItemInfo.excludedSpawnerIds.contains(Integer.valueOf(var14)) || var1.stackTagCompound != null)
                    {
                        var15 = EntityList.getStringFromID(var14);
                    }

                    if (var15 == null || var15.equals(""))
                    {
                        var15 = "Pig";
                    }

                    var13.setMobID(var15);

                    if (var1.stackTagCompound != null)
                    {
                        NBTTagCompound var16 = new NBTTagCompound();
                        var13.writeToNBT(var16);
                        Iterator var17 = var1.stackTagCompound.getTags().iterator();

                        while (var17.hasNext())
                        {
                            Object var18 = var17.next();
                            NBTBase var19 = (NBTBase)var18;
                            var16.setTag(var19.getName(), var19);
                        }

                        var13.readFromNBT(var16);
                    }

                    return true;
                }
            }
        }
    }
}
