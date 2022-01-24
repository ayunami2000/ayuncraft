package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class KeyParameter implements CipherParameters
{
    private byte[] key;

    public KeyParameter(byte[] par1ArrayOfByte)
    {
        this(par1ArrayOfByte, 0, par1ArrayOfByte.length);
    }

    public KeyParameter(byte[] par1ArrayOfByte, int par2, int par3)
    {
        this.key = new byte[par3];
        System.arraycopy(par1ArrayOfByte, par2, this.key, 0, par3);
    }

    public byte[] getKey()
    {
        return this.key;
    }
}
