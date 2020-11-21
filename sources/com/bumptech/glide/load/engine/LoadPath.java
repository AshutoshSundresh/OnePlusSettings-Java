package com.bumptech.glide.load.engine;

import androidx.core.util.Pools$Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadPath<Data, ResourceType, Transcode> {
    private final List<? extends DecodePath<Data, ResourceType, Transcode>> decodePaths;
    private final String failureMessage;
    private final Pools$Pool<List<Throwable>> listPool;

    public LoadPath(Class<Data> cls, Class<ResourceType> cls2, Class<Transcode> cls3, List<DecodePath<Data, ResourceType, Transcode>> list, Pools$Pool<List<Throwable>> pools$Pool) {
        this.listPool = pools$Pool;
        Preconditions.checkNotEmpty(list);
        this.decodePaths = list;
        this.failureMessage = "Failed LoadPath{" + cls.getSimpleName() + "->" + cls2.getSimpleName() + "->" + cls3.getSimpleName() + "}";
    }

    public Resource<Transcode> load(DataRewinder<Data> dataRewinder, Options options, int i, int i2, DecodePath.DecodeCallback<ResourceType> decodeCallback) throws GlideException {
        List<Throwable> acquire = this.listPool.acquire();
        Preconditions.checkNotNull(acquire);
        List<Throwable> list = acquire;
        try {
            return loadWithExceptionList(dataRewinder, options, i, i2, decodeCallback, list);
        } finally {
            this.listPool.release(list);
        }
    }

    private Resource<Transcode> loadWithExceptionList(DataRewinder<Data> dataRewinder, Options options, int i, int i2, DecodePath.DecodeCallback<ResourceType> decodeCallback, List<Throwable> list) throws GlideException {
        int size = this.decodePaths.size();
        Resource<Transcode> resource = null;
        for (int i3 = 0; i3 < size; i3++) {
            try {
                resource = ((DecodePath) this.decodePaths.get(i3)).decode(dataRewinder, i, i2, options, decodeCallback);
            } catch (GlideException e) {
                list.add(e);
            }
            if (resource != null) {
                break;
            }
        }
        if (resource != null) {
            return resource;
        }
        throw new GlideException(this.failureMessage, new ArrayList(list));
    }

    public String toString() {
        return "LoadPath{decodePaths=" + Arrays.toString(this.decodePaths.toArray()) + '}';
    }
}
