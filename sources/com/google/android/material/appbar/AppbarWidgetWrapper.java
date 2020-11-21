package com.google.android.material.appbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.TintTypedArray;
import com.google.android.material.R$attr;
import com.google.android.material.R$drawable;
import com.google.android.material.R$string;
import com.google.android.material.R$styleable;

public class AppbarWidgetWrapper implements DecorAppbar {
    Appbar mAppbar;
    private View mCustomView;
    private int mDefaultNavigationContentDescription;
    private Drawable mDefaultNavigationIcon;
    private int mDisplayOpts;
    private CharSequence mHomeDescription;
    private Drawable mNavIcon;
    private CharSequence mSubtitle;
    CharSequence mTitle;

    public AppbarWidgetWrapper(Appbar appbar, boolean z) {
        this(appbar, z, R$string.abc_action_bar_up_description, R$drawable.ic_title_bar_back);
    }

    public AppbarWidgetWrapper(Appbar appbar, boolean z, int i, int i2) {
        Drawable drawable;
        this.mDefaultNavigationContentDescription = 0;
        this.mAppbar = appbar;
        this.mTitle = appbar.getTitle();
        this.mSubtitle = appbar.getSubtitle();
        CharSequence charSequence = this.mTitle;
        this.mNavIcon = appbar.getNavigationIcon();
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(appbar.getContext(), null, R$styleable.ActionBar, R$attr.actionBarStyle, 0);
        this.mDefaultNavigationIcon = getContext().getDrawable(i2);
        if (z) {
            CharSequence text = obtainStyledAttributes.getText(R$styleable.ActionBar_title);
            if (!TextUtils.isEmpty(text)) {
                setTitle(text);
            }
            CharSequence text2 = obtainStyledAttributes.getText(R$styleable.ActionBar_subtitle);
            if (!TextUtils.isEmpty(text2)) {
                setSubtitle(text2);
            }
            if (this.mNavIcon == null && (drawable = this.mDefaultNavigationIcon) != null) {
                setNavigationIcon(drawable);
            }
            setDisplayOptions(obtainStyledAttributes.getInt(R$styleable.ActionBar_displayOptions, 0));
            int resourceId = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_customNavigationLayout, 0);
            if (resourceId != 0) {
                setCustomView(LayoutInflater.from(this.mAppbar.getContext()).inflate(resourceId, (ViewGroup) this.mAppbar, false));
                setDisplayOptions(this.mDisplayOpts | 16);
            }
            int layoutDimension = obtainStyledAttributes.getLayoutDimension(R$styleable.ActionBar_height, 0);
            if (layoutDimension > 0) {
                ViewGroup.LayoutParams layoutParams = this.mAppbar.getLayoutParams();
                layoutParams.height = layoutDimension;
                this.mAppbar.setLayoutParams(layoutParams);
            }
            int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_titleTextStyle, 0);
            if (resourceId2 != 0) {
                Appbar appbar2 = this.mAppbar;
                appbar2.setTitleTextAppearance(appbar2.getContext(), resourceId2);
            }
            int resourceId3 = obtainStyledAttributes.getResourceId(R$styleable.ActionBar_subtitleTextStyle, 0);
            if (resourceId3 != 0) {
                Appbar appbar3 = this.mAppbar;
                appbar3.setSubtitleTextAppearance(appbar3.getContext(), resourceId3);
            }
        } else {
            this.mDisplayOpts = detectDisplayOptions();
        }
        obtainStyledAttributes.recycle();
        setDefaultNavigationContentDescription(i);
        this.mHomeDescription = this.mAppbar.getNavigationContentDescription();
    }

    public void setDefaultNavigationContentDescription(int i) {
        if (i != this.mDefaultNavigationContentDescription) {
            this.mDefaultNavigationContentDescription = i;
            if (TextUtils.isEmpty(this.mAppbar.getNavigationContentDescription())) {
                setNavigationContentDescription(this.mDefaultNavigationContentDescription);
            }
        }
    }

    private int detectDisplayOptions() {
        if (this.mAppbar.getNavigationIcon() == null) {
            return 11;
        }
        this.mDefaultNavigationIcon = this.mAppbar.getNavigationIcon();
        return 15;
    }

    public Context getContext() {
        return this.mAppbar.getContext();
    }

    public void setTitle(CharSequence charSequence) {
        setTitleInt(charSequence);
    }

    private void setTitleInt(CharSequence charSequence) {
        this.mTitle = charSequence;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mAppbar.setTitle(charSequence);
        }
    }

    public void setSubtitle(CharSequence charSequence) {
        this.mSubtitle = charSequence;
        if ((this.mDisplayOpts & 8) != 0) {
            this.mAppbar.setSubtitle(charSequence);
        }
    }

    public void setDisplayOptions(int i) {
        View view;
        int i2 = this.mDisplayOpts ^ i;
        this.mDisplayOpts = i;
        if (i2 != 0) {
            if ((i2 & 4) != 0) {
                if ((i & 4) != 0) {
                    updateHomeAccessibility();
                }
                updateNavigationIcon();
            }
            if ((i2 & 8) != 0) {
                if ((i & 8) != 0) {
                    this.mAppbar.setTitle(this.mTitle);
                    this.mAppbar.setSubtitle(this.mSubtitle);
                } else {
                    this.mAppbar.setTitle((CharSequence) null);
                    this.mAppbar.setSubtitle((CharSequence) null);
                }
            }
            if ((i2 & 16) != 0 && (view = this.mCustomView) != null) {
                if ((i & 16) != 0) {
                    this.mAppbar.addView(view);
                } else {
                    this.mAppbar.removeView(view);
                }
            }
        }
    }

    public void setCustomView(View view) {
        View view2 = this.mCustomView;
        if (!(view2 == null || (this.mDisplayOpts & 16) == 0)) {
            this.mAppbar.removeView(view2);
        }
        this.mCustomView = view;
        if (view != null && (this.mDisplayOpts & 16) != 0) {
            this.mAppbar.addView(view);
        }
    }

    public void setNavigationIcon(Drawable drawable) {
        this.mNavIcon = drawable;
        updateNavigationIcon();
    }

    private void updateNavigationIcon() {
        if ((this.mDisplayOpts & 4) != 0) {
            Appbar appbar = this.mAppbar;
            Drawable drawable = this.mNavIcon;
            if (drawable == null) {
                drawable = this.mDefaultNavigationIcon;
            }
            appbar.setNavigationIcon(drawable);
            return;
        }
        this.mAppbar.setNavigationIcon((Drawable) null);
    }

    public void setNavigationContentDescription(CharSequence charSequence) {
        this.mHomeDescription = charSequence;
        updateHomeAccessibility();
    }

    public void setNavigationContentDescription(int i) {
        setNavigationContentDescription(i == 0 ? null : getContext().getString(i));
    }

    private void updateHomeAccessibility() {
        if ((this.mDisplayOpts & 4) == 0) {
            return;
        }
        if (TextUtils.isEmpty(this.mHomeDescription)) {
            this.mAppbar.setNavigationContentDescription(this.mDefaultNavigationContentDescription);
        } else {
            this.mAppbar.setNavigationContentDescription(this.mHomeDescription);
        }
    }
}
