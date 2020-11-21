package com.oneplus.settings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.oneplus.compat.util.OpThemeNative;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OPFontStyleSettings extends SettingsPreferenceFragment implements View.OnClickListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPFontStyleSettings.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            Resources resources = context.getResources();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            searchIndexableRaw.title = resources.getString(C0017R$string.oneplus_font_style);
            searchIndexableRaw.screenTitle = resources.getString(C0017R$string.oneplus_font_style);
            searchIndexableRaw.keywords = resources.getString(C0017R$string.oneplus_font_switch);
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }
    };
    private Context mContext;
    private View mSlateFont;
    private RadioButton mSlateFontButton;
    private View mSystemFont;
    private RadioButton mSystemFontButton;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getActivity() != null) {
            this.mContext = getActivity();
            getActivity().setTitle(C0017R$string.oneplus_font_style);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ViewGroup viewGroup2 = (ViewGroup) onCreateView.findViewById(16908351);
        viewGroup2.removeAllViews();
        View inflate = layoutInflater.inflate(C0012R$layout.op_font_style, viewGroup2, false);
        viewGroup2.addView(inflate);
        this.mSystemFontButton = (RadioButton) inflate.findViewById(C0010R$id.system_font_button);
        this.mSlateFontButton = (RadioButton) inflate.findViewById(C0010R$id.slate_font_button);
        this.mSystemFont = inflate.findViewById(C0010R$id.system_font);
        this.mSlateFont = inflate.findViewById(C0010R$id.slate_font);
        this.mSystemFont.setOnClickListener(this);
        this.mSlateFont.setOnClickListener(this);
        return onCreateView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        boolean z = true;
        int intForUser = Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0);
        this.mSystemFontButton.setChecked(intForUser == 1);
        RadioButton radioButton = this.mSlateFontButton;
        if (intForUser != 2) {
            z = false;
        }
        radioButton.setChecked(z);
    }

    public void onClick(View view) {
        if (view.getId() == C0010R$id.system_font) {
            if (Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0) != 1) {
                setFontStyle(1);
                this.mSystemFontButton.setChecked(true);
                this.mSlateFontButton.setChecked(false);
            }
        } else if (view.getId() == C0010R$id.slate_font && Settings.System.getIntForUser(getContentResolver(), "oem_font_mode", 1, 0) != 2) {
            setFontStyle(2);
            this.mSlateFontButton.setChecked(true);
            this.mSystemFontButton.setChecked(false);
        }
    }

    private void setFontStyle(final int i) {
        new Thread(new Runnable() {
            /* class com.oneplus.settings.OPFontStyleSettings.AnonymousClass1 */

            public void run() {
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Settings.System.putInt(OPFontStyleSettings.this.getContentResolver(), "oem_font_mode", i);
                HashMap hashMap = new HashMap();
                hashMap.put("oneplus_dynamicfont", String.valueOf(i));
                OpThemeNative.enableTheme(OPFontStyleSettings.this.mContext, hashMap);
            }
        }).start();
    }
}
