
package com.honeywell.wholesale.framework.utils;


import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 */
public class PasswordHash
{
    public static String hash(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("sha256");
            md.update(origin.getBytes("UTF-8"));
            BigInteger bi = new BigInteger(1, md.digest());

            return bi.toString(16);
        } catch (Exception e) {
            return "";
        }
    }

}