package com.google.analytics.tracking.android;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/* access modifiers changed from: package-private */
public class HitBuilder {
    static Map<String, String> generateHitParams(Map<String, String> map) {
        HashMap hashMap = new HashMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().startsWith("&") && entry.getValue() != null) {
                String substring = entry.getKey().substring(1);
                if (!TextUtils.isEmpty(substring)) {
                    hashMap.put(substring, entry.getValue());
                }
            }
        }
        return hashMap;
    }

    static String postProcessHit(Hit hit, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(hit.getHitParams());
        if (hit.getHitTime() > 0) {
            long hitTime = j - hit.getHitTime();
            if (hitTime >= 0) {
                sb.append("&qt");
                sb.append("=");
                sb.append(hitTime);
            }
        }
        sb.append("&z");
        sb.append("=");
        sb.append(hit.getHitId());
        return sb.toString();
    }

    static String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            throw new AssertionError("URL encoding failed for: " + str);
        }
    }
}
