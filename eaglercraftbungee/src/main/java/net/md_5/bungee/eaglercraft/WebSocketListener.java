package net.md_5.bungee.eaglercraft;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;

public class WebSocketListener extends WebSocketServer {

	private InetSocketAddress bungeeProxy;
	private ProxyServer bungeeCord;
	
	public WebSocketListener(ListenerInfo info, InetSocketAddress sock, ProxyServer bungeeCord) {
		super(info.getHost());
		this.setTcpNoDelay(true);
		this.setConnectionLostTimeout(5);
		this.start();
		this.bungeeProxy = sock;
		this.bungeeCord = bungeeCord;
	}

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		if(arg0.getAttachment() != null) {
			((WebSocketProxy)arg0.getAttachment()).killConnection();
		}
		System.out.println("websocket closed - " + arg0.getRemoteSocketAddress());
	}

	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		arg1.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket arg0, String arg1) {
	}

	@Override
	public void onMessage(WebSocket arg0, ByteBuffer arg1) {
		if(arg0.getAttachment() != null) {
			((WebSocketProxy)arg0.getAttachment()).sendPacket(arg1);
		}
	}

	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		System.out.println("websocket opened - " + arg0.getRemoteSocketAddress());
		WebSocketProxy proxyObj = new WebSocketProxy(arg0, bungeeProxy);
		arg0.setAttachment(proxyObj);
		if(!proxyObj.connect()) {
			arg0.close();
		}
	}

	@Override
	public void onStart() {
		
	}

}
