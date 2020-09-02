package net.md_5.bungee.eaglercraft;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.event.EventHandler;

public class PluginEaglerSkins extends Plugin implements Listener {
	
	private final HashMap<String,byte[]> skinCollection = new HashMap();
	
	private static final int[] SKIN_DATA_SIZE = new int[] { 64*32*4, 64*64*4, 128*64*4, 128*128*4, 1 };
	
	public PluginEaglerSkins() {
		super(new PluginDescription("EaglerSkins", PluginEaglerSkins.class.getName(), "1.0.0", "LAX1DUDE", Collections.emptySet(), null));
	}

	public void onLoad() {
		
	}

	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
	}

	public void onDisable() {
		
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		if(event.getSender() instanceof UserConnection && event.getData().length > 0) {
			String user = ((UserConnection)event.getSender()).getName();
			byte[] msg = event.getData();
			if("EAG|MySkin".equals(event.getTag())) {
				int t = (int)msg[0] & 0xFF;
				if(t >= 0 && t < SKIN_DATA_SIZE.length && msg.length == (SKIN_DATA_SIZE[t] + 1)) {
					skinCollection.put(user, msg);
				}
			}else if("EAG|FetchSkin".equals(event.getTag())) {
				if(msg.length > 2) {
					String fetch = new String(msg, 2, msg.length - 2, StandardCharsets.UTF_8);
					byte[] data;
					if((data = skinCollection.get(fetch)) != null) {
						byte[] conc = new byte[data.length + 2];
						conc[0] = msg[0]; conc[1] = msg[1]; //synchronization cookie
						System.arraycopy(data, 0, conc, 2, data.length);
						((UserConnection)event.getSender()).sendData("EAG|UserSkin", conc);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		skinCollection.remove(event.getPlayer().getName());
	}

}
