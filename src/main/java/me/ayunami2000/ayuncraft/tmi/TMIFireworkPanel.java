package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StatCollector;

public class TMIFireworkPanel extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    private ItemStack firework = new ItemStack(TMIItemInfo.addItemOffset(145), 64, 0);
    private ItemStack fireworkCharge = new ItemStack(TMIItemInfo.addItemOffset(146), 64, 0);
    private _tmi_MgItemButton itemButton;
    private boolean makeCharge;
    _tmi_MgButton addButton;
    _tmi_MgButton prevButton;
    _tmi_MgButton nextButton;
    _tmi_MgButton createButton;
    _tmi_MgButton favoriteButton;
    _tmi_MgButton typeButton;
    _tmi_MgButton heightButton;
    _tmi_MgButton trailButton;
    _tmi_MgButton flickerButton;
    _tmi_MgButton[] colorButtons;
    _tmi_MgButton[] fadeButtons;
    private _tmi_MgTextField nameField;
    public int page;
    public static boolean trail = false;
    public static boolean flicker = false;
    public static int height = 1;
    public static int type = 0;
    public static int[] colors = new int[] {15, -1, -1};
    public static int[] fadeColors = new int[] { -1, -1, -1};

    public TMIFireworkPanel()
    {
        super(0, 0);
        this.itemButton = new _tmi_MgItemButton("", this.firework, this, "itemType");
        this.makeCharge = false;
        this.colorButtons = new _tmi_MgButton[3];
        this.fadeButtons = new _tmi_MgButton[3];
        this.nameField = new _tmi_MgTextField(Minecraft.getMinecraft().fontRenderer, "Name...", TMI.instance.controller);
        this.page = 0;
        this.createButton = new _tmi_MgButton("Make", this, "make");
        this.createButton.width = 36;
        this.createButton.height = 18;
        this.favoriteButton = new _tmi_MgButton("Favorite", this, "favorite");
        this.favoriteButton.width = 48;
        this.favoriteButton.height = 18;
        this.nextButton = new _tmi_MgButton("", this, "next");
        this.nextButton.icon = TMIImages.iconNext;
        this.nextButton.width = 16;
        this.nextButton.height = 16;
        this.prevButton = new _tmi_MgButton("", this, "prev");
        this.prevButton.icon = TMIImages.iconPrev;
        this.prevButton.width = 16;
        this.prevButton.height = 16;
        this.addButton = new _tmi_MgButton("Add effect...", this, "addEffect");
        this.addButton.width = 70;
        this.addButton.height = 16;
        this.typeButton = new _tmi_MgButton("", this, "type");
        this.heightButton = new _tmi_MgButton("", this, "height");
        this.trailButton = new _tmi_MgButton("", this, "trail");
        this.flickerButton = new _tmi_MgButton("", this, "flicker");
        this.typeButton.width = this.heightButton.width = this.trailButton.width = this.flickerButton.width = 52;
        this.typeButton.height = this.heightButton.height = this.trailButton.height = this.flickerButton.height = 16;
        int var1;

        for (var1 = 0; var1 < this.colorButtons.length; ++var1)
        {
            this.colorButtons[var1] = new _tmi_MgButton("", this, Integer.valueOf(var1));
            this.colorButtons[var1].height = 14;
            this.children.add(this.colorButtons[var1]);
        }

        for (var1 = 0; var1 < this.fadeButtons.length; ++var1)
        {
            this.fadeButtons[var1] = new _tmi_MgButton("", this, Integer.valueOf(-var1 - 1));
            this.fadeButtons[var1].height = 14;
            this.children.add(this.fadeButtons[var1]);
        }

        this.children.add(this.itemButton);
        this.children.add(this.createButton);
        this.children.add(this.favoriteButton);
        this.children.add(this.typeButton);
        this.children.add(this.heightButton);
        this.children.add(this.trailButton);
        this.children.add(this.flickerButton);
        this.children.add(this.nameField);
    }

    public void resize()
    {
        if (this.width < 106)
        {
            this.width = 106;
        }

        this.itemButton.x = this.x;
        this.createButton.x = this.x + 20;
        this.favoriteButton.x = this.createButton.x + this.createButton.width + 2;
        this.itemButton.y = this.createButton.y = this.favoriteButton.y = this.y + 8;
        this.typeButton.x = this.x;
        this.flickerButton.x = this.heightButton.x = this.typeButton.x + this.typeButton.width + 2;
        this.typeButton.y = this.flickerButton.y = this.itemButton.y + 20;
        this.trailButton.x = this.x;
        this.trailButton.y = this.heightButton.y = this.typeButton.y + 18;
        this.nameField.x = this.x + 1;
        this.nameField.width = this.width - 5;
        this.nameField.y = this.trailButton.y + 19;
        int var1 = this.nameField.y + 18 + 18;
        int var2;

        for (var2 = 0; var2 < this.colorButtons.length; ++var2)
        {
            this.colorButtons[var2].width = this.width;
            this.colorButtons[var2].x = this.x;
            this.colorButtons[var2].y = var1;
            var1 += 15;
        }

        var1 += 18;

        for (var2 = 0; var2 < this.fadeButtons.length; ++var2)
        {
            this.fadeButtons[var2].width = this.width;
            this.fadeButtons[var2].x = this.x;
            this.fadeButtons[var2].y = var1;
            var1 += 15;
        }
    }

    public ItemStack createCurrentItem()
    {
        ItemStack var1 = TMIUtils.copyStack(this.itemButton.stack);
        var1.stackTagCompound = new NBTTagCompound();
        int var2 = 0;
        int var3;

        for (var3 = 0; var3 < colors.length; ++var3)
        {
            if (colors[var3] > -1 && colors[var3] < 16)
            {
                ++var2;
            }
        }

        var3 = 0;

        for (int var4 = 0; var4 < fadeColors.length; ++var4)
        {
            if (fadeColors[var4] > -1 && fadeColors[var4] < 16)
            {
                ++var3;
            }
        }

        int[] var9 = new int[var2];
        int var5 = 0;

        for (int var6 = 0; var6 < colors.length; ++var6)
        {
            if (colors[var6] > -1 && colors[var6] < 16)
            {
                var9[var5++] = ItemDye.dyeColors[colors[var6]];
            }
        }

        int[] var10 = null;

        if (var3 > 0)
        {
            var10 = new int[var3];
            var5 = 0;

            for (int var7 = 0; var7 < fadeColors.length; ++var7)
            {
                if (fadeColors[var7] > -1 && fadeColors[var7] < 16)
                {
                    var10[var5++] = ItemDye.dyeColors[fadeColors[var7]];
                }
            }
        }

        if (var9 == null || var9.length < 1)
        {
            var9 = new int[] {16777215};
        }

        NBTTagCompound var11 = TMIUtils.makeExplosionTag(type, var9, var10, flicker, trail);

        if (this.makeCharge)
        {
            var1.stackTagCompound.setCompoundTag("Explosion", var11);
        }
        else
        {
            NBTTagCompound var8 = TMIUtils.makeFireworksTag(height, new NBTTagCompound[] {var11});
            var1.stackTagCompound.setCompoundTag("Fireworks", var8);
        }

        String var12 = this.nameField.value();

        if (var12 != null && !var12.equals(""))
        {
            TMIUtils.nameStack(var1, var12);
        }

        return var1;
    }

    public boolean onButtonPress(Object var1)
    {
        if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("itemType"))
            {
                this.makeCharge = !this.makeCharge;
                this.itemButton.stack = this.makeCharge ? this.fireworkCharge : this.firework;
                return false;
            }

            ItemStack var3;

            if (var2.equals("make"))
            {
                var3 = this.createCurrentItem();
                TMIUtils.giveStack(var3, TMIConfig.getInstance());
                return false;
            }

            if (var2.equals("favorite"))
            {
                var3 = this.createCurrentItem();
                TMIConfig.getInstance().getFavorites().add(var3);
                TMIUtils.savePreferences(TMIConfig.getInstance());
                return false;
            }

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
            else if (var2.equals("type"))
            {
                ++type;

                if (type > 4)
                {
                    type = 0;
                }
            }
            else if (var2.equals("height"))
            {
                ++height;

                if (height > 5)
                {
                    height = 1;
                }
            }
            else if (var2.equals("trail"))
            {
                trail = !trail;
            }
            else if (var2.equals("flicker"))
            {
                flicker = !flicker;
            }
        }
        else if (var1 instanceof Integer)
        {
            int var4 = ((Integer)var1).intValue();

            if (var4 < 0)
            {
                int var5 = -(var4 + 1);
                ++fadeColors[var5];

                if (fadeColors[var5] > 15)
                {
                    fadeColors[var5] = -1;
                }
            }
            else
            {
                ++colors[var4];

                if (colors[var4] > 15)
                {
                    colors[var4] = var4 == 0 ? 0 : -1;
                }
            }
        }

        return true;
    }

    public boolean onButtonRightClick(Object var1)
    {
        if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("type"))
            {
                --type;

                if (type < 0)
                {
                    type = 4;
                }
            }
            else if (var2.equals("height"))
            {
                --height;

                if (height < 1)
                {
                    height = 5;
                }
            }
        }
        else if (var1 instanceof Integer)
        {
            int var4 = ((Integer)var1).intValue();

            if (var4 < 0)
            {
                int var3 = -(var4 + 1);
                --fadeColors[var3];

                if (fadeColors[var3] < -1)
                {
                    fadeColors[var3] = 15;
                }
            }
            else
            {
                --colors[var4];

                if (colors[var4] < -1 || var4 == 0 && colors[var4] < 0)
                {
                    colors[var4] = 15;
                }
            }
        }

        return true;
    }

    public void reLabel()
    {
        this.heightButton.show = !this.makeCharge;
        this.flickerButton.label = flicker ? "Flicker" : "No flicker";
        this.trailButton.label = trail ? "Trail" : "No trail";
        this.heightButton.label = "Flight: " + height;
        this.typeButton.label = StatCollector.translateToLocal("item.fireworksCharge.type." + type);
        int var1;

        for (var1 = 0; var1 < this.colorButtons.length; ++var1)
        {
            if (colors[var1] == -1)
            {
                this.colorButtons[var1].label = "None";
            }
            else
            {
                this.colorButtons[var1].label = StatCollector.translateToLocal("item.fireworksCharge." + ItemDye.dyeColorNames[colors[var1]]);
            }
        }

        for (var1 = 0; var1 < this.fadeButtons.length; ++var1)
        {
            if (fadeColors[var1] == -1)
            {
                this.fadeButtons[var1].label = "None";
            }
            else
            {
                this.fadeButtons[var1].label = StatCollector.translateToLocal("item.fireworksCharge." + ItemDye.dyeColorNames[fadeColors[var1]]);
            }
        }
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            this.reLabel();
            var1.drawText(this.x + 1, this.colorButtons[0].y - 12, "Colors:", -1);
            var1.drawText(this.x + 1, this.fadeButtons[0].y - 12, "Fade colors:", -1);
            this.drawChildren(var1, var2, var3);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return this.delegateClickToChildren(var1, var2, var3);
    }
}
