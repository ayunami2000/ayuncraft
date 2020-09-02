package com.baislsl.png.util;

/**
 * Created by baislsl on 17-7-9.
 */
public class CRC {
    private final static long[] crcTable = new long[256];

    static {
        for (int i = 0; i < 256; i++) {
            long c = i;
            for (int k = 0; k < 8; k++) {
                if ((c & 1) != 0) {
                    c = 0xedb88320L ^ (c >> 1);
                } else {
                    c >>= 1;
                }
            }
            crcTable[i] = c;
        }
    }


    private static long updateCrc(long crc, byte[] buf, int size){
        long ans = crc;
        for(int i=0;i<size;i++){
            ans = crcTable[(int)((ans^buf[i])&0xff)] ^ (ans >> 8);
        }
        return ans;
    }

    public static long crc(byte[] buf, int size){
        return updateCrc(0xffffffffL, buf, size) ^ 0xffffffffL;
    }

}
