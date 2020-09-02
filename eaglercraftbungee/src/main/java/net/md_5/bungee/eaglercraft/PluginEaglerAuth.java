package net.md_5.bungee.eaglercraft;

import java.util.Collections;
import java.util.HashSet;

import net.md_5.bungee.PacketConstants;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Packet1Login;
import net.md_5.bungee.protocol.packet.Packet9Respawn;

public class PluginEaglerAuth extends Plugin implements Listener {
	
	public PluginEaglerAuth() {
		super(new PluginDescription("EaglerAuth", PluginEaglerAuth.class.getName(), "1.0.0", "LAX1DUDE", Collections.emptySet(), null));
	}
	
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
	}

	@Override
	public void onDisable() {
		
	}
	
	private final HashSet<ProxiedPlayer> playersInLimbo = new HashSet();
	
	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		//playersInLimbo.add(event.getPlayer());
	}
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		//if(playersInLimbo.contains(player)) {
		//	event.setCancelled(true);
		//	player.unsafe().sendPacket(new Packet1Login(0, "END", (byte) 1, 1, (byte) 0, (byte) 0, (byte) 1));
		//	player.unsafe().sendPacket(new Packet9Respawn(1, (byte) 0, (byte) 1, (short) 255, "END"));
		//}
	}

}
