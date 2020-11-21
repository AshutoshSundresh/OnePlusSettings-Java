package com.bumptech.glide.load.model;

import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Map;

public class GlideUrl implements Key {
    private volatile byte[] cacheKeyBytes;
    private int hashCode;
    private final Headers headers;
    private String safeStringUrl;
    private URL safeUrl;
    private final String stringUrl;
    private final URL url;

    public GlideUrl(URL url2) {
        this(url2, Headers.DEFAULT);
    }

    public GlideUrl(String str) {
        this(str, Headers.DEFAULT);
    }

    public GlideUrl(URL url2, Headers headers2) {
        Preconditions.checkNotNull(url2);
        this.url = url2;
        this.stringUrl = null;
        Preconditions.checkNotNull(headers2);
        this.headers = headers2;
    }

    public GlideUrl(String str, Headers headers2) {
        this.url = null;
        Preconditions.checkNotEmpty(str);
        this.stringUrl = str;
        Preconditions.checkNotNull(headers2);
        this.headers = headers2;
    }

    public URL toURL() throws MalformedURLException {
        return getSafeUrl();
    }

    private URL getSafeUrl() throws MalformedURLException {
        if (this.safeUrl == null) {
            this.safeUrl = new URL(getSafeStringUrl());
        }
        return this.safeUrl;
    }

    private String getSafeStringUrl() {
        if (TextUtils.isEmpty(this.safeStringUrl)) {
            String str = this.stringUrl;
            if (TextUtils.isEmpty(str)) {
                URL url2 = this.url;
                Preconditions.checkNotNull(url2);
                str = url2.toString();
            }
            this.safeStringUrl = Uri.encode(str, "@#&=*+-_.,:!?()/~'%;$");
        }
        return this.safeStringUrl;
    }

    public Map<String, String> getHeaders() {
        return this.headers.getHeaders();
    }

    public String getCacheKey() {
        String str = this.stringUrl;
        if (str != null) {
            return str;
        }
        URL url2 = this.url;
        Preconditions.checkNotNull(url2);
        return url2.toString();
    }

    public String toString() {
        return getCacheKey();
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(getCacheKeyBytes());
    }

    private byte[] getCacheKeyBytes() {
        if (this.cacheKeyBytes == null) {
            this.cacheKeyBytes = getCacheKey().getBytes(Key.CHARSET);
        }
        return this.cacheKeyBytes;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object obj) {
        if (!(obj instanceof GlideUrl)) {
            return false;
        }
        GlideUrl glideUrl = (GlideUrl) obj;
        if (!getCacheKey().equals(glideUrl.getCacheKey()) || !this.headers.equals(glideUrl.headers)) {
            return false;
        }
        return true;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        if (this.hashCode == 0) {
            int hashCode2 = getCacheKey().hashCode();
            this.hashCode = hashCode2;
            this.hashCode = (hashCode2 * 31) + this.headers.hashCode();
        }
        return this.hashCode;
    }
}
