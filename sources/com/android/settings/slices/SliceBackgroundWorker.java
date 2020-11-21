package com.android.settings.slices;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SliceBackgroundWorker<E> implements Closeable {
    private static final Map<Uri, SliceBackgroundWorker> LIVE_WORKERS = new ArrayMap();
    private List<E> mCachedResults;
    private final Context mContext;
    private final Uri mUri;

    /* access modifiers changed from: protected */
    public abstract void onSlicePinned();

    /* access modifiers changed from: protected */
    public abstract void onSliceUnpinned();

    protected SliceBackgroundWorker(Context context, Uri uri) {
        this.mContext = context;
        this.mUri = uri;
    }

    /* access modifiers changed from: protected */
    public Uri getUri() {
        return this.mUri;
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return this.mContext;
    }

    public static <T extends SliceBackgroundWorker> T getInstance(Uri uri) {
        return (T) LIVE_WORKERS.get(uri);
    }

    static SliceBackgroundWorker getInstance(Context context, Sliceable sliceable, Uri uri) {
        SliceBackgroundWorker instance = getInstance(uri);
        if (instance != null) {
            return instance;
        }
        SliceBackgroundWorker createInstance = createInstance(context.getApplicationContext(), uri, sliceable.getBackgroundWorkerClass());
        LIVE_WORKERS.put(uri, createInstance);
        return createInstance;
    }

    private static SliceBackgroundWorker createInstance(Context context, Uri uri, Class<? extends SliceBackgroundWorker> cls) {
        Log.d("SliceBackgroundWorker", "create instance: " + cls);
        try {
            return (SliceBackgroundWorker) cls.getConstructor(Context.class, Uri.class).newInstance(context, uri);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Invalid slice background worker: " + cls, e);
        }
    }

    /* access modifiers changed from: package-private */
    public static void shutdown() {
        for (SliceBackgroundWorker sliceBackgroundWorker : LIVE_WORKERS.values()) {
            try {
                sliceBackgroundWorker.close();
            } catch (IOException e) {
                Log.w("SliceBackgroundWorker", "Shutting down worker failed", e);
            }
        }
        LIVE_WORKERS.clear();
    }

    public final List<E> getResults() {
        if (this.mCachedResults == null) {
            return null;
        }
        return new ArrayList(this.mCachedResults);
    }

    /* access modifiers changed from: protected */
    public final void updateResults(List<E> list) {
        boolean z = true;
        if (list != null) {
            z = true ^ areListsTheSame(list, this.mCachedResults);
        } else if (this.mCachedResults == null) {
            z = false;
        }
        if (z) {
            this.mCachedResults = list;
            notifySliceChange();
        }
    }

    /* access modifiers changed from: protected */
    public boolean areListsTheSame(List<E> list, List<E> list2) {
        return list.equals(list2);
    }

    /* access modifiers changed from: protected */
    public final void notifySliceChange() {
        NotifySliceChangeHandler.getInstance().updateSlice(this);
    }

    /* access modifiers changed from: package-private */
    public void pin() {
        onSlicePinned();
    }

    /* access modifiers changed from: package-private */
    public void unpin() {
        onSliceUnpinned();
        NotifySliceChangeHandler.getInstance().cancelSliceUpdate(this);
    }

    /* access modifiers changed from: private */
    public static class NotifySliceChangeHandler extends Handler {
        private static NotifySliceChangeHandler sHandler;
        private final Map<Uri, Long> mLastUpdateTimeLookup = new ArrayMap();

        /* access modifiers changed from: private */
        public static NotifySliceChangeHandler getInstance() {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("NotifySliceChangeHandler", 10);
                handlerThread.start();
                sHandler = new NotifySliceChangeHandler(handlerThread.getLooper());
            }
            return sHandler;
        }

        private NotifySliceChangeHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            if (message.what == 1000) {
                SliceBackgroundWorker sliceBackgroundWorker = (SliceBackgroundWorker) message.obj;
                Uri uri = sliceBackgroundWorker.getUri();
                Context context = sliceBackgroundWorker.getContext();
                this.mLastUpdateTimeLookup.put(uri, Long.valueOf(SystemClock.uptimeMillis()));
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void updateSlice(SliceBackgroundWorker sliceBackgroundWorker) {
            if (!hasMessages(1000, sliceBackgroundWorker)) {
                Message obtainMessage = obtainMessage(1000, sliceBackgroundWorker);
                long longValue = this.mLastUpdateTimeLookup.getOrDefault(sliceBackgroundWorker.getUri(), 0L).longValue();
                if (longValue == 0) {
                    sendMessageDelayed(obtainMessage, 300);
                } else if (SystemClock.uptimeMillis() - longValue > 300) {
                    sendMessage(obtainMessage);
                } else {
                    sendMessageAtTime(obtainMessage, longValue + 300);
                }
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void cancelSliceUpdate(SliceBackgroundWorker sliceBackgroundWorker) {
            removeMessages(1000, sliceBackgroundWorker);
            this.mLastUpdateTimeLookup.remove(sliceBackgroundWorker.getUri());
        }
    }
}
