package com.android.settings.fuelgauge.batterysaver;

import android.text.Annotation;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.widget.FooterPreference;

public class BatterySaverSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.battery_saver_settings);
    private SpannableStringBuilder mFooterText;
    private String mHelpUri;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BatterySaverSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 52;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        setupFooter();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.battery_saver_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_battery_saver_settings;
    }

    /* access modifiers changed from: package-private */
    public void setupFooter() {
        this.mFooterText = new SpannableStringBuilder(getText(17039753));
        String string = getString(C0017R$string.help_url_battery_saver_settings);
        this.mHelpUri = string;
        if (!TextUtils.isEmpty(string)) {
            addHelpLink();
        }
    }

    /* access modifiers changed from: package-private */
    public void addHelpLink() {
        FooterPreference footerPreference = (FooterPreference) getPreferenceScreen().findPreference("battery_saver_footer_preference");
        if (footerPreference != null) {
            SupportPageLearnMoreSpan.linkify(this.mFooterText, this, this.mHelpUri);
            footerPreference.setTitle(this.mFooterText);
        }
    }

    public static class SupportPageLearnMoreSpan extends URLSpan {
        private final Fragment mFragment;
        private final String mUriString;

        public SupportPageLearnMoreSpan(Fragment fragment, String str) {
            super("");
            this.mFragment = fragment;
            this.mUriString = str;
        }

        public void onClick(View view) {
            Fragment fragment = this.mFragment;
            if (fragment != null) {
                fragment.startActivityForResult(HelpUtils.getHelpIntent(fragment.getContext(), this.mUriString, ""), 0);
            }
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }

        public static CharSequence linkify(Spannable spannable, Fragment fragment, String str) {
            Annotation[] annotationArr = (Annotation[]) spannable.getSpans(0, spannable.length(), Annotation.class);
            for (Annotation annotation : annotationArr) {
                int spanStart = spannable.getSpanStart(annotation);
                int spanEnd = spannable.getSpanEnd(annotation);
                if ("url".equals(annotation.getValue())) {
                    Object supportPageLearnMoreSpan = new SupportPageLearnMoreSpan(fragment, str);
                    spannable.removeSpan(annotation);
                    spannable.setSpan(supportPageLearnMoreSpan, spanStart, spanEnd, 33);
                }
            }
            return spannable;
        }
    }
}
