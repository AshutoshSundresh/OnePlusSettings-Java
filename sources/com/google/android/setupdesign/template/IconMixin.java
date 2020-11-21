package com.google.android.setupdesign.template;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.Mixin;
import com.google.android.setupdesign.R$dimen;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.util.HeaderAreaStyler;
import com.google.android.setupdesign.util.PartnerStyleHelper;

public class IconMixin implements Mixin {
    private final int originalHeight;
    private final ImageView.ScaleType originalScaleType;
    private final TemplateLayout templateLayout;

    public IconMixin(TemplateLayout templateLayout2, AttributeSet attributeSet, int i) {
        this.templateLayout = templateLayout2;
        Context context = templateLayout2.getContext();
        ImageView view = getView();
        if (view != null) {
            this.originalHeight = view.getLayoutParams().height;
            this.originalScaleType = view.getScaleType();
        } else {
            this.originalHeight = 0;
            this.originalScaleType = null;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SudIconMixin, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SudIconMixin_android_icon, 0);
        if (resourceId != 0) {
            setIcon(resourceId);
        }
        setUpscaleIcon(obtainStyledAttributes.getBoolean(R$styleable.SudIconMixin_sudUpscaleIcon, false));
        int color = obtainStyledAttributes.getColor(R$styleable.SudIconMixin_sudIconTint, 0);
        if (color != 0) {
            setIconTint(color);
        }
        obtainStyledAttributes.recycle();
    }

    public void tryApplyPartnerCustomizationStyle() {
        ImageView imageView;
        if (PartnerStyleHelper.isPartnerHeavyThemeLayout(this.templateLayout) && (imageView = (ImageView) this.templateLayout.findManagedViewById(R$id.sud_layout_icon)) != null) {
            HeaderAreaStyler.applyPartnerCustomizationIconStyle(imageView);
        }
    }

    public void setIcon(Drawable drawable) {
        ImageView view = getView();
        if (view != null) {
            view.setImageDrawable(drawable);
            view.setVisibility(drawable != null ? 0 : 8);
        }
    }

    public void setIcon(int i) {
        ImageView view = getView();
        if (view != null) {
            view.setImageResource(i);
            view.setVisibility(i != 0 ? 0 : 8);
        }
    }

    public Drawable getIcon() {
        ImageView view = getView();
        if (view != null) {
            return view.getDrawable();
        }
        return null;
    }

    public void setUpscaleIcon(boolean z) {
        int i;
        ImageView view = getView();
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (Build.VERSION.SDK_INT >= 16) {
                i = view.getMaxHeight();
            } else {
                i = (int) view.getResources().getDimension(R$dimen.sud_glif_icon_max_height);
            }
            if (!z) {
                i = this.originalHeight;
            }
            layoutParams.height = i;
            view.setLayoutParams(layoutParams);
            view.setScaleType(z ? ImageView.ScaleType.FIT_CENTER : this.originalScaleType);
        }
    }

    public void setIconTint(int i) {
        ImageView view = getView();
        if (view != null) {
            view.setColorFilter(i);
        }
    }

    /* access modifiers changed from: protected */
    public ImageView getView() {
        return (ImageView) this.templateLayout.findManagedViewById(R$id.sud_layout_icon);
    }
}
