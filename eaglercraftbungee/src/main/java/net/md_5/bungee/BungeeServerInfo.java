// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee;

import java.beans.ConstructorProperties;
import java.util.LinkedList;
import java.util.ArrayList;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelHandler;
import net.md_5.bungee.netty.PipelineUtils;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.Future;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.netty.HandlerBoss;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.protocol.packet.PacketFAPluginMessage;
import java.util.Objects;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.CommandSender;
import java.util.Collections;
import net.md_5.bungee.protocol.packet.DefinedPacket;
import java.util.Queue;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.Collection;
import java.net.InetSocketAddress;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeServerInfo implements ServerInfo {
	private final String name;
	private final InetSocketAddress address;
	private final Collection<ProxiedPlayer> players;
	private final boolean restricted;
	private final Queue<DefinedPacket> packetQueue;

	public void addPlayer(final ProxiedPlayer player) {
		synchronized (this.players) {
			this.players.add(player);
		}
	}

	public void removePlayer(final ProxiedPlayer player) {
		synchronized (this.players) {
			this.players.remove(player);
		}
	}

	@Override
	public Collection<ProxiedPlayer> getPlayers() {
		synchronized (this.players) {
			return Collections.unmodifiableCollection((Collection<? extends ProxiedPlayer>) this.players);
		}
	}

	@Override
	public boolean canAccess(final CommandSender player) {
		Preconditions.checkNotNull((Object) player, (Object) "player");
		return !this.restricted || player.hasPermission("bungeecord.server." + this.name);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ServerInfo && Objects.equals(this.getAddress(), ((ServerInfo) obj).getAddress());
	}

	@Override
	public int hashCode() {
		return this.address.hashCode();
	}

	@Override
	public void sendData(final String channel, final byte[] data) {
		Preconditions.checkNotNull((Object) channel, (Object) "channel");
		Preconditions.checkNotNull((Object) data, (Object) "data");
		final Server server = this.players.isEmpty() ? null : this.players.iterator().next().getServer();
		if (server != null) {
			server.sendData(channel, data);
		} else {
			synchronized (this.packetQueue) {
				this.packetQueue.add(new PacketFAPluginMessage(channel, data));
			}
		}
	}

	@Override
	public void ping(final Callback<ServerPing> callback) {
		Preconditions.checkNotNull((Object) callback, (Object) "callback");
		final ChannelFutureListener listener = (ChannelFutureListener) new ChannelFutureListener() {
			public void operationComplete(final ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					((HandlerBoss) future.channel().pipeline().get((Class) HandlerBoss.class)).setHandler(new PingHandler(BungeeServerInfo.this, callback));
				} else {
					callback.done(null, future.cause());
				}
			}
		};
		((Bootstrap) ((Bootstrap) ((Bootstrap) ((Bootstrap) new Bootstrap().channel((Class) NioSocketChannel.class)).group((EventLoopGroup) BungeeCord.getInstance().eventLoops)).handler((ChannelHandler) PipelineUtils.BASE))
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(5000))).remoteAddress((SocketAddress) this.getAddress()).connect().addListener((GenericFutureListener) listener);
	}

	@ConstructorProperties({ "name", "address", "restricted" })
	public BungeeServerInfo(final String name, final InetSocketAddress address, final boolean restricted) {
		this.players = new ArrayList<ProxiedPlayer>();
		this.packetQueue = new LinkedList<DefinedPacket>();
		this.name = name;
		this.address = address;
		this.restricted = restricted;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public InetSocketAddress getAddress() {
		return this.address;
	}

	public boolean isRestricted() {
		return this.restricted;
	}

	public Queue<DefinedPacket> getPacketQueue() {
		return this.packetQueue;
	}
}
