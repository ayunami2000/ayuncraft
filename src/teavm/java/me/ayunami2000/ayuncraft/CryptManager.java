package me.ayunami2000.ayuncraft;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import me.ayunami2000.ayuncraft.java.security.Key;
import me.ayunami2000.ayuncraft.java.security.PrivateKey;
import me.ayunami2000.ayuncraft.java.security.PublicKey;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import me.ayunami2000.ayuncraft.javax.crypto.SecretKey;
import me.ayunami2000.ayuncraft.javax.crypto.spec.SecretKeySpec;

public class CryptManager
{
    //i hate security !!
    /**
     * Generate a new shared secret AES key from a static preset key
     */
    private static final byte[] baseSharedKey = new byte[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    public static SecretKey createNewSharedKey(){
        return new SecretKeySpec(baseSharedKey,"AES");
    }

    /**
     * Create a new PublicKey from encoded X.509 data
     */
    public static PublicKey decodePublicKey(byte[] par0ArrayOfByte)
    {
        return new ModifiablePublicKey("RSA","X.509",par0ArrayOfByte);
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     */
    public static SecretKey decryptSharedKey(PrivateKey par0PrivateKey, byte[] par1ArrayOfByte)
    {
        return new SecretKeySpec(decryptData(par0PrivateKey, par1ArrayOfByte), "AES");
    }

    private static RSA rsa=new RSA(2048);

    /**
     * Encrypt byte[] data with RSA public key
     */
    public static byte[] encryptData(Key par0Key, byte[] par1ArrayOfByte)
    {
        rsa.setPublicKey(new BigInteger(par0Key.getEncoded()));
        BigInteger res=rsa.encrypt(new BigInteger(par1ArrayOfByte));
        return res.toByteArray();
    }

    /**
     * Decrypt byte[] data with RSA private key
     */
    public static byte[] decryptData(Key par0Key, byte[] par1ArrayOfByte)
    {
        rsa.setPrivateKey(new BigInteger(par0Key.getEncoded()));
        BigInteger res=rsa.decrypt(new BigInteger(par1ArrayOfByte));
        return res.toByteArray();
    }

    /**
     * Create a new BufferedBlockCipher instance
     */
    public static BufferedBlockCipher createBufferedBlockCipher(boolean par0, Key par1Key)
    {
        BufferedBlockCipher var2 = new BufferedBlockCipher(new CFBBlockCipher(new AESFastEngine(), 8));
        var2.init(par0, new ParametersWithIV(new KeyParameter(par1Key.getEncoded()), par1Key.getEncoded(), 0, 16));
        return var2;
    }

    public static OutputStream encryptOuputStream(SecretKey par0SecretKey, OutputStream par1OutputStream)
    {
        return new CipherOutputStream(par1OutputStream, createBufferedBlockCipher(true, par0SecretKey));
    }

    public static InputStream decryptInputStream(SecretKey par0SecretKey, InputStream par1InputStream)
    {
        return new CipherInputStream(par1InputStream, createBufferedBlockCipher(false, par0SecretKey));
    }

    public static OutputStream encryptOuputStream(BufferedBlockCipher bufferedBlockCipher, OutputStream par1OutputStream)
    {
        return new CipherOutputStream(par1OutputStream, bufferedBlockCipher);
    }

    public static InputStream decryptInputStream(BufferedBlockCipher bufferedBlockCipher, InputStream par1InputStream)
    {
        return new CipherInputStream(par1InputStream, bufferedBlockCipher);
    }
}