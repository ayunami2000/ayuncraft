package me.ayunami2000.ayuncraft.tmi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PotionEffect;

public class TMIPotionPanel extends _tmi_MgWidget implements _tmi_MgButtonHandler
{
    private ItemStack potionTypeRegular = new ItemStack(TMIItemInfo.addItemOffset(117), 1, 1);
    private ItemStack potionTypeSplash = new ItemStack(TMIItemInfo.addItemOffset(117), 1, 16385);
    private _tmi_MgItemButton potionTypeButton;
    private static boolean makeSplashPotion = false;
    private TMIPotionEffectPicker picker;
    private TMIPotionEffectControl pickerTarget;
    private _tmi_MgButton addButton;
    private _tmi_MgButton prevButton;
    private _tmi_MgButton nextButton;
    private _tmi_MgButton createButton;
    private _tmi_MgButton favoriteButton;
    private _tmi_MgTextField nameField;
    public int page;
    protected static List controls = new ArrayList();

    public TMIPotionPanel()
    {
        super(0, 0);
        this.potionTypeButton = new _tmi_MgItemButton("", this.potionTypeRegular, this, "potionType");
        this.picker = new TMIPotionEffectPicker(this);
        this.pickerTarget = null;
        this.nameField = new _tmi_MgTextField(Minecraft.getMinecraft().fontRenderer, "Name...", TMI.instance.controller);
        this.page = 0;
        this.createButton = new _tmi_MgButton("Make", this, "createPotion");
        this.createButton.width = 36;
        this.createButton.height = 18;
        this.favoriteButton = new _tmi_MgButton("Favorite", this, "favoritePotion");
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
        this.children.add(this.picker);
        this.children.add(this.potionTypeButton);
        this.children.add(this.createButton);
        this.children.add(this.favoriteButton);
        this.children.add(this.nextButton);
        this.children.add(this.prevButton);
        this.children.add(this.addButton);
        this.children.add(this.nameField);

        if (controls.size() > 0)
        {
            Iterator var1 = controls.iterator();

            while (var1.hasNext())
            {
                TMIPotionEffectControl var2 = (TMIPotionEffectControl)var1.next();
                var2.panel = this;
                this.children.add(var2);
            }
        }
        else
        {
            controls.add(new TMIPotionEffectControl(this));
            this.children.add(controls.get(0));
        }

        this.potionTypeButton.stack = makeSplashPotion ? this.potionTypeSplash : this.potionTypeRegular;
    }

    public void resize()
    {
        if (this.width < 106)
        {
            this.width = 106;
        }

        this.potionTypeButton.x = this.x;
        this.createButton.x = this.x + 20;
        this.favoriteButton.x = this.createButton.x + this.createButton.width + 2;
        this.potionTypeButton.y = this.createButton.y = this.favoriteButton.y = this.y + 8;
        this.nameField.width = this.width - 5;
        this.nameField.height = 14;
        this.nameField.x = this.x + 1;
        this.nameField.y = this.createButton.y + 21;
        List var1 = this.getEffects();
        int var2 = this.x;
        int var3 = this.y + 8 + 18 + 2 + 18;
        int var4 = var3;

        if (var1.size() > 0)
        {
            TMIPotionEffectControl var6;

            for (Iterator var5 = var1.iterator(); var5.hasNext(); var6.show = false)
            {
                var6 = (TMIPotionEffectControl)var5.next();
            }

            int var15 = ((TMIPotionEffectControl)var1.get(0)).width;
            int var16 = ((TMIPotionEffectControl)var1.get(0)).height;
            int var7 = this.width / var15;
            int var8 = (this.height - var3 - 16 - 2) / var16;
            int var9 = var7 * var8;
            int var10 = var1.size();
            int var11 = var10 / var9 + (var10 % var9 > 0 ? 1 : 0);
            this.nextButton.show = this.prevButton.show = var11 > 1;

            if (this.page > var11 - 1 || this.page < 0)
            {
                this.page = 0;
            }

            int var12 = this.page * var9;

            for (int var13 = 0; var13 < var8 && var12 < var1.size(); ++var13)
            {
                var2 = this.x;

                for (int var14 = 0; var14 < var7 && var12 < var1.size(); ++var14)
                {
                    ((TMIPotionEffectControl)var1.get(var12)).show = true;
                    ((TMIPotionEffectControl)var1.get(var12)).x = var2;
                    ((TMIPotionEffectControl)var1.get(var12)).y = var3;
                    ((TMIPotionEffectControl)var1.get(var12)).resize();
                    ++var12;
                    var2 += var15 + 2;
                }

                var3 += var16 + 2;
            }

            var4 = ((TMIPotionEffectControl)var1.get(var12 - 1)).y + var16 + 2;
        }

        this.addButton.x = this.x;
        this.prevButton.x = this.addButton.x + this.addButton.width + 2;
        this.nextButton.x = this.prevButton.x + this.prevButton.width + 2;
        this.addButton.y = this.prevButton.y = this.nextButton.y = var4;
    }

    public List getEffects()
    {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.children.iterator();

        while (var2.hasNext())
        {
            _tmi_MgWidget var3 = (_tmi_MgWidget)var2.next();

            if (var3 instanceof TMIPotionEffectControl)
            {
                var1.add((TMIPotionEffectControl)var3);
            }
        }

        return var1;
    }

    public ItemStack createCurrentPotion()
    {
        ItemStack var1 = new ItemStack(TMIItemInfo.addItemOffset(117), 64, makeSplashPotion ? 16384 : 1);
        Iterator var2 = this.getEffects().iterator();

        while (var2.hasNext())
        {
            TMIPotionEffectControl var3 = (TMIPotionEffectControl)var2.next();
            PotionEffect var4 = new PotionEffect(var3.effectId, var3.isInstant() ? 0 : var3.tickDuration, var3.level);
            TMIUtils.addEffectToPotion(var1, var4);
        }

        String var5 = this.nameField.value();

        if (!var5.equals(""))
        {
            TMIUtils.nameStack(var1, var5);
        }

        return var1;
    }

    public void openEffectPicker(TMIPotionEffectControl var1)
    {
        this.pickerTarget = var1;
        this.picker.z = -100;
        this.picker.width = this.width;
        this.picker.height = this.height;
        this.picker.x = this.x;
        this.picker.y = this.y;
        this.picker.show = true;
        this.picker.resize();
    }

    public void pickerPicked(int var1)
    {
        if (this.pickerTarget == null)
        {
            TMIPotionEffectControl var2 = new TMIPotionEffectControl(this);
            var2.effectId = var1;
            controls.add(var2);
            this.children.add(var2);
            this.resize();
        }
        else
        {
            this.pickerTarget.effectId = var1;
        }
    }

    public void removeEffectControl(TMIPotionEffectControl var1)
    {
        this.children.remove(var1);
        controls.remove(var1);
        this.resize();
    }

    public boolean onButtonPress(Object var1)
    {
        if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("potionType"))
            {
                makeSplashPotion = !makeSplashPotion;
                this.potionTypeButton.stack = makeSplashPotion ? this.potionTypeSplash : this.potionTypeRegular;
                return false;
            }

            ItemStack var3;

            if (var2.equals("createPotion"))
            {
                var3 = this.createCurrentPotion();
                TMIUtils.giveStack(var3, TMIConfig.getInstance());
                return false;
            }

            if (var2.equals("favoritePotion"))
            {
                var3 = this.createCurrentPotion();
                TMIConfig.getInstance().getFavorites().add(var3);
                TMIUtils.savePreferences(TMIConfig.getInstance());
                return false;
            }

            if (var2.equals("addEffect"))
            {
                this.openEffectPicker((TMIPotionEffectControl)null);
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
            this.drawChildren(var1, var2, var3);
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        return this.delegateClickToChildren(var1, var2, var3);
    }
}
