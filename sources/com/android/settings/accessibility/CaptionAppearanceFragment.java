package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.widget.SubtitleView;
import com.android.settings.C0003R$array;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.accessibility.ListDialogPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CaptionAppearanceFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, ListDialogPreference.OnValueChangedListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.captioning_appearance);
    private ColorPreference mBackgroundColor;
    private ColorPreference mBackgroundOpacity;
    private CaptioningManager mCaptioningManager;
    private PreferenceCategory mCustom;
    private ColorPreference mEdgeColor;
    private EdgeTypePreference mEdgeType;
    private ListPreference mFontSize;
    private ColorPreference mForegroundColor;
    private ColorPreference mForegroundOpacity;
    private final View.OnLayoutChangeListener mLayoutChangeListener = new View.OnLayoutChangeListener() {
        /* class com.android.settings.accessibility.CaptionAppearanceFragment.AnonymousClass1 */

        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            CaptionAppearanceFragment.this.mPreviewViewport.removeOnLayoutChangeListener(this);
            CaptionAppearanceFragment.this.refreshPreviewText();
        }
    };
    private final List<Preference> mPreferenceList = new ArrayList();
    private PresetPreference mPreset;
    private SubtitleView mPreviewText;
    private View mPreviewViewport;
    private View mPreviewWindow;
    private boolean mShowingCustom;
    private ListPreference mTypeface;
    private ColorPreference mWindowColor;
    private ColorPreference mWindowOpacity;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1819;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        addPreferencesFromResource(C0019R$xml.captioning_appearance);
        initializeAllPreferences();
        updateAllPreferences();
        refreshShowingCustom();
        installUpdateListeners();
        refreshPreviewText();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshPreviewText() {
        SubtitleView subtitleView;
        FragmentActivity activity = getActivity();
        if (activity != null && (subtitleView = this.mPreviewText) != null) {
            applyCaptionProperties(this.mCaptioningManager, subtitleView, this.mPreviewViewport, this.mCaptioningManager.getRawUserStyle());
            Locale locale = this.mCaptioningManager.getLocale();
            if (locale != null) {
                subtitleView.setText(AccessibilityUtils.getTextForLocale(activity, locale, C0017R$string.captioning_preview_text));
            } else {
                subtitleView.setText(C0017R$string.captioning_preview_text);
            }
            CaptioningManager.CaptionStyle userStyle = this.mCaptioningManager.getUserStyle();
            if (userStyle.hasWindowColor()) {
                this.mPreviewWindow.setBackgroundColor(userStyle.windowColor);
            } else {
                this.mPreviewWindow.setBackgroundColor(CaptioningManager.CaptionStyle.DEFAULT.windowColor);
            }
        }
    }

    public static void applyCaptionProperties(CaptioningManager captioningManager, SubtitleView subtitleView, View view, int i) {
        subtitleView.setStyle(i);
        Context context = subtitleView.getContext();
        context.getContentResolver();
        float fontScale = captioningManager.getFontScale();
        if (view != null) {
            subtitleView.setTextSize((((float) Math.max(view.getWidth() * 9, view.getHeight() * 16)) / 16.0f) * 0.0533f * fontScale);
        } else {
            subtitleView.setTextSize(context.getResources().getDimension(C0007R$dimen.caption_preview_text_size) * fontScale);
        }
        Locale locale = captioningManager.getLocale();
        if (locale != null) {
            subtitleView.setText(AccessibilityUtils.getTextForLocale(context, locale, C0017R$string.captioning_preview_characters));
        } else {
            subtitleView.setText(C0017R$string.captioning_preview_characters);
        }
    }

    private void initializeAllPreferences() {
        LayoutPreference layoutPreference = (LayoutPreference) findPreference("caption_preview");
        this.mPreviewText = layoutPreference.findViewById(C0010R$id.preview_text);
        this.mPreviewWindow = layoutPreference.findViewById(C0010R$id.preview_window);
        View findViewById = layoutPreference.findViewById(C0010R$id.preview_viewport);
        this.mPreviewViewport = findViewById;
        findViewById.addOnLayoutChangeListener(this.mLayoutChangeListener);
        Resources resources = getResources();
        int[] intArray = resources.getIntArray(C0003R$array.captioning_preset_selector_values);
        String[] stringArray = resources.getStringArray(C0003R$array.captioning_preset_selector_titles);
        PresetPreference presetPreference = (PresetPreference) findPreference("captioning_preset");
        this.mPreset = presetPreference;
        presetPreference.setValues(intArray);
        this.mPreset.setTitles(stringArray);
        ListPreference listPreference = (ListPreference) findPreference("captioning_font_size");
        this.mFontSize = listPreference;
        this.mPreferenceList.add(listPreference);
        this.mPreferenceList.add(this.mPreset);
        this.mCustom = (PreferenceCategory) findPreference("custom");
        this.mShowingCustom = true;
        int[] intArray2 = resources.getIntArray(C0003R$array.captioning_color_selector_values);
        String[] stringArray2 = resources.getStringArray(C0003R$array.captioning_color_selector_titles);
        ColorPreference colorPreference = (ColorPreference) this.mCustom.findPreference("captioning_foreground_color");
        this.mForegroundColor = colorPreference;
        colorPreference.setTitles(stringArray2);
        this.mForegroundColor.setValues(intArray2);
        int[] intArray3 = resources.getIntArray(C0003R$array.captioning_opacity_selector_values);
        String[] stringArray3 = resources.getStringArray(C0003R$array.captioning_opacity_selector_titles);
        ColorPreference colorPreference2 = (ColorPreference) this.mCustom.findPreference("captioning_foreground_opacity");
        this.mForegroundOpacity = colorPreference2;
        colorPreference2.setTitles(stringArray3);
        this.mForegroundOpacity.setValues(intArray3);
        ColorPreference colorPreference3 = (ColorPreference) this.mCustom.findPreference("captioning_edge_color");
        this.mEdgeColor = colorPreference3;
        colorPreference3.setTitles(stringArray2);
        this.mEdgeColor.setValues(intArray2);
        int[] iArr = new int[(intArray2.length + 1)];
        String[] strArr = new String[(stringArray2.length + 1)];
        System.arraycopy(intArray2, 0, iArr, 1, intArray2.length);
        System.arraycopy(stringArray2, 0, strArr, 1, stringArray2.length);
        iArr[0] = 0;
        strArr[0] = getString(C0017R$string.color_none);
        ColorPreference colorPreference4 = (ColorPreference) this.mCustom.findPreference("captioning_background_color");
        this.mBackgroundColor = colorPreference4;
        colorPreference4.setTitles(strArr);
        this.mBackgroundColor.setValues(iArr);
        ColorPreference colorPreference5 = (ColorPreference) this.mCustom.findPreference("captioning_background_opacity");
        this.mBackgroundOpacity = colorPreference5;
        colorPreference5.setTitles(stringArray3);
        this.mBackgroundOpacity.setValues(intArray3);
        ColorPreference colorPreference6 = (ColorPreference) this.mCustom.findPreference("captioning_window_color");
        this.mWindowColor = colorPreference6;
        colorPreference6.setTitles(strArr);
        this.mWindowColor.setValues(iArr);
        ColorPreference colorPreference7 = (ColorPreference) this.mCustom.findPreference("captioning_window_opacity");
        this.mWindowOpacity = colorPreference7;
        colorPreference7.setTitles(stringArray3);
        this.mWindowOpacity.setValues(intArray3);
        this.mEdgeType = (EdgeTypePreference) this.mCustom.findPreference("captioning_edge_type");
        this.mTypeface = (ListPreference) this.mCustom.findPreference("captioning_typeface");
    }

    private void installUpdateListeners() {
        this.mPreset.setOnValueChangedListener(this);
        this.mForegroundColor.setOnValueChangedListener(this);
        this.mForegroundOpacity.setOnValueChangedListener(this);
        this.mEdgeColor.setOnValueChangedListener(this);
        this.mBackgroundColor.setOnValueChangedListener(this);
        this.mBackgroundOpacity.setOnValueChangedListener(this);
        this.mWindowColor.setOnValueChangedListener(this);
        this.mWindowOpacity.setOnValueChangedListener(this);
        this.mEdgeType.setOnValueChangedListener(this);
        this.mTypeface.setOnPreferenceChangeListener(this);
        this.mFontSize.setOnPreferenceChangeListener(this);
    }

    private void updateAllPreferences() {
        this.mPreset.setValue(this.mCaptioningManager.getRawUserStyle());
        this.mFontSize.setValue(Float.toString(this.mCaptioningManager.getFontScale()));
        CaptioningManager.CaptionStyle customStyle = CaptioningManager.CaptionStyle.getCustomStyle(getContentResolver());
        this.mEdgeType.setValue(customStyle.edgeType);
        this.mEdgeColor.setValue(customStyle.edgeColor);
        int i = 16777215;
        parseColorOpacity(this.mForegroundColor, this.mForegroundOpacity, customStyle.hasForegroundColor() ? customStyle.foregroundColor : 16777215);
        parseColorOpacity(this.mBackgroundColor, this.mBackgroundOpacity, customStyle.hasBackgroundColor() ? customStyle.backgroundColor : 16777215);
        if (customStyle.hasWindowColor()) {
            i = customStyle.windowColor;
        }
        parseColorOpacity(this.mWindowColor, this.mWindowOpacity, i);
        String str = customStyle.mRawTypeface;
        ListPreference listPreference = this.mTypeface;
        if (str == null) {
            str = "";
        }
        listPreference.setValue(str);
    }

    private void parseColorOpacity(ColorPreference colorPreference, ColorPreference colorPreference2, int i) {
        int i2;
        int i3;
        if (!CaptioningManager.CaptionStyle.hasColor(i)) {
            i2 = (i & 255) << 24;
            i3 = 16777215;
        } else if ((i >>> 24) == 0) {
            i3 = 0;
            i2 = (i & 255) << 24;
        } else {
            i3 = i | -16777216;
            i2 = -16777216 & i;
        }
        colorPreference2.setValue(i2 | 16777215);
        colorPreference.setValue(i3);
    }

    private int mergeColorOpacity(ColorPreference colorPreference, ColorPreference colorPreference2) {
        int value = colorPreference.getValue();
        int value2 = colorPreference2.getValue();
        if (!CaptioningManager.CaptionStyle.hasColor(value)) {
            return 16776960 | Color.alpha(value2);
        }
        return value == 0 ? Color.alpha(value2) : (value & 16777215) | (value2 & -16777216);
    }

    private void refreshShowingCustom() {
        boolean z = this.mPreset.getValue() == -1;
        if (!z && this.mShowingCustom) {
            getPreferenceScreen().removePreference(this.mCustom);
            this.mShowingCustom = false;
        } else if (z && !this.mShowingCustom) {
            getPreferenceScreen().addPreference(this.mCustom);
            this.mShowingCustom = true;
        }
    }

    @Override // com.android.settings.accessibility.ListDialogPreference.OnValueChangedListener
    public void onValueChanged(ListDialogPreference listDialogPreference, int i) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mForegroundColor == listDialogPreference || this.mForegroundOpacity == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_foreground_color", mergeColorOpacity(this.mForegroundColor, this.mForegroundOpacity));
        } else if (this.mBackgroundColor == listDialogPreference || this.mBackgroundOpacity == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_background_color", mergeColorOpacity(this.mBackgroundColor, this.mBackgroundOpacity));
        } else if (this.mWindowColor == listDialogPreference || this.mWindowOpacity == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_window_color", mergeColorOpacity(this.mWindowColor, this.mWindowOpacity));
        } else if (this.mEdgeColor == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_edge_color", i);
        } else if (this.mPreset == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_preset", i);
            refreshShowingCustom();
        } else if (this.mEdgeType == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_edge_type", i);
        }
        refreshPreviewText();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mTypeface == preference) {
            Settings.Secure.putString(contentResolver, "accessibility_captioning_typeface", (String) obj);
            refreshPreviewText();
            return true;
        } else if (this.mFontSize != preference) {
            return true;
        } else {
            Settings.Secure.putFloat(contentResolver, "accessibility_captioning_font_scale", Float.parseFloat((String) obj));
            refreshPreviewText();
            return true;
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_caption;
    }
}
