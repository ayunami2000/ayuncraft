package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.ItemStack;

import java.util.Iterator;

class TMIEnchantItemPicker extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    private TMIEnchantPanel enchantPanel;
    public long mouseLastInArea = 0L;
    public static final int ITEM_SPACING = 18;
    public static final int MARGIN = 0;
    public static final int ITEM_MARGIN = 1;

    public TMIEnchantItemPicker(TMIEnchantPanel var1)
    {
        super(0, 0);
        this.enchantPanel = var1;
        int[] var2 = new int[] {20, 5, 21, 22, 23, 37, 54, 55, 56, 57, 93, 147};

        for (int var3 = 0; var3 < var2.length; ++var3)
        {
            ItemStack var4 = new ItemStack(TMIItemInfo.addItemOffset(var2[var3]), 1, 0);
            _tmi_MgItemButton var5 = new _tmi_MgItemButton("", var4, this, Integer.valueOf(var2[var3]));
            this.children.add(var5);
        }
    }

    public void resize()
    {
        int var1 = 0;
        int var2 = 0;
        Iterator var3 = this.children.iterator();

        while (var3.hasNext())
        {
            _tmi_MgWidget var4 = (_tmi_MgWidget)var3.next();
            this.height = var2 + 18;
            var4.x = this.x + var1;
            var4.y = this.y + var2;
            var1 += 18;

            if (var1 + 18 > this.width)
            {
                var1 = 0;
                var2 += 18;
            }
        }
    }

    public void open()
    {
        this.show = true;
        this.mouseLastInArea = System.currentTimeMillis();
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        long var4 = System.currentTimeMillis();

        if (this.contains(var2, var3))
        {
            this.mouseLastInArea = var4;
        }
        else if (var4 - this.mouseLastInArea > 1200L)
        {
            this.show = false;
            return;
        }

        if (this.show)
        {
            var1.drawRect(this.x, this.y, this.width, this.height, -301989888);
            this.drawChildren(var1, var2, var3);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return this.delegateClickToChildren(var1, var2, var3);
    }

    public boolean onButtonPress(Object var1)
    {
        this.enchantPanel.chooseItem(new ItemStack(TMIItemInfo.addItemOffset(((Integer)var1).intValue()), 1, 0));
        this.show = false;
        return true;
    }

    public boolean onButtonRightClick(Object var1)
    {
        return true;
    }
}
