package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.ItemStack;

public class _tmi_MgItemButton extends _tmi_MgButton
{
    public ItemStack stack;

    public _tmi_MgItemButton(String var1, _tmi_MgButtonHandler var2, Object var3)
    {
        this(var1, (ItemStack)null, var2, var3);
    }

    public _tmi_MgItemButton(String var1, ItemStack var2, _tmi_MgButtonHandler var3, Object var4)
    {
        super(var1, var3, var4);
        this.stack = null;
        this.stack = var2;
        this.width = 18;
        this.height = 18;
    }

    public int graphicWidth(_tmi_MgCanvas var1)
    {
        return this.stack == null ? 0 : 16;
    }

    protected boolean drawGraphic(_tmi_MgCanvas var1, int var2)
    {
        if (this.stack != null)
        {
            int var3 = this.y + (this.height - 16) / 2;
            var1.drawItem(var2, var3, this.stack);
            return true;
        }
        else
        {
            return false;
        }
    }
}
