package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithIV implements CipherParameters
{
    private byte[] iv;
    private CipherParameters parameters;

    public ParametersWithIV(CipherParameters par1CipherParameters, byte[] par2ArrayOfByte, int par3, int par4)
    {
        this.iv = new byte[par4];
        this.parameters = par1CipherParameters;
        System.arraycopy(par2ArrayOfByte, par3, this.iv, 0, par4);
    }

    public byte[] getIV()
    {
        return this.iv;
    }

    public CipherParameters getParameters()
    {
        return this.parameters;
    }
}
