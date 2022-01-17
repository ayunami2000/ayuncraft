package me.ayunami2000.ayuncraft.tmi;

import java.util.List;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.src.ItemStack;

public class TMIView implements _tmi_MgTooltipHandler
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private _tmi_MgCanvas canvas;
    private TMIConfig config;
    private TMIController controller;
    private boolean widgetsCreated = false;
    private String activeTooltip = null;
    private static String savedTabName = "items";
    private _tmi_MgButton prevButton;
    private _tmi_MgButton nextButton;
    private _tmi_MgButton trashButton;
    private _tmi_MgButton[] stateButtons;
    private _tmi_MgButton[] deleteButtons;
    public TMIItemPanel itemPanel;
    public _tmi_MgTabView tabView = new _tmi_MgTabView();
    public TMIEnchantPanel enchantPanel = new TMIEnchantPanel(this);
    public TMIConfigPanel configPanel = new TMIConfigPanel();
    public TMIFavoritesPanel favoritesPanel;
    public TMIPotionPanel potionPanel = new TMIPotionPanel();
    public TMIFireworkPanel fireworkPanel = new TMIFireworkPanel();
    public _tmi_MgButton rain;
    public _tmi_MgButton survival;
    public _tmi_MgButton creative;
    public _tmi_MgButton adventure;
    public _tmi_MgButton delete;
    public _tmi_MgButton noon;
    public _tmi_MgButton dawn;
    public _tmi_MgButton dusk;
    public _tmi_MgButton midnight;
    public _tmi_MgButton difficulty;
    public _tmi_MgButton health;

    public TMIView(_tmi_MgCanvas var1, TMIConfig var2, TMIController var3)
    {
        this.canvas = var1;
        this.config = var2;
        this.controller = var3;
        this.stateButtons = new _tmi_MgButton[this.config.getNumSaves()];
        this.deleteButtons = new _tmi_MgButton[this.config.getNumSaves()];
        this.createWidgets();
    }

    public void createWidgets()
    {
        this.itemPanel = new TMIItemPanel(0, 0, 0, 0, 0, this.config.getItems(), this.controller);
        this.favoritesPanel = new TMIFavoritesPanel(this.config, this.controller);
        this.tabView.addChild("items", TMIImages.iconChest, TMIImages.iconChestLight, this.itemPanel);
        this.tabView.addChild("favorites", TMIImages.iconStar, TMIImages.iconStarLight, this.favoritesPanel);
        this.tabView.addChild("enchant", TMIImages.iconBook, TMIImages.iconBookLight, this.enchantPanel);
        TMIConfig var10000 = this.config;

        if (!TMIConfig.isMultiplayer())
        {
            this.tabView.addChild("potions", TMIImages.iconPotion, TMIImages.iconPotion, this.potionPanel);
            this.tabView.addChild("fireworks", TMIImages.iconFirework, TMIImages.iconFirework, this.fireworkPanel);
        }

        this.canvas.widgets.add(this.tabView);
        this.tabView.setActiveChild(savedTabName);
        this.delete = new _tmi_MgButton("", this.controller, "deleteMode");
        this.delete.icon = TMIImages.iconDelete;
        this.delete.showState = true;
        this.delete.setOwnWidth(this.canvas);
        this.delete.height = 14;
        this.canvas.widgets.add(this.delete);
        this.rain = new _tmi_MgButton("", this.controller, "rain");
        this.rain.showState = true;
        this.rain.icon = TMIImages.iconRain;
        this.rain.setOwnWidth(this.canvas);
        this.rain.height = 14;
        this.canvas.widgets.add(this.rain);
        this.survival = new _tmi_MgButton("", this.controller, "survival");
        this.survival.icon = TMIImages.iconModeS;
        this.survival.setOwnWidth(this.canvas);
        this.survival.height = 14;
        this.canvas.widgets.add(this.survival);
        this.creative = new _tmi_MgButton("", this.controller, "creative");
        this.creative.icon = TMIImages.iconModeC;
        this.creative.setOwnWidth(this.canvas);
        this.creative.height = 14;
        this.canvas.widgets.add(this.creative);
        this.adventure = new _tmi_MgButton("", this.controller, "adventure");
        this.adventure.icon = TMIImages.iconModeA;
        this.adventure.setOwnWidth(this.canvas);
        this.adventure.height = 14;
        this.canvas.widgets.add(this.adventure);
        this.noon = new _tmi_MgButton("", this.controller, "noon");
        this.noon.icon = TMIImages.iconNoon;
        this.noon.setOwnWidth(this.canvas);
        this.noon.height = 14;
        this.canvas.widgets.add(this.noon);
        this.dawn = new _tmi_MgButton("", this.controller, "dawn");
        this.dawn.icon = TMIImages.iconDawn;
        this.dawn.setOwnWidth(this.canvas);
        this.dawn.height = 14;
        this.canvas.widgets.add(this.dawn);
        this.dusk = new _tmi_MgButton("", this.controller, "dusk");
        this.dusk.icon = TMIImages.iconDusk;
        this.dusk.setOwnWidth(this.canvas);
        this.dusk.height = 14;
        this.canvas.widgets.add(this.dusk);
        this.midnight = new _tmi_MgButton("", this.controller, "midnight");
        this.midnight.icon = TMIImages.iconMidnight;
        this.midnight.setOwnWidth(this.canvas);
        this.midnight.height = 14;
        this.canvas.widgets.add(this.midnight);
        this.difficulty = new _tmi_MgButton("", this.controller, "difficulty");
        this.difficulty.icon = TMIImages.iconDifficulty;
        this.difficulty.setOwnWidth(this.canvas);
        this.difficulty.height = 14;
        this.canvas.widgets.add(this.difficulty);
        this.health = new _tmi_MgButton("", this.controller, "health");
        this.health.icon = TMIImages.iconHeart;
        this.health.setOwnWidth(this.canvas);
        this.health.height = 14;
        this.canvas.widgets.add(this.health);
        this.delete.x = 2;
        this.delete.y = 2;
        this.canvas.arrangeHorizontally(1, 1001, new _tmi_MgWidget[] {this.delete, this.survival, this.creative, this.adventure, this.rain, this.dawn, this.noon, this.dusk, this.midnight, this.difficulty, this.health});
        this.stateButtons = new _tmi_MgButton[this.config.getNumSaves()];

        for (int var1 = 0; var1 < this.config.getNumSaves(); ++var1)
        {
            this.stateButtons[var1] = new _tmi_MgButton("Save " + (var1 + 1), this.controller, new TMIStateButtonData(var1, 0));
            this.canvas.widgets.add(this.stateButtons[var1]);
            this.deleteButtons[var1] = new _tmi_MgButton("x", this.controller, new TMIStateButtonData(var1, 1));
            this.canvas.widgets.add(this.deleteButtons[var1]);
        }

        this.widgetsCreated = true;
    }

    public void layout(int var1, int var2, int var3, int var4)
    {
        int var5 = (var1 - var3) / 2;

        if (!this.widgetsCreated)
        {
            this.createWidgets();
        }

        this.itemPanel.x = (var1 + var3) / 2 + 5;
        this.itemPanel.y = 20;
        this.itemPanel.resize();
        this.tabView.x = (var1 + var3) / 2 + 5;
        this.tabView.y = 0;
        this.tabView.width = var1 - this.itemPanel.x - 2;
        this.tabView.height = var2 - 1;
        savedTabName = this.tabView.getActiveChildName();
        this.tabView.resize();
        this.rain.state = TMIUtils.isRaining();
        this.delete.state = this.controller.deleteMode;
        int var6 = TMIUtils.getGameMode();
        this.survival.icon = var6 == 0 ? TMIImages.iconModeSs : TMIImages.iconModeS;
        this.creative.icon = var6 == 1 ? TMIImages.iconModeCs : TMIImages.iconModeC;
        this.adventure.icon = var6 == 2 ? TMIImages.iconModeAs : TMIImages.iconModeA;
        this.canvas.drawRect(0, 0, var1, 18, -16445675);
        StringBuilder var10000 = (new StringBuilder()).append("");
        TMIItemPanel var10001 = this.itemPanel;
        var10000.append(TMIItemPanel.page + 1).append("/").append(this.itemPanel.numPages).toString();
        this.canvas.drawText(2, var2 - 13, "TMI 1.5.2 2013-04-25", -1);
        this.rain.show = TMIConfig.canChangeWeather();
        this.creative.show = this.survival.show = this.adventure.show = TMIConfig.canChangeCreativeMode();
        this.delete.show = TMIConfig.canDelete();
        this.dawn.show = this.noon.show = this.dusk.show = this.midnight.show = TMIConfig.canChangeTime();
        this.difficulty.show = TMIConfig.canChangeDifficulty();
        this.health.show = !TMIConfig.isMultiplayer();
        boolean var8 = TMIConfig.canRestoreSaves();
        int var9 = 0;
        int var10;

        for (var10 = 0; var10 < this.config.getNumSaves(); ++var10)
        {
            this.deleteButtons[var10].x = -1000;
            this.stateButtons[var10].y = 30 + var10 * 22;
            this.stateButtons[var10].height = 20;
            String var11 = (String)this.config.getSettings().get("save-name" + (var10 + 1));

            if (var11 == null)
            {
                var11 = "" + (var10 + 1);
            }

            if (this.config.isStateSaved(var10))
            {
                this.stateButtons[var10].label = "Load " + var11;
            }
            else
            {
                this.stateButtons[var10].label = "Save " + var11;
            }

            int var12 = this.canvas.getTextWidth(this.stateButtons[var10].label) + 26;

            if (var12 + 2 + 20 > var5)
            {
                var12 = var5 - 20 - 2;
            }

            if (var12 > var9)
            {
                var9 = var12;
            }
        }

        for (var10 = 0; var10 < this.config.getNumSaves(); ++var10)
        {
            this.stateButtons[var10].width = var9;
            this.stateButtons[var10].show = var8;
            this.deleteButtons[var10].show = var8;

            if (this.config.isStateSaved(var10))
            {
                this.deleteButtons[var10].x = this.stateButtons[var10].width + 2;
                this.deleteButtons[var10].y = this.stateButtons[var10].y;
                this.deleteButtons[var10].width = 20;
                this.deleteButtons[var10].height = 20;
            }
        }
    }

    public void determineTooltip(int var1, int var2)
    {
        if (this.rain != null && this.rain.contains(var1, var2))
        {
            this.setTooltip("Rain/snow is " + (this.rain.state ? "ON" : "OFF"));
        }
        else if (this.creative != null && this.creative.contains(var1, var2))
        {
            this.setTooltip("Creative mode");
        }
        else if (this.survival != null && this.survival.contains(var1, var2))
        {
            this.setTooltip("Survival mode");
        }
        else if (this.adventure != null && this.adventure.contains(var1, var2))
        {
            this.setTooltip("Adventure mode");
        }
        else if (this.noon != null && this.noon.contains(var1, var2))
        {
            this.setTooltip("Set time to noon");
        }
        else if (this.dawn != null && this.dawn.contains(var1, var2))
        {
            this.setTooltip("Set time to sunrise");
        }
        else if (this.dusk != null && this.dusk.contains(var1, var2))
        {
            this.setTooltip("Set time to sunset");
        }
        else if (this.midnight != null && this.midnight.contains(var1, var2))
        {
            this.setTooltip("Set time to midnight");
        }
        else if (this.difficulty != null && this.difficulty.contains(var1, var2))
        {
            this.setTooltip(TMIUtils.getDifficultyString());
        }
        else if (this.health != null && this.health.contains(var1, var2))
        {
            this.setTooltip("Fill health and food");
        }
        else
        {
            ItemStack var3;

            if (this.delete != null && this.delete.contains(var1, var2))
            {
                var3 = TMIUtils.getHeldItem();

                if (var3 == null)
                {
                    if (TMIUtils.shiftKey())
                    {
                        this.setTooltip("DELETE ALL ITEMS from current inventory screen");
                    }
                    else
                    {
                        this.setTooltip("Delete mode is " + (this.delete.state ? "ON" : "OFF"));
                    }
                }
                else if (TMIUtils.shiftKey())
                {
                    this.setTooltip("DELETE ALL " + TMIUtils.itemDisplayName(var3));
                }
                else
                {
                    this.setTooltip("DELETE " + TMIUtils.itemDisplayName(var3));
                }
            }
            else
            {
                ItemStack var4;
                List var5;

                if (this.itemPanel.contains(var1, var2))
                {
                    var3 = TMIUtils.getHeldItem();

                    if (var3 == null)
                    {
                        var4 = this.itemPanel.getHoverItem();

                        if (var4 != null)
                        {
                            if (!EaglerAdapter.isKeyDown(56) && !EaglerAdapter.isKeyDown(184))
                            {
                                var5 = TMIUtils.itemDisplayNameMultiline(var4, true);
                                this.canvas.drawMultilineTip(var1, var2, var5, var4);
                            }
                            else
                            {
                                this.setTooltip("Add " + var4.itemID + ":" + var4.getItemDamageForDisplay() + " to favorites");
                            }
                        }
                    }
                    else
                    {
                        this.setTooltip("DELETE " + TMIUtils.itemDisplayName(var3));
                    }
                }
                else if (this.favoritesPanel.contains(var1, var2))
                {
                    var3 = TMIUtils.getHeldItem();

                    if (var3 == null)
                    {
                        var4 = this.favoritesPanel.getHoverItem();

                        if (var4 != null)
                        {
                            if (!EaglerAdapter.isKeyDown(56) && !EaglerAdapter.isKeyDown(184))
                            {
                                var5 = TMIUtils.itemDisplayNameMultiline(var4, true);
                                this.canvas.drawMultilineTip(var1, var2, var5, var4);
                            }
                            else
                            {
                                this.setTooltip("Remove " + TMIUtils.itemDisplayName(var4));
                            }
                        }
                    }
                    else
                    {
                        this.setTooltip("Add " + TMIUtils.itemDisplayName(var3));
                    }
                }
            }
        }

        this.showToolTip(var1, var2);
    }

    public boolean isInitialized()
    {
        return this.widgetsCreated;
    }

    public void setTooltip(String var1)
    {
        this.activeTooltip = var1;
    }

    public String getTooltip()
    {
        return this.activeTooltip;
    }

    public boolean hasTooltip()
    {
        return this.activeTooltip != null;
    }

    public void showToolTip(int var1, int var2)
    {
        if (this.activeTooltip != null)
        {
            this.canvas.drawTip(var1, var2, this.activeTooltip);
        }
    }
}
