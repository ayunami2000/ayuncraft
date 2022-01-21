package org.bouncycastle.crypto;

import java.security.SecureRandom;

public class CipherKeyGenerator
{
    protected SecureRandom random;
    protected int strength;

    public void init(KeyGenerationParameters par1)
    {
        this.random = par1.getRandom();
        this.strength = (par1.getStrength() + 7) / 8;
    }

    public byte[] generateKey()
    {
        byte[] var1 = new byte[this.strength];
        this.random.nextBytes(var1);
        return var1;
    }
}
