// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.api.connection;

import net.md_5.bungee.api.config.ListenerInfo;
import java.net.InetSocketAddress;

public interface PendingConnection extends Connection {
	String getName();

	byte getVersion();

	InetSocketAddress getVirtualHost();

	ListenerInfo getListener();
}
