package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class CFBBlockCipher implements BlockCipher
{
    private byte[] IV;
    private byte[] cfbV;
    private byte[] cfbOutV;
    private int blockSize;
    private BlockCipher cipher = null;
    private boolean encrypting;

    public CFBBlockCipher(BlockCipher par1BlockCipher, int par2)
    {
        this.cipher = par1BlockCipher;
        this.blockSize = par2 / 8;
        this.IV = new byte[par1BlockCipher.getBlockSize()];
        this.cfbV = new byte[par1BlockCipher.getBlockSize()];
        this.cfbOutV = new byte[par1BlockCipher.getBlockSize()];
    }

    public void init(boolean par1, CipherParameters par2CipherParameters) throws IllegalArgumentException
    {
        this.encrypting = par1;

        if (par2CipherParameters instanceof ParametersWithIV)
        {
            ParametersWithIV var3 = (ParametersWithIV)par2CipherParameters;
            byte[] var4 = var3.getIV();

            if (var4.length < this.IV.length)
            {
                System.arraycopy(var4, 0, this.IV, this.IV.length - var4.length, var4.length);

                for (int var5 = 0; var5 < this.IV.length - var4.length; ++var5)
                {
                    this.IV[var5] = 0;
                }
            }
            else
            {
                System.arraycopy(var4, 0, this.IV, 0, this.IV.length);
            }

            this.reset();

            if (var3.getParameters() != null)
            {
                this.cipher.init(true, var3.getParameters());
            }
        }
        else
        {
            this.reset();
            this.cipher.init(true, par2CipherParameters);
        }
    }

    /**
     * Return the name of the algorithm the cipher implements.
     */
    public String getAlgorithmName()
    {
        return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
    }

    /**
     * Return the block size for this cipher (in bytes).
     */
    public int getBlockSize()
    {
        return this.blockSize;
    }

    public int processBlock(byte[] par1ArrayOfByte, int par2, byte[] par3ArrayOfByte, int par4) throws DataLengthException, IllegalStateException
    {
        return this.encrypting ? this.encryptBlock(par1ArrayOfByte, par2, par3ArrayOfByte, par4) : this.decryptBlock(par1ArrayOfByte, par2, par3ArrayOfByte, par4);
    }

    public int encryptBlock(byte[] par1ArrayOfByte, int par2, byte[] par3ArrayOfByte, int par4) throws DataLengthException, IllegalStateException
    {
        if (par2 + this.blockSize > par1ArrayOfByte.length)
        {
            throw new DataLengthException("input buffer too short");
        }
        else if (par4 + this.blockSize > par3ArrayOfByte.length)
        {
            throw new DataLengthException("output buffer too short");
        }
        else
        {
            this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);

            for (int var5 = 0; var5 < this.blockSize; ++var5)
            {
                par3ArrayOfByte[par4 + var5] = (byte)(this.cfbOutV[var5] ^ par1ArrayOfByte[par2 + var5]);
            }

            System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
            System.arraycopy(par3ArrayOfByte, par4, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
            return this.blockSize;
        }
    }

    public int decryptBlock(byte[] par1ArrayOfByte, int par2, byte[] par3ArrayOfByte, int par4) throws DataLengthException, IllegalStateException
    {
        if (par2 + this.blockSize > par1ArrayOfByte.length)
        {
            throw new DataLengthException("input buffer too short");
        }
        else if (par4 + this.blockSize > par3ArrayOfByte.length)
        {
            throw new DataLengthException("output buffer too short");
        }
        else
        {
            this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
            System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
            System.arraycopy(par1ArrayOfByte, par2, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);

            for (int var5 = 0; var5 < this.blockSize; ++var5)
            {
                par3ArrayOfByte[par4 + var5] = (byte)(this.cfbOutV[var5] ^ par1ArrayOfByte[par2 + var5]);
            }

            return this.blockSize;
        }
    }

    public void reset()
    {
        System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
        this.cipher.reset();
    }
}
