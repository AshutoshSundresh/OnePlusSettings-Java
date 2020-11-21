package com.bumptech.glide.load.resource.bitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;

public class BitmapDrawableDecoder<DataType> implements ResourceDecoder<DataType, BitmapDrawable> {
    private final ResourceDecoder<DataType, Bitmap> decoder;
    private final Resources resources;

    public BitmapDrawableDecoder(Resources resources2, ResourceDecoder<DataType, Bitmap> resourceDecoder) {
        Preconditions.checkNotNull(resources2);
        this.resources = resources2;
        Preconditions.checkNotNull(resourceDecoder);
        this.decoder = resourceDecoder;
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(DataType datatype, Options options) throws IOException {
        return this.decoder.handles(datatype, options);
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<BitmapDrawable> decode(DataType datatype, int i, int i2, Options options) throws IOException {
        return LazyBitmapDrawableResource.obtain(this.resources, this.decoder.decode(datatype, i, i2, options));
    }
}
