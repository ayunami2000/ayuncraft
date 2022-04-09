package com.baislsl.png.decode;

import com.baislsl.png.chunk.IDAT;

import java.util.ArrayList;

public class IDATManager extends ArrayList<IDAT> {

	public byte[] getIDATData() {
		int dataSize = 0;
		for (IDAT idat : this) {
			dataSize += idat.dataLength();
		}
		byte[] data = new byte[dataSize];
		int curPos = 0;
		for (IDAT idat : this) {
			System.arraycopy(idat.getData(), 0, data, curPos, (int) idat.dataLength());
			curPos += idat.dataLength();
		}
		return data;
	}

}
