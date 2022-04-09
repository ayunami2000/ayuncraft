package me.ayunami2000.ayuncraft;

import java.security.Key;
import javax.crypto.SecretKey;

public class KeyHolder {
    public SecretKey sharedKeyForEncryption;
    public Key toKey(){
        return (Key) sharedKeyForEncryption;
    }
}