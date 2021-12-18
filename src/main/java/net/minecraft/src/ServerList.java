package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.Base64;
import net.lax1dude.eaglercraft.ConfigConstants;
import net.lax1dude.eaglercraft.LocalStorageManager;
import net.minecraft.client.Minecraft;

public class ServerList {
	/** The Minecraft instance. */
	private final Minecraft mc;

	/** List of ServerData instances. */
	private final List servers = new ArrayList();
	
	public static final List<ServerData> forcedServers = new ArrayList();

	public ServerList(Minecraft par1Minecraft) {
		this.mc = par1Minecraft;
		this.loadServerList();
	}
	
	public static void loadDefaultServers(String base64) {
		try {
			NBTTagCompound nbt = CompressedStreamTools.readUncompressed(Base64.decodeBase64(base64));
			if(nbt.getBoolean("profanity")) {
				ConfigConstants.profanity = true;
			}
			forcedServers.clear();
			NBTTagList list = nbt.getTagList("servers");
			for (int i = 0; i < list.tagCount(); ++i) {
				forcedServers.add(ServerData.getServerDataFromNBTCompound((NBTTagCompound) list.tagAt(i)));
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
		this.servers.clear();
		this.servers.addAll(forcedServers);
		NBTTagList servers = LocalStorageManager.gameSettingsStorage.getTagList("servers");
		for (int i = 0; i < servers.tagCount(); ++i) {
			this.servers.add(ServerData.getServerDataFromNBTCompound((NBTTagCompound) servers.tagAt(i)));
		}
	}

	/**
	 * Runs getNBTCompound on each ServerData instance, puts everything into a
	 * "servers" NBT list and writes it to servers.dat.
	 */
	public void saveServerList() {
		NBTTagList servers = new NBTTagList();
		for(int i = forcedServers.size(); i < this.servers.size(); ++i) {
			servers.appendTag(((ServerData) this.servers.get(i)).getNBTCompound());
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
		this.servers.remove(par1);
	}

	/**
	 * Adds the given ServerData instance to the list.
	 */
	public void addServerData(ServerData par1ServerData) {
		this.servers.add(par1ServerData);
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
	public void swapServers(int par1, int par2) {
		ServerData var3 = this.getServerData(par1);
		this.servers.set(par1, this.getServerData(par2));
		this.servers.set(par2, var3);
		this.saveServerList();
	}

	/**
	 * Sets the given index in the list to the given ServerData instance.
	 */
	public void setServer(int par1, ServerData par2ServerData) {
		this.servers.set(par1, par2ServerData);
	}

	public static void func_78852_b(ServerData par0ServerData) {
		ServerList var1 = new ServerList(Minecraft.getMinecraft());
		var1.loadServerList();

		for (int var2 = 0; var2 < var1.countServers(); ++var2) {
			ServerData var3 = var1.getServerData(var2);

			if (var3.serverName.equals(par0ServerData.serverName) && var3.serverIP.equals(par0ServerData.serverIP)) {
				var1.setServer(var2, par0ServerData);
				break;
			}
		}

		var1.saveServerList();
	}
}
