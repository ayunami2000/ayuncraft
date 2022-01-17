package net.lax1dude.eaglercraft;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.baislsl.png.decode.DecodeException;
import com.baislsl.png.decode.Decoder;
import com.baislsl.png.decode.PNG;

public class EaglerImage {
	
	public final int[] data;
	public final int w;
	public final int h;
	public final boolean alpha;

	public EaglerImage(int pw, int ph, boolean palpha) {
		this.w = pw;
		this.h = ph;
		this.alpha = palpha;
		this.data = new int[pw * ph];
	}
	
	public EaglerImage(int[] pdata, int pw, int ph, boolean palpha) {
		if(pdata.length != pw*ph) {
			throw new IllegalArgumentException("array size does not equal image size");
		}
		this.w = pw;
		this.h = ph;
		this.alpha = palpha;
		if(!palpha) {
			for(int i = 0; i < pdata.length; ++i) {
				pdata[i] = pdata[i] | 0xFF000000;
			}
		}
		this.data = pdata;
	}
	
	public static final EaglerImage loadImage(byte[] file) {
		try {
			PNG p = (new Decoder(new ByteArrayInputStream(file))).readInPNG();
			return new EaglerImage(p.getColor(), (int)p.getWidth(), (int)p.getHeight(), p.ihdr.getBpp() == 4);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (DecodeException e) {
			e.printStackTrace();
			return null;
		}
	}

	public EaglerImage getSubImage(int x, int y, int pw, int ph) {
		int[] img = new int[pw * ph];
		for(int i = 0; i < ph; ++i) {
			System.arraycopy(data, (i + y) * this.w + x, img, i * pw, pw);
		}
		return new EaglerImage(img, pw, ph, alpha);
	}

}
