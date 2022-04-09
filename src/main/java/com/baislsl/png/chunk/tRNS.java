package com.baislsl.png.chunk;

import com.baislsl.png.decode.DecodeException;

/**
 * Created by baislsl on 17-7-9.
 */
public class tRNS extends Chunk {

	public tRNS(byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException {
		super(length, type, data, crc);
	}

	public int getAlpha() {
		return (int)data[0] & 0xFF;
	}

}
