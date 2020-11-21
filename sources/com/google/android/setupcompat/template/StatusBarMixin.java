package com.google.android.setupcompat.template;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.R$id;
import com.google.android.setupcompat.R$styleable;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.view.StatusBarBackgroundLayout;

public class StatusBarMixin implements Mixin {
    private final View decorView;
    private LinearLayout linearLayout;
    private final PartnerCustomizationLayout partnerCustomizationLayout;
    private StatusBarBackgroundLayout statusBarLayout;

    public StatusBarMixin(PartnerCustomizationLayout partnerCustomizationLayout2, Window window, AttributeSet attributeSet, int i) {
        this.partnerCustomizationLayout = partnerCustomizationLayout2;
        View findManagedViewById = partnerCustomizationLayout2.findManagedViewById(R$id.suc_layout_status);
        if (findManagedViewById != null) {
            if (findManagedViewById instanceof StatusBarBackgroundLayout) {
                this.statusBarLayout = (StatusBarBackgroundLayout) findManagedViewById;
            } else {
                this.linearLayout = (LinearLayout) findManagedViewById;
            }
            this.decorView = window.getDecorView();
            if (Build.VERSION.SDK_INT >= 23) {
                window.setStatusBarColor(0);
                TypedArray obtainStyledAttributes = partnerCustomizationLayout2.getContext().obtainStyledAttributes(attributeSet, R$styleable.SucStatusBarMixin, i, 0);
                setLightStatusBar(obtainStyledAttributes.getBoolean(R$styleable.SucStatusBarMixin_sucLightStatusBar, isLightStatusBar()));
                setStatusBarBackground(obtainStyledAttributes.getDrawable(R$styleable.SucStatusBarMixin_sucStatusBarBackground));
                obtainStyledAttributes.recycle();
                return;
            }
            return;
        }
        throw new NullPointerException("sucLayoutStatus cannot be null in StatusBarMixin");
    }

    public void setStatusBarBackground(Drawable drawable) {
        if (this.partnerCustomizationLayout.shouldApplyPartnerResource()) {
            Context context = this.partnerCustomizationLayout.getContext();
            drawable = PartnerConfigHelper.get(context).getDrawable(context, PartnerConfig.CONFIG_STATUS_BAR_BACKGROUND);
        }
        StatusBarBackgroundLayout statusBarBackgroundLayout = this.statusBarLayout;
        if (statusBarBackgroundLayout == null) {
            this.linearLayout.setBackgroundDrawable(drawable);
        } else {
            statusBarBackgroundLayout.setStatusBarBackground(drawable);
        }
    }

    public void setLightStatusBar(boolean z) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.partnerCustomizationLayout.shouldApplyPartnerResource()) {
                Context context = this.partnerCustomizationLayout.getContext();
                z = PartnerConfigHelper.get(context).getBoolean(context, PartnerConfig.CONFIG_LIGHT_STATUS_BAR, false);
            }
            if (z) {
                View view = this.decorView;
                view.setSystemUiVisibility(view.getSystemUiVisibility() | 8192);
                return;
            }
            View view2 = this.decorView;
            view2.setSystemUiVisibility(view2.getSystemUiVisibility() & -8193);
        }
    }

    public boolean isLightStatusBar() {
        if (Build.VERSION.SDK_INT < 23 || (this.decorView.getSystemUiVisibility() & 8192) == 8192) {
            return true;
        }
        return false;
    }
}
