package me.ayunami2000.ayuncraft.tmi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class _tmi_MgTabView extends _tmi_MgWidget
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private Map children = new LinkedHashMap();
    private Map icons = new HashMap();
    private Map iconsSelected = new HashMap();
    private String activeChildName = null;
    public int tabsHeight = 18;

    public _tmi_MgTabView()
    {
        super(0, 0);
    }

    public _tmi_MgTabView(int var1, int var2, int var3, int var4, int var5)
    {
        super(var1, var2, var3, var4, var5);
    }

    public void addChild(String var1, _tmi_MgImage var2, _tmi_MgImage var3, _tmi_MgWidget var4)
    {
        this.children.put(var1, var4);
        this.icons.put(var1, var2);
        this.iconsSelected.put(var1, var3);

        if (this.activeChildName == null)
        {
            this.activeChildName = var1;
        }
    }

    public _tmi_MgWidget getChild(String var1)
    {
        return (_tmi_MgWidget)this.children.get(var1);
    }

    public _tmi_MgWidget getActiveChild()
    {
        return this.getChild(this.getActiveChildName());
    }

    public String getActiveChildName()
    {
        return this.activeChildName;
    }

    public void setActiveChild(String var1)
    {
        _tmi_MgWidget var3;

        for (Iterator var2 = this.children.values().iterator(); var2.hasNext(); var3.show = false)
        {
            var3 = (_tmi_MgWidget)var2.next();
        }

        ((_tmi_MgWidget)this.children.get(var1)).show = true;
        this.activeChildName = var1;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        int var4 = this.x;
        int var5 = this.y;
        int var9;

        for (Iterator var6 = this.children.keySet().iterator(); var6.hasNext(); var4 += var9)
        {
            String var7 = (String)var6.next();
            _tmi_MgImage var8;

            if (var7 == this.activeChildName)
            {
                var8 = (_tmi_MgImage)this.iconsSelected.get(var7);
            }
            else
            {
                var8 = (_tmi_MgImage)this.icons.get(var7);
            }

            var9 = var8.width + 6;
            var1.drawChrome(var4 + 3, var5 + (this.tabsHeight - var8.height) / 2, var8);
        }

        if (this.activeChildName != null)
        {
            this.getActiveChild().draw(var1, var2, var3);
        }
    }

    public void resize()
    {
        if (this.activeChildName != null)
        {
            _tmi_MgWidget var1 = this.getActiveChild();
            var1.x = this.x;
            var1.y = this.y + this.tabsHeight;
            var1.width = this.width;
            var1.height = this.height - this.tabsHeight;
            var1.resize();
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        int var4 = var1 - this.x;
        int var5 = var2 - this.y;

        if (var4 > 0 && var4 < this.width && var5 > 0 && var5 < this.tabsHeight)
        {
            int var6 = 0;
            _tmi_MgImage var9;

            for (Iterator var7 = this.children.keySet().iterator(); var7.hasNext(); var6 += var9.width + 6)
            {
                String var8 = (String)var7.next();
                var9 = (_tmi_MgImage)this.icons.get(var8);

                if (var4 <= var6 + var9.width + 6)
                {
                    this.setActiveChild(var8);
                    return false;
                }
            }
        }
        else if (this.activeChildName != null)
        {
            return this.getActiveChild().click(var1, var2, var3);
        }

        return true;
    }
}
