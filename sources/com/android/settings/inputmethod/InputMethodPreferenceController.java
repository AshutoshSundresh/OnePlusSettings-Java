package com.android.settings.inputmethod;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.inputmethod.InputMethodPreference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InputMethodPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart {
    private DevicePolicyManager mDpm;
    private InputMethodManager mImm;
    private Preference mPreference;
    PreferenceScreen mScreen;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public InputMethodPreferenceController(Context context, String str) {
        super(context, str);
        this.mImm = (InputMethodManager) context.getSystemService(InputMethodManager.class);
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        updateInputMethodPreferenceViews();
    }

    private void updateInputMethodPreferenceViews() {
        ArrayList arrayList = new ArrayList();
        List permittedInputMethodsForCurrentUser = this.mDpm.getPermittedInputMethodsForCurrentUser();
        List<InputMethodInfo> enabledInputMethodList = this.mImm.getEnabledInputMethodList();
        int size = enabledInputMethodList == null ? 0 : enabledInputMethodList.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo inputMethodInfo = enabledInputMethodList.get(i);
            boolean z = permittedInputMethodsForCurrentUser == null || permittedInputMethodsForCurrentUser.contains(inputMethodInfo.getPackageName());
            Drawable loadIcon = inputMethodInfo.loadIcon(this.mContext.getPackageManager());
            InputMethodPreference inputMethodPreference = new InputMethodPreference(this.mScreen.getContext(), inputMethodInfo, false, z, (InputMethodPreference.OnSavePreferenceListener) null);
            inputMethodPreference.setIcon(loadIcon);
            arrayList.add(inputMethodPreference);
        }
        arrayList.sort(new Comparator(Collator.getInstance()) {
            /* class com.android.settings.inputmethod.$$Lambda$InputMethodPreferenceController$Ol61cLuM2ad374yBqj6H6h0A_hY */
            public final /* synthetic */ Collator f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return InputMethodPreferenceController.lambda$updateInputMethodPreferenceViews$0(this.f$0, (InputMethodPreference) obj, (InputMethodPreference) obj2);
            }
        });
        this.mScreen.removeAll();
        for (int i2 = 0; i2 < size; i2++) {
            InputMethodPreference inputMethodPreference2 = (InputMethodPreference) arrayList.get(i2);
            inputMethodPreference2.setOrder(i2);
            this.mScreen.addPreference(inputMethodPreference2);
            inputMethodPreference2.updatePreferenceViews();
        }
        this.mScreen.addPreference(this.mPreference);
    }
}
