package me.ayunami2000.ayuncraft.tmi;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiTextField;

public class _tmi_MgTextField extends _tmi_MgWidget
{
    public static final String COPYRIGHT = "All of TooManyItems except for thesmall portion excerpted from the original Minecraft game is copyright 2011Marglyph. TooManyItems is free for personal use only. Do not redistributeTooManyItems, including in mod packs, and do not use TooManyItems\' sourcecode or graphics in your own mods.";
    public String placeholder;
    private GuiTextField textField;
    private _tmi_MgFocusHandler focusHandler;
    private boolean focused;

    public _tmi_MgTextField(FontRenderer var1, String var2)
    {
        super(0, 0);
        this.placeholder = "";
        this.focusHandler = null;
        this.focused = false;
        this.placeholder = var2;
        this.textField = new GuiTextField(var1, 0, 0, 0, 0);
        this.height = 14;
    }

    public _tmi_MgTextField(FontRenderer var1, String var2, _tmi_MgFocusHandler var3)
    {
        this(var1, var2);
        this.focusHandler = var3;
    }

    public void draw(_tmi_MgCanvas var1, int var2, int var3)
    {
        if (this.show)
        {
            this.textField.xPos=this.x;
            this.textField.yPos=this.y;
            this.textField.width=this.width;
            this.textField.height=this.height;

            this.textField.drawTextBox();

            if ((this.value() == null || this.value().equals("")) && !this.focused)
            {
                var1.drawText(this.x + 3, this.y + 3, this.placeholder, -7829368);
            }
        }
    }

    public boolean click(int var1, int var2, int var3)
    {
        this.textField.setFocused(true);
        this.textField.mouseClicked(var1, var2, var3);

        if (this.focusHandler != null)
        {
            this.focusHandler.focus(this);
        }

        this.focused = true;
        return true;
    }

    public boolean keypress(char var1, int var2)
    {
        if (var2 == 1)
        {
            this.blur();
        }

        this.textField.textboxKeyTyped(var1, var2);
        return true;
    }

    public void blur()
    {
        this.textField.setFocused(false);
        this.focused = false;

        if (this.focusHandler != null)
        {
            this.focusHandler.blur(this);
        }
    }

    public String value()
    {
        return this.textField.getText();
    }

    public void setValue(String var1)
    {
        this.textField.setText(var1);
    }
}
