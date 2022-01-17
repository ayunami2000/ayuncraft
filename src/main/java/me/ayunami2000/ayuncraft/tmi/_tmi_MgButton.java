package me.ayunami2000.ayuncraft.tmi;

public class _tmi_MgButton extends _tmi_MgWidget
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    public String label;
    public _tmi_MgButtonHandler controller;
    public Object data;
    public _tmi_MgImage icon;
    public boolean showState;
    public boolean state;
    public boolean agateType;
    public boolean centerText;

    public _tmi_MgButton(String var1, _tmi_MgButtonHandler var2, Object var3)
    {
        this(0, 0, 0, 0, 0, var1, var2, var3);
    }

    public _tmi_MgButton(int var1, int var2, int var3, int var4, int var5, String var6, _tmi_MgButtonHandler var7, Object var8)
    {
        super(var1, var2, var3, var4, var5);
        this.showState = false;
        this.state = false;
        this.agateType = false;
        this.centerText = true;
        this.label = var6;
        this.controller = var7;
        this.data = var8;
    }

    public void setOwnWidth(_tmi_MgCanvas var1)
    {
        this.width = var1.getTextWidth(this.label, this.scaleFactor()) + this.graphicWidth(var1) + this.getMargin();
    }

    public int graphicWidth(_tmi_MgCanvas var1)
    {
        int var2 = 0;

        if (this.icon != null)
        {
            var2 += this.icon.width;

            if (this.label != null && this.label.length() > 0)
            {
                var2 += 2;
            }
        }

        if (this.showState)
        {
            var2 += TMIImages.buttonStateOff.width;

            if (this.label != null && this.label.length() > 0)
            {
                ++var2;
            }
        }

        return var2;
    }

    public int getMargin()
    {
        return this.label != null && this.label.length() > 0 ? 6 : 2;
    }

    protected float scaleFactor()
    {
        return this.agateType ? 0.5F : 1.0F;
    }

    protected int neededWidth(_tmi_MgCanvas var1, String var2)
    {
        int var3 = var1.getTextWidth(var2, this.scaleFactor());
        var3 += this.graphicWidth(var1);

        if (!this.centerText)
        {
            var3 += this.getMargin() * 2;
        }

        return var3;
    }

    protected boolean drawGraphic(_tmi_MgCanvas var1, int var2)
    {
        if (this.icon != null)
        {
            int var3 = this.y + (this.height - this.icon.height) / 2;
            var1.drawChrome(var2, var3, this.icon);

            if (this.showState)
            {
                _tmi_MgImage var4 = this.state ? TMIImages.buttonStateOn : TMIImages.buttonStateOff;
                int var10000 = this.y + (this.height - var4.height) / 2;
                var1.drawChrome(var2 + this.icon.width, var3, var4);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            var1.drawRect(this.x, this.y, this.width, this.height, this.contains(var2, var3) ? -297791480 : -301989888);
            var1.drawText(this.x, this.y, "", -1);
            String var4 = this.label;
            int var5 = var1.getTextWidth(var4, this.scaleFactor());
            int var6;

            for (var6 = this.graphicWidth(var1); var5 + var6 > this.width && var4.length() > 0; var5 = var1.getTextWidth(var4, this.scaleFactor()))
            {
                var4 = var4.substring(0, var4.length() - 1);
            }

            int var7 = var5 + var6;
            int var8 = this.x + this.getMargin();

            if (this.centerText)
            {
                var8 = this.x + (this.width - var7) / 2;
            }

            int var9 = this.y + (this.height - 8) / 2;
            boolean var10 = this.drawGraphic(var1, var8);
            var8 += this.graphicWidth(var1);

            if (var10 && var5 > 0)
            {
                var8 += 2;
            }

            var1.drawText(var8, var9, var4, -1, this.scaleFactor());
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return var3 == 0 ? this.controller.onButtonPress(this.data) : (var3 == 1 ? this.controller.onButtonRightClick(this.data) : true);
    }
}
