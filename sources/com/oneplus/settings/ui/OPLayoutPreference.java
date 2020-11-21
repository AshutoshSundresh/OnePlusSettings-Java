package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.internal.R;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.R$styleable;
import com.android.settings.Utils;

public class OPLayoutPreference extends Preference {
    private boolean mAllowDividerAbove;
    private boolean mAllowDividerBelow;
    private final View.OnClickListener mClickListener;
    View mRootView;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$OPLayoutPreference(View view) {
        performClick(view);
    }

    public OPLayoutPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mClickListener = new View.OnClickListener() {
            /* class com.oneplus.settings.ui.$$Lambda$OPLayoutPreference$VqWBg58BddXXxYU5xl0ZZGKTck */

            public final void onClick(View view) {
                OPLayoutPreference.this.lambda$new$0$OPLayoutPreference(view);
            }
        };
        init(context, attributeSet, 0);
    }

    public OPLayoutPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mClickListener = new View.OnClickListener() {
            /* class com.oneplus.settings.ui.$$Lambda$OPLayoutPreference$VqWBg58BddXXxYU5xl0ZZGKTck */

            public final void onClick(View view) {
                OPLayoutPreference.this.lambda$new$0$OPLayoutPreference(view);
            }
        };
        init(context, attributeSet, i);
    }

    public OPLayoutPreference(Context context, int i) {
        this(context, LayoutInflater.from(context).inflate(i, (ViewGroup) null, false));
    }

    public OPLayoutPreference(Context context, View view) {
        super(context);
        this.mClickListener = new View.OnClickListener() {
            /* class com.oneplus.settings.ui.$$Lambda$OPLayoutPreference$VqWBg58BddXXxYU5xl0ZZGKTck */

            public final void onClick(View view) {
                OPLayoutPreference.this.lambda$new$0$OPLayoutPreference(view);
            }
        };
        setView(view);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.Preference);
        int i2 = R$styleable.Preference_allowDividerAbove;
        this.mAllowDividerAbove = TypedArrayUtils.getBoolean(obtainStyledAttributes, i2, i2, false);
        int i3 = R$styleable.Preference_allowDividerBelow;
        this.mAllowDividerBelow = TypedArrayUtils.getBoolean(obtainStyledAttributes, i3, i3, false);
        obtainStyledAttributes.recycle();
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.Preference, i, 0);
        int resourceId = obtainStyledAttributes2.getResourceId(3, 0);
        if (resourceId != 0) {
            obtainStyledAttributes2.recycle();
            setView(LayoutInflater.from(getContext()).inflate(resourceId, (ViewGroup) null, false));
            return;
        }
        throw new IllegalArgumentException("LayoutPreference requires a layout to be defined");
    }

    private void setView(View view) {
        setLayoutResource(C0012R$layout.layout_preference_frame);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(C0010R$id.all_details);
        if (viewGroup != null) {
            Utils.forceCustomPadding(viewGroup, true);
        }
        this.mRootView = view;
        setShouldDisableView(false);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.setOnClickListener(this.mClickListener);
        boolean isSelectable = isSelectable();
        preferenceViewHolder.itemView.setFocusable(isSelectable);
        preferenceViewHolder.itemView.setClickable(isSelectable);
        preferenceViewHolder.setDividerAllowedAbove(this.mAllowDividerAbove);
        preferenceViewHolder.setDividerAllowedBelow(this.mAllowDividerBelow);
        FrameLayout frameLayout = (FrameLayout) preferenceViewHolder.itemView;
        frameLayout.removeAllViews();
        ViewGroup viewGroup = (ViewGroup) this.mRootView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mRootView);
        }
        frameLayout.addView(this.mRootView);
    }

    public <T extends View> T findViewById(int i) {
        return (T) this.mRootView.findViewById(i);
    }
}
