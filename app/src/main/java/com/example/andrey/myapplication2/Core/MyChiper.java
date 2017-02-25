package com.example.andrey.myapplication2.Core;

import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

/**
 * Created by 1 on 14.02.2017.
 */

public class MyChiper {
    private AppCompatActivity _act;
    public MyChiper(AppCompatActivity act){
      _act=act;
    }
    private KeyStore.PrivateKeyEntry GetPrivateKeyEntry(){
        try {
            String alias = "key11";
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(alias)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
                KeyPairGeneratorSpec spec = new      KeyPairGeneratorSpec.Builder(_act)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                kpg.initialize(spec);
                kpg.generateKeyPair();
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            return privateKeyEntry;
        }
        catch (Exception ex){
            return null;
        }
    }
    public String Shifr(String mes) {
        try {
            PublicKey publicKey = (PublicKey) GetPrivateKeyEntry().getCertificate().getPublicKey();
            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[]cipherText = inCipher.doFinal(mes.getBytes());
            String encryptedtext=new String(cipherText, "UTF8");
            return encryptedtext;
        } catch (Exception ex) {
            return mes;
        }
    }

    public String DeShifr(String mes) {
        try {
            PrivateKey privateKey = (PrivateKey) GetPrivateKeyEntry().getPrivateKey();
            Cipher outCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            outCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[]newPlainText = outCipher.doFinal(mes.getBytes());
            String text=new String(newPlainText, "UTF8");
            return text;
        } catch (Exception ex) {
            return mes;
        }
    }
}
