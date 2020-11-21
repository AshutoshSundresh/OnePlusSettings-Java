package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

public class MainlineModuleVersionPreferenceController extends BasePreferenceController {
    static final Intent MODULE_UPDATE_INTENT = new Intent("android.settings.MODULE_UPDATE_SETTINGS");
    static final Intent MODULE_UPDATE_V2_INTENT = new Intent("android.settings.MODULE_UPDATE_VERSIONS");
    private static final String TAG = "MainlineModuleControl";
    private static final List<String> VERSION_NAME_DATE_PATTERNS = Arrays.asList("yyyy-MM-dd", "yyyy-MM");
    private String mModuleVersion;
    private final PackageManager mPackageManager = this.mContext.getPackageManager();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public MainlineModuleVersionPreferenceController(Context context, String str) {
        super(context, str);
        initModules();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !TextUtils.isEmpty(this.mModuleVersion) ? 0 : 3;
    }

    private void initModules() {
        String string = this.mContext.getString(17039877);
        if (!TextUtils.isEmpty(string)) {
            try {
                this.mModuleVersion = this.mPackageManager.getPackageInfo(string, 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get mainline version.", e);
                this.mModuleVersion = null;
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mPackageManager.resolveActivity(MODULE_UPDATE_V2_INTENT, 0) != null) {
            preference.setIntent(MODULE_UPDATE_V2_INTENT);
            preference.setSelectable(true);
        } else if (this.mPackageManager.resolveActivity(MODULE_UPDATE_INTENT, 0) != null) {
            preference.setIntent(MODULE_UPDATE_INTENT);
            preference.setSelectable(true);
        } else {
            Log.d(TAG, "The ResolveInfo of the update intent is null.");
            preference.setIntent(null);
            preference.setSelectable(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (TextUtils.isEmpty(this.mModuleVersion)) {
            return this.mModuleVersion;
        }
        Optional<Date> parseDateFromVersionName = parseDateFromVersionName(this.mModuleVersion);
        if (parseDateFromVersionName.isPresent()) {
            return DateFormat.getLongDateFormat(this.mContext).format(parseDateFromVersionName.get());
        }
        Log.w("Could not parse mainline versionName (%s) as date.", this.mModuleVersion);
        return this.mModuleVersion;
    }

    private Optional<Date> parseDateFromVersionName(String str) {
        Iterator<String> it = VERSION_NAME_DATE_PATTERNS.iterator();
        while (it.hasNext()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(it.next(), Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getDefault());
                return Optional.of(simpleDateFormat.parse(str));
            } catch (ParseException unused) {
            }
        }
        return Optional.empty();
    }
}
