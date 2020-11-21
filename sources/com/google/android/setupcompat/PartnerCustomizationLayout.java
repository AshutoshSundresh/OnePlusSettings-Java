package com.google.android.setupcompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.setupcompat.internal.LifecycleFragment;
import com.google.android.setupcompat.internal.PersistableBundles;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.logging.CustomEvent;
import com.google.android.setupcompat.logging.MetricKey;
import com.google.android.setupcompat.logging.SetupMetricsLogger;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupcompat.template.SystemNavBarMixin;
import com.google.android.setupcompat.util.WizardManagerHelper;

public class PartnerCustomizationLayout extends TemplateLayout {
    private Activity activity;
    private boolean usePartnerResourceAttr;

    /* access modifiers changed from: protected */
    public boolean enablePartnerResourceLoading() {
        return true;
    }

    public PartnerCustomizationLayout(Context context) {
        this(context, 0, 0);
    }

    public PartnerCustomizationLayout(Context context, int i) {
        this(context, i, 0);
    }

    public PartnerCustomizationLayout(Context context, int i, int i2) {
        super(context, i, i2);
        init(null, R$attr.sucLayoutTheme);
    }

    public PartnerCustomizationLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, R$attr.sucLayoutTheme);
    }

    @TargetApi(11)
    public PartnerCustomizationLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        int i2 = Build.VERSION.SDK_INT;
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SucPartnerCustomizationLayout, i, 0);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.SucPartnerCustomizationLayout_sucLayoutFullscreen, true);
        obtainStyledAttributes.recycle();
        if (i2 >= 21 && z) {
            setSystemUiVisibility(1024);
        }
        registerMixin(StatusBarMixin.class, new StatusBarMixin(this, this.activity.getWindow(), attributeSet, i));
        registerMixin(SystemNavBarMixin.class, new SystemNavBarMixin(this, this.activity.getWindow()));
        registerMixin(FooterBarMixin.class, new FooterBarMixin(this, attributeSet, i));
        ((SystemNavBarMixin) getMixin(SystemNavBarMixin.class)).applyPartnerCustomizations(attributeSet, i);
        if (i2 >= 21) {
            this.activity.getWindow().addFlags(Integer.MIN_VALUE);
            this.activity.getWindow().clearFlags(67108864);
            this.activity.getWindow().clearFlags(134217728);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.partner_customization_layout;
        }
        return inflateTemplate(layoutInflater, 0, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public void onBeforeTemplateInflated(AttributeSet attributeSet, int i) {
        boolean z = true;
        this.usePartnerResourceAttr = true;
        Activity lookupActivityFromContext = lookupActivityFromContext(getContext());
        this.activity = lookupActivityFromContext;
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(lookupActivityFromContext.getIntent());
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SucPartnerCustomizationLayout, i, 0);
        if (!obtainStyledAttributes.hasValue(R$styleable.SucPartnerCustomizationLayout_sucUsePartnerResource)) {
            Log.e("PartnerCustomizedLayout", "Attribute sucUsePartnerResource not found in " + this.activity.getComponentName());
        }
        if (!isAnySetupWizard && !obtainStyledAttributes.getBoolean(R$styleable.SucPartnerCustomizationLayout_sucUsePartnerResource, true)) {
            z = false;
        }
        this.usePartnerResourceAttr = z;
        obtainStyledAttributes.recycle();
        if (Log.isLoggable("PartnerCustomizedLayout", 3)) {
            Log.d("PartnerCustomizedLayout", "activity=" + this.activity.getClass().getSimpleName() + " isSetupFlow=" + isAnySetupWizard + " enablePartnerResourceLoading=" + enablePartnerResourceLoading() + " usePartnerResourceAttr=" + this.usePartnerResourceAttr);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.suc_layout_content;
        }
        return super.findContainer(i);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        LifecycleFragment.attachNow(this.activity);
        ((FooterBarMixin) getMixin(FooterBarMixin.class)).onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        PersistableBundle persistableBundle;
        PersistableBundle persistableBundle2;
        super.onDetachedFromWindow();
        int i = Build.VERSION.SDK_INT;
        if (i >= 21 && i >= 29 && WizardManagerHelper.isAnySetupWizard(this.activity.getIntent())) {
            FooterBarMixin footerBarMixin = (FooterBarMixin) getMixin(FooterBarMixin.class);
            footerBarMixin.onDetachedFromWindow();
            FooterButton primaryButton = footerBarMixin.getPrimaryButton();
            FooterButton secondaryButton = footerBarMixin.getSecondaryButton();
            if (primaryButton != null) {
                persistableBundle = primaryButton.getMetrics("PrimaryFooterButton");
            } else {
                persistableBundle = PersistableBundle.EMPTY;
            }
            if (secondaryButton != null) {
                persistableBundle2 = secondaryButton.getMetrics("SecondaryFooterButton");
            } else {
                persistableBundle2 = PersistableBundle.EMPTY;
            }
            SetupMetricsLogger.logCustomEvent(getContext(), CustomEvent.create(MetricKey.get("SetupCompatMetrics", this.activity), PersistableBundles.mergeBundles(footerBarMixin.getLoggingMetrics(), persistableBundle, persistableBundle2)));
        }
    }

    private static Activity lookupActivityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return lookupActivityFromContext(((ContextWrapper) context).getBaseContext());
        }
        throw new IllegalArgumentException("Cannot find instance of Activity in parent tree");
    }

    public boolean shouldApplyPartnerResource() {
        if (enablePartnerResourceLoading() && this.usePartnerResourceAttr && Build.VERSION.SDK_INT >= 29 && PartnerConfigHelper.get(getContext()).isAvailable()) {
            return true;
        }
        return false;
    }
}
