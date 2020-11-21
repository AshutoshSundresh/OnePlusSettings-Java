package com.android.settingslib.media;

import android.app.Notification;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class MediaManager {
    protected final Collection<MediaDeviceCallback> mCallbacks = new CopyOnWriteArrayList();
    protected Context mContext;
    protected final List<MediaDevice> mMediaDevices = new ArrayList();

    public interface MediaDeviceCallback {
        void onConnectedDeviceChanged(String str);

        void onDeviceAttributesChanged();

        void onDeviceListAdded(List<MediaDevice> list);

        void onRequestFailed(int i);
    }

    MediaManager(Context context, Notification notification) {
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public void registerCallback(MediaDeviceCallback mediaDeviceCallback) {
        if (!this.mCallbacks.contains(mediaDeviceCallback)) {
            this.mCallbacks.add(mediaDeviceCallback);
        }
    }

    /* access modifiers changed from: protected */
    public void unregisterCallback(MediaDeviceCallback mediaDeviceCallback) {
        if (this.mCallbacks.contains(mediaDeviceCallback)) {
            this.mCallbacks.remove(mediaDeviceCallback);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDeviceListAdded() {
        for (MediaDeviceCallback mediaDeviceCallback : getCallbacks()) {
            mediaDeviceCallback.onDeviceListAdded(new ArrayList(this.mMediaDevices));
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchConnectedDeviceChanged(String str) {
        for (MediaDeviceCallback mediaDeviceCallback : getCallbacks()) {
            mediaDeviceCallback.onConnectedDeviceChanged(str);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDataChanged() {
        for (MediaDeviceCallback mediaDeviceCallback : getCallbacks()) {
            mediaDeviceCallback.onDeviceAttributesChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchOnRequestFailed(int i) {
        for (MediaDeviceCallback mediaDeviceCallback : getCallbacks()) {
            mediaDeviceCallback.onRequestFailed(i);
        }
    }

    private Collection<MediaDeviceCallback> getCallbacks() {
        return new CopyOnWriteArrayList(this.mCallbacks);
    }
}
