package com.google.android.setupcompat.template;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.setupcompat.PartnerCustomizationLayout;
import com.google.android.setupcompat.R$id;
import com.google.android.setupcompat.R$layout;
import com.google.android.setupcompat.R$style;
import com.google.android.setupcompat.R$styleable;
import com.google.android.setupcompat.internal.FooterButtonPartnerConfig;
import com.google.android.setupcompat.internal.Preconditions;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.logging.internal.FooterBarMixinMetrics;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupcompat.template.FooterButton;
import java.util.concurrent.atomic.AtomicInteger;

public class FooterBarMixin implements Mixin {
    private static final AtomicInteger nextGeneratedId = new AtomicInteger(1);
    final boolean applyPartnerResources;
    private LinearLayout buttonContainer;
    private final Context context;
    int defaultPadding;
    private int footerBarPaddingBottom;
    private int footerBarPaddingTop;
    private final int footerBarPrimaryBackgroundColor;
    private final int footerBarSecondaryBackgroundColor;
    private final ViewStub footerStub;
    public final FooterBarMixinMetrics metrics = new FooterBarMixinMetrics();
    private FooterButton primaryButton;
    private int primaryButtonId;
    public FooterButtonPartnerConfig primaryButtonPartnerConfigForTesting;
    ColorStateList primaryDefaultTextColor = null;
    private boolean removeFooterBarWhenEmpty = true;
    private FooterButton secondaryButton;
    private int secondaryButtonId;
    public FooterButtonPartnerConfig secondaryButtonPartnerConfigForTesting;
    ColorStateList secondaryDefaultTextColor = null;

    private FooterButton.OnButtonEventListener createButtonEventListener(final int i) {
        return new FooterButton.OnButtonEventListener() {
            /* class com.google.android.setupcompat.template.FooterBarMixin.AnonymousClass1 */

            @Override // com.google.android.setupcompat.template.FooterButton.OnButtonEventListener
            public void onEnabledChanged(boolean z) {
                Button button;
                PartnerConfig partnerConfig;
                if (FooterBarMixin.this.buttonContainer != null && (button = (Button) FooterBarMixin.this.buttonContainer.findViewById(i)) != null) {
                    button.setEnabled(z);
                    FooterBarMixin footerBarMixin = FooterBarMixin.this;
                    if (footerBarMixin.applyPartnerResources) {
                        if (i == footerBarMixin.primaryButtonId) {
                            partnerConfig = PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR;
                        } else {
                            partnerConfig = PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_TEXT_COLOR;
                        }
                        footerBarMixin.updateButtonTextColorWithPartnerConfig(button, partnerConfig);
                    }
                }
            }

            @Override // com.google.android.setupcompat.template.FooterButton.OnButtonEventListener
            public void onVisibilityChanged(int i) {
                Button button;
                if (FooterBarMixin.this.buttonContainer != null && (button = (Button) FooterBarMixin.this.buttonContainer.findViewById(i)) != null) {
                    button.setVisibility(i);
                    FooterBarMixin.this.autoSetButtonBarVisibility();
                }
            }

            @Override // com.google.android.setupcompat.template.FooterButton.OnButtonEventListener
            public void onTextChanged(CharSequence charSequence) {
                Button button;
                if (FooterBarMixin.this.buttonContainer != null && (button = (Button) FooterBarMixin.this.buttonContainer.findViewById(i)) != null) {
                    button.setText(charSequence);
                }
            }
        };
    }

    public FooterBarMixin(TemplateLayout templateLayout, AttributeSet attributeSet, int i) {
        this.context = templateLayout.getContext();
        this.footerStub = (ViewStub) templateLayout.findManagedViewById(R$id.suc_layout_footer);
        this.applyPartnerResources = (templateLayout instanceof PartnerCustomizationLayout) && ((PartnerCustomizationLayout) templateLayout).shouldApplyPartnerResource();
        TypedArray obtainStyledAttributes = this.context.obtainStyledAttributes(attributeSet, R$styleable.SucFooterBarMixin, i, 0);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SucFooterBarMixin_sucFooterBarPaddingVertical, 0);
        this.defaultPadding = dimensionPixelSize;
        this.footerBarPaddingTop = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SucFooterBarMixin_sucFooterBarPaddingTop, dimensionPixelSize);
        this.footerBarPaddingBottom = obtainStyledAttributes.getDimensionPixelSize(R$styleable.SucFooterBarMixin_sucFooterBarPaddingBottom, this.defaultPadding);
        this.footerBarPrimaryBackgroundColor = obtainStyledAttributes.getColor(R$styleable.SucFooterBarMixin_sucFooterBarPrimaryFooterBackground, 0);
        this.footerBarSecondaryBackgroundColor = obtainStyledAttributes.getColor(R$styleable.SucFooterBarMixin_sucFooterBarSecondaryFooterBackground, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SucFooterBarMixin_sucFooterBarPrimaryFooterButton, 0);
        int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.SucFooterBarMixin_sucFooterBarSecondaryFooterButton, 0);
        obtainStyledAttributes.recycle();
        FooterButtonInflater footerButtonInflater = new FooterButtonInflater(this.context);
        if (resourceId2 != 0) {
            setSecondaryButton(footerButtonInflater.inflate(resourceId2));
            this.metrics.logPrimaryButtonInitialStateVisibility(true, true);
        }
        if (resourceId != 0) {
            setPrimaryButton(footerButtonInflater.inflate(resourceId));
            this.metrics.logSecondaryButtonInitialStateVisibility(true, true);
        }
    }

    private View addSpace() {
        LinearLayout ensureFooterInflated = ensureFooterInflated();
        View view = new View(ensureFooterInflated.getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));
        view.setVisibility(4);
        ensureFooterInflated.addView(view);
        return view;
    }

    private LinearLayout ensureFooterInflated() {
        if (this.buttonContainer == null) {
            if (this.footerStub != null) {
                LinearLayout linearLayout = (LinearLayout) inflateFooter(R$layout.suc_footer_button_bar);
                this.buttonContainer = linearLayout;
                onFooterBarInflated(linearLayout);
                onFooterBarApplyPartnerResource(this.buttonContainer);
            } else {
                throw new IllegalStateException("Footer stub is not found in this template");
            }
        }
        return this.buttonContainer;
    }

    /* access modifiers changed from: protected */
    public void onFooterBarInflated(LinearLayout linearLayout) {
        if (linearLayout != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                linearLayout.setId(View.generateViewId());
            } else {
                linearLayout.setId(generateViewId());
            }
            updateFooterBarPadding(linearLayout, linearLayout.getPaddingLeft(), this.footerBarPaddingTop, linearLayout.getPaddingRight(), this.footerBarPaddingBottom);
        }
    }

    /* access modifiers changed from: protected */
    public void onFooterBarApplyPartnerResource(LinearLayout linearLayout) {
        if (linearLayout != null && this.applyPartnerResources) {
            linearLayout.setBackgroundColor(PartnerConfigHelper.get(this.context).getColor(this.context, PartnerConfig.CONFIG_FOOTER_BAR_BG_COLOR));
            this.footerBarPaddingTop = (int) PartnerConfigHelper.get(this.context).getDimension(this.context, PartnerConfig.CONFIG_FOOTER_BUTTON_PADDING_TOP);
            this.footerBarPaddingBottom = (int) PartnerConfigHelper.get(this.context).getDimension(this.context, PartnerConfig.CONFIG_FOOTER_BUTTON_PADDING_BOTTOM);
            updateFooterBarPadding(linearLayout, linearLayout.getPaddingLeft(), this.footerBarPaddingTop, linearLayout.getPaddingRight(), this.footerBarPaddingBottom);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"InflateParams"})
    public FooterActionButton createThemedButton(Context context2, int i) {
        return (FooterActionButton) LayoutInflater.from(new ContextThemeWrapper(context2, i)).inflate(R$layout.suc_button, (ViewGroup) null, false);
    }

    public void setPrimaryButton(FooterButton footerButton) {
        Preconditions.ensureOnMainThread("setPrimaryButton");
        ensureFooterInflated();
        FooterButtonPartnerConfig.Builder builder = new FooterButtonPartnerConfig.Builder(footerButton);
        builder.setPartnerTheme(getPartnerTheme(footerButton, R$style.SucPartnerCustomizationButton_Primary, PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR));
        builder.setButtonBackgroundConfig(PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR);
        builder.setButtonDisableAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_ALPHA);
        builder.setButtonDisableBackgroundConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_BG_COLOR);
        builder.setButtonIconConfig(getDrawablePartnerConfig(footerButton.getButtonType()));
        builder.setButtonRadiusConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RADIUS);
        builder.setButtonRippleColorAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RIPPLE_COLOR_ALPHA);
        builder.setTextColorConfig(PartnerConfig.CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR);
        builder.setTextSizeConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_SIZE);
        builder.setTextTypeFaceConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_FONT_FAMILY);
        FooterButtonPartnerConfig build = builder.build();
        FooterActionButton inflateButton = inflateButton(footerButton, build);
        this.primaryButtonId = inflateButton.getId();
        this.primaryDefaultTextColor = inflateButton.getTextColors();
        this.primaryButton = footerButton;
        this.primaryButtonPartnerConfigForTesting = build;
        onFooterButtonInflated(inflateButton, this.footerBarPrimaryBackgroundColor);
        onFooterButtonApplyPartnerResource(inflateButton, build);
        repopulateButtons();
    }

    public FooterButton getPrimaryButton() {
        return this.primaryButton;
    }

    public Button getPrimaryButtonView() {
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout == null) {
            return null;
        }
        return (Button) linearLayout.findViewById(this.primaryButtonId);
    }

    /* access modifiers changed from: package-private */
    public boolean isPrimaryButtonVisible() {
        return getPrimaryButtonView() != null && getPrimaryButtonView().getVisibility() == 0;
    }

    public void setSecondaryButton(FooterButton footerButton) {
        Preconditions.ensureOnMainThread("setSecondaryButton");
        ensureFooterInflated();
        FooterButtonPartnerConfig.Builder builder = new FooterButtonPartnerConfig.Builder(footerButton);
        builder.setPartnerTheme(getPartnerTheme(footerButton, R$style.SucPartnerCustomizationButton_Secondary, PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_BG_COLOR));
        builder.setButtonBackgroundConfig(PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_BG_COLOR);
        builder.setButtonDisableAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_ALPHA);
        builder.setButtonDisableBackgroundConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_DISABLED_BG_COLOR);
        builder.setButtonIconConfig(getDrawablePartnerConfig(footerButton.getButtonType()));
        builder.setButtonRadiusConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RADIUS);
        builder.setButtonRippleColorAlphaConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_RIPPLE_COLOR_ALPHA);
        builder.setTextColorConfig(PartnerConfig.CONFIG_FOOTER_SECONDARY_BUTTON_TEXT_COLOR);
        builder.setTextSizeConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_TEXT_SIZE);
        builder.setTextTypeFaceConfig(PartnerConfig.CONFIG_FOOTER_BUTTON_FONT_FAMILY);
        FooterButtonPartnerConfig build = builder.build();
        FooterActionButton inflateButton = inflateButton(footerButton, build);
        this.secondaryButtonId = inflateButton.getId();
        this.secondaryDefaultTextColor = inflateButton.getTextColors();
        this.secondaryButton = footerButton;
        this.secondaryButtonPartnerConfigForTesting = build;
        onFooterButtonInflated(inflateButton, this.footerBarSecondaryBackgroundColor);
        onFooterButtonApplyPartnerResource(inflateButton, build);
        repopulateButtons();
    }

    /* access modifiers changed from: protected */
    public void repopulateButtons() {
        LinearLayout ensureFooterInflated = ensureFooterInflated();
        Button primaryButtonView = getPrimaryButtonView();
        Button secondaryButtonView = getSecondaryButtonView();
        ensureFooterInflated.removeAllViews();
        if (secondaryButtonView != null) {
            ensureFooterInflated.addView(secondaryButtonView);
        }
        addSpace();
        if (primaryButtonView != null) {
            ensureFooterInflated.addView(primaryButtonView);
        }
    }

    /* access modifiers changed from: protected */
    public void onFooterButtonInflated(Button button, int i) {
        if (i != 0) {
            updateButtonBackground(button, i);
        }
        this.buttonContainer.addView(button);
        autoSetButtonBarVisibility();
    }

    private int getPartnerTheme(FooterButton footerButton, int i, PartnerConfig partnerConfig) {
        int theme = footerButton.getTheme();
        if (footerButton.getTheme() != 0 && !this.applyPartnerResources) {
            i = theme;
        }
        if (!this.applyPartnerResources) {
            return i;
        }
        int color = PartnerConfigHelper.get(this.context).getColor(this.context, partnerConfig);
        if (color == 0) {
            return R$style.SucPartnerCustomizationButton_Secondary;
        }
        return color != 0 ? R$style.SucPartnerCustomizationButton_Primary : i;
    }

    public LinearLayout getButtonContainer() {
        return this.buttonContainer;
    }

    public FooterButton getSecondaryButton() {
        return this.secondaryButton;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void autoSetButtonBarVisibility() {
        Button primaryButtonView = getPrimaryButtonView();
        Button secondaryButtonView = getSecondaryButtonView();
        boolean z = true;
        int i = 0;
        boolean z2 = primaryButtonView != null && primaryButtonView.getVisibility() == 0;
        if (secondaryButtonView == null || secondaryButtonView.getVisibility() != 0) {
            z = false;
        }
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout != null) {
            if (!z2 && !z) {
                i = this.removeFooterBarWhenEmpty ? 8 : 4;
            }
            linearLayout.setVisibility(i);
        }
    }

    public int getVisibility() {
        return this.buttonContainer.getVisibility();
    }

    public Button getSecondaryButtonView() {
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout == null) {
            return null;
        }
        return (Button) linearLayout.findViewById(this.secondaryButtonId);
    }

    /* access modifiers changed from: package-private */
    public boolean isSecondaryButtonVisible() {
        return getSecondaryButtonView() != null && getSecondaryButtonView().getVisibility() == 0;
    }

    private static int generateViewId() {
        int i;
        int i2;
        do {
            i = nextGeneratedId.get();
            i2 = i + 1;
            if (i2 > 16777215) {
                i2 = 1;
            }
        } while (!nextGeneratedId.compareAndSet(i, i2));
        return i;
    }

    private FooterActionButton inflateButton(FooterButton footerButton, FooterButtonPartnerConfig footerButtonPartnerConfig) {
        FooterActionButton createThemedButton = createThemedButton(this.context, footerButtonPartnerConfig.getPartnerTheme());
        if (Build.VERSION.SDK_INT >= 17) {
            createThemedButton.setId(View.generateViewId());
        } else {
            createThemedButton.setId(generateViewId());
        }
        createThemedButton.setText(footerButton.getText());
        createThemedButton.setOnClickListener(footerButton);
        createThemedButton.setVisibility(footerButton.getVisibility());
        createThemedButton.setEnabled(footerButton.isEnabled());
        createThemedButton.setFooterButton(footerButton);
        footerButton.setOnButtonEventListener(createButtonEventListener(createThemedButton.getId()));
        return createThemedButton;
    }

    @TargetApi(29)
    private void onFooterButtonApplyPartnerResource(Button button, FooterButtonPartnerConfig footerButtonPartnerConfig) {
        if (this.applyPartnerResources) {
            updateButtonTextColorWithPartnerConfig(button, footerButtonPartnerConfig.getButtonTextColorConfig());
            updateButtonTextSizeWithPartnerConfig(button, footerButtonPartnerConfig.getButtonTextSizeConfig());
            updateButtonTypeFaceWithPartnerConfig(button, footerButtonPartnerConfig.getButtonTextTypeFaceConfig());
            updateButtonBackgroundWithPartnerConfig(button, footerButtonPartnerConfig.getButtonBackgroundConfig(), footerButtonPartnerConfig.getButtonDisableAlphaConfig(), footerButtonPartnerConfig.getButtonDisableBackgroundConfig());
            updateButtonRadiusWithPartnerConfig(button, footerButtonPartnerConfig.getButtonRadiusConfig());
            updateButtonIconWithPartnerConfig(button, footerButtonPartnerConfig.getButtonIconConfig());
            updateButtonRippleColorWithPartnerConfig(button, footerButtonPartnerConfig);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateButtonTextColorWithPartnerConfig(Button button, PartnerConfig partnerConfig) {
        if (button.isEnabled()) {
            int color = PartnerConfigHelper.get(this.context).getColor(this.context, partnerConfig);
            if (color != 0) {
                button.setTextColor(ColorStateList.valueOf(color));
                return;
            }
            return;
        }
        button.setTextColor(button.getId() == this.primaryButtonId ? this.primaryDefaultTextColor : this.secondaryDefaultTextColor);
    }

    private void updateButtonTextSizeWithPartnerConfig(Button button, PartnerConfig partnerConfig) {
        float dimension = PartnerConfigHelper.get(this.context).getDimension(this.context, partnerConfig);
        if (dimension > 0.0f) {
            button.setTextSize(0, dimension);
        }
    }

    private void updateButtonTypeFaceWithPartnerConfig(Button button, PartnerConfig partnerConfig) {
        Typeface create = Typeface.create(PartnerConfigHelper.get(this.context).getString(this.context, partnerConfig), 0);
        if (create != null) {
            button.setTypeface(create);
        }
    }

    @TargetApi(29)
    private void updateButtonBackgroundWithPartnerConfig(Button button, PartnerConfig partnerConfig, PartnerConfig partnerConfig2, PartnerConfig partnerConfig3) {
        Preconditions.checkArgument(Build.VERSION.SDK_INT >= 29, "Update button background only support on sdk Q or higher");
        int[] iArr = {-16842910};
        int[] iArr2 = new int[0];
        int color = PartnerConfigHelper.get(this.context).getColor(this.context, partnerConfig);
        float fraction = PartnerConfigHelper.get(this.context).getFraction(this.context, partnerConfig2, 0.0f);
        int color2 = PartnerConfigHelper.get(this.context).getColor(this.context, partnerConfig3);
        if (color != 0) {
            if (fraction <= 0.0f) {
                TypedArray obtainStyledAttributes = this.context.obtainStyledAttributes(new int[]{16842803});
                fraction = obtainStyledAttributes.getFloat(0, 0.26f);
                obtainStyledAttributes.recycle();
            }
            if (color2 == 0) {
                color2 = color;
            }
            ColorStateList colorStateList = new ColorStateList(new int[][]{iArr, iArr2}, new int[]{convertRgbToArgb(color2, fraction), color});
            button.getBackground().mutate().setState(new int[0]);
            button.refreshDrawableState();
            button.setBackgroundTintList(colorStateList);
        }
    }

    private void updateButtonBackground(Button button, int i) {
        button.getBackground().mutate().setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    }

    private void updateButtonRadiusWithPartnerConfig(Button button, PartnerConfig partnerConfig) {
        if (Build.VERSION.SDK_INT >= 24) {
            float dimension = PartnerConfigHelper.get(this.context).getDimension(this.context, partnerConfig);
            GradientDrawable gradientDrawable = getGradientDrawable(button);
            if (gradientDrawable != null) {
                gradientDrawable.setCornerRadius(dimension);
            }
        }
    }

    private void updateButtonRippleColorWithPartnerConfig(Button button, FooterButtonPartnerConfig footerButtonPartnerConfig) {
        RippleDrawable rippleDrawable;
        if (Build.VERSION.SDK_INT >= 21 && (rippleDrawable = getRippleDrawable(button)) != null) {
            rippleDrawable.setColor(new ColorStateList(new int[][]{new int[]{16842919}, StateSet.NOTHING}, new int[]{convertRgbToArgb(PartnerConfigHelper.get(this.context).getColor(this.context, footerButtonPartnerConfig.getButtonTextColorConfig()), PartnerConfigHelper.get(this.context).getFraction(this.context, footerButtonPartnerConfig.getButtonRippleColorAlphaConfig())), 0}));
        }
    }

    private void updateButtonIconWithPartnerConfig(Button button, PartnerConfig partnerConfig) {
        if (button != null) {
            Drawable drawable = null;
            if (partnerConfig != null) {
                drawable = PartnerConfigHelper.get(this.context).getDrawable(this.context, partnerConfig);
            }
            setButtonIcon(button, drawable);
        }
    }

    private void setButtonIcon(Button button, Drawable drawable) {
        Drawable drawable2;
        if (button != null) {
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            }
            if (button.getId() == this.primaryButtonId) {
                drawable2 = drawable;
                drawable = null;
            } else if (button.getId() == this.secondaryButtonId) {
                drawable2 = null;
            } else {
                drawable2 = null;
                drawable = null;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                button.setCompoundDrawablesRelative(drawable, null, drawable2, null);
            } else {
                button.setCompoundDrawables(drawable, null, drawable2, null);
            }
        }
    }

    private static PartnerConfig getDrawablePartnerConfig(int i) {
        switch (i) {
            case 1:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_ADD_ANOTHER;
            case 2:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_CANCEL;
            case 3:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_CLEAR;
            case 4:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_DONE;
            case 5:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_NEXT;
            case 6:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_OPT_IN;
            case 7:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_SKIP;
            case 8:
                return PartnerConfig.CONFIG_FOOTER_BUTTON_ICON_STOP;
            default:
                return null;
        }
    }

    /* access modifiers changed from: package-private */
    public GradientDrawable getGradientDrawable(Button button) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        Drawable background = button.getBackground();
        if (background instanceof InsetDrawable) {
            return (GradientDrawable) ((LayerDrawable) ((InsetDrawable) background).getDrawable()).getDrawable(0);
        }
        if (background instanceof RippleDrawable) {
            return (GradientDrawable) ((InsetDrawable) ((RippleDrawable) background).getDrawable(0)).getDrawable();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public RippleDrawable getRippleDrawable(Button button) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        Drawable background = button.getBackground();
        if (background instanceof InsetDrawable) {
            return (RippleDrawable) ((InsetDrawable) background).getDrawable();
        }
        if (background instanceof RippleDrawable) {
            return (RippleDrawable) background;
        }
        return null;
    }

    private static int convertRgbToArgb(int i, float f) {
        return Color.argb((int) (f * 255.0f), Color.red(i), Color.green(i), Color.blue(i));
    }

    /* access modifiers changed from: protected */
    public View inflateFooter(int i) {
        if (Build.VERSION.SDK_INT >= 16) {
            this.footerStub.setLayoutInflater(LayoutInflater.from(new ContextThemeWrapper(this.context, R$style.SucPartnerCustomizationButtonBar_Stackable)));
        }
        this.footerStub.setLayoutResource(i);
        return this.footerStub.inflate();
    }

    private void updateFooterBarPadding(LinearLayout linearLayout, int i, int i2, int i3, int i4) {
        if (linearLayout != null) {
            linearLayout.setPadding(i, i2, i3, i4);
        }
    }

    /* access modifiers changed from: package-private */
    public int getPaddingTop() {
        LinearLayout linearLayout = this.buttonContainer;
        return linearLayout != null ? linearLayout.getPaddingTop() : this.footerStub.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public int getPaddingBottom() {
        LinearLayout linearLayout = this.buttonContainer;
        if (linearLayout != null) {
            return linearLayout.getPaddingBottom();
        }
        return this.footerStub.getPaddingBottom();
    }

    public void onAttachedToWindow() {
        this.metrics.logPrimaryButtonInitialStateVisibility(isPrimaryButtonVisible(), false);
        this.metrics.logSecondaryButtonInitialStateVisibility(isSecondaryButtonVisible(), false);
    }

    public void onDetachedFromWindow() {
        this.metrics.updateButtonVisibility(isPrimaryButtonVisible(), isSecondaryButtonVisible());
    }

    @TargetApi(29)
    public PersistableBundle getLoggingMetrics() {
        return this.metrics.getMetrics();
    }
}
