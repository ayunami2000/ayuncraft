package com.baislsl.png.chunk;

import com.baislsl.png.decode.DecodeException;
import com.baislsl.png.decode.PNG;

/**
 * Created by baislsl on 17-7-9.
 */
public enum ChunkType {

    IHDR {
        @Override
        public void apply(PNG png, byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException {
            png.setIhdr(new IHDR(length, type, data, crc));
        }
    },
    PLTE {
        @Override
        public void apply(PNG png, byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException {
            png.setPlte(new PLTE(length, type, data, crc));
        }
    },
    IDAT {
        @Override
        public void apply(PNG png, byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException {
            png.add(new IDAT(length, type, data, crc));
        }
    },
    IEND {
        @Override
        public void apply(PNG png, byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException {
            png.setIend(new IEND(length, type, data, crc));
        }
    };

    public abstract void apply(PNG png, byte[] length, byte[] type, byte[] data, byte[] crc) throws DecodeException;

}
