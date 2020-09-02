package net.lax1dude.eaglercraft;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import com.jcraft.jzlib.InflaterInputStream;

public class AssetRepository {
	
	private static final HashMap<String,byte[]> filePool = new HashMap();
	
	public static final void install(byte[] pkg) throws IOException {
		ByteArrayInputStream in2 = new ByteArrayInputStream(pkg);
		DataInputStream in = new DataInputStream(in2);
		byte[] header = new byte[8];
		in.read(header);
		if(!"EAGPKG!!".equals(new String(header, Charset.forName("UTF-8")))) throw new IOException("invalid epk file");
		in.readUTF();
		in = new DataInputStream(new InflaterInputStream(in2));
		String s = null;
		SHA1Digest dg = new SHA1Digest();
		while("<file>".equals(s = in.readUTF())) {
			String path = in.readUTF();
			byte[] digest = new byte[20];
			byte[] digest2 = new byte[20];
			in.read(digest);
			int len = in.readInt();
			byte[] file = new byte[len];
			in.read(file);
			if(filePool.containsKey(path)) continue;
			dg.update(file, 0, len); dg.doFinal(digest2, 0);
			if(!Arrays.equals(digest, digest2)) throw new IOException("invalid file hash for "+path);
			filePool.put(path, file);
			if(!"</file>".equals(in.readUTF())) throw new IOException("invalid epk file");
		}
		if(in.available() > 0 || !" end".equals(s)) throw new IOException("invalid epk file");
	}
	
	public static final byte[] getResource(String path) {
		if(path.startsWith("/")) path = path.substring(1);
		return filePool.get(path);
	}

}
