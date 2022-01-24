package com.rhg.rsa;

import java.math.BigInteger;
import java.io.*;
import java.util.Arrays;

/**
 * Class representing a public RSA key.
 * 
 * @author Rob 
 * @version 05/30/2010
 */
public class RSAPublicKey extends RSAKey
{
    /** The public exponent. */
    private BigInteger e;
    
    /** Default constructor. */
    public RSAPublicKey() {
        super(null);
        setPubExp(null);
        return;
    }

    /** Main constructor. */
    public RSAPublicKey(BigInteger modulus, BigInteger pubExp)
    {
        super(modulus);
        setPubExp(pubExp);
        return;
    }
    
    /** Performs the classical RSA computation. */
    protected BigInteger encrypt(BigInteger m) {
        return m.modPow(getPubExp(), getModulus());
    }
    
    /** Returns the public exponent. */
    public BigInteger getPubExp() {
        return e;
    }

    /** Sets the public exponent. */
    public void setPubExp(BigInteger pubExp)
    {
        e = weedOut(pubExp);
        return;
    }
    
    /** Uses the key and returns true if encryption was successful. */
    /*
    public boolean use(String source, String destination) {
        byte[] sourceBytes = getBytes(source);
        if (isNull(sourceBytes)) {
            System.err.println(String.format("%s contained nothing.", source));
            return false;
        }

        int k = getModulusByteSize();
        byte BT = 0x02;
        byte[] C, M;
        byte[][] D = reshape(sourceBytes, k - 11);
        ByteArrayOutputStream EB = new ByteArrayOutputStream(k);
        FileOutputStream out = null;
        BigInteger m, c;

        try {
            out = new FileOutputStream(destination);
            for (int i = 0; i < D.length; i++) {
                EB.reset();
                EB.write(0x00); EB.write(BT); EB.write(makePaddingString(k - D[i].length - 3));
                EB.write(0x00); EB.write(D[i]);
                M = EB.toByteArray();
                m = new BigInteger(M);
                c = encrypt(m);
                C = toByteArray(c, k);
                out.write(C);
            }
            out.close();
        } catch (Exception e) {
            String errMsg = "An exception occured!%n%s%n%s%n%s";
            System.err.println(String.format(errMsg, e.getClass(), e.getMessage(), e.getStackTrace()));
            return false;
        }

        return true;
    }
    */

    public boolean use(byte[] sourceBytes, ByteArrayOutputStream out) {
        System.out.println(1);
        int k = getModulusByteSize();
        System.out.println(2);
        byte BT = 0x02;
        byte[] C, M;
        byte[][] D = reshape(sourceBytes, k - 11);
        System.out.println(3);
        ByteArrayOutputStream EB = new ByteArrayOutputStream(k);
        System.out.println(4);
        BigInteger m, c;

        try {
            for (int i = 0; i < D.length; i++) {
                System.out.println(5);
                EB.reset();
                System.out.println(6);
                EB.write(0x00); EB.write(BT); EB.write(makePaddingString(k - D[i].length - 3));
                System.out.println(7);
                EB.write(0x00); EB.write(D[i]);
                System.out.println(8);
                M = EB.toByteArray();
                System.out.println(9);
                m = new BigInteger(M);
                System.out.println(10);
                c = encrypt(m);
                System.out.println(11);
                C = toByteArray(c, k);
                System.out.println(12);
                out.write(C);
                System.out.println("FARD: "+Arrays.toString(C));
            }
            out.flush();
            //out.close();
        } catch (Exception e) {
            /*
            String errMsg = "An exception occured!%n%s%n%s%n%s";
            System.err.println(String.format(errMsg, e.getClass(), e.getMessage(), Arrays.toString(e.getStackTrace())));
            */
            System.err.println(e.getClass().getName()+"\n"+e.getMessage());
            return false;
        }

        return true;
    }
}
