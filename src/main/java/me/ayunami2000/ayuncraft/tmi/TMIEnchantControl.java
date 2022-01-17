package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.Enchantment;

class TMIEnchantControl extends _tmi_MgWidget
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private String name;
    private boolean normallyPossible = true;
    private Enchantment enchantment;

    public TMIEnchantControl(int var1, int var2, int var3, int var4, int var5, String var6, Enchantment var7, boolean var8)
    {
        super(var1, var2, var3, var4, var5);
        this.name = var6;
        this.enchantment = var7;
        this.normallyPossible = var8;
    }

    public boolean click(int var1, int var2, int var3)
    {
        boolean var4 = var1 < this.x + 13;
        TMIEnchanting.adjustEnchantmentLevel(this.enchantment, var4 ? -1 : 1);
        return false;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        int var4 = this.normallyPossible ? -1 : -5609882;
        var1.drawRect(this.x, this.y, 12, 12, -301989888);
        var1.drawRect(this.x + 13, this.y, this.width - 13, 12, -301989888);
        var1.drawTextCentered(this.x, this.y, 12, 12, "-", var4);
        int var5 = TMIEnchanting.getEnchantmentLevel(this.enchantment);
        String var6 = " " + var5;
        String var7;

        for (var7 = "+ " + this.name; var1.getTextWidth(var7 + var6) + 14 > this.width; var7 = var7.substring(0, var7.length() - 1))
        {
            ;
        }

        var1.drawText(this.x + 14, this.y + 2, var7 + var6, var4);
    }
}
