package com.android.settings.deviceinfo;

import android.content.res.ColorStateList;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.deviceinfo.StorageSettings;

public class StorageVolumePreference extends Preference {
    private static final String TAG = StorageVolumePreference.class.getSimpleName();
    private ColorStateList mColorTintList;
    private final StorageManager mStorageManager;
    private final View.OnClickListener mUnmountListener = new View.OnClickListener() {
        /* class com.android.settings.deviceinfo.StorageVolumePreference.AnonymousClass1 */

        public void onClick(View view) {
            new StorageSettings.UnmountTask(StorageVolumePreference.this.getContext(), StorageVolumePreference.this.mVolume).execute(new Void[0]);
        }
    };
    private int mUsedPercent = -1;
    private final VolumeInfo mVolume;

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00d1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StorageVolumePreference(android.content.Context r18, android.os.storage.VolumeInfo r19, long r20) {
        /*
        // Method dump skipped, instructions count: 264
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.StorageVolumePreference.<init>(android.content.Context, android.os.storage.VolumeInfo, long):void");
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.unmount);
        if (imageView != null) {
            imageView.setOnClickListener(this.mUnmountListener);
        }
        ProgressBar progressBar = (ProgressBar) preferenceViewHolder.findViewById(16908301);
        if (this.mVolume.getType() != 1 || this.mUsedPercent == -1) {
            progressBar.setVisibility(8);
        } else {
            progressBar.setVisibility(0);
            progressBar.setProgress(this.mUsedPercent);
            progressBar.setProgressTintList(this.mColorTintList);
        }
        super.onBindViewHolder(preferenceViewHolder);
    }
}
