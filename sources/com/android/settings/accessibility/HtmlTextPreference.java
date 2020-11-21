package com.android.settings.accessibility;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;

public final class HtmlTextPreference extends StaticTextPreference {
    private int mFlag = 63;
    private Html.ImageGetter mImageGetter;
    private Html.TagHandler mTagHandler;

    HtmlTextPreference(Context context) {
        super(context);
    }

    @Override // com.android.settings.accessibility.StaticTextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908304);
        if (textView != null && !TextUtils.isEmpty(getSummary())) {
            textView.setText(Html.fromHtml(getSummary().toString(), this.mFlag, this.mImageGetter, this.mTagHandler));
        }
    }

    public void setImageGetter(Html.ImageGetter imageGetter) {
        if (imageGetter != null && !imageGetter.equals(this.mImageGetter)) {
            this.mImageGetter = imageGetter;
            notifyChanged();
        }
    }
}
