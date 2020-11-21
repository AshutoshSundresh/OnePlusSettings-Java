package com.android.settings.datetime.timezone.model;

import android.content.Context;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settingslib.utils.AsyncLoaderCompat;

public class TimeZoneDataLoader extends AsyncLoaderCompat<TimeZoneData> {

    public interface OnDataReadyCallback {
        void onTimeZoneDataReady(TimeZoneData timeZoneData);
    }

    /* access modifiers changed from: protected */
    public void onDiscardResult(TimeZoneData timeZoneData) {
    }

    public TimeZoneDataLoader(Context context) {
        super(context);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public TimeZoneData loadInBackground() {
        return TimeZoneData.getInstance();
    }

    public static class LoaderCreator implements LoaderManager.LoaderCallbacks<TimeZoneData> {
        private final OnDataReadyCallback mCallback;
        private final Context mContext;

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<TimeZoneData> loader) {
        }

        public LoaderCreator(Context context, OnDataReadyCallback onDataReadyCallback) {
            this.mContext = context;
            this.mCallback = onDataReadyCallback;
        }

        /* Return type fixed from 'androidx.loader.content.Loader' to match base method */
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<TimeZoneData> onCreateLoader(int i, Bundle bundle) {
            return new TimeZoneDataLoader(this.mContext);
        }

        public void onLoadFinished(Loader<TimeZoneData> loader, TimeZoneData timeZoneData) {
            OnDataReadyCallback onDataReadyCallback = this.mCallback;
            if (onDataReadyCallback != null) {
                onDataReadyCallback.onTimeZoneDataReady(timeZoneData);
            }
        }
    }
}
