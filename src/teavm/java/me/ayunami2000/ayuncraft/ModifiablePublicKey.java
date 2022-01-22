package me.ayunami2000.ayuncraft;

import me.ayunami2000.ayuncraft.java.security.PublicKey;

public class ModifiablePublicKey implements PublicKey {
    private String algorithm;
    private String format;
    private byte[] encoded;

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public byte[] getEncoded() {
        return encoded;
    }

    public ModifiablePublicKey(String algo,String form,byte[] data){
        algorithm=algo;
        format=form;
        encoded=data;
    }
}
