package com.android.settings.notification.app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.R$drawable;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.Utils;

public class ConversationPriorityPreference extends Preference {
    private View mAlertButton;
    private Context mContext;
    private int mImportance;
    private boolean mIsConfigurable = true;
    private int mOriginalImportance;
    private View mPriorityButton;
    private boolean mPriorityConversation;
    private View mSilenceButton;
    Drawable selectedBackground;
    Drawable unselectedBackground;

    public ConversationPriorityPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    public ConversationPriorityPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public ConversationPriorityPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ConversationPriorityPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.selectedBackground = context.getDrawable(R$drawable.button_border_selected);
        this.unselectedBackground = this.mContext.getDrawable(R$drawable.button_border_unselected);
        setLayoutResource(R$layout.notif_priority_conversation_preference);
    }

    public void setImportance(int i) {
        this.mImportance = i;
    }

    public void setConfigurable(boolean z) {
        this.mIsConfigurable = z;
    }

    public void setPriorityConversation(boolean z) {
        this.mPriorityConversation = z;
    }

    public void setOriginalImportance(int i) {
        this.mOriginalImportance = i;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setClickable(false);
        this.mSilenceButton = preferenceViewHolder.findViewById(R$id.silence);
        this.mAlertButton = preferenceViewHolder.findViewById(R$id.alert);
        this.mPriorityButton = preferenceViewHolder.findViewById(R$id.priority_group);
        if (!this.mIsConfigurable) {
            this.mSilenceButton.setEnabled(false);
            this.mAlertButton.setEnabled(false);
            this.mPriorityButton.setEnabled(false);
        }
        updateToggles((ViewGroup) preferenceViewHolder.itemView, this.mImportance, this.mPriorityConversation, false);
        this.mSilenceButton.setOnClickListener(new View.OnClickListener(preferenceViewHolder) {
            /* class com.android.settings.notification.app.$$Lambda$ConversationPriorityPreference$K72F6HovLWgM44HfChgcbgJhSBM */
            public final /* synthetic */ PreferenceViewHolder f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ConversationPriorityPreference.this.lambda$onBindViewHolder$0$ConversationPriorityPreference(this.f$1, view);
            }
        });
        this.mAlertButton.setOnClickListener(new View.OnClickListener(preferenceViewHolder) {
            /* class com.android.settings.notification.app.$$Lambda$ConversationPriorityPreference$S2ys7PNbWbZyeA0QpzyJJY_5qA */
            public final /* synthetic */ PreferenceViewHolder f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ConversationPriorityPreference.this.lambda$onBindViewHolder$1$ConversationPriorityPreference(this.f$1, view);
            }
        });
        this.mPriorityButton.setOnClickListener(new View.OnClickListener(preferenceViewHolder) {
            /* class com.android.settings.notification.app.$$Lambda$ConversationPriorityPreference$l2VPoHMAmob0qs4QBlqvw5C_2Ik */
            public final /* synthetic */ PreferenceViewHolder f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ConversationPriorityPreference.this.lambda$onBindViewHolder$2$ConversationPriorityPreference(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$ConversationPriorityPreference(PreferenceViewHolder preferenceViewHolder, View view) {
        callChangeListener(new Pair(2, Boolean.FALSE));
        updateToggles((ViewGroup) preferenceViewHolder.itemView, 2, false, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$1 */
    public /* synthetic */ void lambda$onBindViewHolder$1$ConversationPriorityPreference(PreferenceViewHolder preferenceViewHolder, View view) {
        int max = Math.max(this.mOriginalImportance, 3);
        callChangeListener(new Pair(Integer.valueOf(max), Boolean.FALSE));
        updateToggles((ViewGroup) preferenceViewHolder.itemView, max, false, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$2 */
    public /* synthetic */ void lambda$onBindViewHolder$2$ConversationPriorityPreference(PreferenceViewHolder preferenceViewHolder, View view) {
        int max = Math.max(this.mOriginalImportance, 3);
        callChangeListener(new Pair(Integer.valueOf(max), Boolean.TRUE));
        updateToggles((ViewGroup) preferenceViewHolder.itemView, max, true, true);
    }

    private ColorStateList getAccentTint() {
        return Utils.getColorAccent(getContext());
    }

    private ColorStateList getRegularTint() {
        return Utils.getColorAttr(getContext(), 16842806);
    }

    /* access modifiers changed from: package-private */
    public void updateToggles(ViewGroup viewGroup, int i, boolean z, boolean z2) {
        if (z2) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(100L);
            TransitionManager.beginDelayedTransition(viewGroup, autoTransition);
        }
        ColorStateList accentTint = getAccentTint();
        ColorStateList regularTint = getRegularTint();
        ImageView imageView = (ImageView) viewGroup.findViewById(R$id.silence_icon);
        TextView textView = (TextView) viewGroup.findViewById(R$id.silence_label);
        TextView textView2 = (TextView) viewGroup.findViewById(R$id.silence_summary);
        ImageView imageView2 = (ImageView) viewGroup.findViewById(R$id.alert_icon);
        TextView textView3 = (TextView) viewGroup.findViewById(R$id.alert_label);
        TextView textView4 = (TextView) viewGroup.findViewById(R$id.alert_summary);
        ImageView imageView3 = (ImageView) viewGroup.findViewById(R$id.priority_icon);
        TextView textView5 = (TextView) viewGroup.findViewById(R$id.priority_label);
        TextView textView6 = (TextView) viewGroup.findViewById(R$id.priority_summary);
        if (i <= 2 && i > -1000) {
            textView4.setVisibility(8);
            imageView2.setImageTintList(regularTint);
            textView3.setTextColor(regularTint);
            textView6.setVisibility(8);
            imageView3.setImageTintList(regularTint);
            textView5.setTextColor(regularTint);
            imageView.setImageTintList(accentTint);
            textView.setTextColor(accentTint);
            textView2.setVisibility(0);
            this.mAlertButton.setBackground(this.unselectedBackground);
            this.mPriorityButton.setBackground(this.unselectedBackground);
            this.mSilenceButton.setBackground(this.selectedBackground);
            viewGroup.post(new Runnable() {
                /* class com.android.settings.notification.app.$$Lambda$ConversationPriorityPreference$w7FPnqM1HNJ343rNh5LUWHeYeM0 */

                public final void run() {
                    ConversationPriorityPreference.this.lambda$updateToggles$3$ConversationPriorityPreference();
                }
            });
        } else if (z) {
            textView4.setVisibility(8);
            imageView2.setImageTintList(regularTint);
            textView3.setTextColor(regularTint);
            textView6.setVisibility(0);
            imageView3.setImageTintList(accentTint);
            textView5.setTextColor(accentTint);
            imageView.setImageTintList(regularTint);
            textView.setTextColor(regularTint);
            textView2.setVisibility(8);
            this.mAlertButton.setBackground(this.unselectedBackground);
            this.mPriorityButton.setBackground(this.selectedBackground);
            this.mSilenceButton.setBackground(this.unselectedBackground);
            viewGroup.post(new Runnable() {
                /* class com.android.settings.notification.app.$$Lambda$ConversationPriorityPreference$YGuDDUlCrxtfCedBviQ89woD_I */

                public final void run() {
                    ConversationPriorityPreference.this.lambda$updateToggles$4$ConversationPriorityPreference();
                }
            });
        } else {
            textView4.setVisibility(0);
            imageView2.setImageTintList(accentTint);
            textView3.setTextColor(accentTint);
            textView6.setVisibility(8);
            imageView3.setImageTintList(regularTint);
            textView5.setTextColor(regularTint);
            imageView.setImageTintList(regularTint);
            textView.setTextColor(regularTint);
            textView2.setVisibility(8);
            this.mAlertButton.setBackground(this.selectedBackground);
            this.mPriorityButton.setBackground(this.unselectedBackground);
            this.mSilenceButton.setBackground(this.unselectedBackground);
            viewGroup.post(new Runnable() {
                /* class com.android.settings.notification.app.$$Lambda$ConversationPriorityPreference$AnZ07IIuetUw_TeDv44U7RxSEgg */

                public final void run() {
                    ConversationPriorityPreference.this.lambda$updateToggles$5$ConversationPriorityPreference();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateToggles$3 */
    public /* synthetic */ void lambda$updateToggles$3$ConversationPriorityPreference() {
        this.mSilenceButton.setSelected(true);
        this.mAlertButton.setSelected(false);
        this.mPriorityButton.setSelected(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateToggles$4 */
    public /* synthetic */ void lambda$updateToggles$4$ConversationPriorityPreference() {
        this.mSilenceButton.setSelected(false);
        this.mAlertButton.setSelected(false);
        this.mPriorityButton.setSelected(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateToggles$5 */
    public /* synthetic */ void lambda$updateToggles$5$ConversationPriorityPreference() {
        this.mSilenceButton.setSelected(false);
        this.mAlertButton.setSelected(true);
        this.mPriorityButton.setSelected(false);
    }
}
