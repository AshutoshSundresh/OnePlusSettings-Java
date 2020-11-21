package com.google.analytics.tracking.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

/* access modifiers changed from: package-private */
public class SimpleNetworkDispatcher implements Dispatcher {
    private final Context ctx;
    private GoogleAnalytics gaInstance;
    private final HttpClient httpClient;
    private URL mOverrideHostUrl;
    private final String userAgent;

    SimpleNetworkDispatcher(HttpClient httpClient2, GoogleAnalytics googleAnalytics, Context context) {
        this.ctx = context.getApplicationContext();
        this.userAgent = createUserAgentString("GoogleAnalytics", "3.0", Build.VERSION.RELEASE, Utils.getLanguage(Locale.getDefault()), Build.MODEL, Build.ID);
        this.httpClient = httpClient2;
        this.gaInstance = googleAnalytics;
    }

    SimpleNetworkDispatcher(HttpClient httpClient2, Context context) {
        this(httpClient2, GoogleAnalytics.getInstance(context), context);
    }

    @Override // com.google.analytics.tracking.android.Dispatcher
    public boolean okToDispatch() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.ctx.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        Log.v("...no network connectivity");
        return false;
    }

    @Override // com.google.analytics.tracking.android.Dispatcher
    public int dispatchHits(List<Hit> list) {
        int min = Math.min(list.size(), 40);
        boolean z = true;
        int i = 0;
        for (int i2 = 0; i2 < min; i2++) {
            Hit hit = list.get(i2);
            URL url = getUrl(hit);
            if (url != null) {
                HttpHost httpHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
                String path = url.getPath();
                String postProcessHit = TextUtils.isEmpty(hit.getHitParams()) ? "" : HitBuilder.postProcessHit(hit, System.currentTimeMillis());
                HttpEntityEnclosingRequest buildRequest = buildRequest(postProcessHit, path);
                if (buildRequest != null) {
                    buildRequest.addHeader("Host", httpHost.toHostString());
                    if (Log.isVerbose()) {
                        logDebugInformation(buildRequest);
                    }
                    if (postProcessHit.length() > 8192) {
                        Log.w("Hit too long (> 8192 bytes)--not sent");
                    } else if (this.gaInstance.isDryRunEnabled()) {
                        Log.i("Dry run enabled. Hit not actually sent.");
                    } else {
                        if (z) {
                            try {
                                GANetworkReceiver.sendRadioPoweredBroadcast(this.ctx);
                                z = false;
                            } catch (ClientProtocolException unused) {
                                Log.w("ClientProtocolException sending hit; discarding hit...");
                            } catch (IOException e) {
                                Log.w("Exception sending hit: " + e.getClass().getSimpleName());
                                Log.w(e.getMessage());
                                return i;
                            }
                        }
                        HttpResponse execute = this.httpClient.execute(httpHost, buildRequest);
                        int statusCode = execute.getStatusLine().getStatusCode();
                        HttpEntity entity = execute.getEntity();
                        if (entity != null) {
                            entity.consumeContent();
                        }
                        if (statusCode != 200) {
                            Log.w("Bad response: " + execute.getStatusLine().getStatusCode());
                        }
                    }
                }
            } else if (Log.isVerbose()) {
                Log.w("No destination: discarding hit: " + hit.getHitParams());
            } else {
                Log.w("No destination: discarding hit.");
            }
            i++;
        }
        return i;
    }

    private HttpEntityEnclosingRequest buildRequest(String str, String str2) {
        BasicHttpEntityEnclosingRequest basicHttpEntityEnclosingRequest;
        if (TextUtils.isEmpty(str)) {
            Log.w("Empty hit, discarding.");
            return null;
        }
        String str3 = str2 + "?" + str;
        if (str3.length() < 2036) {
            basicHttpEntityEnclosingRequest = new BasicHttpEntityEnclosingRequest("GET", str3);
        } else {
            BasicHttpEntityEnclosingRequest basicHttpEntityEnclosingRequest2 = new BasicHttpEntityEnclosingRequest("POST", str2);
            try {
                basicHttpEntityEnclosingRequest2.setEntity(new StringEntity(str));
                basicHttpEntityEnclosingRequest = basicHttpEntityEnclosingRequest2;
            } catch (UnsupportedEncodingException unused) {
                Log.w("Encoding error, discarding hit");
                return null;
            }
        }
        basicHttpEntityEnclosingRequest.addHeader("User-Agent", this.userAgent);
        return basicHttpEntityEnclosingRequest;
    }

    private void logDebugInformation(HttpEntityEnclosingRequest httpEntityEnclosingRequest) {
        int available;
        StringBuffer stringBuffer = new StringBuffer();
        for (Header header : httpEntityEnclosingRequest.getAllHeaders()) {
            stringBuffer.append(header.toString());
            stringBuffer.append("\n");
        }
        stringBuffer.append(httpEntityEnclosingRequest.getRequestLine().toString());
        stringBuffer.append("\n");
        if (httpEntityEnclosingRequest.getEntity() != null) {
            try {
                InputStream content = httpEntityEnclosingRequest.getEntity().getContent();
                if (content != null && (available = content.available()) > 0) {
                    byte[] bArr = new byte[available];
                    content.read(bArr);
                    stringBuffer.append("POST:\n");
                    stringBuffer.append(new String(bArr));
                    stringBuffer.append("\n");
                }
            } catch (IOException unused) {
                Log.v("Error Writing hit to log...");
            }
        }
        Log.v(stringBuffer.toString());
    }

    /* access modifiers changed from: package-private */
    public String createUserAgentString(String str, String str2, String str3, String str4, String str5, String str6) {
        return String.format("%s/%s (Linux; U; Android %s; %s; %s Build/%s)", str, str2, str3, str4, str5, str6);
    }

    /* access modifiers changed from: package-private */
    public URL getUrl(Hit hit) {
        URL url = this.mOverrideHostUrl;
        if (url != null) {
            return url;
        }
        try {
            return new URL("http:".equals(hit.getHitUrlScheme()) ? "http://www.google-analytics.com/collect" : "https://ssl.google-analytics.com/collect");
        } catch (MalformedURLException unused) {
            Log.e("Error trying to parse the hardcoded host url. This really shouldn't happen.");
            return null;
        }
    }

    @Override // com.google.analytics.tracking.android.Dispatcher
    public void overrideHostUrl(String str) {
        try {
            this.mOverrideHostUrl = new URL(str);
        } catch (MalformedURLException unused) {
            this.mOverrideHostUrl = null;
        }
    }
}
