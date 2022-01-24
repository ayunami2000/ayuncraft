package org.bouncycastle.asn1;

public abstract class ASN1Primitive extends ASN1Object
{
    public final boolean equals(Object par1Obj)
    {
        return this == par1Obj ? true : par1Obj instanceof ASN1Encodable && this.asn1Equals(((ASN1Encodable)par1Obj).toASN1Primitive());
    }

    public ASN1Primitive toASN1Primitive()
    {
        return this;
    }

    public abstract int hashCode();

    abstract boolean asn1Equals(ASN1Primitive var1);
}
