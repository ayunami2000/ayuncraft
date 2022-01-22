package com.rhg.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
/**
 * Class representing private and public keys with the ability to generate new keys.
 * 
 * @author Rob 
 * @version 05/31/2010
 */
public class RSAKeyGenerator extends RSACompleteKey 
{
    /** Minimum number of bits in the modulus allowed. */
    private static final int MIN_BIT_LENGTH = 1024;
    
    /** Source of securely (pseudo-) random bits. */
    SecureRandom rand = new SecureRandom();

    /** The number of bits required in the modulus. */
    private int bitLength;
    
    /** Default constructor. */
    public RSAKeyGenerator() {
        super();
        setBitLength(MIN_BIT_LENGTH);
        generateNewKeys();
        return;
    }
    
    /** Main constructor. */
    public RSAKeyGenerator(int bitLength) {
        super();
        setBitLength(bitLength);
        generateNewKeys();
        return;
    }
    
    /** Generates new private and public keys. */
    public void generateNewKeys() {
        setPrimes(BigInteger.probablePrime(75 * bitLength / 100, rand),
                  BigInteger.probablePrime(25 * bitLength / 100, rand));
		setModulus(getPrimeOne().multiply(getPrimeTwo()));
		computePhi();
		
		BigInteger i;
		for (i = BigInteger.probablePrime((bitLength / 10), rand);
			    i.compareTo(getModulus()) < 0;
			    i = i.nextProbablePrime()) {
			if (i.gcd(getPhi()).equals(ONE)) {
				setPubExp(i);
				break;
			}
		}

		setPriExp(getPubExp().modInverse(getPhi()));
		setCRTExpOne(getPubExp().modInverse(pMinusOne()));
		setCRTExpTwo(getPubExp().modInverse(qMinusOne()));
		setCRTCoeff(getPrimeTwo().modInverse(getPrimeOne()));

		return;
    }
    
    /** Makes a new RSA key. */
    public RSAKey makeKey(byte whichKey) {
        switch (whichKey) {
            case PUBLIC_KEY:
                return makePublicKey();
            case PRIVATE_KEY:
                return makePrivateKey();
            case COMPLETE_KEY:
                return makeCompleteKey();
            default:
                return null;
        }
    }
    
    /** Makes complete RSA key objects. */
    private RSACompleteKey makeCompleteKey() {
        return new RSACompleteKey(getPrimeOne(), 
                                  getPrimeTwo(), 
                                  getModulus(), 
                                  getPubExp(),
                                  getPriExp(),
                                  getCRTExpOne(),
                                  getCRTExpTwo(),
                                  getCRTCoeff());
    }
    
    /** Makes private RSA key objects. */
    private RSAPrivateKey makePrivateKey() {
        return new RSAPrivateKey(getModulus(), getPriExp());
    }
    
    /** Makes public RSA key objects. */
    private RSAPublicKey makePublicKey() {
        return new RSAPublicKey(getModulus(), getPubExp());
    }
    
    /** Sets the number of bits in the modulus. */
    public void setBitLength(int bitLength) {
        this.bitLength = (bitLength >= MIN_BIT_LENGTH ? bitLength : MIN_BIT_LENGTH);
        return;
    }
}
