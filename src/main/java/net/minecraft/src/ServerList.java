package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.lax1dude.eaglercraft.Base64;
import net.lax1dude.eaglercraft.ConfigConstants;
import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.LocalStorageManager;
import net.lax1dude.eaglercraft.ServerQuery.QueryResponse;
import net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2.RateLimit;
import net.minecraft.client.Minecraft;

public class ServerList {
	/** The Minecraft instance. */
	private final Minecraft mc;

	/** List of ServerData instances. */
	private final List<ServerData> servers = new ArrayList();
	private final List<ServerData> allServers = new ArrayList();
	
	public static final List<ServerData> forcedServers = new ArrayList();
	private static final Set<String> motdLocks = new HashSet();
	
	public static boolean hideDownDefaultServers = false;

	public ServerList(Minecraft par1Minecraft) {
		this.mc = par1Minecraft;
		this.loadServerList();
	}
	
	public static void loadDefaultServers(String base64) {
		try {
			NBTTagCompound nbt = CompressedStreamTools.readUncompressed(Base64.decodeBase64(base64));
			ConfigConstants.profanity = nbt.getBoolean("profanity");
			hideDownDefaultServers = nbt.getBoolean("hide_down");
			forcedServers.clear();
			NBTTagList list = nbt.getTagList("servers");
			for (int i = 0; i < list.tagCount(); ++i) {
				NBTTagCompound tag = (NBTTagCompound) list.tagAt(i);
				tag.setBoolean("default", true);
				forcedServers.add(ServerData.getServerDataFromNBTCompound(tag));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a list of servers from servers.dat, by running
	 * ServerData.getServerDataFromNBTCompound on each NBT compound found in the
	 * "servers" tag list.
	 */
	public void loadServerList() {
		freeServerIcons();
		this.servers.clear();
		this.allServers.clear();
		for(ServerData dat : forcedServers) {
			dat.pingSentTime = -1l;
			dat.hasPing = false;
			this.servers.add(dat);
			this.allServers.add(dat);
		}
		NBTTagList servers = LocalStorageManager.gameSettingsStorage.getTagList("servers");
		for (int i = 0; i < servers.tagCount(); ++i) {
			ServerData dat = ServerData.getServerDataFromNBTCompound((NBTTagCompound) servers.tagAt(i));
			this.servers.add(dat);
			this.allServers.add(dat);
		}
	}

	/**
	 * Runs getNBTCompound on each ServerData instance, puts everything into a
	 * "servers" NBT list and writes it to servers.dat.
	 */
	public void saveServerList() {
		NBTTagList servers = new NBTTagList();
		for(int i = forcedServers.size(); i < this.allServers.size(); ++i) {
			servers.appendTag(((ServerData) this.allServers.get(i)).getNBTCompound());
		}
		LocalStorageManager.gameSettingsStorage.setTag("servers", servers);
		LocalStorageManager.saveStorageG();
	}

	/**
	 * Gets the ServerData instance stored for the given index in the list.
	 */
	public ServerData getServerData(int par1) {
		return (ServerData) this.servers.get(par1);
	}

	/**
	 * Removes the ServerData instance stored for the given index in the list.
	 */
	public void removeServerData(int par1) {
		ServerData dat = this.servers.remove(par1);
		this.allServers.remove(dat);
		if(dat != null) {
			dat.freeIcon();
		}
	}

	/**
	 * Adds the given ServerData instance to the list.
	 */
	public void addServerData(ServerData par1ServerData) {
		par1ServerData.pingSentTime = -1l;
		par1ServerData.hasPing = false;
		this.allServers.add(par1ServerData);
		refreshServerPing();
	}

	/**
	 * Counts the number of ServerData instances in the list.
	 */
	public int countServers() {
		return this.servers.size();
	}

	/**
	 * Takes two list indexes, and swaps their order around.
	 */
	public void swapServers(int par1, int par2) { // will be fixed eventually
		/*
		ServerData var3 = this.getServerData(par1);
		ServerData dat = this.getServerData(par2);
		this.servers.set(par1, dat);
		this.servers.set(par2, var3);
		int i = this.allServers.indexOf(dat);
		this.allServers.set(par1, this.allServers.get(i));
		this.allServers.set(i, var3);
		this.saveServerList();
		*/
	}
	
	public void freeServerIcons() {
		for(ServerData dat : allServers) {
			if(dat.currentQuery != null && dat.currentQuery.isQueryOpen()) {
				dat.currentQuery.close();
			}
			if(dat.serverIconGL != -1) {
				EaglerAdapter.glDeleteTextures(dat.serverIconGL);
				dat.serverIconGL = -1;
			}
			dat.serverIconDirty = false;
			dat.serverIconEnabled = false;
		}
	}
	
	public void refreshServerPing() {
		this.servers.clear();
		this.servers.addAll(this.allServers);
		for(ServerData dat : servers) {
			if(dat.currentQuery != null && dat.currentQuery.isQueryOpen()) {
				dat.currentQuery.close();
			}
			dat.hasPing = false;
			dat.pingSentTime = -1l;
		}
	}
	
	public void updateServerPing() {
		int total = 0;
		Iterator<ServerData> itr = servers.iterator();
		while(itr.hasNext()) {
			ServerData dat = itr.next();
			if(dat.pingSentTime <= 0l) {
				dat.pingToServer = -2l;
				String addr = dat.serverIP;
				if(!addr.startsWith("ws://") && !addr.startsWith("wss://")) {
					if(EaglerAdapter.isSSLPage()) {
						addr = "wss://" + addr;
					}else {
						addr = "ws://" + addr;
					}
				}
				dat.pingSentTime = System.currentTimeMillis();
				dat.currentQuery = EaglerAdapter.openQuery("MOTD", addr);
				if(dat.currentQuery == null) {
					dat.hasPing = true;
				}else {
					++total;
				}
			}else if(dat.currentQuery != null) {
				if(!dat.hasPing) {
					++total;
				}
				if(dat.currentQuery.responseAvailable() > 0) {
					QueryResponse pkt;
					do {
						pkt = dat.currentQuery.getResponse();
					}while(dat.currentQuery.responseAvailable() > 0);
					if(pkt.rateLimitStatus != null) {
						if(pkt.rateLimitStatus == RateLimit.LOCKED) {
							dat.setRateLimitError(true, pkt.rateLimitIsTCP);
						}else if(pkt.rateLimitStatus == RateLimit.BLOCKED) {
							dat.setRateLimitError(false, pkt.rateLimitIsTCP);
						}
						dat.currentQuery.close();
						dat.pingToServer = -1l;
						dat.hasPing = true;
					}else {
						if(pkt.responseType.equalsIgnoreCase("MOTD") && pkt.isResponseJSON()) {
							dat.setMOTDFromQuery(pkt);
							if(!dat.hasPing) {
								dat.pingToServer = pkt.clientTime - dat.pingSentTime;
								dat.hasPing = true;
							}
						}
					}
				}
				if(dat.currentQuery.responseBinaryAvailable() > 0) {
					byte[] r;
					do {
						r = dat.currentQuery.getBinaryResponse();
					}while(dat.currentQuery.responseBinaryAvailable() > 0);
					if(r.length == 4096 * 4) {
						if(dat.serverIcon == null) {
							dat.serverIcon = new int[4096];
						}
						for(int i = 0; i < 4096; ++i) {
							dat.serverIcon[i] = (((int)r[i * 4 + 3]&0xFF) << 24) | (((int)r[i * 4]&0xFF) << 16) | (((int)r[i * 4 + 1]&0xFF) << 8) | ((int)r[i * 4 + 2]&0xFF);
						}
						dat.serverIconDirty = true;
					}
				}
				if(!dat.currentQuery.isQueryOpen() && dat.pingSentTime > 0l && !dat.hasPing) {
					dat.pingToServer = -1l;
					dat.hasPing = true;
				}
				if(ServerList.hideDownDefaultServers && dat.isDefault && dat.pingToServer == -1l && dat.hasPing == true) {
					itr.remove();
				}
			}
			if(total >= 4) {
				break;
			}
		}
	}
	
}
