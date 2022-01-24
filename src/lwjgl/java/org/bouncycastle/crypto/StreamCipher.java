package org.bouncycastle.crypto;

public interface StreamCipher
{
    /**
     * Encrypt/decrypt a single byte, returning the result.
     */
    byte returnByte(byte var1);

    void processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException;
}
