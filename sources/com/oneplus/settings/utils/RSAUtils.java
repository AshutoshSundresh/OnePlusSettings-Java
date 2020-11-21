package com.oneplus.settings.utils;

import android.util.Base64;
import com.oneplus.settings.SettingsBaseApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public final class RSAUtils {
    private static final Map<String, PrivateKey> PRIVATE_KEY_MAP = new HashMap(1);

    static {
        "#PART#".getBytes();
        new HashMap(1);
    }

    public static PrivateKey loadPrivateKey(String str) throws Exception {
        try {
            RSAPrivateKey rSAPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(str, 2)));
            PRIVATE_KEY_MAP.put("RSAPrivateKey", rSAPrivateKey);
            return rSAPrivateKey;
        } catch (NoSuchAlgorithmException unused) {
            throw new Exception("private key noSuchAlgorithm");
        } catch (InvalidKeySpecException unused2) {
            throw new Exception("private key invalid");
        } catch (NullPointerException unused3) {
            throw new Exception("private key null");
        }
    }

    public static PrivateKey loadPrivateKey(InputStream inputStream) throws Exception {
        try {
            return loadPrivateKey(readKey(inputStream));
        } catch (IOException unused) {
            throw new Exception("private key error");
        } catch (NullPointerException unused2) {
            throw new Exception("private key inputStream null");
        }
    }

    private static String readKey(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return sb.toString();
            }
            if (readLine.charAt(0) != '-') {
                sb.append(readLine);
                sb.append('\r');
            }
        }
    }

    public static PrivateKey getAlitaPrivateKey() {
        if (PRIVATE_KEY_MAP.containsKey("RSAPrivateKey")) {
            return PRIVATE_KEY_MAP.get("RSAPrivateKey");
        }
        try {
            return loadPrivateKey(SettingsBaseApplication.getContext().getResources().getAssets().open(OPUtils.isH2() ? "rsa_private_key_cn.pem" : "rsa_private_key.pem"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
