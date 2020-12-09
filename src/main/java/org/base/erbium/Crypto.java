package org.base.erbium;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

//import com.sun.mail.util.BASE64EncoderStream;

public class Crypto {

    private SecretKey $key;
    private String $Base64EncodedKey = "WxzcATcZdqg="; // default key

    public void setKey(String Base64EncodedKey) {
        $Base64EncodedKey = Base64EncodedKey;
    }

    /*String getKey() {
        return $Base64EncodedKey;
    }*/

    public String generateKey() {

        SecretKey key = null;

        try {

            key = KeyGenerator.getInstance("DES").generateKey();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm:" + e.getMessage());
        }

        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String encryptDES(String string) {

        byte[] enc = null;

        byte[] usualKeyBase64Decoded = Base64.getDecoder().decode($Base64EncodedKey);
        SecretKey usualKey = new SecretKeySpec(usualKeyBase64Decoded, 0, usualKeyBase64Decoded.length, "DES");

        try {

            Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, usualKey);

            byte[] utf8 = string.getBytes("UTF8");
            enc = ecipher.doFinal(utf8);
            enc = BASE64EncoderStream.encode(enc);

        } catch (NoSuchPaddingException e) {
            System.out.println("No Such Padding:" + e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println("Invalid Key:" + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return new String(enc);
    }

    public String decryptDES(String string) {

        byte[] utf8 = null;
        String decryptedString = null;

        try {

            // decode with base64 to get bytes

            byte[] decodedKey = Base64.getDecoder().decode($Base64EncodedKey);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");


            Cipher dcipher = Cipher.getInstance("DES");
            dcipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] dec = BASE64DecoderStream.decode(string.getBytes());
            utf8 = dcipher.doFinal(dec);

            decryptedString = new String(utf8, "UTF8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }

    //public byte[]

    /*public void encryptDES(String string) {



        try {
            Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);

        } catch (NoSuchPaddingException e) {
            System.out.println("No Such Padding:" + e.getMessage());
            return;
        }

    }*/


}

