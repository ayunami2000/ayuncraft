package me.ayunami2000.ayuncraft;

import java.math.BigInteger;

public class PubKey {
    private static byte[] encoded=null;
    private static BigInteger modulus=null;
    private static BigInteger priexp=null;

    public PubKey(byte[] e, BigInteger m, BigInteger p){
        encoded=e;
        modulus=m;
        priexp=p;
    }

    public byte[] getEncoded(){
        return encoded;
    }

    public BigInteger getModulus(){
        return modulus;
    }

    public BigInteger getPriExp(){
        return priexp;
    }
}
