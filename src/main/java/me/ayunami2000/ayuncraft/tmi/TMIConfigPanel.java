package me.ayunami2000.ayuncraft.tmi;

public class TMIConfigPanel extends _tmi_MgWidget
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";

    public TMIConfigPanel()
    {
        super(0, 0);
    }

    public void resize() {}

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            this.drawChildren(var1, var2, var3);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return this.delegateClickToChildren(var1, var2, var3);
    }
}
