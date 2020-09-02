import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.DeflaterOutputStream;

public class CompilePackage {
	
	private static ArrayList<File> files = new ArrayList();
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		File root = new File(args[0]);
		listDirectory(root);
		ByteArrayOutputStream osb = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(osb);
		String start = root.getAbsolutePath();
		os.write("EAGPKG!!".getBytes(Charset.forName("UTF-8")));
		os.writeUTF("\n\n #  eaglercraft package file - assets copyright mojang ab\n #  eagler eagler eagler eagler eagler eagler eagler\n\n");
		Deflater d = new Deflater(9);
		os = new DataOutputStream(new DeflaterOutputStream(osb, d));
		MessageDigest md = MessageDigest.getInstance("SHA-1"); 
		for(File f : files) {
			os.writeUTF("<file>");
			String p = f.getAbsolutePath().replace(start, "").replace('\\', '/');
			if(p.startsWith("/")) p = p.substring(1);
			os.writeUTF(p);
			
			InputStream stream = new FileInputStream(f);
			byte[] targetArray = new byte[stream.available()];
			stream.read(targetArray);
			stream.close();
			
			os.write(md.digest(targetArray));
			os.writeInt(targetArray.length);
			os.write(targetArray);
			os.writeUTF("</file>");
		}
		os.writeUTF(" end");
		os.flush();
		os.close();
		FileOutputStream out = new FileOutputStream(new File("out.epk"));
		out.write(osb.toByteArray());
		out.close();
	}
	
	public static void listDirectory(File dir) {
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) {
				listDirectory(f);
			}else {
				files.add(f);
			}
		}
	}
	
}
