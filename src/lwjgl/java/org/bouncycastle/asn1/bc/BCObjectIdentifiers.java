package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface BCObjectIdentifiers
{
    ASN1ObjectIdentifier bc = new ASN1ObjectIdentifier("1.3.6.1.4.1.22554");
    ASN1ObjectIdentifier bc_pbe = new ASN1ObjectIdentifier(bc.getId() + ".1");
    ASN1ObjectIdentifier bc_pbe_sha1 = new ASN1ObjectIdentifier(bc_pbe.getId() + ".1");
    ASN1ObjectIdentifier bc_pbe_sha256 = new ASN1ObjectIdentifier(bc_pbe.getId() + ".2.1");
    ASN1ObjectIdentifier bc_pbe_sha384 = new ASN1ObjectIdentifier(bc_pbe.getId() + ".2.2");
    ASN1ObjectIdentifier bc_pbe_sha512 = new ASN1ObjectIdentifier(bc_pbe.getId() + ".2.3");
    ASN1ObjectIdentifier bc_pbe_sha224 = new ASN1ObjectIdentifier(bc_pbe.getId() + ".2.4");
    ASN1ObjectIdentifier bc_pbe_sha1_pkcs5 = new ASN1ObjectIdentifier(bc_pbe_sha1.getId() + ".1");
    ASN1ObjectIdentifier bc_pbe_sha1_pkcs12 = new ASN1ObjectIdentifier(bc_pbe_sha1.getId() + ".2");
    ASN1ObjectIdentifier bc_pbe_sha256_pkcs5 = new ASN1ObjectIdentifier(bc_pbe_sha256.getId() + ".1");
    ASN1ObjectIdentifier bc_pbe_sha256_pkcs12 = new ASN1ObjectIdentifier(bc_pbe_sha256.getId() + ".2");
    ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes128_cbc = new ASN1ObjectIdentifier(bc_pbe_sha1_pkcs12.getId() + ".1.2");
    ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes192_cbc = new ASN1ObjectIdentifier(bc_pbe_sha1_pkcs12.getId() + ".1.22");
    ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes256_cbc = new ASN1ObjectIdentifier(bc_pbe_sha1_pkcs12.getId() + ".1.42");
    ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes128_cbc = new ASN1ObjectIdentifier(bc_pbe_sha256_pkcs12.getId() + ".1.2");
    ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes192_cbc = new ASN1ObjectIdentifier(bc_pbe_sha256_pkcs12.getId() + ".1.22");
    ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes256_cbc = new ASN1ObjectIdentifier(bc_pbe_sha256_pkcs12.getId() + ".1.42");
}
