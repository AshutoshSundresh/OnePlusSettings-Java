package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.hardware.display.ColorDisplayManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;

public class ColorModePreferenceFragment extends RadioButtonPickerFragment {
    static final String KEY_COLOR_MODE_AUTOMATIC = "color_mode_automatic";
    static final String KEY_COLOR_MODE_BOOSTED = "color_mode_boosted";
    static final String KEY_COLOR_MODE_NATURAL = "color_mode_natural";
    static final String KEY_COLOR_MODE_SATURATED = "color_mode_saturated";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.color_mode_settings) {
        /* class com.android.settings.display.ColorModePreferenceFragment.AnonymousClass2 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            int[] intArray = context.getResources().getIntArray(17235992);
            return intArray != null && intArray.length > 0 && !ColorDisplayManager.areAccessibilityTransformsEnabled(context);
        }
    };
    private ColorDisplayManager mColorDisplayManager;
    private ContentObserver mContentObserver;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1143;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            /* class com.android.settings.display.ColorModePreferenceFragment.AnonymousClass1 */

            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                if (ColorDisplayManager.areAccessibilityTransformsEnabled(ColorModePreferenceFragment.this.getContext())) {
                    ColorModePreferenceFragment.this.getActivity().finish();
                }
            }
        };
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), false, this.mContentObserver, this.mUserId);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled"), false, this.mContentObserver, this.mUserId);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        if (this.mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.color_mode_settings;
    }

    /* access modifiers changed from: package-private */
    public void configureAndInstallPreview(LayoutPreference layoutPreference, PreferenceScreen preferenceScreen) {
        layoutPreference.setSelectable(false);
        preferenceScreen.addPreference(layoutPreference);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void addStaticPreferences(PreferenceScreen preferenceScreen) {
        configureAndInstallPreview(new LayoutPreference(preferenceScreen.getContext(), C0012R$layout.color_mode_preview), preferenceScreen);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        int[] intArray = context.getResources().getIntArray(17235992);
        ArrayList arrayList = new ArrayList();
        if (intArray != null) {
            for (int i : intArray) {
                if (i == 0) {
                    arrayList.add(new ColorModeCandidateInfo(context.getText(C0017R$string.color_mode_option_natural), KEY_COLOR_MODE_NATURAL, true));
                } else if (i == 1) {
                    arrayList.add(new ColorModeCandidateInfo(context.getText(C0017R$string.color_mode_option_boosted), KEY_COLOR_MODE_BOOSTED, true));
                } else if (i == 2) {
                    arrayList.add(new ColorModeCandidateInfo(context.getText(C0017R$string.color_mode_option_saturated), KEY_COLOR_MODE_SATURATED, true));
                } else if (i == 3) {
                    arrayList.add(new ColorModeCandidateInfo(context.getText(C0017R$string.color_mode_option_automatic), KEY_COLOR_MODE_AUTOMATIC, true));
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        int colorMode = this.mColorDisplayManager.getColorMode();
        if (colorMode == 3) {
            return KEY_COLOR_MODE_AUTOMATIC;
        }
        if (colorMode == 2) {
            return KEY_COLOR_MODE_SATURATED;
        }
        return colorMode == 1 ? KEY_COLOR_MODE_BOOSTED : KEY_COLOR_MODE_NATURAL;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        char c;
        switch (str.hashCode()) {
            case -2029194174:
                if (str.equals(KEY_COLOR_MODE_BOOSTED)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -739564821:
                if (str.equals(KEY_COLOR_MODE_AUTOMATIC)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -365217559:
                if (str.equals(KEY_COLOR_MODE_NATURAL)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 765917269:
                if (str.equals(KEY_COLOR_MODE_SATURATED)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mColorDisplayManager.setColorMode(0);
        } else if (c == 1) {
            this.mColorDisplayManager.setColorMode(1);
        } else if (c == 2) {
            this.mColorDisplayManager.setColorMode(2);
        } else if (c == 3) {
            this.mColorDisplayManager.setColorMode(3);
        }
        return true;
    }

    static class ColorModeCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        ColorModeCandidateInfo(CharSequence charSequence, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mKey = str;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mLabel;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }
    }
}
