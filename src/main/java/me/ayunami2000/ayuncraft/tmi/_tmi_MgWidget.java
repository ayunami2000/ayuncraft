package me.ayunami2000.ayuncraft.tmi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class _tmi_MgWidget
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    public int x;
    public int y;
    public int z;
    public int width;
    public int height;
    public boolean show;
    public boolean mouseOver;
    public List children;

    public _tmi_MgWidget(int var1, int var2)
    {
        this(var1, var2, 0, 0, 0);
    }

    public _tmi_MgWidget(int var1, int var2, int var3)
    {
        this(var1, var2, var3, 0, 0);
    }

    public _tmi_MgWidget(int var1, int var2, int var3, int var4, int var5)
    {
        this.show = true;
        this.mouseOver = false;
        this.children = new ArrayList();
        this.x = var1;
        this.y = var2;
        this.z = var3;
        this.width = var4;
        this.height = var5;
    }

    public static _tmi_MgZOrder getComparator()
    {
        return new _tmi_MgZOrder();
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3) {}

    public boolean click(int var1, int var2, int var3)
    {
        return true;
    }

    public boolean contains(int var1, int var2)
    {
        return this.show && var1 >= this.x && var1 <= this.x + this.width && var2 >= this.y && var2 <= this.y + this.height;
    }

    public void position(int var1, int var2, int var3, int var4, int var5)
    {
        this.x = var1;
        this.y = var2;
        this.z = var3;
        this.width = var4;
        this.height = var5;
        this.resize();
    }

    public void resize() {}

    protected boolean delegateClickToChildren(int var1, int var2, int var3)
    {
        Collections.sort(this.children, getComparator());
        Iterator var4 = this.children.iterator();
        _tmi_MgWidget var5;

        do
        {
            if (!var4.hasNext())
            {
                return true;
            }

            var5 = (_tmi_MgWidget)var4.next();
        }
        while (!var5.show || !var5.contains(var1, var2));

        return var5.click(var1, var2, var3);
    }

    protected void drawChildren(_tmi_MgCanvas var1, int var2, int var3)
    {
        Collections.sort(this.children, Collections.reverseOrder(getComparator()));
        Iterator var4 = this.children.iterator();

        while (var4.hasNext())
        {
            _tmi_MgWidget var5 = (_tmi_MgWidget)var4.next();
            var5.draw(var1, var2, var3);
        }
    }
}
