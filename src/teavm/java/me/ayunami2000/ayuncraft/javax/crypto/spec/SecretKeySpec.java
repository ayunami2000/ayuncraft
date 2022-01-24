package me.ayunami2000.ayuncraft.javax.crypto.spec;

import me.ayunami2000.ayuncraft.java.security.spec.KeySpec;
import me.ayunami2000.ayuncraft.javax.crypto.SecretKey;

public class SecretKeySpec implements KeySpec, SecretKey {
   private String algorithm;
   private byte[] key;

   public String getAlgorithm() {
      return this.algorithm;
   }

   public String getFormat() {
      return "RAW";
   }

   public byte[] getEncoded() {
      byte[] tmp = new byte[this.key.length];
      System.arraycopy(this.key, 0, tmp, 0, tmp.length);
      return tmp;
   }

   public int hashCode() {
      int code = this.algorithm.toUpperCase().hashCode();

      for(int i = 0; i != this.key.length; ++i) {
         code ^= this.key[i] << 8 * (i % 4);
      }

      return code;
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof SecretKeySpec) {
         SecretKeySpec spec = (SecretKeySpec)obj;
         if (!this.algorithm.equalsIgnoreCase(spec.algorithm)) {
            return false;
         } else if (this.key.length != spec.key.length) {
            return false;
         } else {
            for(int i = 0; i != this.key.length; ++i) {
               if (this.key[i] != spec.key[i]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public SecretKeySpec(byte[] key, String algorithm) {
      if (key == null) {
         throw new IllegalArgumentException("null key passed");
      } else if (algorithm == null) {
         throw new IllegalArgumentException("null algorithm passed");
      } else {
         this.key = new byte[key.length];
         System.arraycopy(key, 0, this.key, 0, key.length);
         this.algorithm = algorithm;
      }
   }

   public SecretKeySpec(byte[] key, int offset, int len, String algorithm) {
      if (key == null) {
         throw new IllegalArgumentException("Null key passed");
      } else if (key.length - offset < len) {
         throw new IllegalArgumentException("Bad offset/len");
      } else if (algorithm == null) {
         throw new IllegalArgumentException("Null algorithm string passed");
      } else {
         this.key = new byte[len];
         System.arraycopy(key, offset, this.key, 0, len);
         this.algorithm = algorithm;
      }
   }
}
