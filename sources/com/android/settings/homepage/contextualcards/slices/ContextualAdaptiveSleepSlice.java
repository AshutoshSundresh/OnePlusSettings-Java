package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.display.AdaptiveSleepPreferenceController;
import com.android.settings.display.AdaptiveSleepSettings;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBuilderUtils;
import java.util.concurrent.TimeUnit;

public class ContextualAdaptiveSleepSlice implements CustomSliceable {
    static final long DEFERRED_TIME_DAYS = TimeUnit.DAYS.toMillis(14);
    static final String PREF_KEY_SETUP_TIME = "adaptive_sleep_setup_time";
    private Context mContext;

    public ContextualAdaptiveSleepSlice(Context context) {
        this.mContext = context;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        if (this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).getLong(PREF_KEY_SETUP_TIME, DEFERRED_TIME_DAYS) == DEFERRED_TIME_DAYS) {
            this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).edit().putLong(PREF_KEY_SETUP_TIME, System.currentTimeMillis()).apply();
            return null;
        } else if (!isSettingsAvailable() || isUserInteracted() || isRecentlySetup() || isTurnedOn()) {
            return null;
        } else {
            IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_settings_adaptive_sleep);
            CharSequence text = this.mContext.getText(C0017R$string.adaptive_sleep_contextual_slice_title);
            CharSequence text2 = this.mContext.getText(C0017R$string.adaptive_sleep_contextual_slice_summary);
            SliceAction createDeeplink = SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, text);
            ListBuilder listBuilder = new ListBuilder(this.mContext, CustomSliceRegistry.CONTEXTUAL_ADAPTIVE_SLEEP_URI, -1);
            listBuilder.setAccentColor(-1);
            ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
            rowBuilder.setTitleItem(createWithResource, 0);
            rowBuilder.setTitle(text);
            rowBuilder.setSubtitle(text2);
            rowBuilder.setPrimaryAction(createDeeplink);
            listBuilder.addRow(rowBuilder);
            return listBuilder.build();
        }
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.CONTEXTUAL_ADAPTIVE_SLEEP_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        CharSequence text = this.mContext.getText(C0017R$string.adaptive_sleep_title);
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, AdaptiveSleepSettings.class.getName(), AdaptiveSleepPreferenceController.PREF_NAME, text.toString(), 1401).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath(AdaptiveSleepPreferenceController.PREF_NAME).build());
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 0);
    }

    private boolean isTurnedOn() {
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), AdaptiveSleepPreferenceController.PREF_NAME, 0) != 0) {
            return true;
        }
        return false;
    }

    private boolean isUserInteracted() {
        return this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).getBoolean("adaptive_sleep_interacted", false);
    }

    private boolean isRecentlySetup() {
        if (this.mContext.getSharedPreferences("adaptive_sleep_slice", 0).getLong(PREF_KEY_SETUP_TIME, DEFERRED_TIME_DAYS) > System.currentTimeMillis() - DEFERRED_TIME_DAYS) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isSettingsAvailable() {
        return AdaptiveSleepPreferenceController.isControllerAvailable(this.mContext) == 1;
    }
}
