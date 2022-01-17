package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.Potion;
import net.minecraft.src.StatCollector;

public class TMIPotionEffectControl extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    public TMIPotionPanel panel;
    public int level = 0;
    public int tickDuration = 1200;
    public int effectId = 1;
    public static final int MAX_TIME = 12000;
    private _tmi_MgButton typeButton = new _tmi_MgButton("", this, "type");
    private _tmi_MgButton closeButton = new _tmi_MgButton("x", this, "close");
    private _tmi_MgButton levelButton = new _tmi_MgButton("", this, "level");
    private _tmi_MgButton timeButton = new _tmi_MgButton("", this, "time");

    public TMIPotionEffectControl(TMIPotionPanel var1)
    {
        super(0, 0);
        this.panel = var1;
        this.width = 106;
        this.height = 34;
        this.typeButton.height = this.closeButton.height = this.levelButton.height = this.timeButton.height = 16;
        this.children.add(this.typeButton);
        this.children.add(this.closeButton);
        this.children.add(this.levelButton);
        this.children.add(this.timeButton);
    }

    public TMIPotionEffectControl copy(TMIPotionPanel var1)
    {
        TMIPotionEffectControl var2 = new TMIPotionEffectControl(var1);
        var2.level = this.level;
        var2.tickDuration = this.tickDuration;
        var2.effectId = this.effectId;
        return var2;
    }

    public void resize()
    {
        this.typeButton.width = 88;
        this.closeButton.width = 16;
        this.levelButton.width = 52;
        this.timeButton.width = 52;
        this.typeButton.x = this.x;
        this.typeButton.centerText = false;
        this.closeButton.x = this.typeButton.x + this.typeButton.width + 2;
        this.closeButton.y = this.typeButton.y = this.y;
        this.levelButton.x = this.x;
        this.timeButton.x = this.levelButton.x + this.levelButton.width + 2;
        this.levelButton.y = this.timeButton.y = this.y + 18;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            try
            {
                this.typeButton.label = StatCollector.translateToLocal(Potion.potionTypes[this.effectId].getName());
                this.timeButton.show = !this.isInstant();
            }
            catch (Exception var7)
            {
                this.typeButton.label = "[Bad ID]";
            }

            this.levelButton.label = "" + (this.level + 1);
            int var4 = this.tickDuration / 20;
            int var5 = var4 / 60;
            int var6 = var4 % 60;
            this.timeButton.label = "" + var5 + ":" + (var6 < 10 ? "0" : "") + var6;
            this.drawChildren(var1, var2, var3);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return this.delegateClickToChildren(var1, var2, var3);
    }

    public boolean isInstant()
    {
        return this.effectId < Potion.potionTypes.length && Potion.potionTypes[this.effectId] != null && Potion.potionTypes[this.effectId].isInstant();
    }

    public boolean onButtonPress(Object var1)
    {
        if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("type"))
            {
                this.panel.openEffectPicker(this);
                return false;
            }

            if (var2.equals("close"))
            {
                this.panel.removeEffectControl(this);
                return false;
            }

            if (var2.equals("level"))
            {
                ++this.level;

                if (this.level > 3)
                {
                    this.level = 0;
                }

                return false;
            }

            if (var2.equals("time"))
            {
                this.tickDuration += 600;

                if (this.tickDuration > 12000)
                {
                    this.tickDuration = 600;
                }

                return false;
            }
        }

        return true;
    }

    public boolean onButtonRightClick(Object var1)
    {
        if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("level"))
            {
                --this.level;

                if (this.level < 0)
                {
                    this.level = 3;
                }

                return false;
            }

            if (var2.equals("time"))
            {
                this.tickDuration -= 600;

                if (this.tickDuration <= 0)
                {
                    this.tickDuration = 12000;
                }

                return false;
            }
        }

        return true;
    }
}
