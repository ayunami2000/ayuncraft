package me.ayunami2000.ayuncraft.tmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Enchantment;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StatCollector;

public class TMIEnchantPanel extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private Item selectedItem = null;
    private Map enchantmentSettings = new HashMap();
    private List items;
    _tmi_MgButton prevButton;
    _tmi_MgButton nextButton;
    _tmi_MgButton createButton;
    _tmi_MgButton favoriteButton;
    _tmi_MgItemButton itemButton;
    public int page = 0;
    private _tmi_MgTooltipHandler tooltipHandler;
    private TMIEnchantItemPicker itemPicker;
    private boolean showItemPicker = false;
    private _tmi_MgTextField nameField;

    public TMIEnchantPanel(_tmi_MgTooltipHandler var1)
    {
        super(0, 0);
        this.nameField = new _tmi_MgTextField(Minecraft.getMinecraft().fontRenderer, "Name...", TMI.instance.controller);
        this.tooltipHandler = var1;
        this.items = TMIConfig.getInstance().getEnchantableItems();

        if (this.items.size() > 0)
        {
            this.selectedItem = (Item)this.items.get(0);
        }

        this.itemButton = new _tmi_MgItemButton("", this, "item");
        this.nextButton = new _tmi_MgButton("", this, "next");
        this.nextButton.icon = TMIImages.iconNext;
        this.nextButton.width = 16;
        this.nextButton.height = 16;
        this.prevButton = new _tmi_MgButton("", this, "prev");
        this.prevButton.icon = TMIImages.iconPrev;
        this.prevButton.width = 16;
        this.prevButton.height = 16;
        this.createButton = new _tmi_MgButton("Make", this, "createEnchanted");
        this.createButton.width = 36;
        this.createButton.height = 18;
        this.favoriteButton = new _tmi_MgButton("Favorite", this, "favoriteEnchanted");
        this.favoriteButton.width = 48;
        this.favoriteButton.height = 18;
        this.itemPicker = new TMIEnchantItemPicker(this);
        this.itemPicker.show = false;
    }

    public void resize()
    {
        this.createChildren();
        this.nextButton.x = this.x + this.width - this.nextButton.width - 2;
        this.nextButton.y = this.y + this.height - this.nextButton.height - 2;
        this.prevButton.x = this.nextButton.x - this.prevButton.width - 1;
        this.prevButton.y = this.nextButton.y;
        this.itemButton.x = this.x;
        this.itemButton.y = this.y + 8;
        this.createButton.x = this.x + 20;
        this.createButton.y = this.y + 8;
        this.favoriteButton.x = this.createButton.x + this.createButton.width + 2;
        this.favoriteButton.y = this.y + 8;
        this.nameField.width = this.width - 5;
        this.nameField.height = 14;
        this.nameField.x = this.x + 1;
        this.nameField.y = this.itemButton.y + 21;
        this.itemPicker.x = this.x;
        this.itemPicker.y = this.itemButton.y + this.itemButton.height + 1;
        this.itemPicker.width = this.width;
        this.itemPicker.resize();
        this.itemPicker.z = -100;
    }

    private void createChildren()
    {
        this.children.clear();
        this.children.add(this.nextButton);
        this.children.add(this.prevButton);
        this.children.add(this.createButton);
        this.children.add(this.favoriteButton);
        this.children.add(this.itemButton);
        this.children.add(this.itemPicker);
        this.children.add(this.nameField);
        byte var1 = 14;
        byte var2 = 47;
        int var3 = this.height - this.nextButton.height - var2 - 4;
        int var4 = var3 / var1;
        ArrayList var5 = new ArrayList(TMIEnchanting.currentEnchantmentLevels.keySet());
        int var6 = var5.size();

        if (this.page < 0)
        {
            this.page = 0;
        }
        else if (this.page > var6 / var4)
        {
            this.page = var6 / var4;
        }

        int var7 = this.page * var4;

        for (int var8 = 0; var8 < var4 && var8 + var7 < var6; ++var8)
        {
            Enchantment var9 = (Enchantment)var5.get(var8 + var7);
            int var10 = this.y + var2 + var8 * var1;
            String var11 = StatCollector.translateToLocal(var9.getName());
            boolean var12 = TMIUtils.isEnchantmentNormallyPossible(var9, TMIEnchanting.currentItem);
            TMIEnchantControl var13 = new TMIEnchantControl(this.x, var10, 100, this.width - 3, 14, var11, var9, var12);
            this.children.add(var13);
        }
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            this.itemButton.stack = TMIEnchanting.currentItem;
            this.drawChildren(var1, var2, var3);

            if (this.createButton.contains(var2, var3) && TMIConfig.isMultiplayer())
            {
                this.tooltipHandler.setTooltip("May not work on MP servers");
            }
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        ItemStack var4 = TMIUtils.getHeldItem();

        if (var4 == null)
        {
            return this.delegateClickToChildren(var1, var2, var3);
        }
        else
        {
            this.chooseItem(var4);
            return false;
        }
    }

    public void chooseItem(ItemStack var1)
    {
        TMIEnchanting.setItem(var1);
        this.nameField.setValue("");
        this.createChildren();
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
                return false;
            }

            if (var2.equals("prev"))
            {
                --this.page;
                this.resize();
                return false;
            }

            if (var2.equals("item"))
            {
                if (this.itemPicker.show)
                {
                    this.itemPicker.show = false;
                }
                else
                {
                    this.itemPicker.open();
                }

                this.resize();
                return false;
            }

            ItemStack var3;
            String var4;

            if (var2.equals("createEnchanted"))
            {
                var3 = TMIEnchanting.createStack();
                var4 = this.nameField.value();

                if (!var4.equals(""))
                {
                    TMIUtils.nameStack(var3, var4);
                }

                TMIUtils.giveStack(var3, TMIConfig.getInstance());
                return false;
            }

            if (var2.equals("favoriteEnchanted"))
            {
                var3 = TMIEnchanting.createStack();
                var4 = this.nameField.value();

                if (!var4.equals(""))
                {
                    TMIUtils.nameStack(var3, var4);
                }

                TMIConfig.getInstance().getFavorites().add(var3);
                TMIUtils.savePreferences(TMIConfig.getInstance());
                return false;
            }
        }

        return true;
    }

    public boolean onButtonRightClick(Object var1)
    {
        return true;
    }
}
