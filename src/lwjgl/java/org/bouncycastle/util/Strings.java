package org.bouncycastle.util;

public final class Strings
{
    /**
     * A locale independent version of toLowerCase that returns a US ASCII lowercase String.
     */
    public static String toLowerCase(String par0Str)
    {
        boolean var1 = false;
        char[] var2 = par0Str.toCharArray();

        for (int var3 = 0; var3 != var2.length; ++var3)
        {
            char var4 = var2[var3];

            if (65 <= var4 && 90 >= var4)
            {
                var1 = true;
                var2[var3] = (char)(var4 - 65 + 97);
            }
        }

        if (var1)
        {
            return new String(var2);
        }
        else
        {
            return par0Str;
        }
    }
}
