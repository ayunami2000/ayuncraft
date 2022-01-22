package me.ayunami2000.ayuncraft;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ParseRSAPublicKey {
    private static final int SEQUENCE = 0x30;
    private static final int INTEGER = 0x02;
    private final ByteBuffer derBuf;

    public ParseRSAPublicKey(byte[] der) {
        derBuf = ByteBuffer.wrap(der);
    }

    public byte get() {
        return derBuf.get();
    }

    /**
     * @return the next byte of the buffer as an int
     */
    public int getAsInt() {
        return get() & 0xff;
    }

    public byte[] getArray(int len) {
        byte [] arr = new byte[len];
        derBuf.get(arr);
        return arr;
    }

    public int parseId() {
        // Only the low-tag form is legal.
        int idOctect = getAsInt();
        if (idOctect >= 0x31) {
            throw new RuntimeException("Invalid identifier octets");
        }
        return idOctect;
    }

    public long parseLength() {
        int octet1 = getAsInt();
        if (octet1 < 128) {
            // short form of length
            return octet1;
        } else {
            // long form of length
            int lengthOfLength = octet1 & 0x7f;
            BigInteger bigLen = new BigInteger(1, getArray(lengthOfLength));

            if (bigLen.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0){
                throw new RuntimeException("Length is too long");
            }
            return bigLen.longValue();
        }
    }

    public BigInteger parseInteger() {
        if (parseId() != INTEGER) {
            throw new RuntimeException("expected SEQUENCE tag");
        }

        long length = parseLength();
        if (length > Integer.MAX_VALUE){
            throw new RuntimeException("Length is too long");
        }
        return new BigInteger(1, getArray((int) length));
    }
    public BigInteger[] parse() {
        // Parse SEQUENCE header
        if (parseId() != SEQUENCE) {
            throw new RuntimeException("expected SEQUENCE tag");
        }

        @SuppressWarnings("unused")
        long seqLength = parseLength(); // We ignore this

        // Parse INTEGER modulus
        BigInteger n = parseInteger();
        BigInteger e = parseInteger();
        return new BigInteger[] {n, e};

    }
}