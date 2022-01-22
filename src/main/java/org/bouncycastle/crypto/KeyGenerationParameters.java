package org.bouncycastle.crypto;

import java.util.Random;

public class KeyGenerationParameters
{
    private Random random;
    private int strength;

    public KeyGenerationParameters(Random par1SecureRandom, int par2)
    {
        this.random = par1SecureRandom;
        this.strength = par2;
    }

    /**
     * Return the random source associated with this generator.
     */
    public Random getRandom()
    {
        return this.random;
    }

    /**
     * Return the bit strength for keys produced by this generator.
     */
    public int getStrength()
    {
        return this.strength;
    }
}
