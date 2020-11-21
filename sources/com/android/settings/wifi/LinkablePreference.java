package com.android.settings.wifi;

import android.content.Context;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.LinkifyUtils;
import com.android.settingslib.R$attr;

public class LinkablePreference extends Preference {
    private LinkifyUtils.OnClickListener mClickListener;
    private CharSequence mContentDescription;
    private CharSequence mContentTitle;

    public LinkablePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setSelectable(false);
    }

    public LinkablePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.footerPreferenceStyle, 16842894));
    }

    public LinkablePreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908310);
        if (textView != null) {
            textView.setSingleLine(false);
            if (this.mContentTitle != null && this.mClickListener != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.mContentTitle);
                if (this.mContentDescription != null) {
                    sb.append("\n\n");
                    sb.append(this.mContentDescription);
                }
                if (LinkifyUtils.linkify(textView, sb, this.mClickListener) && this.mContentTitle != null) {
                    Spannable spannable = (Spannable) textView.getText();
                    spannable.setSpan(new TextAppearanceSpan(getContext(), 16973894), 0, this.mContentTitle.length(), 17);
                    textView.setText(spannable);
                    textView.setMovementMethod(new LinkMovementMethod());
                }
            }
        }
    }

    public void setText(CharSequence charSequence, CharSequence charSequence2, LinkifyUtils.OnClickListener onClickListener) {
        this.mContentTitle = charSequence;
        this.mContentDescription = charSequence2;
        this.mClickListener = onClickListener;
        super.setTitle(charSequence);
    }

    @Override // androidx.preference.Preference
    public void setTitle(int i) {
        this.mContentTitle = null;
        this.mContentDescription = null;
        super.setTitle(i);
    }

    @Override // androidx.preference.Preference
    public void setTitle(CharSequence charSequence) {
        this.mContentTitle = null;
        this.mContentDescription = null;
        super.setTitle(charSequence);
    }
}
