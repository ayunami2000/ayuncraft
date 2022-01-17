package me.ayunami2000.ayuncraft.tmi;

import java.util.Comparator;

public class _tmi_MgZOrder implements Comparator
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";

    public int compare(_tmi_MgWidget var1, _tmi_MgWidget var2)
    {
        return var1.z > var2.z ? 1 : (var1.z < var2.z ? -1 : 0);
    }

    public int compare(Object var1, Object var2)
    {
        return this.compare((_tmi_MgWidget)var1, (_tmi_MgWidget)var2);
    }
}
