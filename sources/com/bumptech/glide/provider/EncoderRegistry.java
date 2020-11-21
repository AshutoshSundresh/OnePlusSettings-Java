package com.bumptech.glide.provider;

import com.bumptech.glide.load.Encoder;
import java.util.ArrayList;
import java.util.List;

public class EncoderRegistry {
    private final List<Entry<?>> encoders = new ArrayList();

    public synchronized <T> Encoder<T> getEncoder(Class<T> cls) {
        for (Entry<?> entry : this.encoders) {
            if (entry.handles(cls)) {
                return entry.encoder;
            }
        }
        return null;
    }

    public synchronized <T> void append(Class<T> cls, Encoder<T> encoder) {
        this.encoders.add(new Entry<>(cls, encoder));
    }

    /* access modifiers changed from: private */
    public static final class Entry<T> {
        private final Class<T> dataClass;
        final Encoder<T> encoder;

        Entry(Class<T> cls, Encoder<T> encoder2) {
            this.dataClass = cls;
            this.encoder = encoder2;
        }

        /* access modifiers changed from: package-private */
        public boolean handles(Class<?> cls) {
            return this.dataClass.isAssignableFrom(cls);
        }
    }
}
