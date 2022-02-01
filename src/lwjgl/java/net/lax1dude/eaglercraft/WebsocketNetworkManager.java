package net.lax1dude.eaglercraft;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import java.security.Key;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.src.*;
import me.ayunami2000.ayuncraft.CryptManager;
import org.bouncycastle.crypto.BufferedBlockCipher;

import javax.crypto.SecretKey;

public class WebsocketNetworkManager implements INetworkManager {
	private boolean isInputBeingDecrypted;
	private boolean isOutputEncrypted;
	private SecretKey sharedKeyForEncryption;

	private final boolean logpackets=false;

	private BufferedBlockCipher inputBufferedBlockCipher=null;
	private BufferedBlockCipher outputBufferedBlockCipher=null;

	private NetHandler netHandler;

	Pattern ipPattern = Pattern.compile("^"
			+ "(((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}" // Domain name
			+ "|"
			+ "localhost" // localhost
			+ "|"
			+ "(([0-9]{1,3}\\.){3})[0-9]{1,3})" // Ip
			+ "(:"
			+ "[0-9]{1,5})?$"); // Port

	public WebsocketNetworkManager(String uri, String eagler, NetHandler netHandler) throws IOException {
		this.netHandler = netHandler;
		this.sharedKeyForEncryption = null;
		this.isInputBeingDecrypted = false;
		this.isOutputEncrypted = false;
		String proxyUrl=Minecraft.getMinecraft().gameSettings.proxy;
		boolean stillConnect=true;
		if(!proxyUrl.equals("")&&!uri.contains("/")){
			stillConnect=false;
			if (ipPattern.matcher(proxyUrl).matches()&&ipPattern.matcher(uri).matches()) {
				String ip = uri;
				String port = "25565";
				if (uri.contains(":")) {
					String[] ipPort = uri.split(":", 2);
					ip = ipPort[0];
					port = ipPort[1];
				}
				//send initial request (lag client)
				URL url = new URL("http"+(EaglerAdapter.isSSLPage()?"s":"")+"://"+proxyUrl+"/api/vm/net/connect");
				URLConnection con = url.openConnection();
				HttpURLConnection http = (HttpURLConnection)con;
				http.setRequestMethod("POST");
				http.setDoOutput(true);
				byte[] out = ("{\"port\":\""+port+"\",\"host\":\""+ip+"\"}").getBytes(StandardCharsets.UTF_8);
				http.setFixedLengthStreamingMode(out.length);
				http.setRequestProperty("Content-Type","application/json; charset=UTF-8");
				http.setConnectTimeout(5000);
				http.connect();
				http.getOutputStream().write(out);
				Reader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				for (int c; (c = in.read()) >= 0;)
					sb.append((char)c);
				String response = sb.toString();
				response=response.substring(10);
				response=response.split("\"",2)[0];
				uri="ws"+(EaglerAdapter.isSSLPage()?"s":"")+"://"+proxyUrl+"/api/vm/net/socket?token="+response;
				stillConnect=true;
			}
		}
		if(!stillConnect||!EaglerAdapter.startConnection(uri)) {
			throw new IOException("websocket to "+uri+" failed");
		}
		EaglerAdapter.setDebugVar("minecraftServer", uri);
	}

	public void setNetHandler(NetHandler netHandler) {
		this.netHandler = netHandler;
	}

	private ByteArrayOutputStream sendBuffer = new ByteArrayOutputStream();

	public void addToSendQueue(Packet var1) {
		try {
			sendBuffer.reset();

			DataOutputStream yee;
			if(this.isOutputEncrypted&&!(var1 instanceof Packet252SharedKey)){
				yee = this.encryptOuputStream();
			}else{
				yee = new DataOutputStream(sendBuffer);
			}

			if (Minecraft.getMinecraft().gameSettings.useDefaultProtocol && var1 instanceof Packet252SharedKey && !this.isOutputEncrypted)
			{
				this.sharedKeyForEncryption = ((Packet252SharedKey)var1).getSharedKey();
				this.isOutputEncrypted=true;
				//yee=this.encryptOuputStream(yee);
			}
			Packet.writePacket(var1, yee);
			if(logpackets)System.out.println("SENDING: "+var1);
			yee.flush();
			EaglerAdapter.writePacket(sendBuffer.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void wakeThreads() {
	}

	private static class ByteBufferDirectInputStream extends InputStream {
		private ByteBuffer buf;
		private ByteBufferDirectInputStream(ByteBuffer b) {
			this.buf = b;
		}

		@Override
		public int read() throws IOException {
			return buf.remaining() > 0 ? ((int)buf.get() & 0xFF) : -1;
		}

		@Override
		public int available() {
			return buf.remaining();
		}
	}

	private ByteBuffer oldChunkBuffer = null;
	private LinkedList<ByteBuffer> readChunks = new LinkedList();

	private ByteBuffer oldDecryptedChunkBuffer = null;
	private LinkedList<ByteBuffer> decryptedReadChunks = new LinkedList();

	public void processReadPackets() {
		readChunks.clear();

		if(oldChunkBuffer != null) {
			readChunks.add(oldChunkBuffer);
		}

		byte[] packet;
		while((packet = EaglerAdapter.readPacket()) != null) {
			readChunks.add(ByteBuffer.wrap(packet));
		}
		if(!readChunks.isEmpty()) {

			int cap = 0;
			for(ByteBuffer b : readChunks) {
				cap += b.limit();
			}

			ByteBuffer stream = ByteBuffer.allocate(cap);
			for(ByteBuffer b : readChunks) {
				stream.put(b);
			}
			stream.flip();

			if(this.isInputBeingDecrypted){
				decryptedReadChunks.clear();

				if (oldDecryptedChunkBuffer != null) {
					decryptedReadChunks.add(oldDecryptedChunkBuffer);
					oldDecryptedChunkBuffer = null;
				}

				byte[] block = new byte[/*2048*/32];
				byte[] decryp = new byte[this.inputBufferedBlockCipher.getOutputSize(/*2048*/32)];
				while (stream.remaining() >= /*2048*/32) {
					stream.get(block);
					int i = this.inputBufferedBlockCipher.processByte(block, 0, /*2048*/32, decryp, 0);
					ByteBuffer chunk = ByteBuffer.allocate(i);
					chunk.put(decryp, 0, i);
					chunk.flip();
					decryptedReadChunks.add(chunk);
				}

				oldChunkBuffer = stream.remaining() > 0 ? stream.slice() : null;

				int cap2 = 0;
				for (ByteBuffer b : decryptedReadChunks) {
					cap2 += b.limit();
				}

				ByteBuffer decStream = ByteBuffer.allocate(cap2);
				for (ByteBuffer b : decryptedReadChunks) {
					decStream.put(b);
				}
				decStream.flip();

				DataInputStream packetStream = new DataInputStream(new ByteBufferDirectInputStream(decStream));
				while (decStream.hasRemaining()) {
					decStream.mark();
					try {
						Packet pkt = Packet.readPacket(packetStream, false);
						if(logpackets)System.out.println("RECEIVING: " + pkt);
						pkt.processPacket(this.netHandler);
					} catch (EOFException e) {
						decStream.reset();
						break;
					} catch (IOException e) {
						continue;
					} catch (Throwable e2) {
						e2.printStackTrace();
					}
				}

				if (decStream.hasRemaining()) {
					oldDecryptedChunkBuffer = decStream.slice();
				} else {
					oldDecryptedChunkBuffer = null;
				}
			}else {
				DataInputStream packetStream = new DataInputStream(new ByteBufferDirectInputStream(stream));
				while (stream.hasRemaining()) {
					stream.mark();
					try {
						Packet pkt = Packet.readPacket(packetStream, false);
						boolean change=false;
						if (pkt != null) {
							if (Minecraft.getMinecraft().gameSettings.useDefaultProtocol && pkt instanceof Packet252SharedKey && !this.isInputBeingDecrypted) {
								packetStream = this.decryptInputStream(new ByteBufferDirectInputStream(stream));
								change=true;
							}
							if(logpackets)System.out.println("RECEIVING: " + pkt);
							pkt.processPacket(this.netHandler);
							if(change){
								processReadPackets();
								return;
								//break;
							}
						}
					} catch (EOFException e) {
						stream.reset();
						break;
					} catch (IOException e) {
						continue;
					} catch (Throwable e2) {
						e2.printStackTrace();
					}
				}

				if (stream.hasRemaining()) {
					oldChunkBuffer = stream.slice();
				} else {
					oldChunkBuffer = null;
				}
			}
		}
	}

	public void serverShutdown() {
		if(EaglerAdapter.connectionOpen()) {
			EaglerAdapter.endConnection();
			EaglerAdapter.setDebugVar("minecraftServer", "null");
		}
	}

	private DataInputStream decryptInputStream(ByteBufferDirectInputStream var1) throws IOException
	{
		this.isInputBeingDecrypted = true;
		if(this.inputBufferedBlockCipher==null){
			this.inputBufferedBlockCipher = CryptManager.createBufferedBlockCipher(false, (Key) this.sharedKeyForEncryption);
		}
		return new DataInputStream(CryptManager.decryptInputStream(this.inputBufferedBlockCipher, var1));
	}

	/**
	 * flushes the stream and replaces it with an encryptedOutputStream
	 */
	private DataOutputStream encryptOuputStream(DataOutputStream var0) throws IOException
	{
		var0.flush();
		this.isOutputEncrypted = true;
		BufferedOutputStream var1 = new BufferedOutputStream(CryptManager.encryptOuputStream(this.sharedKeyForEncryption, var0), 5120);
		return new DataOutputStream(var1);
	}
	private DataOutputStream encryptOuputStream() throws IOException
	{
		if(this.outputBufferedBlockCipher==null){
			this.outputBufferedBlockCipher = CryptManager.createBufferedBlockCipher(true, (Key) this.sharedKeyForEncryption);
		}
		BufferedOutputStream var1 = new BufferedOutputStream(CryptManager.encryptOuputStream(this.outputBufferedBlockCipher, sendBuffer), 5120);
		return new DataOutputStream(var1);
	}

	public int packetSize() {
		return 0;
	}

	public void networkShutdown(String var1, Object... var2) {
		serverShutdown();
	}

	public void closeConnections() {
		if(EaglerAdapter.connectionOpen()) {
			EaglerAdapter.endConnection();
			EaglerAdapter.setDebugVar("minecraftServer", "null");
		}
	}

}