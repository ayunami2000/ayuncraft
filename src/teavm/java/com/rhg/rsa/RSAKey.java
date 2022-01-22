package com.rhg.rsa;

import java.math.BigInteger;
import java.io.*;
import java.util.Random;

/**
 * Abstract class representing common features of RSA keys.
 * 
 * @author Rob
 * @version 06/01/2010
 */
public abstract class RSAKey implements RSAConstants, RSABaseInterface
{
    /** The modulus. */
    private BigInteger n;

    /** Default constructor. */
    protected RSAKey() {
        setModulus(null);
        return;
    }
    
    /** Main constructor. */
    protected RSAKey(BigInteger modulus) {
        setModulus(modulus);
        return;
    }
    
    /** Returns the contents of a file as a byte array. */
    /*
    protected byte[] getBytes(String fileName) {
        File fIn = new File(fileName);
        if (!fIn.canRead()) {
        	System.err.println("Can't read " + fileName);
            return null;
        }
        
        FileInputStream in = null;
        byte[] bytes = null;
        try {
            in = new FileInputStream(fIn);

            long fileSize = fIn.length();
            if (fileSize > Integer.MAX_VALUE) {
                System.out.println("Sorry, file was too large!");
            }
    
            bytes = new byte[(int) fileSize];
    
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
            }
        }

        return bytes;
    }
    */
    
    /** Returns the modulus. */
    public BigInteger getModulus() {
        return n;
    }
    
    /** Returns the number of bytes required to store the modulus. */
    protected int getModulusByteSize() {
        return (int) Math.ceil(getModulus().bitLength() / 8.0);
    }
    
    /** Returns a portion of the array argument. */
    protected byte[] getSubArray(byte[] inBytes, int start, int end) {
        if (start >= inBytes.length) {
            return null;
        }
        if (end > inBytes.length) {
            end = inBytes.length;
        }
        int bytesToGet = end - start;
        if (bytesToGet < 1) {
            return null;
        }
        
        byte[] outBytes = new byte[bytesToGet];
        for (int i = start; i < end; i++) {
            outBytes[i - start] = inBytes[i];
        }

        return outBytes;
    }
    
    /** Returns true when the argument is null. */
    public final boolean isNull(Object obj) {
        return !(obj != null);
    }
    
    /** Returns true when the argument is greater than zero. */
    public final boolean isPositive(BigInteger number) {
        return (number.compareTo(ZERO) > 0);
    }
    
    /** Returns true when the argument is prime. */
    public boolean isPrime(BigInteger number) {
        return number.isProbablePrime(100);
    }
    
    /** Computes the least common multiple. */
    public BigInteger lcm(BigInteger a, BigInteger b) {
        return (a.multiply(b).divide(a.gcd(b)));
    }
            
    /** Generates an array of pseudo-random nonzero bytes. */
    protected byte[] makePaddingString(int len) {
        if (len < 8) return null;
        Random rndm = new Random();
        
        byte[] PS = new byte[len];
        for (int i = 0; i < len; i++) {
            PS[i] = (byte)(rndm.nextInt(255) + 1);
        }
        
        return PS;
    }
    
    /** Reshapes a byte array into an array of byte arrays. */
    protected byte[][] reshape(byte[] inBytes, int colSize) {
        if (colSize < 1) {
            colSize = 1;
        }
        
        int rowSize = (int) Math.ceil((double)inBytes.length / (double)colSize);
        
        if (rowSize == 0) {
            return null;
        }
        
        byte[][] outBytes = new byte[rowSize][];
        
        for (int i = 0; i < rowSize; i++) {
            outBytes[i] = getSubArray(inBytes, i * colSize, (i + 1) * colSize);
        }
        return outBytes;
    }
    
    /** Sets the modulus. */
    public void setModulus(BigInteger modulus) {
        n = weedOut(modulus);
        if (isNull(n) || n.bitLength() < 96) {
            n = null;
        }
        return;
    }
    
    /** Converts a BigInteger into a byte array of the specified length. */
    protected byte[] toByteArray(BigInteger x, int numBytes) {
        if (x.compareTo(TWO_FIFTY_SIX.pow(numBytes)) >= 0) {
            return null;    // number is to big to fit in the byte array
        }
        
        byte[] ba = new byte[numBytes--];
        BigInteger[] divAndRem = new BigInteger[2];
        
        for (int power = numBytes; power >= 0; power--) {
            divAndRem = x.divideAndRemainder(TWO_FIFTY_SIX.pow(power));
            ba[numBytes - power] = (byte) divAndRem[0].intValue();
            x = divAndRem[1];
        }
        
        return ba;
    }
    
    /** Converts a byte array into a BigInteger. */
    protected BigInteger toInteger(byte[] X) {
        BigInteger x = ZERO;
        
        for (int i = 0; i < X.length; i++) {
            x = x.add(BigInteger.valueOf(X[i]).multiply(TWO_FIFTY_SIX.pow(X.length - 1 - i)));
        }
        
        return x;
    }
    
    /** Uses the key and returns true if use was successful. */
    //public abstract boolean use(String source, String destination);
    public abstract boolean use(byte[] sourceBytes, ByteArrayOutputStream baos);
    
    /** Weeds out bad inputs. */
    public final BigInteger weedOut(BigInteger arg) {
        if (!isNull(arg) && isPositive(arg)) {
            return arg;
        } else {
            return null;
        }
    }
}
