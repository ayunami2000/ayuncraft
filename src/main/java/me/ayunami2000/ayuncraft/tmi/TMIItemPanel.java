package me.ayunami2000.ayuncraft.tmi;

import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemStack;

class TMIItemPanel extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    protected TMIController controller;
    protected List items;
    public static int page = 0;
    public int numPages = 0;
    public static final int SPACING = 18;
    protected ItemStack hoverItem = null;
    protected int hoverItemIndex = -1;
    private int marginLeft;
    private int marginTop;
    private int cols;
    private int rows;
    private int itemsPerPage;
    private _tmi_MgButton prevButton;
    private _tmi_MgButton nextButton;
    public _tmi_MgTextField textField;
    private static final String BUTTON_PREV = "prev";
    private static final String BUTTON_NEXT = "next";

    public TMIItemPanel(int var1, int var2, int var3, int var4, int var5, List var6, TMIController var7)
    {
        super(var1, var2, var3, var4, var5);
        this.items = var6;
        this.controller = var7;
        this.prevButton = new _tmi_MgButton("", this, "prev");
        this.prevButton.icon = TMIImages.iconPrev;
        this.prevButton.width = 12;
        this.prevButton.height = 12;
        this.children.add(this.prevButton);
        this.nextButton = new _tmi_MgButton("", this, "next");
        this.nextButton.icon = TMIImages.iconNext;
        this.nextButton.width = 12;
        this.nextButton.height = 12;
        this.children.add(this.nextButton);
        this.textField = new _tmi_MgTextField(Minecraft.getMinecraft().fontRenderer, "Search", var7);
        this.children.add(this.textField);
    }

    public boolean onButtonPress(Object var1)
    {
        if (var1 == "prev")
        {
            --page;
            return false;
        }
        else if (var1 == "next")
        {
            ++page;
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean onButtonRightClick(Object var1)
    {
        return true;
    }

    public void resize()
    {
        this.marginLeft = this.x + this.width % 18 / 2;
        this.marginTop = this.y + this.height % 18 / 2;
        this.cols = this.width / 18;
        this.rows = this.height / 18 - 1;
        this.itemsPerPage = this.rows * this.cols;
        this.numPages = (int)Math.ceil((double)(1.0F * (float)this.items.size() / (float)this.itemsPerPage));
        page = page < 0 ? this.numPages - 1 : (page >= this.numPages ? 0 : page);
        this.nextButton.x = this.x + this.width - this.nextButton.width - 3;
        this.prevButton.x = this.nextButton.x - this.prevButton.width - 1;
        this.nextButton.y = this.y + 1;
        this.prevButton.y = this.y + 1;
        this.textField.x = this.x + 2;
        this.textField.y = this.y + 2;
        this.textField.height = 14;
        this.textField.width = this.prevButton.x - this.textField.x - 2;
    }

    public ItemStack getHoverItem()
    {
        return this.hoverItem;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            int var4 = 0;
            int var5 = 1;
            this.hoverItem = null;
            this.hoverItemIndex = -1;
            var1.hardSetFlatMode(false);

            for (int var6 = page * this.itemsPerPage; var6 < this.itemsPerPage * (page + 1) && var6 < this.items.size(); ++var6)
            {
                ItemStack var7 = (ItemStack)this.items.get(var6);
                int var8 = this.marginLeft + var4 * 18;
                int var9 = this.marginTop + var5 * 18;

                if (var2 >= var8 && var2 < var8 + 18 && var3 >= var9 && var3 < var9 + 18)
                {
                    this.hoverItem = var7;
                    this.hoverItemIndex = var6;
                }

                var1.drawItem(var8, var9, var7);

                if (var7.itemID == 52)
                {
                    String var10 = TMIUtils.itemDisplayName(var7);
                    Pattern var11 = Pattern.compile("\u00a7.");
                    var10 = var11.matcher(var10).replaceAll("");
                    var10 = var10.substring(0, 3);
                    var1.drawText(var8 + 1, var9 + 1, var10, -2236963, 0.5F);
                    var1.hardSetFlatMode(false);
                }
                else if (var7.itemID == 9 || var7.itemID == 11)
                {
                    var1.drawText(var8 + 1, var9 + 1, "Static", -2236963, 0.5F);
                    var1.hardSetFlatMode(false);
                }

                ++var4;

                if (var4 == this.cols)
                {
                    var4 = 0;
                    ++var5;
                }
            }

            this.drawChildren(var1, var2, var3);
            var1.drawTextCentered(this.prevButton.x, this.prevButton.y + this.prevButton.height + 2, this.prevButton.width * 2 + 1, 6, "" + (page + 1) + "/" + this.numPages, -2236963, 0.5F);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        ItemStack var4 = TMIUtils.getHeldItem();

        if (var4 != null)
        {
            TMIUtils.deleteHeldItem();
            return false;
        }
        else
        {
            return this.hoverItem != null ? this.controller.onItemEvent(this.hoverItem, var3) : this.delegateClickToChildren(var1, var2, var3);
        }
    }
}
