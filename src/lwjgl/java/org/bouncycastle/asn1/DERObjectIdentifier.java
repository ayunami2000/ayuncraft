package org.bouncycastle.asn1;

public class DERObjectIdentifier extends ASN1Primitive
{
    String identifier;
    private static ASN1ObjectIdentifier[][] cache = new ASN1ObjectIdentifier[255][];

    public DERObjectIdentifier(String par1Str)
    {
        if (!isValidIdentifier(par1Str))
        {
            throw new IllegalArgumentException("string " + par1Str + " not an OID");
        }
        else
        {
            this.identifier = par1Str;
        }
    }

    public String getId()
    {
        return this.identifier;
    }

    public int hashCode()
    {
        return this.identifier.hashCode();
    }

    boolean asn1Equals(ASN1Primitive par1ASN1Primitive)
    {
        return !(par1ASN1Primitive instanceof DERObjectIdentifier) ? false : this.identifier.equals(((DERObjectIdentifier)par1ASN1Primitive).identifier);
    }

    public String toString()
    {
        return this.getId();
    }

    private static boolean isValidIdentifier(String par0Str)
    {
        if (par0Str.length() >= 3 && par0Str.charAt(1) == 46)
        {
            char var1 = par0Str.charAt(0);

            if (var1 >= 48 && var1 <= 50)
            {
                boolean var2 = false;

                for (int var3 = par0Str.length() - 1; var3 >= 2; --var3)
                {
                    char var4 = par0Str.charAt(var3);

                    if (48 <= var4 && var4 <= 57)
                    {
                        var2 = true;
                    }
                    else
                    {
                        if (var4 != 46)
                        {
                            return false;
                        }

                        if (!var2)
                        {
                            return false;
                        }

                        var2 = false;
                    }
                }

                return var2;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
