package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;

public class AddPreference extends RestrictedPreference implements View.OnClickListener {
    private View mAddWidget;
    private OnAddClickListener mListener;
    private View mWidgetFrame;

    public interface OnAddClickListener {
        void onAddClick(AddPreference addPreference);
    }

    public AddPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public int getAddWidgetResId() {
        return C0010R$id.add_preference_widget;
    }

    public void setOnAddClickListener(OnAddClickListener onAddClickListener) {
        this.mListener = onAddClickListener;
        View view = this.mWidgetFrame;
        if (view != null) {
            view.setVisibility(shouldHideSecondTarget() ? 8 : 0);
        }
    }

    public void setAddWidgetEnabled(boolean z) {
        View view = this.mAddWidget;
        if (view != null) {
            view.setEnabled(z);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public int getSecondTargetResId() {
        return C0012R$layout.preference_widget_add;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public boolean shouldHideSecondTarget() {
        return this.mListener == null;
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mWidgetFrame = preferenceViewHolder.findViewById(16908312);
        View findViewById = preferenceViewHolder.findViewById(getAddWidgetResId());
        this.mAddWidget = findViewById;
        findViewById.setEnabled(true);
        this.mAddWidget.setOnClickListener(this);
    }

    public void onClick(View view) {
        OnAddClickListener onAddClickListener;
        if (view.getId() == getAddWidgetResId() && (onAddClickListener = this.mListener) != null) {
            onAddClickListener.onAddClick(this);
        }
    }
}
