package com.android.settings.widget;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.emptyview.EmptyPageView;

public abstract class EmptyTextSettings extends SettingsPreferenceFragment {
    private EmptyPageView mEmpty;

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((ViewGroup) view.findViewById(16908351)).setPadding(0, getResources().getDimensionPixelSize(C0007R$dimen.op_control_margin_space4), 0, 0);
        EmptyPageView emptyPageView = (EmptyPageView) getActivity().findViewById(16908292);
        this.mEmpty = emptyPageView;
        emptyPageView.getEmptyTextView().setText(C0017R$string.user_credential_none_installed);
        this.mEmpty.getEmptyImageView().setImageResource(C0008R$drawable.op_empty);
        setEmptyView(this.mEmpty);
    }

    /* access modifiers changed from: protected */
    public void setEmptyText(int i) {
        this.mEmpty.getEmptyTextView().setText(i);
    }
}
