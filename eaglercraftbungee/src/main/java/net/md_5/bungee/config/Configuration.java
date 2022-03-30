// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.config;

import net.md_5.bungee.api.config.AuthServiceInfo;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import java.util.Map;
import net.md_5.bungee.util.CaseInsensitiveMap;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ProxyServer;
import java.util.UUID;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.eaglercraft.EaglercraftBungee;
import gnu.trove.map.TMap;
import net.md_5.bungee.api.config.ListenerInfo;
import java.util.Collection;

public class Configuration {
	private int timeout;
	private String uuid;
	private Collection<ListenerInfo> listeners;
	private TMap<String, ServerInfo> servers;
	private AuthServiceInfo authInfo;
	private boolean onlineMode;
	private int playerLimit;
	private String name;

	public Configuration() {
		this.timeout = 30000;
		this.uuid = UUID.randomUUID().toString();
		this.onlineMode = true;
		this.playerLimit = -1;
	}

	public void load() {
		final ConfigurationAdapter adapter = ProxyServer.getInstance().getConfigurationAdapter();
		adapter.load();
		this.listeners = adapter.getListeners();
		this.timeout = adapter.getInt("timeout", this.timeout);
		this.uuid = adapter.getString("stats", this.uuid);
		this.authInfo = adapter.getAuthSettings();
		this.onlineMode = false;
		this.playerLimit = adapter.getInt("player_limit", this.playerLimit);
		this.name = adapter.getString("server_name", EaglercraftBungee.brand + " Server");
		Preconditions.checkArgument(this.listeners != null && !this.listeners.isEmpty(), (Object) "No listeners defined.");
		final Map<String, ServerInfo> newServers = adapter.getServers();
		Preconditions.checkArgument(newServers != null && !newServers.isEmpty(), (Object) "No servers defined");
		if (this.servers == null) {
			this.servers = (TMap<String, ServerInfo>) new CaseInsensitiveMap(newServers);
		} else {
			for (final ServerInfo oldServer : this.servers.values()) {
				Preconditions.checkArgument(newServers.containsValue(oldServer), "Server %s removed on reload!", new Object[] { oldServer.getName() });
			}
			for (final Map.Entry<String, ServerInfo> newServer : newServers.entrySet()) {
				if (!this.servers.containsValue(newServer.getValue())) {
					this.servers.put(newServer.getKey(), newServer.getValue());
				}
			}
		}
		for (final ListenerInfo listener : this.listeners) {
			Preconditions.checkArgument(this.servers.containsKey((Object) listener.getDefaultServer()), "Default server %s is not defined", new Object[] { listener.getDefaultServer() });
		}
	}

	public int getTimeout() {
		return this.timeout;
	}

	public String getUuid() {
		return this.uuid;
	}

	public Collection<ListenerInfo> getListeners() {
		return this.listeners;
	}

	public TMap<String, ServerInfo> getServers() {
		return this.servers;
	}

	public boolean isOnlineMode() {
		return this.onlineMode;
	}

	public int getPlayerLimit() {
		return this.playerLimit;
	}
	
	public AuthServiceInfo getAuthInfo() {
		return authInfo;
	}

	public String getServerName() {
		return name;
	}
}
