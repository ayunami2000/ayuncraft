package net.md_5.bungee.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ServerIcon {
	
	public static int[] createServerIcon(BufferedImage awtIcon) {
		if(awtIcon.getWidth() != 64 || awtIcon.getHeight() != 64) {
			throw new IllegalArgumentException("Image must be 64x64 (was " + awtIcon.getWidth() + "x" + awtIcon.getHeight() + ")");
		}
		return awtIcon.getRGB(0, 0, 64, 64, new int[4096], 0, 64);
	}
	
	public static int[] createServerIcon(InputStream f) {
		try {
			return createServerIcon(ImageIO.read(f));
		}catch(Throwable t) {
			return null;
		}
	}
	
	public static int[] createServerIcon(File f) {
		try {
			return createServerIcon(ImageIO.read(f));
		}catch(Throwable t) {
			return null;
		}
	}
	
}
