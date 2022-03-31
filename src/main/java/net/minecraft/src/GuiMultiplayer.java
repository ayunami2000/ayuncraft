package net.minecraft.src;

import java.util.Collections;
import java.util.List;

import net.lax1dude.eaglercraft.EaglerAdapter;

public class GuiMultiplayer extends GuiScreen {
	/** Number of outstanding ThreadPollServers threads */
	private static int threadsPending = 0;

	/** Lock object for use with synchronized() */
	private static Object lock = new Object();

	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;

	/** Slot container for the server list */
	private GuiSlotServer serverSlotContainer;
	private ServerList internetServerList;

	/** Index of the currently selected server */
	private int selectedServer = -1;
	private GuiButton field_96289_p;

	/** The 'Join Server' button */
	private GuiButton buttonSelect;

	/** The 'Delete' button */
	private GuiButton buttonDelete;

	/** The 'Delete' button was clicked */
	private boolean deleteClicked = false;

	/** The 'Add server' button was clicked */
	private boolean addClicked = false;

	/** The 'Edit' button was clicked */
	private boolean editClicked = false;

	/** The 'Direct Connect' button was clicked */
	private boolean directClicked = false;

	/** This GUI's lag tooltip text or null if no lag icon is being hovered. */
	private String lagTooltip = null;

	/** Instance of ServerData. */
	private ServerData theServerData = null;

	/** How many ticks this Gui is already opened */
	private int ticksOpened;
	private boolean field_74024_A;
	private List listofLanServers = Collections.emptyList();

	public GuiMultiplayer(GuiScreen par1GuiScreen) {
		this.parentScreen = par1GuiScreen;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		EaglerAdapter.enableRepeatEvents(true);
		this.buttonList.clear();

		if (!this.field_74024_A) {
			this.field_74024_A = true;
			this.internetServerList = new ServerList(this.mc);
			this.internetServerList.loadServerList();
			

			this.serverSlotContainer = new GuiSlotServer(this);
		} else {
			this.serverSlotContainer.func_77207_a(this.width, this.height, 32, this.height - 64);
		}

		this.initGuiControls();
	}

	/**
	 * Populate the GuiScreen controlList
	 */
	public void initGuiControls() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.buttonList.add(this.field_96289_p = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, var1.translateKey("selectServer.edit")));
		this.buttonList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, var1.translateKey("selectServer.delete")));
		this.buttonList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, var1.translateKey("selectServer.select")));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, var1.translateKey("selectServer.direct")));
		this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, var1.translateKey("selectServer.add")));
		this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, var1.translateKey("selectServer.refresh")));
		this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, var1.translateKey("gui.cancel")));
		boolean var2 = this.selectedServer >= 0 && this.selectedServer < this.serverSlotContainer.getSize();
		this.buttonSelect.enabled = var2;
		this.field_96289_p.enabled = var2;
		this.buttonDelete.enabled = var2;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		super.updateScreen();
		internetServerList.updateServerPing();
		++this.ticksOpened;
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		EaglerAdapter.enableRepeatEvents(false);
		if(field_74024_A) {
			internetServerList.freeServerIcons();
			field_74024_A = false;
		}
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.enabled) {
			if (par1GuiButton.id == 2) {
				String var2 = this.internetServerList.getServerData(this.selectedServer).serverName;

				if (var2 != null) {
					this.deleteClicked = true;
					StringTranslate var3 = StringTranslate.getInstance();
					String var4 = var3.translateKey("selectServer.deleteQuestion");
					String var5 = "\'" + var2 + "\' " + var3.translateKey("selectServer.deleteWarning");
					String var6 = var3.translateKey("selectServer.deleteButton");
					String var7 = var3.translateKey("gui.cancel");
					GuiYesNo var8 = new GuiYesNo(this, var4, var5, var6, var7, this.selectedServer);
					this.mc.displayGuiScreen(var8);
				}
			} else if (par1GuiButton.id == 1) {
				this.joinServer(this.selectedServer);
			} else if (par1GuiButton.id == 4) {
				this.directClicked = true;
				this.mc.displayGuiScreen(new GuiScreenServerList(this, this.theServerData = new ServerData(StatCollector.translateToLocal("selectServer.defaultName"), "")));
			} else if (par1GuiButton.id == 3) {
				this.addClicked = true;
				this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.theServerData = new ServerData(StatCollector.translateToLocal("selectServer.defaultName"), "")));
			} else if (par1GuiButton.id == 7) {
				this.editClicked = true;
				ServerData var9 = this.internetServerList.getServerData(this.selectedServer);
				this.theServerData = new ServerData(var9.serverName, var9.serverIP);
				this.theServerData.setHideAddress(var9.isHidingAddress());
				this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.theServerData));
			} else if (par1GuiButton.id == 0) {
				this.mc.displayGuiScreen(this.parentScreen);
			} else if (par1GuiButton.id == 8) {
				this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
			} else {
				this.serverSlotContainer.actionPerformed(par1GuiButton);
			}
		}
	}

	public void confirmClicked(boolean par1, int par2) {
		if (this.deleteClicked) {
			this.deleteClicked = false;

			if (par1) {
				this.internetServerList.removeServerData(par2);
				this.internetServerList.saveServerList();
				this.selectedServer = -1;
			}

			this.mc.displayGuiScreen(this);
		} else if (this.directClicked) {
			this.directClicked = false;

			if (par1) {
				this.connectToServer(this.theServerData);
			} else {
				this.mc.displayGuiScreen(this);
			}
		} else if (this.addClicked) {
			this.addClicked = false;

			if (par1) {
				this.internetServerList.addServerData(this.theServerData);
				this.internetServerList.saveServerList();
				this.selectedServer = -1;
			}

			this.mc.displayGuiScreen(this);
		} else if (this.editClicked) {
			this.editClicked = false;

			if (par1) {
				ServerData var3 = this.internetServerList.getServerData(this.selectedServer);
				var3.serverName = this.theServerData.serverName;
				var3.serverIP = this.theServerData.serverIP;
				var3.setHideAddress(this.theServerData.isHidingAddress());
				var3.pingSentTime = -1l;
				this.internetServerList.saveServerList();
			}

			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
		int var3 = this.selectedServer;

		if (par2 == 59) {
			this.mc.gameSettings.hideServerAddress = !this.mc.gameSettings.hideServerAddress;
			this.mc.gameSettings.saveOptions();
		} else {
			if (isShiftKeyDown() && par2 == 200) {
				if (var3 > 0 && var3 < this.internetServerList.countServers()) {
					this.internetServerList.swapServers(var3, var3 - 1);
					--this.selectedServer;

					if (var3 < this.internetServerList.countServers() - 1) {
						this.serverSlotContainer.func_77208_b(-this.serverSlotContainer.slotHeight);
					}
				}
			} else if (isShiftKeyDown() && par2 == 208) {
				if (var3 < this.internetServerList.countServers() - 1) {
					this.internetServerList.swapServers(var3, var3 + 1);
					++this.selectedServer;

					if (var3 > 0) {
						this.serverSlotContainer.func_77208_b(this.serverSlotContainer.slotHeight);
					}
				}
			} else if (par1 == 13) {
				this.actionPerformed((GuiButton) this.buttonList.get(2));
			} else {
				super.keyTyped(par1, par2);
			}
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		this.lagTooltip = null;
		StringTranslate var4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.serverSlotContainer.drawScreen(par1, par2, par3);
		this.drawCenteredString(this.fontRenderer, var4.translateKey("multiplayer.title"), this.width / 2, 20, 16777215);
		super.drawScreen(par1, par2, par3);

		if (this.lagTooltip != null) {
			this.func_74007_a(this.lagTooltip, par1, par2);
		}
	}

	/**
	 * Join server by slot index
	 */
	private void joinServer(int par1) {
		this.connectToServer(this.internetServerList.getServerData(par1));
	}

	private void connectToServer(ServerData par1ServerData) {
		this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, par1ServerData));
	}

	protected void func_74007_a(String par1Str, int par2, int par3) {
		if (par1Str != null) {
			if(par1Str.indexOf('\n') >= 0) {
				String[] strs = par1Str.split("\n");
				int var6 = 0;
				int full = 0;
				for(int i = 0; i < strs.length; ++i) {
					strs[i] = strs[i].replace('\r', ' ').trim();
					if(strs[i].length() > 0) {
						int w = this.fontRenderer.getStringWidth(strs[i]);
						if(w > var6) {
							var6 = w;
						}
						++full;
					}
				}
				int var4 = par2 + 12;
				int var5 = par3 - 12;
				this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + full * 9 + 2, -1073741824, -1073741824);
				full = 0;
				for(int i = 0; i < strs.length; ++i) {
					if(strs[i].length() > 0) {
						this.fontRenderer.drawStringWithShadow(strs[i], var4, var5 + 9 * full++, -1);
					}
				}
			}else {
				int var4 = par2 + 12;
				int var5 = par3 - 12;
				int var6 = this.fontRenderer.getStringWidth(par1Str);
				this.drawGradientRect(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
				this.fontRenderer.drawStringWithShadow(par1Str, var4, var5, -1);
			}
		}
	}

	static ServerList getInternetServerList(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.internetServerList;
	}

	static List getListOfLanServers(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.listofLanServers;
	}

	static int getSelectedServer(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.selectedServer;
	}

	static int getAndSetSelectedServer(GuiMultiplayer par0GuiMultiplayer, int par1) {
		return par0GuiMultiplayer.selectedServer = par1;
	}

	/**
	 * Return buttonSelect GuiButton
	 */
	static GuiButton getButtonSelect(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.buttonSelect;
	}

	/**
	 * Return buttonEdit GuiButton
	 */
	static GuiButton getButtonEdit(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.field_96289_p;
	}

	/**
	 * Return buttonDelete GuiButton
	 */
	static GuiButton getButtonDelete(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.buttonDelete;
	}

	static void func_74008_b(GuiMultiplayer par0GuiMultiplayer, int par1) {
		par0GuiMultiplayer.joinServer(par1);
	}

	static int getTicksOpened(GuiMultiplayer par0GuiMultiplayer) {
		return par0GuiMultiplayer.ticksOpened;
	}

	/**
	 * Returns the lock object for use with synchronized()
	 */
	static Object getLock() {
		return lock;
	}

	static int getThreadsPending() {
		return threadsPending;
	}

	static int increaseThreadsPending() {
		return threadsPending++;
	}

	static int decreaseThreadsPending() {
		return threadsPending--;
	}

	static String getAndSetLagTooltip(GuiMultiplayer par0GuiMultiplayer, String par1Str) {
		return par0GuiMultiplayer.lagTooltip = par1Str;
	}
}
