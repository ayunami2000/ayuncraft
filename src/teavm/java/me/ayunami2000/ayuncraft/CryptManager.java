package me.ayunami2000.ayuncraft;

import java.io.InputStream;
import java.io.OutputStream;

import me.ayunami2000.ayuncraft.java.security.Key;
import me.ayunami2000.ayuncraft.java.security.PrivateKey;
import me.ayunami2000.ayuncraft.java.security.PublicKey;

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

    /*
    @JSBody(params = {"base64enc"}, script = "window.servercertraw=base64enc;window.servercert=new x509.X509Certificate(base64enc);")
    private static native void setupDecode(String base64enc);

    @JSBody(params = {}, script = "return window.servercert.publickey.algorithm.name;")
    private static native String decodeAlgorithm();

    @JSBody(params = {}, script = "return window.servercert.publickey.algorithm.publicExponent;")
    private static native Uint8Array decodePublicExponent();

    @JSBody(params = {}, script = "return window.servercert.publickey.algorithm.modulusLength;")
    private static native int decodeModulusLength();

    @JSBody(params = {}, script = "return new Uint8Array(window.servercert.publickey.rawData);")
    private static native Uint8Array decodeData();
    */

    /**
     * Create a new PublicKey from encoded X.509 data
     */
    public static PublicKey decodePublicKey(byte[] par0ArrayOfByte)
    {
        /*
        setupDecode(Base64.encodeBase64String(par0ArrayOfByte));
        Uint8Array a = decodeData();
        byte[] b = new byte[a.getByteLength()];
        for(int i = 0; i < b.length; ++i) {
            b[i] = (byte) (a.get(i) & 0xFF);
        }
        */
        return new ModifiablePublicKey("RSA","X.509",par0ArrayOfByte);
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     */
    public static SecretKey decryptSharedKey(PrivateKey par0PrivateKey, byte[] par1ArrayOfByte)
    {
        return new SecretKeySpec(decryptData(par0PrivateKey, par1ArrayOfByte), "AES");
    }

    /*
    private static RSA rsa=new RSA(1024);

    static {
        rsa.setModulus(BigInteger.valueOf(16));
    }
    */
    //private static final BigInteger modu = new BigInteger("1000000000000000000000000000000");

    @JSBody(params = {"pubkey", "indata"}, script = "var enc=new JSEncrypt();enc.setPublicKey(pubkey);var res=enc.encrypt(indata);return res;")
    private static native String encryptDataNative(String pubkey, String indata);

    @JSBody(params = {"privkey", "indata"}, script = "var dec=new JSEncrypt();dec.setPrivateKey(privkey);var res=dec.decrypt(indata);return res;")
    private static native String decryptDataNative(String privkey, String indata);

    /**
     * Encrypt byte[] data with RSA public key
     */
    public static byte[] encryptData(Key par0Key, byte[] par1ArrayOfByte)
    {
        return Base64.decodeBase64(encryptDataNative("-----BEGIN PUBLIC KEY-----\n"+Base64.encodeBase64String(par0Key.getEncoded()).replaceAll("(.{64})", "$1\n")+"\n-----END PUBLIC KEY-----",Base64.encodeBase64String(par1ArrayOfByte)));
        /*
        try {
            //System.out.println(Arrays.toString(par0Key.getEncoded()));
            //System.out.println(Arrays.toString(par1ArrayOfByte));
            RSAPublicKey rsaPublicKey = new RSAPublicKey(modu, new BigInteger(par0Key.getEncoded()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rsaPublicKey.use(par1ArrayOfByte, baos);
            return baos.toByteArray();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + "\n" + e.getMessage());
            //e.printStackTrace();
            return null;
        }
        */
        /*
        try {
            rsa.setPublicKey(new BigInteger(par0Key.getEncoded()));
            BigInteger res = rsa.encrypt(new BigInteger(par1ArrayOfByte));
            return res.toByteArray();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + "\n" + e.getMessage());
            //e.printStackTrace();
            return null;
        }
        */
    }

    /**
     * Decrypt byte[] data with RSA private key
     */
    public static byte[] decryptData(Key par0Key, byte[] par1ArrayOfByte)
    {
        return Base64.decodeBase64(decryptDataNative("-----BEGIN RSA PRIVATE KEY-----\n"+Base64.encodeBase64String(par0Key.getEncoded()).replaceAll("(.{64})", "$1\n")+"\n-----END RSA PRIVATE KEY-----",Base64.encodeBase64String(par1ArrayOfByte)));
        /*
        try{
            RSAPrivateKey rsaPrivateKey = new RSAPrivateKey(modu,new BigInteger(par0Key.getEncoded()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rsaPrivateKey.use(par1ArrayOfByte,baos);
            return baos.toByteArray();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + "\n" + e.getMessage());
            return null;
        }
        */
        /*
        try {
            rsa.setPrivateKey(new BigInteger(par0Key.getEncoded()));
            BigInteger res = rsa.decrypt(new BigInteger(par1ArrayOfByte));
            return res.toByteArray();
        }catch(Exception e){
            System.err.println(e.getClass().getName() + "\n" + e.getMessage());
            //e.printStackTrace();
            return null;
        }
        */
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