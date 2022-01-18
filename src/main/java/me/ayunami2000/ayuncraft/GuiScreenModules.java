package me.ayunami2000.ayuncraft;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.src.*;

public class GuiScreenModules extends GuiScreen {

    public GuiScreenModules(GuiScreen parent) {
        this.parent = parent;
    }

    protected String screenTitle = "ayuncraft";
    private GuiScreen parent;
    private GuiTextField iteminfo;

    public void initGui() {
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 148, "Close"));
        this.iteminfo = new GuiTextField(this.fontRenderer, this.width / 2 - 98, this.height / 6 + 24, 195, 20);
        this.iteminfo.setFocused(true);
        this.iteminfo.setText("383");
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 6 + 52, "Give"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 6 + 76, "Toggle Notebot"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 6 + 100, "Toggle Legit mode"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 6 + 124, "Toggle Flight"));
    }

    public void onGuiClosed() {
        EaglerAdapter.enableRepeatEvents(false);
    }

    public void drawScreen(int mx, int my, float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 15, 16777215);
        super.drawScreen(mx, my, par3);
        this.iteminfo.drawTextBox();
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        if(par1GuiButton.id == 200) {
            this.mc.displayGuiScreen(parent);
        }else if(par1GuiButton.id == 1) {
            try{
                String[] pieces=iteminfo.getText().split(":",2);
                int itemid=Integer.parseInt(pieces[0]);
                int dmg=pieces.length==1?0:Integer.parseInt(pieces[1]);
                ItemStack theitem=new ItemStack(itemid, 64, dmg);
                mc.thePlayer.inventory.addItemStackToInventory(theitem);
                //mc.thePlayer.inventoryContainer.detectAndSendChanges();
                //mc.thePlayer.dropPlayerItem(theitem);
            }catch(NumberFormatException e){}
        }else if(par1GuiButton.id == 2) {
            if(NoteblockPlayer.playing){
                NoteblockPlayer.playing=false;
                NoteblockPlayer.thr=null;
                mc.thePlayer.sendChatToPlayer("Stopped notebot!");
            }else {
                NoteblockPlayer.thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EaglerAdapter.openFileChooser("nbs", ".nbs,.mid,.midi");
                        try {
                            Thread.sleep(1000);
                            while(!EaglerAdapter.isFocused()){
                                Thread.sleep(100);
                            }
                            Thread.sleep(500);
                        } catch (InterruptedException e) {}
                        byte[] b;
                        if ((b = EaglerAdapter.getFileChooserResult()) != null && b.length > 0) {
                            String name = EaglerAdapter.getFileChooserResultName();
                            NoteblockPlayer.songdata = b;
                            mc.thePlayer.sendChatToPlayer("Playing \""+name+"\" on notebot!");
                            NoteblockPlayer.play((name.toLowerCase().endsWith(".nbs")?NoteblockPlayer.loadSong():MidiConverter.midiToTxt()).split("\n"));
                        }
                    }
                });
                NoteblockPlayer.thr.start();
            }
        }else if(par1GuiButton.id == 3){
            NoteblockPlayer.legit=!NoteblockPlayer.legit;
        }else if(par1GuiButton.id == 4){
            NoteblockPlayer.flying=!NoteblockPlayer.flying;
            if(!NoteblockPlayer.flying)mc.thePlayer.capabilities.isFlying=false;
        }
    }

    public void updateScreen() {
        this.iteminfo.updateCursorCounter();
    }

    protected void keyTyped(char par1, int par2) {
        this.iteminfo.textboxKeyTyped(par1, par2);
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        this.iteminfo.mouseClicked(par1, par2, par3);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}