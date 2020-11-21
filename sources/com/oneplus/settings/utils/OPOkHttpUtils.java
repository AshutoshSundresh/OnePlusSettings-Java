package com.oneplus.settings.utils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OPOkHttpUtils {
    private static final byte[] LOCKER = new byte[0];
    private static OPOkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    public interface NetCall {
        void failed(Call call, IOException iOException);

        void success(Call call, Response response) throws IOException;
    }

    static {
        MediaType.parse("application/json; charset=utf-8");
    }

    private OPOkHttpUtils() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(100, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        builder.hostnameVerifier(new HostnameVerifier(this) {
            /* class com.oneplus.settings.utils.OPOkHttpUtils.AnonymousClass1 */

            public boolean verify(String str, SSLSession sSLSession) {
                return true;
            }
        });
        this.mOkHttpClient = builder.build();
    }

    public static OPOkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OPOkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public void postDataAsyn(String str, Map<String, String> map, final NetCall netCall) {
        RequestBody requestBody = setRequestBody(map);
        Request.Builder builder = new Request.Builder();
        builder.post(requestBody);
        builder.url(str);
        this.mOkHttpClient.newCall(builder.build()).enqueue(new Callback(this) {
            /* class com.oneplus.settings.utils.OPOkHttpUtils.AnonymousClass3 */

            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException iOException) {
                netCall.failed(call, iOException);
            }

            @Override // okhttp3.Callback
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);
            }
        });
    }

    private RequestBody setRequestBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            for (String str : map.keySet()) {
                String str2 = str.toString();
                builder.add(str2, map.get(str2));
            }
        }
        return builder.build();
    }
}
