package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.Potion;
import net.minecraft.src.StatCollector;

public class TMIPotionEffectPicker extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    int page = 0;
    private TMIPotionPanel panel;
    private _tmi_MgButton closeButton = new _tmi_MgButton("x", this, "close");
    private _tmi_MgButton nextButton = new _tmi_MgButton("", this, "next");
    private _tmi_MgButton prevButton = new _tmi_MgButton("", this, "prev");

    public TMIPotionEffectPicker(TMIPotionPanel var1)
    {
        super(0, 0);
        this.show = false;
        this.panel = var1;
        this.nextButton.icon = TMIImages.iconNext;
        this.nextButton.width = 16;
        this.nextButton.height = 16;
        this.prevButton.icon = TMIImages.iconPrev;
        this.prevButton.width = 16;
        this.prevButton.height = 16;
        this.closeButton.width = 16;
        this.closeButton.height = 16;
    }

    public void resize()
    {
        this.children.clear();
        byte var1 = 24;
        this.nextButton.y = this.prevButton.y = this.closeButton.y = this.y + 2;
        this.prevButton.x = this.x + 52;
        this.nextButton.x = this.x + 70;
        this.closeButton.x = this.x + 88;
        this.children.add(this.nextButton);
        this.children.add(this.prevButton);
        this.children.add(this.closeButton);
        int var2 = this.y + var1;
        int var3 = this.height - var1;
        int var4 = var3 / 13;
        Potion[] var5 = this.getAvailablePotions();
        int var6 = var5.length / var4 + (var5.length % var4 > 0 ? 1 : 0);
        this.nextButton.show = this.prevButton.show = var6 > 1;

        if (this.page > var6 - 1 || this.page < 0)
        {
            this.page = 0;
        }

        int var7 = this.page * var4;

        for (int var8 = var7; var8 < var7 + var4 && var8 < var5.length; ++var8)
        {
            Potion var9 = var5[var8];
            _tmi_MgButton var10 = new _tmi_MgButton(StatCollector.translateToLocal(var9.getName()), this, Integer.valueOf(var9.id));
            var10.x = this.x;
            var10.y = var2;
            var10.width = this.width;
            var10.height = 12;
            var10.centerText = false;
            var2 += 13;
            this.children.add(var10);
        }
    }

    public int countAvailablePotions()
    {
        int var1 = 0;

        for (int var2 = 0; var2 < Potion.potionTypes.length; ++var2)
        {
            if (Potion.potionTypes[var2] != null)
            {
                ++var1;
            }
        }

        return var1;
    }

    public Potion[] getAvailablePotions()
    {
        int var1 = this.countAvailablePotions();
        Potion[] var2 = new Potion[var1];
        int var3 = 0;

        for (int var4 = 0; var4 < Potion.potionTypes.length; ++var4)
        {
            if (Potion.potionTypes[var4] != null)
            {
                var2[var3++] = Potion.potionTypes[var4];
            }
        }

        return var2;
    }

    public boolean onButtonPress(Object var1)
    {
        if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("next"))
            {
                ++this.page;
                this.resize();
            }
            else if (var2.equals("prev"))
            {
                --this.page;
                this.resize();
            }
            else if (var2.equals("close"))
            {
                this.show = false;
            }
        }
        else if (var1 instanceof Integer)
        {
            int var3 = ((Integer)var1).intValue();
            this.panel.pickerPicked(var3);
            this.show = false;
        }

        return true;
    }

    public boolean onButtonRightClick(Object var1)
    {
        return true;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            var1.drawRect(this.x, this.y, this.width, this.height, -301989888);
            var1.drawText(this.x + 1, this.y + 6, "Effect:", -1);
            this.drawChildren(var1, var2, var3);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return this.delegateClickToChildren(var1, var2, var3);
    }
}
