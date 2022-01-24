package org.bouncycastle.crypto;

import java.util.Random;

public class CipherKeyGenerator
{
    protected Random random;
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
