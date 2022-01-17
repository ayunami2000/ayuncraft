package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.Block;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemBlock;

public class TMIItemSnow extends ItemBlock
{
    public TMIItemSnow(int var1)
    {
        super(var1);
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    public Icon getIconFromDamage(int var1)
    {
        try
        {
            TMIPrivateFields.setBlockBounds.invoke(Block.snow, new Object[] {Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(1.0F), Float.valueOf((float)(2 * (1 + var1)) / 16.0F), Float.valueOf(1.0F)});
        }
        catch (Exception var3)
        {
            System.out.println(var3);
        }

        return super.getIconFromDamage(var1);
    }
}
