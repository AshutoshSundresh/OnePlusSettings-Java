package com.android.settings.wifi.dpp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0018R$style;
import com.android.settings.core.InstrumentedFragment;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;

public abstract class WifiDppQrCodeBaseFragment extends InstrumentedFragment {
    protected GlifLayout mGlifLayout;
    protected boolean mIsInSetupWizard;
    protected FooterButton mLeftButton;
    protected FooterButton mRightButton;
    protected TextView mSummary;

    /* access modifiers changed from: protected */
    public abstract boolean isFooterAvailable();

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIsInSetupWizard = WizardManagerHelper.isAnySetupWizard(getActivity().getIntent());
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mSummary = (TextView) view.findViewById(16908304);
        if (view instanceof GlifLayout) {
            this.mGlifLayout = (GlifLayout) view;
            if (isFooterAvailable()) {
                FooterButton.Builder builder = new FooterButton.Builder(getContext());
                builder.setButtonType(2);
                builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
                this.mLeftButton = builder.build();
                ((FooterBarMixin) this.mGlifLayout.getMixin(FooterBarMixin.class)).setSecondaryButton(this.mLeftButton);
                FooterButton.Builder builder2 = new FooterButton.Builder(getContext());
                builder2.setButtonType(5);
                builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
                this.mRightButton = builder2.build();
                ((FooterBarMixin) this.mGlifLayout.getMixin(FooterBarMixin.class)).setPrimaryButton(this.mRightButton);
            }
            this.mGlifLayout.getHeaderTextView().setAccessibilityLiveRegion(1);
            if (!this.mIsInSetupWizard) {
                this.mGlifLayout.setIcon(null);
                ((StatusBarMixin) this.mGlifLayout.getMixin(StatusBarMixin.class)).setStatusBarBackground(null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setHeaderIconImageResource(int i) {
        this.mGlifLayout.setIcon(getDrawable(i));
    }

    private Drawable getDrawable(int i) {
        try {
            return getContext().getDrawable(i);
        } catch (Resources.NotFoundException unused) {
            Log.e("WifiDppQrCodeBaseFragment", "Resource does not exist: " + i);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void setHeaderTitle(String str) {
        if (this.mIsInSetupWizard) {
            this.mGlifLayout.setHeaderText(str);
        }
    }

    /* access modifiers changed from: protected */
    public void setHeaderTitle(int i, Object... objArr) {
        if (this.mIsInSetupWizard) {
            this.mGlifLayout.setHeaderText(getString(i, objArr));
        }
    }

    /* access modifiers changed from: protected */
    public void setProgressBarShown(boolean z) {
        this.mGlifLayout.setProgressBarShown(z);
    }
}
