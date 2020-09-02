package net.md_5.bungee.eaglercraft;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Not the ideal solution but what are we supposed to do
 * 
 */
public class WebSocketProxy extends SimpleChannelInboundHandler<ByteBuf> {
	
	private WebSocket client;
	private InetSocketAddress tcpListener;
	private NioSocketChannel tcpChannel;
	
	private static final EventLoopGroup group = new NioEventLoopGroup(4);
	
	public WebSocketProxy(WebSocket w, InetSocketAddress addr) {
		client = w;
		tcpListener = addr;
		tcpChannel = null;
	}
	
	public void killConnection() {
		if(client.isOpen()) {
			client.close();
		}
		if(tcpChannel != null && tcpChannel.isOpen()) {
			try {
				tcpChannel.disconnect().sync();
			} catch (InterruptedException e) {
				;
			}
		}
	}

	public boolean connect() {
		try {
			if(tcpChannel == null) {
				Bootstrap clientBootstrap = new Bootstrap();
				clientBootstrap.group(group);
				clientBootstrap.channel(NioSocketChannel.class);
				clientBootstrap.remoteAddress(tcpListener);
				clientBootstrap.option(ChannelOption.TCP_NODELAY, true);
				clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
				    protected void initChannel(SocketChannel socketChannel) throws Exception {
				        socketChannel.pipeline().addLast(WebSocketProxy.this);
				    }
				});
				tcpChannel = (NioSocketChannel) clientBootstrap.connect().sync().channel();
				return true;
			}
		}catch(Throwable t) {
			t.printStackTrace();
		}
		return false;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext arg0, ByteBuf buffer) throws Exception {
    	ByteBuffer toSend = ByteBuffer.allocateDirect(buffer.capacity());
    	toSend.put(buffer.nioBuffer());
    	toSend.flip();
    	client.send(toSend);
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

	public void sendPacket(ByteBuffer arg1) {
		if(tcpChannel != null && tcpChannel.isOpen()) {
			tcpChannel.write(Unpooled.wrappedBuffer(arg1));
		}
	}
	
}
