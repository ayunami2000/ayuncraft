// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.api.config;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.Collection;
import java.net.InetSocketAddress;

public interface ServerInfo {
	String getName();

	InetSocketAddress getAddress();

	Collection<ProxiedPlayer> getPlayers();

	boolean canAccess(final CommandSender p0);

	void sendData(final String p0, final byte[] p1);

	void ping(final Callback<ServerPing> p0);
}
