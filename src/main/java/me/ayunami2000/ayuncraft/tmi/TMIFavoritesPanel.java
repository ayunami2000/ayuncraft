package me.ayunami2000.ayuncraft.tmi;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.src.ItemStack;

class TMIFavoritesPanel extends TMIItemPanel
{
    private TMIConfig config = null;
    private static final String[] blankPanelTip = new String[] {"Favorites:", "Drop items here", "or Alt-click them", "in the items panel."};

    public TMIFavoritesPanel(TMIConfig var1, TMIController var2)
    {
        super(0, 0, 0, 0, 0, var1.getFavorites(), var2);
        this.config = var1;
        this.textField.show = false;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.items.size() > 0)
        {
            super.draw(var1, var2, var3);
        }
        else if (this.show)
        {
            this.hoverItem = null;
            this.hoverItemIndex = -1;
            int var4 = this.y + (this.height / 2 - 13 * blankPanelTip.length / 2);
            String[] var5 = blankPanelTip;
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7)
            {
                String var8 = var5[var7];
                var1.drawTextCentered(this.x, var4, this.width, 12, var8, -1);
                var4 += 13;
            }
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        ItemStack var4 = TMIUtils.getHeldItem();

        if (var4 != null)
        {
            ItemStack var5 = TMIUtils.copyStack(var4);
            var5.stackSize = TMIUtils.maxStackSize(var5.itemID);

            if (var5.itemID == TMIItemInfo.addItemOffset(117))
            {
                var5.stackSize = 64;
            }

            this.items.add(var5);
            TMIUtils.savePreferences(this.config);
            return false;
        }
        else if (this.hoverItem != null)
        {
            if (!EaglerAdapter.isKeyDown(56) && !EaglerAdapter.isKeyDown(184))
            {
                return this.controller.onItemEvent(this.hoverItem, var3);
            }
            else
            {
                this.items.remove(this.hoverItemIndex);
                TMIUtils.savePreferences(this.config);
                return false;
            }
        }
        else
        {
            return this.delegateClickToChildren(var1, var2, var3);
        }
    }
}
