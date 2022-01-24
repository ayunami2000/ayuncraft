package me.ayunami2000.ayuncraft;

import java.io.*;
import java.math.BigInteger;

import me.ayunami2000.ayuncraft.java.security.Key;

import net.lax1dude.eaglercraft.Base64;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import me.ayunami2000.ayuncraft.javax.crypto.SecretKey;
import me.ayunami2000.ayuncraft.javax.crypto.spec.SecretKeySpec;
import org.teavm.jso.JSBody;
import org.teavm.jso.typedarrays.Uint8Array;

public class CryptManager
{
    //i hate security !!
    /**
     * Generate a new shared secret AES key from a static preset key
     */
    private static final byte[] baseSharedKey = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    public static SecretKey createNewSharedKey(){
        return new SecretKeySpec(baseSharedKey,"AES");
    }

    @JSBody(params = {"base64enc"}, script =
            "var xd=window.ASN1.parse(window.PEM.parseBlock(base64enc).der);" +
            "xd=xd.children.find(e=>{return e.type==3;});" +
            "xd=xd.children.find(e=>{return e.type==48;});" +
            "return [xd.children[0].value,xd.children[1].value];")
    private static native Uint8Array[] x509decode(String base64enc);

    /**
     * Create a new PublicKey from encoded X.509 data
     */
    public static PubKey decodePublicKey(byte[] par0ArrayOfByte)
    {
        Uint8Array[] resarr=x509decode(Base64.encodeBase64String(par0ArrayOfByte));
        PubKey results = new PubKey(par0ArrayOfByte,new BigInteger(uInt8toByteArr(resarr[0])),new BigInteger(uInt8toByteArr(resarr[1])));
        return results;
    }

    private static byte[] uInt8toByteArr(Uint8Array a){
        byte[] b = new byte[a.getByteLength()];
        for(int i = 0; i < b.length; ++i) {
            b[i] = (byte) (a.get(i) & 0xFF);
        }
        return b;
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     */
    public static SecretKey decryptSharedKey(PubKey par0PrivateKey, byte[] par1ArrayOfByte)
    {
        return new SecretKeySpec(decryptData(par0PrivateKey, par1ArrayOfByte), "AES");
    }

    ///*
    @JSBody(params = {"pubkey", "mod", "indata"}, script = "var rsa=new RSAKey();rsa.setPublic(b64tohex(mod),b64tohex(pubkey));var res=hex2b64(rsa.encrypt(atob(indata)));return res;")
    private static native String encryptDataNative(String pubkey, String mod, String indata);

    @JSBody(params = {"privkey", "mod", "indata"}, script = "var rsa=new RSAKey();rsa.setPrivate(b64tohex(mod),b64tohex(privkey));var res=rsa.decrypt(b64tohex(indata));return res;")
    private static native String decryptDataNative(String pubkey, String mod, String indata);
    //*/

    /**
     * Encrypt byte[] data with RSA public key
     */
    public static byte[] encryptData(PubKey par0Key, byte[] par1ArrayOfByte)
    {
        return Base64.decodeBase64(encryptDataNative(Base64.encodeBase64String(par0Key.getPriExp().toByteArray()),Base64.encodeBase64String(par0Key.getModulus().toByteArray()),Base64.encodeBase64String(par1ArrayOfByte)));
    }

    /**
     * Decrypt byte[] data with RSA private key
     */
    public static byte[] decryptData(PubKey par0Key, byte[] par1ArrayOfByte)
    {
        return Base64.decodeBase64(decryptDataNative(Base64.encodeBase64String(par0Key.getPriExp().toByteArray()),Base64.encodeBase64String(par0Key.getModulus().toByteArray()),Base64.encodeBase64String(par1ArrayOfByte)));
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