package com.google.tagmanager;

import android.content.Context;
import android.os.Build;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.apache.http.client.HttpClient;

class SimpleNetworkDispatcher implements Dispatcher {

    public interface DispatchListener {
    }

    SimpleNetworkDispatcher(HttpClient httpClient, Context context, DispatchListener dispatchListener) {
        context.getApplicationContext();
        createUserAgentString("GoogleTagManager", "3.02", Build.VERSION.RELEASE, getUserAgentLanguage(Locale.getDefault()), Build.MODEL, Build.ID);
    }

    /* access modifiers changed from: package-private */
    public String createUserAgentString(String str, String str2, String str3, String str4, String str5, String str6) {
        return String.format("%s/%s (Linux; U; Android %s; %s; %s Build/%s)", str, str2, str3, str4, str5, str6);
    }

    static String getUserAgentLanguage(Locale locale) {
        if (locale == null || locale.getLanguage() == null || locale.getLanguage().length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(locale.getLanguage().toLowerCase());
        if (!(locale.getCountry() == null || locale.getCountry().length() == 0)) {
            sb.append("-");
            sb.append(locale.getCountry().toLowerCase());
        }
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public URL getUrl(Hit hit) {
        try {
            return new URL(hit.getHitUrl());
        } catch (MalformedURLException unused) {
            Log.e("Error trying to parse the GTM url.");
            return null;
        }
    }
}
