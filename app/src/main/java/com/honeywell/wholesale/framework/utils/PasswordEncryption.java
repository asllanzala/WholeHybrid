package com.honeywell.wholesale.framework.utils;

import java.security.Key;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Created by xiaofei on 4/19/17.
 *
 */

public class PasswordEncryption {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private String originPassword;
    private int length;

    public PasswordEncryption(String originPassword) {
        this.originPassword = originPassword;
        length = originPassword.length();
    }

    public byte[] genSalt() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    public Key toKey() throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(originPassword.toCharArray(), genSalt(),
                1000, length);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);

        return keyFactory.generateSecret(keySpec);
    }

    public String encrypt()
            throws Exception {
        Key key = toKey();
        PBEParameterSpec paramSpec = new PBEParameterSpec(genSalt(), 1000);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return new String(cipher.doFinal(originPassword.getBytes()));
    }

}
