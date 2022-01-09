package net.lax1dude.eaglercraft;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StringTranslate;

public class GuiScreenEditProfile extends GuiScreen {
	
	private GuiScreen parent;
	private GuiTextField username;
	
	private boolean dropDownOpen = false;
	private String[] dropDownOptions;
	private int slotsVisible = 0;
	private int selectedSlot = 0;
	private int scrollPos = -1;
	private int skinsHeight = 0;
	private boolean dragging = false;
	private int mousex = 0;
	private int mousey = 0;
	
	private static final TextureLocation gui = new TextureLocation("/gui/gui.png");
	
	public static final String[] defaultOptions = new String[] {
			"Default Steve",
			"Default Alex",
			"Tennis Steve",
			"Tennis Alex",
			"Tuxedo Steve",
			"Tuxedo Alex",
			"Athlete Steve",
			"Athlete Alex",
			"Cyclist Steve",
			"Cyclist Alex",
			"Boxer Steve",
			"Boxer Alex",
			"Prisoner Steve",
			"Prisoner Alex",
			"Scottish Steve",
			"Scottish Alex",
			"Developer Steve",
			"Developer Alex",
			"Herobrine",
			"Enderman",
			"Skeleton",
			"Blaze",
			"Barney",
			"Slime",
			"Noob",
			"Trump",
			"Notch",
			"Creeper",
			"Zombie",
			"Pig",
			"Squid",
			"Mooshroom",
			"Villager"
	};
	
	protected String screenTitle = "Edit Profile";
	
	public GuiScreenEditProfile(GuiScreen parent) {
		this.parent = parent;
		this.dropDownOptions = EaglerProfile.concatArrays(EaglerProfile.skinNames.toArray(new String[0]), defaultOptions);
	}
	
	private GuiButton button0, button1, button2;

	public void initGui() {
		super.initGui();
		EaglerAdapter.enableRepeatEvents(true);
		StringTranslate var1 = StringTranslate.getInstance();
		this.screenTitle = var1.translateKey("profile.title");
		this.username = new GuiTextField(this.fontRenderer, this.width / 2 - 20 + 1, this.height / 6 + 24 + 1, 138, 20);
		this.username.setFocused(true);
		this.username.setText(EaglerProfile.username);
		selectedSlot = EaglerProfile.presetSkinId == -1 ? EaglerProfile.customSkinId : (EaglerProfile.presetSkinId + EaglerProfile.skinNames.size());
		//this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, "eeeee"));
		this.buttonList.add(button0 = new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, var1.translateKey("gui.done")));
		this.buttonList.add(button1 = new GuiButton(2, this.width / 2 - 21, this.height / 6 + 110, 71, 20, var1.translateKey("profile.addSkin")));
		this.buttonList.add(button2 = new GuiButton(3, this.width / 2 - 21 + 71, this.height / 6 + 110, 72, 20, var1.translateKey("profile.clearSkin")));
		//this.buttonList.add(new GuiButton(200, this.width / 2, this.height / 6 + 72, 150, 20, var1.translateKey("gui.done")));
	}
	
	public void drawScreen(int mx, int my, float par3) {
		StringTranslate var1 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 15, 16777215);
		this.drawString(this.fontRenderer, var1.translateKey("profile.screenname"), this.width / 2 - 20, this.height / 6 + 8, 10526880);
		this.drawString(this.fontRenderer, var1.translateKey("profile.playerSkin"), this.width / 2 - 20, this.height / 6 + 66, 10526880);
		
		mousex = mx;
		mousey = my;
		
		int skinX = this.width / 2 - 120;
		int skinY = this.height / 6 + 8;
		int skinWidth = 80;
		int skinHeight = 130;
		
		drawRect(skinX, skinY, skinX + skinWidth, skinY + skinHeight, -6250336);
		drawRect(skinX + 1, skinY + 1, skinX + skinWidth - 1, skinY + skinHeight - 1, 0xff000015);
		
		this.username.drawTextBox();
		if(dropDownOpen) {
			super.drawScreen(0, 0, par3);
		}else {
			super.drawScreen(mx, my, par3);
		}
		
		skinX = this.width / 2 - 20;
		skinY = this.height / 6 + 82;
		skinWidth = 140;
		skinHeight = 22;
		
		drawRect(skinX, skinY, skinX + skinWidth, skinY + skinHeight, -6250336);
		drawRect(skinX + 1, skinY + 1, skinX + skinWidth - 21, skinY + skinHeight - 1, -16777216);
		drawRect(skinX + skinWidth - 20, skinY + 1, skinX + skinWidth - 1, skinY + skinHeight - 1, -16777216);
		
		EaglerAdapter.glColor4f(1f, 1f, 1f, 1f);
		gui.bindTexture();
		drawTexturedModalRect(skinX + skinWidth - 18, skinY + 3, 0, 240, 16, 16);
		
		this.fontRenderer.drawStringWithShadow(dropDownOptions[selectedSlot], skinX + 5, skinY + 7, 14737632);

		skinX = this.width / 2 - 20;
		skinY = this.height / 6 + 103;
		skinWidth = 140;
		skinHeight = (this.height - skinY - 10);
		slotsVisible = (skinHeight / 10);
		if(slotsVisible > dropDownOptions.length) slotsVisible = dropDownOptions.length;
		skinHeight = slotsVisible * 10 + 7;
		skinsHeight = skinHeight;
		if(scrollPos == -1) {
			scrollPos = selectedSlot - 2;
		}
		if(scrollPos > (dropDownOptions.length - slotsVisible)) {
			scrollPos = (dropDownOptions.length - slotsVisible);
		}
		if(scrollPos < 0) {
			scrollPos = 0;
		}
		if(dropDownOpen) {
			drawRect(skinX, skinY, skinX + skinWidth, skinY + skinHeight, -6250336);
			drawRect(skinX + 1, skinY + 1, skinX + skinWidth - 1, skinY + skinHeight - 1, -16777216);
			for(int i = 0; i < slotsVisible; i++) {
				if(i + scrollPos < dropDownOptions.length) {
					if(selectedSlot == i + scrollPos) {
						drawRect(skinX + 1, skinY + i*10 + 4, skinX + skinWidth - 1, skinY + i*10 + 14, 0x77ffffff);
					}else if(mx >= skinX && mx < (skinX + skinWidth - 10) && my >= (skinY + i*10 + 5) && my < (skinY + i*10 + 15)) {
						drawRect(skinX + 1, skinY + i*10 + 4, skinX + skinWidth - 1, skinY + i*10 + 14, 0x55ffffff);
					}
					this.fontRenderer.drawStringWithShadow(dropDownOptions[i + scrollPos], skinX + 5, skinY + 5 + i*10, 14737632);
				}
			}
			int scrollerSize = skinHeight * slotsVisible / dropDownOptions.length;
			int scrollerPos = skinHeight * scrollPos / dropDownOptions.length;
			drawRect(skinX + skinWidth - 4, skinY + scrollerPos + 1, skinX + skinWidth - 1, skinY + scrollerPos + scrollerSize, 0xff888888);
		}
		
		int xx = this.width / 2 - 80;
		int yy = this.height / 6 + 130;
		DefaultSkinRenderer.renderPlayerPreview(xx, yy, mx, my, selectedSlot);
		
	}
	
	public void handleMouseInput() {
		super.handleMouseInput();
		if(dropDownOpen) {
			int var1 = EaglerAdapter.mouseGetEventDWheel();
			if(var1 < 0) {
				scrollPos += 3;
			}
			if(var1 > 0) {
				scrollPos -= 3;
			}
		}
	}
	
	protected void actionPerformed(GuiButton par1GuiButton) {
		if(!dropDownOpen) {
			if(par1GuiButton.id == 200) {
				EaglerProfile.username = this.username.getText().length() == 0 ? "null" : this.username.getText();
				EaglerProfile.presetSkinId = selectedSlot - EaglerProfile.skinNames.size();
				if(EaglerProfile.presetSkinId < 0) {
					EaglerProfile.presetSkinId = -1;
					EaglerProfile.customSkinId = selectedSlot;
				}else {
					EaglerProfile.customSkinId = -1;
				}
				
				LocalStorageManager.profileSettingsStorage.setInteger("ps", EaglerProfile.presetSkinId);
				LocalStorageManager.profileSettingsStorage.setInteger("cs", EaglerProfile.customSkinId);
				LocalStorageManager.profileSettingsStorage.setString("name", EaglerProfile.username);
				
				NBTTagCompound skins = new NBTTagCompound();
				for(int i = 0; i < EaglerProfile.skinNames.size(); i++) {
					skins.setByteArray(EaglerProfile.skinNames.get(i), EaglerProfile.skinDatas.get(i));
				}
				LocalStorageManager.profileSettingsStorage.setCompoundTag("skins", skins);
				
				LocalStorageManager.saveStorageP();
				
				this.mc.displayGuiScreen((GuiScreen) parent);
			}else if(par1GuiButton.id == 2) {
				EaglerAdapter.openFileChooser("png", "image/png");
			}else if(par1GuiButton.id == 3) {
				EaglerProfile.skinDatas.clear();
				EaglerProfile.skinNames.clear();
				for(Integer i : EaglerProfile.glTex) {
					this.mc.renderEngine.deleteTexture(i.intValue());
				}
				EaglerProfile.glTex.clear();
				this.dropDownOptions = defaultOptions;
				this.selectedSlot = 0;
			}
		}
	}
	
	public void updateScreen() {
		this.username.updateCursorCounter();
		
		if(dropDownOpen) {
			if(EaglerAdapter.mouseIsButtonDown(0)) {
				int skinX = this.width / 2 - 20;
				int skinY = this.height / 6 + 103;
				int skinWidth = 140;
				if(mousex >= (skinX + skinWidth - 10) && mousex < (skinX + skinWidth) && mousey >= skinY && mousey < (skinY + skinsHeight)) {
					dragging = true;
				}
				if(dragging) {
					int scrollerSize = skinsHeight * slotsVisible / dropDownOptions.length;
					scrollPos = (mousey - skinY - (scrollerSize / 2)) * dropDownOptions.length / skinsHeight;
				}
			}else {
				dragging = false;
			}
		}else {
			dragging = false;
		}
		
		byte[] b;
		if((b = EaglerAdapter.getFileChooserResult()) != null && b.length > 0) {
			EaglerImage img = EaglerImage.loadImage(b);
			if(!((img.w == 64 && img.h == 32) || (img.w == 64 && img.h == 64) || (img.w == 128 && img.h == 64) || (img.w == 128 && img.h == 128))) return;
			byte[] rawSkin = new byte[img.data.length * 4];
			for(int i = 0; i < img.data.length; i++) {
				int i2 = i * 4; int i3 = img.data[i];
				rawSkin[i2] = (byte)(i3);
				rawSkin[i2 + 1] = (byte)(i3 >> 8);
				rawSkin[i2 + 2] = (byte)(i3 >> 16);
				rawSkin[i2 + 3] = (byte)(i3 >> 24);
			}
			String name = EaglerAdapter.getFileChooserResultName();
			if(name.length() > 32) {
				name = name.substring(0, 32);
			}
			EaglerProfile.addSkin(name, rawSkin);
			selectedSlot = EaglerProfile.skinNames.size() - 1;
			this.dropDownOptions = EaglerProfile.concatArrays(EaglerProfile.skinNames.toArray(new String[0]), defaultOptions);
		}
	}
	
	public void onGuiClosed() {
		EaglerAdapter.enableRepeatEvents(false);
	}
	
	protected void keyTyped(char par1, int par2) {
		this.username.textboxKeyTyped(par1, par2);
		
		String text = username.getText();
		if(text.length() > 16) text = text.substring(0, 16);
		text = text.replaceAll("[^A-Za-z0-9\\-_]", "_");
		this.username.setText(text);
		
		if(par2 == 200 && selectedSlot > 0) {
			--selectedSlot;
			scrollPos = selectedSlot - 2;
		}
		if(par2 == 208 && selectedSlot < (dropDownOptions.length - 1)) {
			++selectedSlot;
			scrollPos = selectedSlot - 2;
		}
	}
	
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		this.username.mouseClicked(par1, par2, par3);
		
		if (par3 == 0) {
			int skinX = this.width / 2 + 140 - 40;
			int skinY = this.height / 6 + 82;
		
			if(par1 >= skinX && par1 < (skinX + 20) && par2 >= skinY && par2 < (skinY + 22)) {
				dropDownOpen = !dropDownOpen;
			}
			
			skinX = this.width / 2 - 20;
			skinY = this.height / 6 + 82;
			int skinWidth = 140;
			int skinHeight = skinsHeight;
			
			if(!(par1 >= skinX && par1 < (skinX + skinWidth) && par2 >= skinY && par2 < (skinY + skinHeight + 22))) {
				dropDownOpen = false;
				dragging = false;
			}
			
			skinY += 21;
			
			if(dropDownOpen && !dragging) {
				for(int i = 0; i < slotsVisible; i++) {
					if(i + scrollPos < dropDownOptions.length) {
						if(selectedSlot != i + scrollPos) {
							if(par1 >= skinX && par1 < (skinX + skinWidth - 10) && par2 >= (skinY + i*10 + 5) && par2 < (skinY + i*10 + 15) && selectedSlot != i + scrollPos) {
								selectedSlot = i + scrollPos;
								dropDownOpen = false;
								dragging = false;
							}
						}
					}
				}
			}
			
		}
	}
	
	
	
}
