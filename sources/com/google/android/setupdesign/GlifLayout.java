package com.google.android.setupdesign;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.StatusBarMixin;
import com.google.android.setupdesign.template.HeaderMixin;
import com.google.android.setupdesign.template.IconMixin;
import com.google.android.setupdesign.template.ProgressBarMixin;
import com.google.android.setupdesign.template.RequireScrollMixin;
import com.google.android.setupdesign.template.ScrollViewScrollHandlingDelegate;
import com.google.android.setupdesign.util.DescriptionStyler;

public class GlifLayout extends PartnerCustomizationLayout {
    private boolean applyPartnerHeavyThemeResource;
    private ColorStateList backgroundBaseColor;
    private boolean backgroundPatterned;
    private ColorStateList primaryColor;

    public GlifLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifLayout(Context context, int i) {
        this(context, i, 0);
    }

    public GlifLayout(Context context, int i, int i2) {
        super(context, i, i2);
        this.backgroundPatterned = true;
        this.applyPartnerHeavyThemeResource = false;
        init(null, R$attr.sudLayoutTheme);
    }

    public GlifLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.backgroundPatterned = true;
        this.applyPartnerHeavyThemeResource = false;
        init(attributeSet, R$attr.sudLayoutTheme);
    }

    @TargetApi(11)
    public GlifLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.backgroundPatterned = true;
        this.applyPartnerHeavyThemeResource = false;
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SudGlifLayout, i, 0);
        this.applyPartnerHeavyThemeResource = shouldApplyPartnerResource() && obtainStyledAttributes.getBoolean(R$styleable.SudGlifLayout_sudUsePartnerHeavyTheme, false);
        registerMixin(HeaderMixin.class, new HeaderMixin(this, attributeSet, i));
        registerMixin(IconMixin.class, new IconMixin(this, attributeSet, i));
        registerMixin(ProgressBarMixin.class, new ProgressBarMixin(this));
        RequireScrollMixin requireScrollMixin = new RequireScrollMixin(this);
        registerMixin(RequireScrollMixin.class, requireScrollMixin);
        ScrollView scrollView = getScrollView();
        if (scrollView != null) {
            requireScrollMixin.setScrollHandlingDelegate(new ScrollViewScrollHandlingDelegate(requireScrollMixin, scrollView));
        }
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.SudGlifLayout_sudColorPrimary);
        if (colorStateList != null) {
            setPrimaryColor(colorStateList);
        }
        if (this.applyPartnerHeavyThemeResource) {
            updateContentBackgroundColorWithPartnerConfig();
        }
        setBackgroundBaseColor(obtainStyledAttributes.getColorStateList(R$styleable.SudGlifLayout_sudBackgroundBaseColor));
        setBackgroundPatterned(obtainStyledAttributes.getBoolean(R$styleable.SudGlifLayout_sudBackgroundPatterned, true));
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SudGlifLayout_sudStickyHeader, 0);
        if (resourceId != 0) {
            inflateStickyHeader(resourceId);
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        ((IconMixin) getMixin(IconMixin.class)).tryApplyPartnerCustomizationStyle();
        ((HeaderMixin) getMixin(HeaderMixin.class)).tryApplyPartnerCustomizationStyle();
        tryApplyPartnerCustomizationStyleToShortDescription();
    }

    private void tryApplyPartnerCustomizationStyleToShortDescription() {
        TextView textView;
        if (this.applyPartnerHeavyThemeResource && (textView = (TextView) findManagedViewById(R$id.sud_layout_description)) != null) {
            DescriptionStyler.applyPartnerCustomizationStyle(textView);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupcompat.PartnerCustomizationLayout
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R$layout.sud_glif_template;
        }
        return inflateTemplate(layoutInflater, R$style.SudThemeGlif_Light, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupcompat.internal.TemplateLayout, com.google.android.setupcompat.PartnerCustomizationLayout
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R$id.sud_layout_content;
        }
        return super.findContainer(i);
    }

    public View inflateStickyHeader(int i) {
        ViewStub viewStub = (ViewStub) findManagedViewById(R$id.sud_layout_sticky_header);
        viewStub.setLayoutResource(i);
        return viewStub.inflate();
    }

    public ScrollView getScrollView() {
        View findManagedViewById = findManagedViewById(R$id.sud_scroll_view);
        if (findManagedViewById instanceof ScrollView) {
            return (ScrollView) findManagedViewById;
        }
        return null;
    }

    public TextView getHeaderTextView() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getTextView();
    }

    public void setHeaderText(int i) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(i);
    }

    public void setHeaderText(CharSequence charSequence) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(charSequence);
    }

    public CharSequence getHeaderText() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getText();
    }

    public void setHeaderColor(ColorStateList colorStateList) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setTextColor(colorStateList);
    }

    public ColorStateList getHeaderColor() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getTextColor();
    }

    public void setIcon(Drawable drawable) {
        ((IconMixin) getMixin(IconMixin.class)).setIcon(drawable);
    }

    public Drawable getIcon() {
        return ((IconMixin) getMixin(IconMixin.class)).getIcon();
    }

    public void setPrimaryColor(ColorStateList colorStateList) {
        this.primaryColor = colorStateList;
        updateBackground();
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setColor(colorStateList);
    }

    public ColorStateList getPrimaryColor() {
        return this.primaryColor;
    }

    public void setBackgroundBaseColor(ColorStateList colorStateList) {
        this.backgroundBaseColor = colorStateList;
        updateBackground();
    }

    public ColorStateList getBackgroundBaseColor() {
        return this.backgroundBaseColor;
    }

    public void setBackgroundPatterned(boolean z) {
        this.backgroundPatterned = z;
        updateBackground();
    }

    private void updateBackground() {
        Drawable drawable;
        if (findManagedViewById(R$id.suc_layout_status) != null) {
            int i = 0;
            ColorStateList colorStateList = this.backgroundBaseColor;
            if (colorStateList != null) {
                i = colorStateList.getDefaultColor();
            } else {
                ColorStateList colorStateList2 = this.primaryColor;
                if (colorStateList2 != null) {
                    i = colorStateList2.getDefaultColor();
                }
            }
            if (this.backgroundPatterned) {
                drawable = new GlifPatternDrawable(i);
            } else {
                drawable = new ColorDrawable(i);
            }
            ((StatusBarMixin) getMixin(StatusBarMixin.class)).setStatusBarBackground(drawable);
        }
    }

    public void setProgressBarShown(boolean z) {
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setShown(z);
    }

    public boolean shouldApplyPartnerHeavyThemeResource() {
        return this.applyPartnerHeavyThemeResource;
    }

    private void updateContentBackgroundColorWithPartnerConfig() {
        getRootView().setBackgroundColor(PartnerConfigHelper.get(getContext()).getColor(getContext(), PartnerConfig.CONFIG_LAYOUT_BACKGROUND_COLOR));
    }
}
