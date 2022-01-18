package net.minecraft.src;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import net.lax1dude.eaglercraft.ConfigConstants;
import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.EaglerImage;

import net.lax1dude.eaglercraft.GuiScreenEditProfile;
import net.lax1dude.eaglercraft.GuiScreenVoiceChannel;
import net.lax1dude.eaglercraft.LocalStorageManager;
import net.lax1dude.eaglercraft.TextureLocation;
import net.lax1dude.eaglercraft.adapter.Tessellator;

public class GuiMainMenu extends GuiScreen {
	/** The RNG used by the Main Menu Screen. */
	private static final Random rand = new Random();

	/** The splash message. */
	private String splashText = "missingno";
	private GuiButton buttonResetDemo;
	
	private long start;

	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private static int viewportTexture = -1;
	private boolean field_96141_q = true;
	private static boolean field_96140_r = false;
	private static boolean field_96139_s = false;
	private final Object field_104025_t = new Object();
	private String field_92025_p;
	private String field_104024_v;

	/** An array of all the paths to the panorama pictures. */
	private static final TextureLocation[] titlePanoramaPaths = new TextureLocation[] { new TextureLocation("/title/bg/panorama0.png"), new TextureLocation("/title/bg/panorama1.png"), new TextureLocation("/title/bg/panorama2.png"), new TextureLocation("/title/bg/panorama3.png"), new TextureLocation("/title/bg/panorama4.png"), new TextureLocation("/title/bg/panorama5.png") };
	public static final String field_96138_a = "";
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;

	private int scrollPosition = 0;
	private static final int visibleLines = 21;

	private int dragstart = -1;
	private int dragstartI = -1;
	
	private ArrayList<String> ackLines;
	
	public boolean showAck = false;

	public GuiMainMenu() {
		/*
		 * this.field_92025_p = ""; String var14 =
		 * System.getProperty("os_architecture"); var3 =
		 * System.getProperty("java_version");
		 * 
		 * if ("ppc".equalsIgnoreCase(var14)) { this.field_92025_p = "" +
		 * EnumChatFormatting.BOLD + "Notice!" + EnumChatFormatting.RESET +
		 * " PowerPC compatibility will be dropped in Minecraft 1.6";
		 * this.field_104024_v = "http://tinyurl.com/javappc"; } else if (var3 != null
		 * && var3.startsWith("1.5")) { this.field_92025_p = "" +
		 * EnumChatFormatting.BOLD + "Notice!" + EnumChatFormatting.RESET +
		 * " Java 1.5 compatibility will be dropped in Minecraft 1.6";
		 * this.field_104024_v = "http://tinyurl.com/javappc"; }
		 * 
		 * if (this.field_92025_p.length() == 0) { (new Thread(new
		 * RunnableTitleScreen(this), "1.6 Update Check Thread")).start(); }
		 */

		this.field_92025_p = EaglerAdapter._wisWebGL() ? ("eaglercraft javascript runtime") : ("eaglercraft desktop runtime");
		this.start = System.currentTimeMillis() + System.currentTimeMillis() % 10000l;
		this.ackLines = new ArrayList();
		
		if(!LocalStorageManager.gameSettingsStorage.getBoolean("seenAcknowledgements")) {
			this.showAck = true;
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}
	

	public void handleMouseInput() {
		super.handleMouseInput();
		if(showAck) {
			int var1 = EaglerAdapter.mouseGetEventDWheel();
			if(var1 < 0) {
				scrollPosition += 3;
			}
			if(var1 > 0) {
				scrollPosition -= 3;
			}
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
		if(par2 == 1) {
			hideAck();
		}
	}
	
	private void hideAck() {
		if(!LocalStorageManager.gameSettingsStorage.getBoolean("seenAcknowledgements")) {
			LocalStorageManager.gameSettingsStorage.setBoolean("seenAcknowledgements", true);
			LocalStorageManager.saveStorageG();
		}
		showAck = false;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		if(viewportTexture == -1) viewportTexture = this.mc.renderEngine.makeViewportTexture(256, 256);
		Calendar var1 = Calendar.getInstance();
		var1.setTime(new Date());

		this.splashText = "darviglet!";

		StringTranslate var2 = StringTranslate.getInstance();
		int var4 = this.height / 4 + 48;

		GuiButton single;
		this.buttonList.add(single = new GuiButton(1, this.width / 2 - 100, var4, var2.translateKey("menu.singleplayer")));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, var4 + 24 * 1, var2.translateKey("menu.multiplayer")));
		this.buttonList.add(new GuiButton(3, this.width / 2 - 100, var4 + 24 * 2, var2.translateKey("menu.forkme")));
		single.enabled = false;

		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var4 + 72 + 12, 98, 20, var2.translateKey("menu.options")));
		this.buttonList.add(new GuiButton(4, this.width / 2 + 2, var4 + 72 + 12, 98, 20, var2.translateKey("menu.editprofile")));

		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, var4 + 72 + 12));
		Object var5 = this.field_104025_t;

		synchronized (this.field_104025_t) {
			this.field_92023_s = this.fontRenderer.getStringWidth(this.field_92025_p);
			this.field_92024_r = this.fontRenderer.getStringWidth(field_96138_a);
			int var6 = Math.max(this.field_92023_s, this.field_92024_r);
			this.field_92022_t = (this.width - var6) / 2;
			this.field_92021_u = 82;
			this.field_92020_v = this.field_92022_t + var6;
			this.field_92019_w = this.field_92021_u + 12;
		}
		
		if(this.ackLines.isEmpty()) {
			int width = 315;
			String file = EaglerAdapter.fileContents("/credits.txt");
			if(file == null) {
				for(int i = 0; i < 30; ++i) {
					this.ackLines.add(" -- file not found -- ");
				}
			}else {
				String[] lines = file.split("\n");
				for(String s : lines) {
					String s2 = s.trim();
					if(s2.isEmpty()) {
						this.ackLines.add("");
					}else {
						String[] words = s2.split(" ");
						String currentLine = "   ";
						for(String s3 : words) {
							String cCurrentLine = currentLine + s3 + " ";
							if(this.mc.fontRenderer.getStringWidth(cCurrentLine) < width) {
								currentLine = cCurrentLine;
							}else {
								this.ackLines.add(currentLine);
								currentLine = s3 + " ";
							}
						}
						this.ackLines.add(currentLine);
					}
				}
			}
			
		}
		
	}
	
	protected void mouseClicked(int par1, int par2, int par3) {
		if(!showAck) {
			super.mouseClicked(par1, par2, par3);
			if (par3 == 0) {
				int w = this.fontRenderer.getStringWidth("eaglercraft readme.txt") * 3 / 4;
				if(par1 >= (this.width - w - 4) && par1 <= this.width && par2 >= 0 && par2 <= 9) {
					showAck = true;
				}
				w = this.fontRenderer.getStringWidth("debug console") * 3 / 4;
				if(par1 >= 0 && par1 <= (w + 4) && par2 >= 0 && par2 <= 9) {
					EaglerAdapter.openConsole();
				}
			}
		}else {
			if(par3 == 0) {
				int x = (this.width - 345) / 2;
				int y = (this.height - 230) / 2;
				if(par1 >= (x + 323) && par1 <= (x + 323 + 13) && par2 >= (y + 7) && par2 <= (y + 7 + 13)) {
					hideAck();
				}
				int trackHeight = 193;
				int offset = trackHeight * scrollPosition / this.ackLines.size();
				if(par1 >= (x + 326) && par1 <= (x + 334) && par2 >= (y + 27 + offset) && par2 <= (y + 27 + offset + (visibleLines * trackHeight / this.ackLines.size()) + 1)) {
					dragstart = par2;
					dragstartI = scrollPosition;
				}
			}
		}
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (par1GuiButton.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings));
		}

		if (par1GuiButton.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (par1GuiButton.id == 3) {
			//this.mc.displayGuiScreen(new GuiScreenVoiceChannel(this));
			EaglerAdapter.openLink(ConfigConstants.forkMe);
		}

		if (par1GuiButton.id == 4) {
			this.mc.displayGuiScreen(new GuiScreenEditProfile(this));
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int par1, int par2, float par3) {
		Tessellator var4 = Tessellator.instance;
		EaglerAdapter.glMatrixMode(EaglerAdapter.GL_PROJECTION);
		EaglerAdapter.glPushMatrix();
		EaglerAdapter.glLoadIdentity();
		//EaglerAdapter.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		//EaglerAdapter.gluPerspective(120.0F, (float)this.mc.displayHeight / (float)this.mc.displayWidth, 0.05F, 10.0F);
		EaglerAdapter.gluPerspective(120.0F, (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, 10.0F);
		EaglerAdapter.glMatrixMode(EaglerAdapter.GL_MODELVIEW);
		EaglerAdapter.glPushMatrix();
		EaglerAdapter.glLoadIdentity();
		EaglerAdapter.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		EaglerAdapter.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		//EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
		EaglerAdapter.glDisable(EaglerAdapter.GL_ALPHA_TEST);
		EaglerAdapter.glDisable(EaglerAdapter.GL_CULL_FACE);
		EaglerAdapter.glDepthMask(false);
		//EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
		byte var5 = 8;

		//for (int var6 = 0; var6 < var5 * var5; ++var6) {
			//EaglerAdapter.glPushMatrix();
			//float var7 = ((float) (var6 % var5) / (float) var5 - 0.5F) / 64.0F;
			//float var8 = ((float) (var6 / var5) / (float) var5 - 0.5F) / 64.0F;
			//float var7 = 0.0F;
			//float var8 = 0.0F;
			//float var9 = 0.0F;
			//EaglerAdapter.glTranslatef(var7, var8, var9);
			
			float panTimer = (float)(System.currentTimeMillis() - start) * 0.03f;
			EaglerAdapter.glRotatef(MathHelper.sin(panTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			EaglerAdapter.glRotatef(-(panTimer) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int var10 = 0; var10 < 6; ++var10) {
				EaglerAdapter.glPushMatrix();

				if (var10 == 1) {
					EaglerAdapter.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 2) {
					EaglerAdapter.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 3) {
					EaglerAdapter.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 4) {
					EaglerAdapter.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (var10 == 5) {
					EaglerAdapter.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				titlePanoramaPaths[var10].bindTexture();

				var4.startDrawingQuads();
				//var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
				var4.setColorRGBA_I(16777215, 255);
				float var11 = 0.0F;
				var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double) (0.0F + var11), (double) (0.0F + var11));
				var4.addVertexWithUV(1.0D, -1.0D, 1.0D, (double) (1.0F - var11), (double) (0.0F + var11));
				var4.addVertexWithUV(1.0D, 1.0D, 1.0D, (double) (1.0F - var11), (double) (1.0F - var11));
				var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double) (0.0F + var11), (double) (1.0F - var11));
				var4.draw();

				EaglerAdapter.glPopMatrix();
			}

			//EaglerAdapter.glPopMatrix();
			EaglerAdapter.glColorMask(true, true, true, false);
		//}

		var4.setTranslation(0.0D, 0.0D, 0.0D);
		EaglerAdapter.glColorMask(true, true, true, true);
		EaglerAdapter.glMatrixMode(EaglerAdapter.GL_PROJECTION);
		EaglerAdapter.glPopMatrix();
		EaglerAdapter.glMatrixMode(EaglerAdapter.GL_MODELVIEW);
		EaglerAdapter.glPopMatrix();
		EaglerAdapter.glDepthMask(true);
		EaglerAdapter.glEnable(EaglerAdapter.GL_CULL_FACE);
		EaglerAdapter.glEnable(EaglerAdapter.GL_ALPHA_TEST);
		EaglerAdapter.glEnable(EaglerAdapter.GL_DEPTH_TEST);
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox(float par1) {
		EaglerAdapter.glBindTexture(EaglerAdapter.GL_TEXTURE_2D, viewportTexture);
		this.mc.renderEngine.resetBoundTexture();
		EaglerAdapter.glCopyTexSubImage2D(EaglerAdapter.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
		EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
		EaglerAdapter.glColorMask(true, true, true, true);
		Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();
		byte var3 = 3;

		for (int var4 = 0; var4 < var3; ++var4) {
			var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (var4 + 1));
			int var5 = this.width;
			int var6 = this.height;
			float var7 = (float) (var4 - var3 / 2) / 256.0F;
			var2.addVertexWithUV((double) var5, (double) var6, (double) this.zLevel, (double) (0.0F + var7), 0.0D);
			var2.addVertexWithUV((double) var5, 0.0D, (double) this.zLevel, (double) (1.0F + var7), 0.0D);
			var2.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (1.0F + var7), 1.0D);
			var2.addVertexWithUV(0.0D, (double) var6, (double) this.zLevel, (double) (0.0F + var7), 1.0D);
		}

		var2.draw();
		EaglerAdapter.glColorMask(true, true, true, true);
		this.mc.renderEngine.resetBoundTexture();
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int par1, int par2, float par3) {
		//no more blur >:)
		EaglerAdapter.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		this.drawPanorama(par1, par2, par3);
		/*
		EaglerAdapter.glViewport(0, 0, 256, 256);
		this.drawPanorama(par1, par2, par3);
		EaglerAdapter.glDisable(EaglerAdapter.GL_TEXTURE_2D);
		EaglerAdapter.glEnable(EaglerAdapter.GL_TEXTURE_2D);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		EaglerAdapter.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		Tessellator var4 = Tessellator.instance;
		var4.startDrawingQuads();
		float var5 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
		float var6 = (float) this.height * var5 / 256.0F;
		float var7 = (float) this.width * var5 / 256.0F;
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_MIN_FILTER, EaglerAdapter.GL_LINEAR);
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_MAG_FILTER, EaglerAdapter.GL_LINEAR);
		var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int var8 = this.width;
		int var9 = this.height;
		var4.addVertexWithUV(0.0D, (double) var9, (double) this.zLevel, (double) (0.5F - var6), (double) (0.5F + var7));
		var4.addVertexWithUV((double) var8, (double) var9, (double) this.zLevel, (double) (0.5F - var6), (double) (0.5F - var7));
		var4.addVertexWithUV((double) var8, 0.0D, (double) this.zLevel, (double) (0.5F + var6), (double) (0.5F - var7));
		var4.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (0.5F + var6), (double) (0.5F + var7));
		var4.draw();
		*/
	}

	private static final TextureLocation mclogo = new TextureLocation("/title/mclogo.png");
	private static final TextureLocation ackbk = new TextureLocation("/gui/demo_bg.png");
	private static final TextureLocation beaconx = new TextureLocation("/gui/beacon.png");

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		mousex = par1;
		mousey = par2;
		this.renderSkybox(par1, par2, par3);
		Tessellator var4 = Tessellator.instance;
		short var5 = 274;
		int var6 = this.width / 2 - var5 / 2;
		byte var7 = 30;
		//no more gradient >:)
		//this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
		//this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
		mclogo.bindTexture();
		
		this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 99, 44);
		this.drawTexturedModalRect(var6 + 99, var7 + 0, 129, 0, 27, 44);
		this.drawTexturedModalRect(var6 + 99 + 26, var7 + 0, 126, 0, 3, 44);
		this.drawTexturedModalRect(var6 + 99 + 26 + 3, var7 + 0, 99, 0, 26, 44);
		this.drawTexturedModalRect(var6 + 154, var7 + 0, 0, 45, 155, 44);
		
		/*
		 * var4.setColorOpaque_I(16777215); EaglerAdapter.glPushMatrix();
		 * EaglerAdapter.glTranslatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
		 * EaglerAdapter.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F); float var8 = 1.8F -
		 * MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) /
		 * 1000.0F * (float)Math.PI * 2.0F) * 0.1F); var8 = var8 * 100.0F /
		 * (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
		 * EaglerAdapter.glScalef(var8, var8, var8); this.drawCenteredString(this.fontRenderer,
		 * this.splashText, 0, -8, 16776960); EaglerAdapter.glPopMatrix();
		 */

		this.drawString(this.fontRenderer, "minecraft 1.5.2", 2, this.height - 20, 16777215);
		this.drawString(this.fontRenderer, ConfigConstants.mainMenuString, 2, this.height - 10, 16777215);

		String var10 = "copyright " + Calendar.getInstance().get(Calendar.YEAR) + " calder young";
		this.drawString(this.fontRenderer, var10, this.width - this.fontRenderer.getStringWidth(var10) - 2, this.height - 10, 16777215);

		var10 = "all rights reserved";
		this.drawString(this.fontRenderer, var10, this.width - this.fontRenderer.getStringWidth(var10) - 2, this.height - 20, 16777215);

		if (this.field_92025_p != null && this.field_92025_p.length() > 0) {
			drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
			this.drawString(this.fontRenderer, this.field_92025_p, this.field_92022_t, this.field_92021_u, 16777215);
			// this.drawString(this.fontRenderer, field_96138_a, (this.width -
			// this.field_92024_r) / 2, ((GuiButton)this.buttonList.get(0)).yPosition - 12,
			// 16777215);
		}
		
		var10 = "eaglercraft readme.txt";
		int w = this.fontRenderer.getStringWidth(var10) * 3 / 4;
		if(!showAck && par1 >= (this.width - w - 4) && par1 <= this.width && par2 >= 0 && par2 <= 9) {
			drawRect((this.width - w - 4), 0, this.width, 9, 0x55000099);
		}else {
			drawRect((this.width - w - 4), 0, this.width, 9, 0x55200000);
		}
		EaglerAdapter.glPushMatrix();
		EaglerAdapter.glTranslatef((this.width - w - 2), 1.0f, 0.0f);
		EaglerAdapter.glScalef(0.75f, 0.75f, 0.75f);
		this.drawString(this.fontRenderer, var10, 0, 0, 16777215);
		EaglerAdapter.glPopMatrix();
		
		var10 = "debug console";
		w = this.fontRenderer.getStringWidth(var10) * 3 / 4;
		if(!showAck && par1 >= 0 && par1 <= (w + 4) && par2 >= 0 && par2 <= 9) {
			drawRect(0, 0, w + 4, 9, 0x55000099);
		}else {
			drawRect(0, 0, w + 4, 9, 0x55200000);
		}
		EaglerAdapter.glPushMatrix();
		EaglerAdapter.glTranslatef(2.0f, 1.0f, 0.0f);
		EaglerAdapter.glScalef(0.75f, 0.75f, 0.75f);
		this.drawString(this.fontRenderer, var10, 0, 0, 16777215);
		EaglerAdapter.glPopMatrix();
		
		if(showAck) {
			super.drawScreen(0, 0, par3);
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
			int x = (this.width - 345) / 2;
			int y = (this.height - 230) / 2;
			ackbk.bindTexture();
			EaglerAdapter.glPushMatrix();
			EaglerAdapter.glTranslatef(x, y, 0.0f);
			EaglerAdapter.glScalef(1.39f, 1.39f, 1.39f);
			this.drawTexturedModalRect(0, 0, 0, 0, 248, 166);
			EaglerAdapter.glPopMatrix();
			beaconx.bindTexture();
			this.drawTexturedModalRect(x + 323, y + 7, 114, 223, 13, 13);
			int lines = this.ackLines.size();
			if(scrollPosition < 0) scrollPosition = 0;
			if(scrollPosition + visibleLines > lines) scrollPosition = lines - visibleLines;
			for(int i = 0; i < visibleLines; ++i) {
				this.fontRenderer.drawString(this.ackLines.get(scrollPosition + i), x + 10, y + 10 + (i * 10), 0x404060);
			}
			int trackHeight = 193;
			int offset = trackHeight * scrollPosition / lines;
			drawRect(x + 326, y + 27, x + 334, y + 220, 0x33000020);
			drawRect(x + 326, y + 27 + offset, x + 334, y + 27 + (visibleLines * trackHeight / lines) + offset + 1, 0x66000000);
		}else {
			super.drawScreen(par1, par2, par3);
		}
	}

	private int mousex = 0;
	private int mousey = 0;
	
	public void updateScreen() {
		if(EaglerAdapter.mouseIsButtonDown(0) && dragstart > 0) {
			int trackHeight = 193;
			scrollPosition = (mousey - dragstart) * this.ackLines.size() / trackHeight + dragstartI;
			if(scrollPosition < 0) scrollPosition = 0;
			if(scrollPosition + visibleLines > this.ackLines.size()) scrollPosition = this.ackLines.size() - visibleLines;
		}else {
			dragstart = -1;
		}
	}
	
}
