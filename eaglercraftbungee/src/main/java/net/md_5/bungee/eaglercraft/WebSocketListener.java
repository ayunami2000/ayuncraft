package net.md_5.bungee.eaglercraft;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;

public class WebSocketListener extends WebSocketServer {
	
	public static class PendingSocket {
		public long openTime;
		public InetAddress realAddress;
		protected PendingSocket(long openTime, InetAddress realAddress) {
			this.openTime = openTime;
			this.realAddress = realAddress;
		}
	}

	private InetSocketAddress bungeeProxy;
	private ProxyServer bungeeCord;
	private ListenerInfo info;
	
	public WebSocketListener(ListenerInfo info, InetSocketAddress sock, ProxyServer bungeeCord) {
		super(info.getHost());
		this.setTcpNoDelay(true);
		this.setConnectionLostTimeout(5);
		this.start();
		this.info = info;
		this.bungeeProxy = sock;
		this.bungeeCord = bungeeCord;
	}

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		Object o = arg0.getAttachment();
		if(o != null) {
			if(o instanceof WebSocketProxy) {
				((WebSocketProxy)arg0.getAttachment()).killConnection();
			}
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
		Object o = arg0.getAttachment();
		if(o == null || (o instanceof PendingSocket)) {
			InetAddress realAddr;
			if(o == null) {
				realAddr = arg0.getRemoteSocketAddress().getAddress();
			}else {
				realAddr = ((PendingSocket)o).realAddress;
			}
			System.out.println("connection is binary - " + realAddr);
			WebSocketProxy proxyObj = new WebSocketProxy(arg0, realAddr, bungeeProxy);
			arg0.setAttachment(proxyObj);
			if(!proxyObj.connect()) {
				System.err.println("loopback to '" + bungeeProxy.toString() + "' failed - " + realAddr);
				arg0.close();
				return;
			}
			o = proxyObj;
		}
		if(o != null) {
			if(o instanceof WebSocketProxy) {
				((WebSocketProxy)o).sendPacket(arg1);
			}else {
				System.out.println("error: recieved binary data on text websocket - " + arg0.getRemoteSocketAddress());
				arg0.close();
			}
		}
	}

	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		System.out.println("websocket opened - " + arg0.getRemoteSocketAddress());
		if(info.hasForwardedHeaders()) {
			String s = arg1.getFieldValue("X-Real-IP");
			if(s != null) {
				try {
					InetAddress addr = InetAddress.getByName(s);
					arg0.setAttachment(new PendingSocket(System.currentTimeMillis(), addr));
					System.out.println("real IP of '" + arg0.getRemoteSocketAddress().toString() + "' is '" + addr.getHostAddress() + "'");
				}catch(UnknownHostException e) {
					System.out.println("invalid 'X-Real-IP' header - " + e.toString());
					arg0.close();
				}
			}else {
				arg0.setAttachment(new PendingSocket(System.currentTimeMillis(), arg0.getRemoteSocketAddress().getAddress()));
			}
		}else {
			arg0.setAttachment(new PendingSocket(System.currentTimeMillis(), arg0.getRemoteSocketAddress().getAddress()));
		}
	}

	@Override
	public void onStart() {
	}

	public void closeInactiveSockets() {
		for(WebSocket w : this.getConnections()) {
			Object o = w.getAttachment();
			if(o == null) {
				System.out.println("close inactive websocket - " + w.getRemoteSocketAddress());
				w.close();
			}else if(o instanceof PendingSocket) {
				if(System.currentTimeMillis() - ((PendingSocket)o).openTime > 5000l) {
					System.out.println("close inactive websocket - " + ((PendingSocket)o).realAddress);
					w.close();
				}
			}
		}
	}

}
