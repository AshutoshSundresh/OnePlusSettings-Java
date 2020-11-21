package com.bumptech.glide.request;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class RequestOptions extends BaseRequestOptions<RequestOptions> {
    public static RequestOptions diskCacheStrategyOf(DiskCacheStrategy diskCacheStrategy) {
        return (RequestOptions) new RequestOptions().diskCacheStrategy(diskCacheStrategy);
    }

    public static RequestOptions signatureOf(Key key) {
        return (RequestOptions) new RequestOptions().signature(key);
    }

    public static RequestOptions decodeTypeOf(Class<?> cls) {
        return (RequestOptions) new RequestOptions().decode(cls);
    }
}
