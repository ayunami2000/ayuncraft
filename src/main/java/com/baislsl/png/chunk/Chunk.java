package com.baislsl.png.chunk;

import com.baislsl.png.util.ByteHandler;

/**
 * Created by baislsl on 17-7-9.
 */
public class Chunk {
    protected long length;
    protected ChunkType type;
    protected byte[] data;
    protected byte[] crc = new byte[4];

    public byte[] dump() {
        byte[] output = new byte[4 + 4 + data.length + 4];
        Byte[] lengthBytes = new Byte[4];
        for (int i = 0; i < 4; i++) {
            output[3 - i] = (byte) (length & 0xff);
        }
        String typeName = type.name().toUpperCase();
        for (int i = 0; i < 4; i++) {
            output[4 + i] = (byte) typeName.charAt(i);
        }
        System.arraycopy(data, 0, output, 8, data.length);
        System.arraycopy(crc, 0, output, output.length - crc.length, crc.length);
        return output;
    }


    protected Chunk(byte[] length, byte[] type, byte[] data, byte[] crc) {
        this.length = ByteHandler.byteToLong(length);
        this.data = data;
        this.crc = crc;

        for (ChunkType chunkType : ChunkType.values()) {
            if (chunkType.name().equals(ByteHandler.byteToString(type))) {
                this.type = chunkType;
                break;
            }
        }

    }

    public long dataLength() {
        return data.length;
    }

    public long getLength() {
        return length;
    }

    public ChunkType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getCrc() {
        return crc;
    }
}
