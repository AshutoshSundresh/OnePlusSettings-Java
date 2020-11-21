package com.android.settings.applications;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.accessibility.ListDialogPreference;

public class AppDomainsPreference extends ListDialogPreference {
    private int mNumEntries;

    public AppDomainsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(C0012R$layout.app_domains_dialog);
        setListItemLayoutResource(C0012R$layout.app_domains_item);
    }

    @Override // com.android.settings.accessibility.ListDialogPreference
    public void setTitles(CharSequence[] charSequenceArr) {
        this.mNumEntries = charSequenceArr != null ? charSequenceArr.length : 0;
        super.setTitles(charSequenceArr);
    }

    @Override // com.android.settings.accessibility.ListDialogPreference, androidx.preference.Preference
    public CharSequence getSummary() {
        int i;
        Context context = getContext();
        if (this.mNumEntries == 0) {
            return context.getString(C0017R$string.domain_urls_summary_none);
        }
        CharSequence summary = super.getSummary();
        if (this.mNumEntries == 1) {
            i = C0017R$string.domain_urls_summary_one;
        } else {
            i = C0017R$string.domain_urls_summary_some;
        }
        return context.getString(i, summary);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ListDialogPreference
    public void onBindListItem(View view, int i) {
        CharSequence titleAt = getTitleAt(i);
        if (titleAt != null) {
            ((TextView) view.findViewById(C0010R$id.domain_name)).setText(titleAt);
        }
    }
}
