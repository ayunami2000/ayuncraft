package com.baislsl.png.chunk;

import com.baislsl.png.decode.DecodeException;

/**
 * Created by baislsl on 17-7-9.
 */
public class PLTE extends Chunk {
	private int[] color;

	public PLTE(byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException {
		super(length, type, data, crc);
		build();
	}

	private void build() throws DecodeException {
		if (this.length % 3 != 0)
			throw new DecodeException("PLTE length can not be divide by 3");
		int size = (int) length / 3;
		color = new int[size];
		for (int i = 0; i < size; i++) {
			color[i] = (((int) data[i * 3]) & 0xFF) << 16 | (((int) data[i * 3 + 1]) & 0xFF) << 8
					| (((int) data[i * 3 + 2]) & 0xFF) | 0xFF000000;
		}
	}

	public int getColor(int i) {
		return color[i];
	}

	public int getPaletteSize() {
		return color.length;
	}

}
