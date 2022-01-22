package org.bouncycastle.asn1;

public abstract class ASN1Object implements ASN1Encodable
{
    public int hashCode()
    {
        return this.toASN1Primitive().hashCode();
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ASN1Encodable))
        {
            return false;
        }
        else
        {
            ASN1Encodable var2 = (ASN1Encodable)par1Obj;
            return this.toASN1Primitive().equals(var2.toASN1Primitive());
        }
    }

    public abstract ASN1Primitive toASN1Primitive();
}
