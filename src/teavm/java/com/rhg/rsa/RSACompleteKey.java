package com.rhg.rsa;

import java.math.BigInteger;
/**
 * Class representing full private and public RSA keys.
 * 
 * @author Rob 
 * @version 05/31/2010
 */
public class RSACompleteKey extends RSAPrivateKey 
{
    /** The first CRT exponent. */
    private BigInteger dP;
    
    /** The second CRT exponent. */
    private BigInteger dQ;
    
    /** The public exponent. */
    private BigInteger e;
    
    /** The larger prime factor of the modulus. */
    private BigInteger p;
    
    /** The LCM of the primes. */
    private BigInteger phi;
    
    /** The smaller prime factor of the modulus. */
    private BigInteger q;
    
    /** The CRT coefficient. */
    private BigInteger qInv;
    
    
    
    /** Default constructor. */
    public RSACompleteKey() {
        super(null, null);
        setPubExp(null);
        setPrimes(null, null);
        setCRTExpOne(null);
        setCRTExpTwo(null);
        setCRTCoeff(null);
        return;
    }
    
    /** Main constructor. */
    public RSACompleteKey(BigInteger prime1,
                          BigInteger prime2,
                          BigInteger modulus,
                          BigInteger pubExp,
                          BigInteger priExp,
                          BigInteger crtExp1,
                          BigInteger crtExp2,
                          BigInteger crtCoeff) {
        super(modulus, priExp);
        setPubExp(pubExp);
        setPrimes(prime1, prime2);
        setCRTExpOne(crtExp1);
        setCRTExpTwo(crtExp2);
        setCRTCoeff(crtCoeff);
        return;
    }
    

    
    /** Computes the LCM of the primes. */
    protected void computePhi() {
        phi = lcm(pMinusOne(), qMinusOne());
    }
    
    /** Performs the classical RSA computation. */
    protected BigInteger decrypt(BigInteger c) {
        BigInteger m1, m2, dm, h;
        m1 = c.modPow(getCRTExpOne(), getPrimeOne());
        m2 = c.modPow(getCRTExpTwo(), getPrimeTwo());
        dm = m1.subtract(m2).abs();
        h = getCRTCoeff().multiply(dm).mod(getPrimeOne());
        return m2.add(getPrimeTwo().multiply(h));
    }
    
    /** Returns the first CRT exponent. */
    public BigInteger getCRTExpOne() {
        return dP;
    }
    
    /** Returns the second CRT exponent. */
    public BigInteger getCRTExpTwo() {
        return dQ;
    }
    
    /** Returns the CRT coefficient. */
    public BigInteger getCRTCoeff() {
        return qInv;
    }
    
    /** Returns phi. */
    public BigInteger getPhi() {
        return phi;
    }
    
    /** Returns the larger prime factor. */
    public BigInteger getPrimeOne() {
        return p;
    }
        
    /** Returns the smaller prime factor. */
    public BigInteger getPrimeTwo() {
        return q;
    }
        
    /** Returns the public exponent. */
    public BigInteger getPubExp() {
        return e;
    }
    
    /** Returns true when key is valid. */
    public boolean isValid() {
        if (noValuesNull() && isPrime(p) && isPrime(q)) {
            computePhi();
            return p.multiply(q).equals(getModulus()) &&
                   e.compareTo(THREE) >= 0 &&
                   e.compareTo(getModulus()) < 0 &&
                   e.gcd(phi).equals(ONE) &&
                   getPriExp().compareTo(getModulus()) < 0 &&
                   getPriExp().equals(e.modInverse(phi)) &&
                   dP.compareTo(p) < 0 &&
                   dQ.compareTo(q) < 0 &&
                   dP.equals(e.modInverse(pMinusOne())) &&
                   dQ.equals(e.modInverse(qMinusOne())) &&
                   qInv.compareTo(p) < 0 &&
                   qInv.equals(q.modInverse(p));
        } else {
            return false;
        }
    }
    
    /** Returns true when no fields are null. */
    private boolean noValuesNull() {
        return !(isNull(p) || isNull(q) || isNull(getModulus()) || 
                 isNull(e) || isNull(getPriExp()) || isNull(dP) ||
                 isNull(dQ) || isNull(qInv));
    }
    
    /** Returns p minus one. */
    protected BigInteger pMinusOne() {
        if (isNull(p)) {
            return null;
        } else {
            return p.subtract(ONE);
        }
    }
    
    /** Returns q minus one. */
    protected BigInteger qMinusOne() {
        if (isNull(q)) {
            return null;
        } else {
            return q.subtract(ONE);
        }
    }
    /** Sets the CRT exponent. */
    public void setCRTCoeff(BigInteger crtCoeff) {
        qInv = weedOut(crtCoeff);
        return;
    }
    
    /** Sets the first CRT exponent. */
    public void setCRTExpOne(BigInteger crtExp1) {
        dP = weedOut(crtExp1);
        return;
    }
    
    /** Sets the second CRT exponent. */
    public void setCRTExpTwo(BigInteger crtExp2) {
        dQ = weedOut(crtExp2);
        return;
    }
    
    /** Sets phi. */
    public void setPhi(BigInteger phi) {
        this.phi = weedOut(phi);
    }
    
    /** Sets the prime factors. */
    public void setPrimes(BigInteger prime1, BigInteger prime2) {
        if (isNull(prime1 = weedOut(prime1)) || isNull(prime2 = weedOut(prime2))) {
            return;
        } else {
            if (isPositive(prime1.subtract(prime2))) {
                p = prime1;
                q = prime2;
            } else if (isPositive(prime2.subtract(prime1))) {
                p = prime2;
                q = prime1;
            } else {
                return;
            }
        }
        return;
    }
    
    /** Sets the public exponent. */
    public void setPubExp(BigInteger pubExp) {
        e = weedOut(pubExp);
        return;
    }
    
    
}
