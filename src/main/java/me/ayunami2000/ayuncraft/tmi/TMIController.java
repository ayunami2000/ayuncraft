package me.ayunami2000.ayuncraft.tmi;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import net.lax1dude.eaglercraft.EaglerAdapter;

public class TMIController implements _tmi_MgButtonHandler, _tmi_MgItemHandler, _tmi_MgFocusHandler
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    private GuiContainer window = null;
    private RenderItem drawItems = null;
    private TMIConfig config = TMIConfig.getInstance();
    private TMIView view = null;
    public _tmi_MgTextField focusedTextField = null;
    long lastKeyPressTime = 0L;
    long lastPrefsLoadTime = 0L;
    long deleteAllWaitUntil = 0L;
    public static final long KEY_DELAY = 200L;
    public static final long PREFS_DELAY = 2000L;
    public boolean deleteMode = false;
    private boolean haveReplacedItems = false;
    private boolean wasInWindowLastFrame = false;
    private long windowLastFocused = 0L;
    private int savedChatKeyCode;
    private _tmi_MgCanvas canvas;

    public void onCreate(GuiContainer var1)
    {
        this.window = var1;
        this.drawItems = new RenderItem();
        this.canvas = new _tmi_MgCanvas(var1, this.drawItems);
        this.view = new TMIView(this.canvas, this.config, this);
        TMIUtils.loadItems(this.config);
        TMIUtils.replaceCustomItems();
        this.savedChatKeyCode = Minecraft.getMinecraft().gameSettings.keyBindChat.keyCode;
    }

    public void onEnterFrame(int var1, int var2, int var3, int var4)
    {
        try
        {
            boolean var5 = EaglerAdapter.isFocused();

            if (var5 && !this.wasInWindowLastFrame)
            {
                this.windowLastFocused = System.currentTimeMillis();
            }

            this.wasInWindowLastFrame = var5;
            TMIUtils.suppressAchievementNotice();
            boolean var6 = false;

            if (this.window instanceof GuiContainerCreative && TMIPrivateFields.creativeTab.getInt(this.window) == CreativeTabs.tabAllSearch.getTabIndex())
            {
                var6 = true;
            }

            boolean var7 = this.window instanceof GuiRepair;

            if (System.currentTimeMillis() - this.lastKeyPressTime > 200L)
            {
                if (EaglerAdapter.isKeyDown(this.config.getHotkey()) && this.focusedTextField == null && !var6 && !var7)
                {
                    this.config.toggleEnabled();
                    TMIUtils.savePreferences(this.config);
                    this.lastKeyPressTime = System.currentTimeMillis();
                }

                TMIUtils.updateUnlimitedItems();
            }

            if (System.currentTimeMillis() - this.lastPrefsLoadTime > 2000L)
            {
                TMIUtils.loadPreferences(this.config);

                if (this.lastPrefsLoadTime == 0L)
                {
                    TMIUtils.savePreferences(this.config);
                }

                this.lastPrefsLoadTime = System.currentTimeMillis();
            }

            if (this.config.isEnabled())
            {
                this.view.setTooltip((String)null);
                this.view.layout(this.window.width, this.window.height, var3, var4);
                this.canvas.drawWidgets(var1, var2);
                this.view.determineTooltip(var1, var2);
            }
        }
        catch (Exception var8)
        {
            TMIUtils.safeReportException(var8);
            this.disable();
        }
    }

    public void focus(_tmi_MgWidget var1)
    {
        if (this.focusedTextField != null && this.focusedTextField != var1)
        {
            this.focusedTextField.blur();
        }

        if (var1 instanceof _tmi_MgTextField)
        {
            this.focusedTextField = (_tmi_MgTextField)var1;
            Minecraft.getMinecraft().gameSettings.keyBindChat.keyCode = 0;

            try
            {
                if (TMIUtils.isCreativeMode() && TMIPrivateFields.creativeTab.getInt(this.window) == CreativeTabs.tabAllSearch.getTabIndex())
                {
                    TMIPrivateFields.setCreativeTab.invoke((GuiContainerCreative)this.window, new Object[] {CreativeTabs.tabInventory});
                }
            }
            catch (Exception var3)
            {
                System.out.println(var3);
            }
        }
    }

    public void blur(_tmi_MgWidget var1)
    {
        if (this.focusedTextField == var1)
        {
            this.focusedTextField = null;
            Minecraft.getMinecraft().gameSettings.keyBindChat.keyCode = this.savedChatKeyCode;
        }
    }

    public void onClose()
    {
        Minecraft.getMinecraft().gameSettings.keyBindChat.keyCode = this.savedChatKeyCode;
    }

    public boolean allowRegularTip()
    {
        return !this.view.hasTooltip();
    }

    public void showToolTip(int var1, int var2)
    {
        try
        {
            this.view.showToolTip(var1, var2);
        }
        catch (Exception var4)
        {
            TMIUtils.safeReportException(var4);
            this.disable();
        }
    }

    public void handleScrollWheel(int var1, int var2)
    {
        if (this.view.itemPanel.contains(var1, var2))
        {
            int var3 = EaglerAdapter.mouseGetEventDWheel();

            if (var3 != 0)
            {
                TMIItemPanel var10000 = this.view.itemPanel;
                TMIItemPanel.page += var3 < 0 ? 1 : -1;
            }

            try
            {
                TMIPrivateFields.lwjglMouseEventDWheel.setInt((Object)null, 0);
                TMIPrivateFields.lwjglMouseDWheel.setInt((Object)null, 0);
            }
            catch (Exception var5)
            {
                ;
            }
        }
    }

    public boolean onMouseEvent(int var1, int var2, int var3, boolean var4, Slot var5, Container var6)
    {
        if (!this.config.isEnabled())
        {
            return true;
        }
        else if (this.windowLastFocused > System.currentTimeMillis() - 200L)
        {
            return false;
        }
        else if (!this.onClick(var1, var2, var3))
        {
            return false;
        }
        else
        {
            boolean var7 = var4 && (EaglerAdapter.isKeyDown(42) || EaglerAdapter.isKeyDown(54));
            ItemStack var8 = TMIUtils.getHeldItem();
            ItemStack var9 = null;
            Slot var11 = null;
            int var12 = -1;
            int var13 = -1;

            if (var5 != null)
            {
                var9 = var5.getStack();
                var12 = var5.slotNumber;
                var13 = var5.slotNumber;

                if (var5 instanceof SlotCreativeInventory)
                {
                    try
                    {
                        Slot var10 = (Slot)((Slot)TMIPrivateFields.creativeSlot.get((SlotCreativeInventory)var5));
                        var12 = var10.slotNumber;
                    }
                    catch (Exception var16)
                    {
                        System.out.println(var16);
                    }
                }
                else if (this.window instanceof GuiContainerCreative)
                {
                    if (var12 >= 45)
                    {
                        var12 -= 9;
                    }
                    else
                    {
                        var12 = 0;
                    }
                }

                if (!TMIConfig.isMultiplayer())
                {
                    try
                    {
                        var11 = (Slot)TMIUtils.getPlayer().openContainer.inventorySlots.get(var12);
                    }
                    catch (IndexOutOfBoundsException var15)
                    {
                        System.out.println(var15);
                    }
                }
            }

            if (!var4)
            {
                var12 = -999;
                var13 = -999;

                if (var8 != null && (var8.stackSize < 0 || var8.stackSize > 64))
                {
                    var8.stackSize = 1;
                }
            }

            if (var11 != null && var3 == 0 && this.deleteMode && var12 >= 0 && var5 != null)
            {
                if (TMIUtils.shiftKey() && var9 != null)
                {
                    TMIUtils.deleteItemsOfType(var9, this.window);
                }
                else
                {
                    var11.putStack((ItemStack)null);
                }
            }
            else if (!TMICompatibility.callConvenientInventoryHandler(var12, var3, var7, Minecraft.getMinecraft(), var6))
            {
                if (var12 == 0 && var3 == 1 && this.isCrafting())
                {
                    for (int var14 = 0; var14 < 64; ++var14)
                    {
                        this.window.handleMouseClick(var5, var13, var3, var7 ? 1 : 0);
                    }
                }
                else if (var12 != -1)
                {
                    if (var3 <= 1 && var11 != null && var9 == null && var8 != null && (var8.stackSize < 0 || var8.stackSize > 64))
                    {
                        TMIUtils.deleteHeldItem();
                        var9 = TMIUtils.copyStack(var8);
                        var9.stackSize = 111;
                        var11.putStack(var9);
                    }
                    else
                    {
                        if (!TMIUtils.isCreativeMode() || var11 == null || var8 != null || var9 == null || var9.stackSize <= 64)
                        {
                            return true;
                        }

                        var11.putStack((ItemStack)null);
                        TMIUtils.setHeldItem(var9);
                    }
                }
            }

            return false;
        }
    }

    public boolean onClick(int var1, int var2, int var3)
    {
        try
        {
            if (this.config.isEnabled())
            {
                this.canvas.sortByZOrder();
                Object var4 = null;
                _tmi_MgWidget var5 = null;
                Iterator var6 = this.canvas.widgets.iterator();

                while (var6.hasNext())
                {
                    _tmi_MgWidget var7 = (_tmi_MgWidget)var6.next();

                    if (var7.contains(var1, var2))
                    {
                        var5 = var7;
                        break;
                    }
                }

                if (this.focusedTextField != null && this.focusedTextField != var5)
                {
                    this.focusedTextField.blur();
                    this.focusedTextField = null;
                }

                if (var5 != null)
                {
                    return var5.click(var1, var2, var3);
                }
            }
        }
        catch (Exception var8)
        {
            TMIUtils.safeReportException(var8);
            this.disable();
        }

        return true;
    }

    public boolean onButtonRightClick(Object var1)
    {
        return true;
    }

    public boolean onButtonPress(Object var1)
    {
        if (var1 instanceof TMIStateButtonData)
        {
            TMIStateButtonData var4 = (TMIStateButtonData)var1;

            if (var4.action == 1)
            {
                this.config.clearState(var4.state);
                TMIUtils.savePreferences(this.config);
            }
            else if (this.config.isStateSaved(var4.state))
            {
                this.config.loadState(var4.state);
                TMIUtils.savePreferences(this.config);
            }
            else
            {
                this.config.saveState(var4.state);
                TMIUtils.savePreferences(this.config);
            }

            return false;
        }
        else if (var1 instanceof String)
        {
            String var2 = (String)var1;

            if (var2.equals("rain"))
            {
                TMIUtils.setRaining(!TMIUtils.isRaining());
            }
            else if (var2.equals("survival"))
            {
                TMIUtils.setGameMode(0);
            }
            else if (var2.equals("creative"))
            {
                TMIUtils.setGameMode(1);
            }
            else if (var2.equals("adventure"))
            {
                TMIUtils.setGameMode(2);
            }
            else if (var2.equals("dawn"))
            {
                TMIUtils.setHourForward(0);
            }
            else if (var2.equals("noon"))
            {
                TMIUtils.setHourForward(6);
            }
            else if (var2.equals("dusk"))
            {
                TMIUtils.setHourForward(12);
            }
            else if (var2.equals("midnight"))
            {
                TMIUtils.setHourForward(18);
            }
            else
            {
                TMIItemPanel var10000;

                if (var2.equals("next"))
                {
                    var10000 = this.view.itemPanel;
                    ++TMIItemPanel.page;
                }
                else if (var2.equals("prev"))
                {
                    var10000 = this.view.itemPanel;
                    --TMIItemPanel.page;
                }
                else if (var2.equals("health"))
                {
                    TMIUtils.setPlayerHealth(20);
                    TMIUtils.fillHunger();
                }
                else if (var2.equals("difficulty"))
                {
                    TMIUtils.incrementDifficulty();
                }
                else if (var2.equals("deleteMode"))
                {
                    ItemStack var3 = TMIUtils.getHeldItem();

                    if (var3 != null)
                    {
                        if (TMIUtils.shiftKey())
                        {
                            TMIUtils.deleteHeldItem();
                            TMIUtils.deleteItemsOfType(var3, this.window);
                            this.deleteAllWaitUntil = System.currentTimeMillis() + 1000L;
                        }
                        else
                        {
                            TMIUtils.deleteHeldItem();
                        }
                    }
                    else if (TMIUtils.shiftKey())
                    {
                        if (System.currentTimeMillis() > this.deleteAllWaitUntil)
                        {
                            TMIUtils.deleteInventory();
                        }
                    }
                    else
                    {
                        this.deleteMode = !this.deleteMode;
                    }
                }
            }

            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean onItemEvent(ItemStack var1, int var2)
    {
        if (!TMIConfig.isMultiplayer() && var1.itemID == 52 && TMIUtils.itemDisplayName(var1).contains("Random Firework"))
        {
            TMIUtils.giveStack(TMIUtils.makeRandomFireworkSpawner(), this.config, 1);
            return false;
        }
        else if (var2 != 0)
        {
            if (var2 == 1)
            {
                TMIUtils.giveStack(var1, this.config, 1);
                return false;
            }
            else
            {
                return true;
            }
        }
        else if (!TMIConfig.isMultiplayer() && (EaglerAdapter.isKeyDown(42) || EaglerAdapter.isKeyDown(54)))
        {
            Item var3 = Item.itemsList[var1.itemID];

            if (TMIConfig.isTool(var3))
            {
                TMIUtils.giveStack(new ItemStack(var1.itemID, 1, -32000), this.config, 1);
                return false;
            }
            else if (TMIConfig.canItemBeUnlimited(var3))
            {
                ItemStack var4 = TMIUtils.copyStack(var1);
                NBTTagCompound var5 = TMIUtils.getTagCompoundWithCreate(var4, "TooManyItems");
                var5.setBoolean("Unlimited", true);
                TMIUtils.addLore(var4, "TMI Unlimited");
                TMIUtils.giveStack(var4, this.config);
                return false;
            }
            else
            {
                TMIUtils.giveStack(var1, this.config);
                return false;
            }
        }
        else if (!EaglerAdapter.isKeyDown(56) && !EaglerAdapter.isKeyDown(184))
        {
            TMIUtils.giveStack(var1, this.config);
            return false;
        }
        else
        {
            this.config.getFavorites().add(var1);
            TMIUtils.savePreferences(this.config);
            return false;
        }
    }

    public boolean onKeypress(char var1, int var2)
    {
        if (this.focusedTextField != null)
        {
            boolean var3 = this.focusedTextField.keypress(var1, var2);

            if (this.focusedTextField == this.view.itemPanel.textField)
            {
                TMIUtils.filterItems(this.focusedTextField.value(), this.config);
            }

            return var3;
        }
        else
        {
            return false;
        }
    }

    public boolean shouldPauseGame()
    {
        return false;
    }

    public boolean isChest()
    {
        return this.window.inventorySlots instanceof ContainerChest;
    }

    public boolean isCrafting()
    {
        return this.window.inventorySlots instanceof ContainerPlayer || this.window.inventorySlots instanceof ContainerWorkbench;
    }

    public void disable()
    {
        this.config.setEnabled(false);
    }

    public static void sendWindowClick(Container var0, int var1, int var2, boolean var3)
    {
        Minecraft.getMinecraft().playerController.windowClick(var0.windowId, var1, var2, var3 ? 1 : 0, Minecraft.getMinecraft().thePlayer);
    }
}
