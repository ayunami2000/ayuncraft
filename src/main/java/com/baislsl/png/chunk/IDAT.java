package com.baislsl.png.chunk;

/**
 * Created by baislsl on 17-7-10.
 */
public class IDAT extends Chunk {

    public IDAT(byte[] length, byte[] type, byte[] data, byte[] crc) {
        super(length, type, data, crc);
    }
}
