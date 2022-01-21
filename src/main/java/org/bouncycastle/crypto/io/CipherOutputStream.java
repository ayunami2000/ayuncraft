package org.bouncycastle.crypto.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.StreamCipher;

public class CipherOutputStream extends FilterOutputStream
{
    private BufferedBlockCipher theBufferedBlockCipher;
    private StreamCipher theStreamCipher;
    private byte[] oneByte = new byte[1];
    private byte[] buf;

    public CipherOutputStream(OutputStream par1OutputStream, BufferedBlockCipher par2BufferedBlockCipher)
    {
        super(par1OutputStream);
        this.theBufferedBlockCipher = par2BufferedBlockCipher;
        this.buf = new byte[par2BufferedBlockCipher.getBlockSize()];
    }

    public void write(int par1) throws IOException
    {
        this.oneByte[0] = (byte)par1;

        if (this.theBufferedBlockCipher != null)
        {
            int var2 = this.theBufferedBlockCipher.processByte(this.oneByte, 0, 1, this.buf, 0);

            if (var2 != 0)
            {
                this.out.write(this.buf, 0, var2);
            }
        }
        else
        {
            this.out.write(this.theStreamCipher.returnByte((byte)par1));
        }
    }

    public void write(byte[] par1) throws IOException
    {
        this.write(par1, 0, par1.length);
    }

    public void write(byte[] par1, int par2, int par3) throws IOException
    {
        byte[] var4;

        if (this.theBufferedBlockCipher != null)
        {
            var4 = new byte[this.theBufferedBlockCipher.getOutputSize(par3)];
            int var5 = this.theBufferedBlockCipher.processByte(par1, par2, par3, var4, 0);

            if (var5 != 0)
            {
                this.out.write(var4, 0, var5);
            }
        }
        else
        {
            var4 = new byte[par3];
            this.theStreamCipher.processBytes(par1, par2, par3, var4, 0);
            this.out.write(var4, 0, par3);
        }
    }

    public void flush() throws IOException
    {
        super.flush();
    }

    public void close() throws IOException
    {
        try
        {
            if (this.theBufferedBlockCipher != null)
            {
                byte[] var1 = new byte[this.theBufferedBlockCipher.getOutputSize(0)];
                int var2 = this.theBufferedBlockCipher.doFinal(var1, 0);

                if (var2 != 0)
                {
                    this.out.write(var1, 0, var2);
                }
            }
        }
        catch (Exception var3)
        {
            throw new IOException("Error closing stream: " + var3.toString());
        }

        this.flush();
        super.close();
    }
}
