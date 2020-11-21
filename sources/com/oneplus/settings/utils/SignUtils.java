package com.oneplus.settings.utils;

import android.util.Base64;
import android.util.Log;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SignUtils {
    public static String getSignContent(Map<String, String> map) {
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList<String> arrayList = new ArrayList(map.keySet());
        Collections.sort(arrayList);
        int i = 0;
        for (String str : arrayList) {
            String str2 = map.get(str);
            if (areNotBlank(str, str2)) {
                stringBuffer.append(i == 0 ? "" : "&");
                stringBuffer.append(str);
                stringBuffer.append("=");
                stringBuffer.append(str2);
                i++;
            }
        }
        return stringBuffer.toString();
    }

    public static String rsa256Sign(String str) {
        try {
            return generateSign(str, "SHA256WithRSA");
        } catch (Exception e) {
            Log.e("SignUtils", String.format("RSA content = %s; charset = %s ", str, "UTF-8"));
            e.printStackTrace();
            return "";
        }
    }

    public static String generateSign(String str, String str2) throws Exception {
        PrivateKey alitaPrivateKey = RSAUtils.getAlitaPrivateKey();
        Signature instance = Signature.getInstance(str2);
        instance.initSign(alitaPrivateKey);
        instance.update(str.getBytes("UTF-8"));
        return new String(Base64.encode(instance.sign(), 2));
    }

    public static boolean areNotBlank(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            return false;
        }
        int length = strArr.length;
        boolean z = true;
        for (int i = 0; i < length; i++) {
            z &= !isBlank(strArr[i]);
        }
        return z;
    }

    public static boolean isBlank(String str) {
        int length;
        if (!(str == null || (length = str.length()) == 0)) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
