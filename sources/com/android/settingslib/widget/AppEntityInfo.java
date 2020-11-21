package com.android.settingslib.widget;

import android.graphics.drawable.Drawable;
import android.view.View;

public class AppEntityInfo {
    private final View.OnClickListener mClickListener;
    private final Drawable mIcon;
    private final CharSequence mSummary;
    private final CharSequence mTitle;

    public Drawable getIcon() {
        return this.mIcon;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getSummary() {
        return this.mSummary;
    }

    public View.OnClickListener getClickListener() {
        return this.mClickListener;
    }

    private AppEntityInfo(Builder builder) {
        this.mIcon = builder.mIcon;
        this.mTitle = builder.mTitle;
        this.mSummary = builder.mSummary;
        this.mClickListener = builder.mClickListener;
    }

    public static class Builder {
        private View.OnClickListener mClickListener;
        private Drawable mIcon;
        private CharSequence mSummary;
        private CharSequence mTitle;

        public AppEntityInfo build() {
            return new AppEntityInfo(this);
        }

        public Builder setIcon(Drawable drawable) {
            this.mIcon = drawable;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public Builder setSummary(CharSequence charSequence) {
            this.mSummary = charSequence;
            return this;
        }

        public Builder setOnClickListener(View.OnClickListener onClickListener) {
            this.mClickListener = onClickListener;
            return this;
        }
    }
}
