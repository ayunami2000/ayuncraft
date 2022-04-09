package me.ayunami2000.ayuncraft;

import me.ayunami2000.ayuncraft.java.security.Key;
import me.ayunami2000.ayuncraft.javax.crypto.SecretKey;

public class KeyHolder {
    public SecretKey sharedKeyForEncryption;
    public Key toKey(){
        return (Key) sharedKeyForEncryption;
    }
}
