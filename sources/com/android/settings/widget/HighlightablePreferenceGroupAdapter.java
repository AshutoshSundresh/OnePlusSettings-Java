package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.SettingsPreferenceFragment;

public class HighlightablePreferenceGroupAdapter extends PreferenceGroupAdapter {
    static final long DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    boolean mFadeInAnimated;
    final int mHighlightColor;
    private final String mHighlightKey;
    private int mHighlightPosition = -1;
    private boolean mHighlightRequested;
    private final int mNormalBackgroundRes;

    public static void adjustInitialExpandedChildCount(SettingsPreferenceFragment settingsPreferenceFragment) {
        PreferenceScreen preferenceScreen;
        if (settingsPreferenceFragment != null && (preferenceScreen = settingsPreferenceFragment.getPreferenceScreen()) != null) {
            Bundle arguments = settingsPreferenceFragment.getArguments();
            if (arguments == null || TextUtils.isEmpty(arguments.getString(":settings:fragment_args_key"))) {
                int initialExpandedChildCount = settingsPreferenceFragment.getInitialExpandedChildCount();
                if (initialExpandedChildCount > 0) {
                    preferenceScreen.setInitialExpandedChildrenCount(initialExpandedChildCount);
                    return;
                }
                return;
            }
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
    }

    public HighlightablePreferenceGroupAdapter(PreferenceGroup preferenceGroup, String str, boolean z) {
        super(preferenceGroup);
        this.mHighlightKey = str;
        this.mHighlightRequested = z;
        Context context = preferenceGroup.getContext();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843534, typedValue, true);
        this.mNormalBackgroundRes = typedValue.resourceId;
        this.mHighlightColor = context.getColor(C0006R$color.preference_highligh_color);
    }

    @Override // androidx.preference.PreferenceGroupAdapter
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        super.onBindViewHolder(preferenceViewHolder, i);
        updateBackground(preferenceViewHolder, i);
    }

    /* access modifiers changed from: package-private */
    public void updateBackground(PreferenceViewHolder preferenceViewHolder, int i) {
        View view = preferenceViewHolder.itemView;
        if (i == this.mHighlightPosition) {
            addHighlightBackground(view, !this.mFadeInAnimated);
        } else if (Boolean.TRUE.equals(view.getTag(C0010R$id.preference_highlighted))) {
            removeHighlightBackground(view, false);
        }
    }

    public void requestHighlight(View view, RecyclerView recyclerView) {
        if (!this.mHighlightRequested && recyclerView != null && !TextUtils.isEmpty(this.mHighlightKey)) {
            view.postDelayed(new Runnable(recyclerView) {
                /* class com.android.settings.widget.$$Lambda$HighlightablePreferenceGroupAdapter$tq_kKc8Wy_u27VUZj3YoSb1PWk */
                public final /* synthetic */ RecyclerView f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HighlightablePreferenceGroupAdapter.this.lambda$requestHighlight$0$HighlightablePreferenceGroupAdapter(this.f$1);
                }
            }, DELAY_HIGHLIGHT_DURATION_MILLIS);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestHighlight$0 */
    public /* synthetic */ void lambda$requestHighlight$0$HighlightablePreferenceGroupAdapter(RecyclerView recyclerView) {
        int preferenceAdapterPosition = getPreferenceAdapterPosition(this.mHighlightKey);
        if (preferenceAdapterPosition >= 0) {
            this.mHighlightRequested = true;
            recyclerView.smoothScrollToPosition(preferenceAdapterPosition);
            this.mHighlightPosition = preferenceAdapterPosition;
            notifyItemChanged(preferenceAdapterPosition);
        }
    }

    public boolean isHighlightRequested() {
        return this.mHighlightRequested;
    }

    /* access modifiers changed from: package-private */
    public void requestRemoveHighlightDelayed(View view) {
        view.postDelayed(new Runnable(view) {
            /* class com.android.settings.widget.$$Lambda$HighlightablePreferenceGroupAdapter$CKVsKq8Jy7vb9RpitMwq8ps1ehE */
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                HighlightablePreferenceGroupAdapter.this.lambda$requestRemoveHighlightDelayed$1$HighlightablePreferenceGroupAdapter(this.f$1);
            }
        }, 15000);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestRemoveHighlightDelayed$1 */
    public /* synthetic */ void lambda$requestRemoveHighlightDelayed$1$HighlightablePreferenceGroupAdapter(View view) {
        this.mHighlightPosition = -1;
        removeHighlightBackground(view, true);
    }

    private void addHighlightBackground(View view, boolean z) {
        view.setTag(C0010R$id.preference_highlighted, Boolean.TRUE);
        if (!z) {
            view.setBackgroundColor(this.mHighlightColor);
            Log.d("HighlightableAdapter", "AddHighlight: Not animation requested - setting highlight background");
            requestRemoveHighlightDelayed(view);
            return;
        }
        this.mFadeInAnimated = true;
        int i = this.mNormalBackgroundRes;
        int i2 = this.mHighlightColor;
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i), Integer.valueOf(i2));
        ofObject.setDuration(200L);
        ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(view) {
            /* class com.android.settings.widget.$$Lambda$HighlightablePreferenceGroupAdapter$piymLpeUf2m74Yz5ep7jpdxw2ho */
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        ofObject.setRepeatMode(2);
        ofObject.setRepeatCount(4);
        ofObject.start();
        Log.d("HighlightableAdapter", "AddHighlight: starting fade in animation");
        requestRemoveHighlightDelayed(view);
    }

    private void removeHighlightBackground(final View view, boolean z) {
        Boolean bool = Boolean.FALSE;
        if (!z) {
            view.setTag(C0010R$id.preference_highlighted, bool);
            view.setBackgroundResource(this.mNormalBackgroundRes);
            Log.d("HighlightableAdapter", "RemoveHighlight: No animation requested - setting normal background");
        } else if (!Boolean.TRUE.equals(view.getTag(C0010R$id.preference_highlighted))) {
            Log.d("HighlightableAdapter", "RemoveHighlight: Not highlighted - skipping");
        } else {
            int i = this.mHighlightColor;
            int i2 = this.mNormalBackgroundRes;
            view.setTag(C0010R$id.preference_highlighted, bool);
            ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i), Integer.valueOf(i2));
            ofObject.setDuration(500L);
            ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(view) {
                /* class com.android.settings.widget.$$Lambda$HighlightablePreferenceGroupAdapter$HMY634RMu5R2WoggcFMdrEe8uA0 */
                public final /* synthetic */ View f$0;

                {
                    this.f$0 = r1;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
                }
            });
            ofObject.addListener(new AnimatorListenerAdapter() {
                /* class com.android.settings.widget.HighlightablePreferenceGroupAdapter.AnonymousClass1 */

                public void onAnimationEnd(Animator animator) {
                    view.setBackgroundResource(HighlightablePreferenceGroupAdapter.this.mNormalBackgroundRes);
                }
            });
            ofObject.start();
            Log.d("HighlightableAdapter", "Starting fade out animation");
        }
    }
}
