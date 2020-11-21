package com.bumptech.glide.module;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;

@Deprecated
public interface GlideModule {
    /* synthetic */ void applyOptions(Context context, GlideBuilder glideBuilder);

    /* synthetic */ void registerComponents(Context context, Glide glide, Registry registry);
}
