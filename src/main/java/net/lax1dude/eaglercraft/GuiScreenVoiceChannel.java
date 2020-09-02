package net.lax1dude.eaglercraft;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngameMenu;
import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSlider2;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StringTranslate;

public class GuiScreenVoiceChannel extends GuiScreen {
	
	public GuiScreenVoiceChannel(GuiScreen parent) {
		this.parent = parent;
	}
	
	protected String screenTitle = "Voice Channel";
	private GuiScreen parent;
	private GuiTextField channel;

	private GuiButton done;
	private GuiButton connect;
	private GuiButton disconnect;
	private GuiSlider2 slider;
	
	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.screenTitle = var1.translateKey("voice.title");
		this.channel = new GuiTextField(this.fontRenderer, this.width / 2 - 98, this.height / 6 + 24, 195, 20);
		this.channel.setText(EaglerProfile.myChannel);
		EaglerAdapter.enableRepeatEvents(true);
		this.buttonList.add(done = new GuiButton(200, this.width / 2 - 100, this.height / 6 + 148, var1.translateKey("gui.done")));
		this.buttonList.add(connect = new GuiButton(1, this.width / 2 - 100, this.height / 6 + 52, 99, 20, var1.translateKey("voice.connect")));
		this.buttonList.add(disconnect = new GuiButton(2, this.width / 2 + 1, this.height / 6 + 52, 99, 20, var1.translateKey("voice.disconnect")));
		this.buttonList.add(slider = new GuiSlider2(3, this.width / 2 - 100, this.height / 6 + 103, 200, 20, 0.5f, 2.0f));
	}
	
	public void onGuiClosed() {
		EaglerAdapter.enableRepeatEvents(false);
	}

	public void drawScreen(int mx, int my, float par3) {
		this.drawDefaultBackground();
		StringTranslate var1 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 15, 16777215);
		this.drawString(this.fontRenderer, var1.translateKey("voice.addr"), this.width / 2 - 98, this.height / 6 + 8, 10526880);
		if(voiceRelayed) {
			this.drawCenteredString(this.fontRenderer, var1.translateKey("voice.warning1"), this.width / 2, this.height / 6 + 125, 0xffcccc);
			this.drawCenteredString(this.fontRenderer, var1.translateKey("voice.warning2"), this.width / 2, this.height / 6 + 136, 0xffcccc);
			this.drawCenteredString(this.fontRenderer, var1.translateKey("voice.warning3"), this.width / 2, this.height / 6 + 147, 0xffcccc);
			this.drawString(this.fontRenderer, var1.translateKey("voice.volume"), this.width / 2 - 98, this.height / 6 + 81, 10526880);
			slider.yPosition = this.height / 6 + 95;
			done.yPosition = this.height / 6 + 168;
		}else {
			this.drawString(this.fontRenderer, var1.translateKey("voice.volume"), this.width / 2 - 98, this.height / 6 + 89, 10526880);
			slider.yPosition = this.height / 6 + 103;
			done.yPosition = this.height / 6 + 148;
		}
		super.drawScreen(mx, my, par3);
		this.channel.drawTextBox();
	}
	
	protected void actionPerformed(GuiButton par1GuiButton) {
		if(par1GuiButton.id == 200) {
			this.mc.displayGuiScreen(parent);
		}else if(par1GuiButton.id == 1) {
			EaglerAdapter.voiceConnect(channel.getText());
		}else if(par1GuiButton.id == 2) {
			EaglerAdapter.voiceEnd();
		}
	}

	public void updateScreen() {
		this.channel.updateCursorCounter();
		this.connect.enabled = !voiceActive;
		this.disconnect.enabled = voiceActive;
		this.channel.setEnabled(!voiceActive);
		this.slider.enabled = voiceActive;
	}

	protected void keyTyped(char par1, int par2) {
		this.channel.textboxKeyTyped(par1, par2);
	}

	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		this.channel.mouseClicked(par1, par2, par3);
	}
	
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private static final TextureLocation tex_gui = new TextureLocation("/gui/gui.png");

	private static String[] connectedUsers = new String[0];
	private static String[] talkingUsers = new String[0];
	private static boolean voiceActive = false;
	private static boolean voiceRelayed = false;
	
	public static void tickVoiceConnection() {
		voiceActive = EaglerAdapter.voiceActive();
		if(voiceActive) {
			voiceRelayed = EaglerAdapter.voiceRelayed();
			connectedUsers = EaglerAdapter.voiceUsers();
			talkingUsers = EaglerAdapter.voiceUsersTalking();
			Arrays.sort(talkingUsers);
			Arrays.sort(connectedUsers);
		}else {
			voiceRelayed = false;
		}
	}
	
	public static long fadeInTimer = 0l;
	
	public static void drawOverlay() {
		Minecraft mc = Minecraft.getMinecraft();
		if(System.currentTimeMillis() - fadeInTimer < 1500l) {
			ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			EaglerAdapter.glDisable(EaglerAdapter.GL_TEXTURE_2D);
			EaglerAdapter.glDisable(EaglerAdapter.GL_ALPHA_TEST);
			EaglerAdapter.glDisable(EaglerAdapter.GL_DEPTH_TEST);
			EaglerAdapter.glDepthMask(false);
			EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
			EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
			float i = (float)(System.currentTimeMillis() - fadeInTimer) / 600f;
			i = 1.0f / (i + 1.0f);
			i = i * i * 1.08f - 0.08f;
			if(i < 0.0f) i = 0.0f;
			drawRect(0, 0, res.getScaledWidth(), res.getScaledHeight(), ((int)(i * 255f) << 24) | 0xffffff);
			EaglerAdapter.glEnable(EaglerAdapter.GL_ALPHA_TEST);
			if(System.currentTimeMillis() - fadeInTimer < 130l) {
				mc.showWarningText();
			}
			EaglerAdapter.glEnable(EaglerAdapter.GL_DEPTH_TEST);
			EaglerAdapter.glDepthMask(true);
		}
		boolean titleScreen = (mc.currentScreen != null && (mc.currentScreen instanceof GuiMainMenu));
		if(voiceActive && !(titleScreen && ((GuiMainMenu)mc.currentScreen).showAck) && !mc.gameSettings.showDebugInfo) {
			ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int width = res.getScaledWidth(); int height = res.getScaledHeight();
			if(titleScreen) {
				EaglerAdapter.glPushMatrix();
				EaglerAdapter.glTranslatef(0f, 12f, 0f);
			}
			
			EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
			EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
			EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
			EaglerAdapter.glEnable(EaglerAdapter.GL_TEXTURE_2D);
			EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			String line1 = "voice connected";
			String line2 = "" + connectedUsers.length + " users listening";
			int ll1 = mc.fontRenderer.getStringWidth(line1);
			int ll2 = mc.fontRenderer.getStringWidth(line2);
			drawRect(width - 17 - ll1 - 6, 0, width, 20, 0x33000000);
			
			if(mc.gameSettings.keyBindPlayerList.pressed || (mc.currentScreen != null && ((mc.currentScreen instanceof GuiIngameMenu) || (mc.currentScreen instanceof GuiScreenVoiceChannel)))) {
				if(connectedUsers.length > 0) {
					int wid = 0;
					for(int i = 0; i < connectedUsers.length; ++i) {
						EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
						int sw = mc.fontRenderer.getStringWidth(connectedUsers[i]);
						mc.fontRenderer.drawStringWithShadow(connectedUsers[i], width - 12 - sw, 26 + i*11, 0xffeeeeee);
						if(wid < sw) {
							wid = sw;
						}
						
						boolean isTalking = false;
						for(int j = 0; j < talkingUsers.length; ++j) {
							if(talkingUsers[j].equals(connectedUsers[i])) {
								isTalking = true;
								break;
							}
						}
						
						tex_gui.bindTexture();
						EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
						EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
						EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 0.65f);
						EaglerAdapter.glPushMatrix();
						EaglerAdapter.glTranslatef(width - 9, 27 + i*11, 0f);
						EaglerAdapter.glScalef(0.5f, 0.5f, 0.5f);
						static_drawTexturedModalRect(0, 0, isTalking ? 208 : 224, 0, 15, 15);
						EaglerAdapter.glPopMatrix();
					}
					
					drawRect(width - wid - 15, 24, width, 26 + connectedUsers.length*11, 0x33000000);
				}
			}else {
				if(talkingUsers.length > 0) {
					int wid = 0;
					for(int i = 0; i < talkingUsers.length; ++i) {
						EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
						int sw = mc.fontRenderer.getStringWidth(talkingUsers[i]);
						mc.fontRenderer.drawStringWithShadow(talkingUsers[i], width - 12 - sw, 26 + i*11, 0xffeeeeee);
						if(wid < sw) {
							wid = sw;
						}
						
						tex_gui.bindTexture();
						EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
						EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
						EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 0.65f);
						EaglerAdapter.glPushMatrix();
						EaglerAdapter.glTranslatef(width - 9, 27 + i*11, 0f);
						EaglerAdapter.glScalef(0.5f, 0.5f, 0.5f);
						static_drawTexturedModalRect(0, 0, 208, 0, 15, 15);
						EaglerAdapter.glPopMatrix();
					}
					
					drawRect(width - wid - 15, 24, width, 26 + talkingUsers.length*11, 0x33000000);
				}
			}

			mc.fontRenderer.drawStringWithShadow(line1, width - 16 - ll1 - 4, 2, 0xffffffff);
			
			EaglerAdapter.glPushMatrix();
			EaglerAdapter.glTranslatef(width - 20, 11f, 0f);
			EaglerAdapter.glScalef(0.75f, 0.75f, 0.75f);
			mc.fontRenderer.drawStringWithShadow(line2, -ll2, 0, 0xffffffff);
			EaglerAdapter.glPopMatrix();
			
			boolean b = ((System.currentTimeMillis() / 800l) % 2l) == 1l;
			
			tex_gui.bindTexture();
			EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
			EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
			EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 0.65f);
			static_drawTexturedModalRect(width - 17, 2, b ? 192 : 224, 0, 15, 15);
			
			if(titleScreen) {
				EaglerAdapter.glPopMatrix();
			}
		}
	}

}
