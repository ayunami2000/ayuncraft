package com.baislsl.png.chunk;

/**
 * Created by baislsl on 17-7-9.
 */
public class IEND extends Chunk {
    public IEND(byte[] length, byte[] type, byte[] data, byte[] crc) {
        super(length, type, data, crc);
    }
}
