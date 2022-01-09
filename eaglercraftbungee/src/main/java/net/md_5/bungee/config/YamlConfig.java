// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.config;

import net.md_5.bungee.tab.ServerUnique;
import net.md_5.bungee.tab.GlobalPing;
import net.md_5.bungee.tab.Global;
import net.md_5.bungee.api.tab.TabListHandler;
import net.md_5.bungee.api.config.TexturePackInfo;
import net.md_5.bungee.api.ChatColor;
import java.util.HashSet;
import net.md_5.bungee.api.config.ListenerInfo;
import java.util.Collection;
import java.net.InetSocketAddress;
import java.util.Iterator;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.config.ServerInfo;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import java.io.Writer;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.io.InputStream;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.io.IOException;
import net.md_5.bungee.util.CaseInsensitiveMap;
import java.io.FileInputStream;
import org.yaml.snakeyaml.DumperOptions;
import java.io.File;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.config.AuthServiceInfo;
import net.md_5.bungee.api.config.ConfigurationAdapter;

public class YamlConfig implements ConfigurationAdapter {
	private Yaml yaml;
	private Map config;
	private final File file;

	public YamlConfig() {
		this.file = new File("config.yml");
	}

	@Override
	public void load() {
		try {
			this.file.createNewFile();
			final DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			this.yaml = new Yaml(options);
			try (final InputStream is = new FileInputStream(this.file)) {
				this.config = (Map) this.yaml.load(is);
			}
			if (this.config == null) {
				this.config = (Map) new CaseInsensitiveMap();
			} else {
				this.config = (Map) new CaseInsensitiveMap(this.config);
			}
		} catch (IOException ex) {
			throw new RuntimeException("Could not load configuration!", ex);
		}
		final Map<String, Object> permissions = this.get("permissions", new HashMap<String, Object>());
		if (permissions.isEmpty()) {
			permissions.put("default", Arrays.asList("bungeecord.command.server", "bungeecord.command.list"));
			permissions.put("admin", Arrays.asList("bungeecord.command.alert", "bungeecord.command.end", "bungeecord.command.ip", "bungeecord.command.reload"));
		}
		final Map<String, Object> groups = this.get("groups", new HashMap<String, Object>());
		if (groups.isEmpty()) {
			groups.put("lax1dude", Collections.singletonList("admin"));
		}
		/*
		final Map<String, Object> auth = this.get("authservice", new HashMap<String, Object>());
		if(auth.isEmpty()) {
			auth.put("enabled", false);
			auth.put("limbo", "lobby");
			auth.put("authfile", "passwords.yml");
			auth.put("timeout", 30);
		}
		*/
	}

	private <T> T get(final String path, final T def) {
		return this.get(path, def, this.config);
	}

	private <T> T get(final String path, final T def, final Map submap) {
		final int index = path.indexOf(46);
		if (index == -1) {
			Object val = submap.get(path);
			if (val == null && def != null) {
				val = def;
				submap.put(path, def);
				this.save();
			}
			return (T) val;
		}
		final String first = path.substring(0, index);
		final String second = path.substring(index + 1, path.length());
		Map sub = (Map) submap.get(first);
		if (sub == null) {
			sub = new LinkedHashMap();
			submap.put(first, sub);
		}
		return (T) this.get(second, (Object) def, sub);
	}

	private void save() {
		try (final FileWriter wr = new FileWriter(this.file)) {
			this.yaml.dump((Object) this.config, (Writer) wr);
		} catch (IOException ex) {
			ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not save config", ex);
		}
	}

	@Override
	public int getInt(final String path, final int def) {
		return this.get(path, def);
	}

	@Override
	public String getString(final String path, final String def) {
		return this.get(path, def);
	}

	@Override
	public boolean getBoolean(final String path, final boolean def) {
		return this.get(path, def);
	}

	@Override
	public Map<String, ServerInfo> getServers() {
		final Map<String, HashMap> base = this.get("servers", (Map<String, HashMap>) Collections.singletonMap("lobby", new HashMap()));
		final Map<String, ServerInfo> ret = new HashMap<String, ServerInfo>();
		for (final Map.Entry<String, HashMap> entry : base.entrySet()) {
			final Map<String, Object> val = entry.getValue();
			final String name = entry.getKey();
			final String addr = this.get("address", "localhost:25501", val);
			final boolean restricted = this.get("restricted", false, val);
			final InetSocketAddress address = Util.getAddr(addr);
			final ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, address, restricted);
			ret.put(name, info);
		}
		return ret;
	}

	@Override
	public Collection<ListenerInfo> getListeners() {
		final Collection<HashMap> base = this.get("listeners", (Collection<HashMap>) Arrays.asList(new HashMap()));
		final Map<String, String> forcedDef = new HashMap<String, String>();
		//forcedDef.put("pvp.md-5.net", "pvp");
		final Collection<ListenerInfo> ret = new HashSet<ListenerInfo>();
		for (final Map<String, Object> val : base) {
			String motd = this.get("motd", "&6&lbungeecord eaglercraft server |>", val);
			motd = ChatColor.translateAlternateColorCodes('&', motd);
			final int maxPlayers = this.get("max_players", 60, val);
			final String defaultServer = this.get("default_server", "lobby", val);
			final String fallbackServer = this.get("fallback_server", defaultServer, val);
			final boolean forceDefault = this.get("force_default_server", true, val);
			final boolean websocket = this.get("websocket", true, val);
			final String host = this.get("host", "0.0.0.0:25565", val);
			final int tabListSize = this.get("tab_size", 60, val);
			final InetSocketAddress address = Util.getAddr(host);
			final Map<String, String> forced = (Map<String, String>) new CaseInsensitiveMap(this.get("forced_hosts", forcedDef, val));
			final String textureURL = this.get("texture_url", (String) null, val);
			final int textureSize = this.get("texture_size", 16, val);
			final TexturePackInfo texture = (textureURL == null) ? null : new TexturePackInfo(textureURL, textureSize);
			final String tabListName = this.get("tab_list", "GLOBAL_PING", val);
			DefaultTabList value = DefaultTabList.valueOf(tabListName.toUpperCase());
			if (value == null) {
				value = DefaultTabList.GLOBAL_PING;
			}
			final ListenerInfo info = new ListenerInfo(address, motd, maxPlayers, tabListSize, defaultServer, fallbackServer, forceDefault, websocket, forced, texture, value.clazz);
			ret.add(info);
		}
		return ret;
	}

	@Override
	public Collection<String> getGroups(final String player) {
		final Collection<String> groups = this.get("groups." + player, (Collection<String>) null);
		final Collection<String> ret = (groups == null) ? new HashSet<String>() : new HashSet<String>(groups);
		ret.add("default");
		return ret;
	}

	@Override
	public Collection<String> getPermissions(final String group) {
		return this.get("permissions." + group, (Collection<String>) Collections.EMPTY_LIST);
	}

	private enum DefaultTabList {
		GLOBAL((Class<? extends TabListHandler>) Global.class), GLOBAL_PING((Class<? extends TabListHandler>) GlobalPing.class), SERVER((Class<? extends TabListHandler>) ServerUnique.class);

		private final Class<? extends TabListHandler> clazz;

		private DefaultTabList(final Class<? extends TabListHandler> clazz) {
			this.clazz = clazz;
		}
	}

	@Override
	public AuthServiceInfo getAuthSettings() {
		//final Map<String, Object> auth = this.get("authservice", new HashMap<String, Object>());
		//return new AuthServiceInfo(this.get("enabled", true, auth), this.get("limbo", "lobby", auth), new File(this.get("authfile", "passwords.yml", auth)), this.get("timeout", 30, auth));
		return null;
	}
}
