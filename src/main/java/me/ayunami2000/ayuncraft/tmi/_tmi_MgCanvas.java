package me.ayunami2000.ayuncraft.tmi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderItem;

public class _tmi_MgCanvas
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    public int windowX = 0;
    public int windowY = 0;
    public GuiScreen window;
    private RenderItem drawItems;
    public List widgets = new ArrayList();
    public static final int ALIGN_TOP = 1001;
    public static final int ALIGN_BOTTOM = 1002;
    public static final int ALIGN_LEFT = 1003;
    public static final int ALIGN_RIGHT = 1004;
    public static final int ALIGN_MIDDLE = 1005;
    public static final int WHITE = -1;
    public static final int SHADE = -301989888;
    public static final int RED_SHADE = -297791480;
    public static final int LIGHT_SHADE = -296397483;
    private boolean flatMode = false;

    public _tmi_MgCanvas(GuiScreen var1, RenderItem var2)
    {
        this.window = var1;
        this.drawItems = var2;
    }

    public void drawGradientRectDirect(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        this.flatMode(true);

        try
        {
            TMIPrivateFields.drawGradientRect.invoke(this.window, new Object[] {Integer.valueOf(var1), Integer.valueOf(var2), Integer.valueOf(var3), Integer.valueOf(var4), Integer.valueOf(var5), Integer.valueOf(var6)});
        }
        catch (Exception var8)
        {
            System.out.println("[TMI] Cannot draw gradient rect. So sad.");
            var8.printStackTrace();
        }
    }

    public void drawRect(int var1, int var2, int var3, int var4, int var5)
    {
        this.flatMode(true);
        GuiScreen var10000 = this.window;
        GuiScreen.drawRect(var1 - this.windowX, var2 - this.windowY, var1 + var3 - this.windowX, var2 + var4 - this.windowY, var5);
    }

    public void drawText(int var1, int var2, String var3, int var4)
    {
        this.drawText(var1, var2, var3, var4, 1.0F);
    }

    public void drawText(int var1, int var2, String var3, int var4, float var5)
    {
        this.hardSetFlatMode(true);
        EaglerAdapter.glPushMatrix();
        EaglerAdapter.glScalef(var5, var5, 1.0F);
        this.window.fontRenderer.drawStringWithShadow(var3, (int)((float)(var1 - this.windowX) / var5), (int)((float)(var2 - this.windowY) / var5), var4);
        EaglerAdapter.glPopMatrix();
    }

    public void drawTextCentered(int var1, int var2, int var3, int var4, String var5, int var6, float var7)
    {
        this.drawText(var1 + (var3 - this.getTextWidth(var5, var7)) / 2, var2 + (var4 - 8) / 2, var5, var6, var7);
    }

    public void drawTextCentered(int var1, int var2, int var3, int var4, String var5, int var6)
    {
        this.drawTextCentered(var1, var2, var3, var4, var5, var6, 1.0F);
    }

    public void drawText(int var1, int var2, String var3)
    {
        this.drawText(var1, var2, var3, -1);
    }

    public void drawTip(int var1, int var2, String var3)
    {
        ArrayList var4 = new ArrayList();
        var4.add(var3);
        this.drawMultilineTip(var1, var2, var4, (ItemStack)null);
    }

    public void drawMultilineTip(int var1, int var2, List var3)
    {
        this.drawMultilineTip(var1, var2, var3, (ItemStack)null);
    }

    public void drawMultilineTip(int var1, int var2, List var3, ItemStack var4)
    {
        if (var3.size() > 0)
        {
            byte var5 = 0;
            int var6 = 0;
            Iterator var7 = var3.iterator();
            int var9;

            while (var7.hasNext())
            {
                Object var8 = var7.next();
                var9 = this.getTextWidth((String)var8);

                if (var9 > var6)
                {
                    var6 = var9;
                }
            }

            int var19 = 8;

            if (var3.size() > 1)
            {
                var19 += 2 + (var3.size() - 1) * 10;
            }

            int var20 = var6 + var5 * 2;
            var9 = var19 + var5 * 2;
            int var10 = var1 + 12;
            int var11 = var2 - 15;

            if (var11 - 2 < 0)
            {
                var11 = 0;
            }

            if (var10 + var20 + 2 > this.window.width)
            {
                var10 = this.window.width - var20;
            }

            if (var1 >= var10 && var1 <= var10 + var20 && var2 >= var11 && var2 <= var11 + var9)
            {
                var11 = var2 - var9 - 2;

                if (var11 < 0)
                {
                    var11 = var2 + 2;
                }
            }

            int var12 = -267386864;
            this.drawGradientRectDirect(var10 - 3, var11 - 4, var10 + var20 + 3, var11 - 3, var12, var12);
            this.drawGradientRectDirect(var10 - 3, var11 + var9 + 3, var10 + var20 + 3, var11 + var9 + 4, var12, var12);
            this.drawGradientRectDirect(var10 - 3, var11 - 3, var10 + var20 + 3, var11 + var9 + 3, var12, var12);
            this.drawGradientRectDirect(var10 - 4, var11 - 3, var10 - 3, var11 + var9 + 3, var12, var12);
            this.drawGradientRectDirect(var10 + var20 + 3, var11 - 3, var10 + var20 + 4, var11 + var9 + 3, var12, var12);
            int var13 = 1347420415;
            int var14 = (var13 & 16711422) >> 1 | var13 & -16777216;
            this.drawGradientRectDirect(var10 - 3, var11 - 3 + 1, var10 - 3 + 1, var11 + var9 + 3 - 1, var13, var14);
            this.drawGradientRectDirect(var10 + var20 + 2, var11 - 3 + 1, var10 + var20 + 3, var11 + var9 + 3 - 1, var13, var14);
            this.drawGradientRectDirect(var10 - 3, var11 - 3, var10 + var20 + 3, var11 - 3 + 1, var13, var13);
            this.drawGradientRectDirect(var10 - 3, var11 + var9 + 2, var10 + var20 + 3, var11 + var9 + 3, var14, var14);
            var10 += var5;
            var11 += var5;
            boolean var15 = true;

            for (Iterator var16 = var3.iterator(); var16.hasNext(); var11 += 10)
            {
                String var17 = (String)var16.next();

                if (var4 != null)
                {
                    String var18 = "\u00a7" + (var15 ? Integer.toHexString(var4.getRarity().rarityColor) : "7");
                    var17 = var18 + var17;
                }

                this.drawText(var10, var11, var17, -1);

                if (var15)
                {
                    var11 += 2;
                    var15 = false;
                }
            }
        }
    }

    public void drawItem(int var1, int var2, ItemStack var3)
    {
        this.hardSetFlatMode(false);
        this.drawItems.zLevel = 200.0F;

        try
        {
            int var10004 = var1 - this.windowX;
            int var10005 = var2 - this.windowY;
            this.drawItems.renderItemAndEffectIntoGUI(this.window.fontRenderer, Minecraft.getMinecraft().renderEngine, var3, var10004, var10005);
        }
        catch (Exception var5)
        {
            this.drawItems.renderItemAndEffectIntoGUI(this.window.fontRenderer, Minecraft.getMinecraft().renderEngine, new ItemStack(51, 1, 0), var1 - this.windowX, var2 - this.windowY);
        }
        catch (LinkageError var6)
        {
            this.drawItems.renderItemAndEffectIntoGUI(this.window.fontRenderer, Minecraft.getMinecraft().renderEngine, new ItemStack(51, 1, 0), var1 - this.windowX, var2 - this.windowY);
        }

        this.drawItems.zLevel = 0.0F;
    }

    public void drawChrome(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        this.hardSetFlatMode(true);
        Minecraft.getMinecraft().renderEngine.bindTexture("/tmi.png");
        this.window.drawTexturedModalRect(var1 - this.windowX, var2 - this.windowY, var3, var4, var5, var6);
    }

    public void drawChrome(int var1, int var2, _tmi_MgImage var3)
    {
        this.hardSetFlatMode(true);
        Minecraft.getMinecraft().renderEngine.bindTexture(var3.filename);
        this.window.drawTexturedModalRect(var1 - this.windowX, var2 - this.windowY, var3.x, var3.y, var3.width, var3.height);
    }

    public void sortByZOrder()
    {
        Collections.sort(this.widgets, _tmi_MgWidget.getComparator());
    }

    public void drawWidgets(int var1, int var2)
    {
        boolean var3 = !EaglerAdapter.glBlendEnabled;
        EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
        this.sortByZOrder();
        Iterator var4 = this.widgets.iterator();

        while (var4.hasNext())
        {
            _tmi_MgWidget var5 = (_tmi_MgWidget)var4.next();
            EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
            var5.draw(this, var1, var2);
        }

        if (var3)
        {
            EaglerAdapter.glDisable(EaglerAdapter.GL_BLEND);
        }
    }

    public int getTextWidth(String var1)
    {
        return this.getTextWidth(var1, 1.0F);
    }

    public int getTextWidth(String var1, float var2)
    {
        return var1 != null && !var1.equals("") ? (int)((float)this.window.fontRenderer.getStringWidth(var1) * var2) : 0;
    }

    public void flatMode(boolean var1)
    {
        if (var1 && !this.flatMode)
        {
            EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
            EaglerAdapter.glDisable(EaglerAdapter.GL_DEPTH_TEST);
        }
        else if (!var1 && this.flatMode)
        {
            EaglerAdapter.glEnable(EaglerAdapter.GL_LIGHTING);
            EaglerAdapter.glEnable(EaglerAdapter.GL_DEPTH_TEST);
        }

        this.flatMode = var1;
    }

    public void hardSetFlatMode(boolean var1)
    {
        if (var1)
        {
            EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
            EaglerAdapter.glDisable(EaglerAdapter.GL_DEPTH_TEST);
        }
        else
        {
            EaglerAdapter.glEnable(EaglerAdapter.GL_LIGHTING);
            EaglerAdapter.glEnable(EaglerAdapter.GL_DEPTH_TEST);
        }

        this.flatMode = var1;
    }

    public void arrangeHorizontally(int var1, int var2, _tmi_MgWidget ... var3)
    {
        if (var3.length > 1)
        {
            int var4 = var3[0].x;
            int var5 = var3[0].y;

            if (var2 == 1002)
            {
                var5 += var3[0].height;
            }
            else if (var2 == 1005)
            {
                var5 += var3[0].height / 2;
            }

            _tmi_MgWidget[] var6 = var3;
            int var7 = var3.length;

            for (int var8 = 0; var8 < var7; ++var8)
            {
                _tmi_MgWidget var9 = var6[var8];
                var9.x = var4;

                switch (var2)
                {
                    case 1001:
                        var9.y = var5;
                        break;

                    case 1002:
                        var9.y = var5 - var9.height;

                    case 1003:
                    case 1004:
                    default:
                        break;

                    case 1005:
                        var9.y = var5 - var9.height / 2;
                }

                var4 += var1 + var9.width;
            }
        }
    }
}
