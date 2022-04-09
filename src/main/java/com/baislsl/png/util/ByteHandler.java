package com.baislsl.png.util;

/**
 * Created by baislsl on 17-7-10.
 */
public class ByteHandler {

	public static long byteToLong(byte[] data, int offset, int size) {
		long result = 0;
		for (int i = 0; i < size; i++) {
			result <<= 8;
			result |= ((long) data[offset + i] & 0xff);
		}
		return result;
	}

	public static long byteToLong(byte[] data, int offset) {
		return byteToLong(data, offset, 4);
	}

	public static long byteToLong(byte[] data) {
		return byteToLong(data, 0, 4);
	}

	public static String byteToString(byte[] data) {
		StringBuilder str = new StringBuilder();
		for (byte b : data) {
			str.append((char) (0x0ff & b));
		}
		return str.toString();
	}
}
