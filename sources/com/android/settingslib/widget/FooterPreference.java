package com.android.settingslib.widget;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.R$attr;
import com.android.settingslib.R$drawable;

public class FooterPreference extends Preference {
    public FooterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.footerPreferenceStyle, 16842894));
        init();
    }

    public FooterPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        textView.setMovementMethod(new LinkMovementMethod());
        textView.setClickable(false);
        textView.setLongClickable(false);
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        setTitle(charSequence);
    }

    @Override // androidx.preference.Preference
    public void setSummary(int i) {
        setTitle(i);
    }

    @Override // androidx.preference.Preference
    public CharSequence getSummary() {
        return getTitle();
    }

    private void init() {
        if (getIcon() == null) {
            setIcon(R$drawable.ic_info_outline_24);
        }
        setOrder(2147483646);
        if (TextUtils.isEmpty(getKey())) {
            setKey("footer_preference");
        }
    }

    public static class Builder {
        private Context mContext;
        private String mKey;
        private CharSequence mTitle;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public Builder setTitle(int i) {
            this.mTitle = this.mContext.getText(i);
            return this;
        }

        public FooterPreference build() {
            FooterPreference footerPreference = new FooterPreference(this.mContext);
            footerPreference.setSelectable(false);
            if (!TextUtils.isEmpty(this.mTitle)) {
                footerPreference.setTitle(this.mTitle);
                if (!TextUtils.isEmpty(this.mKey)) {
                    footerPreference.setKey(this.mKey);
                }
                return footerPreference;
            }
            throw new IllegalArgumentException("Footer title cannot be empty!");
        }
    }
}
