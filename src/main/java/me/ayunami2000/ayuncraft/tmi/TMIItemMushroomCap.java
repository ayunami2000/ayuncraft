package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.ItemBlock;

public class TMIItemMushroomCap extends ItemBlock
{
    public TMIItemMushroomCap(int var1)
    {
        super(var1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(int var1)
    {
        return var1;
    }
}
