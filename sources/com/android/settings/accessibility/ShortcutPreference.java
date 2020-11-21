package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class ShortcutPreference extends Preference {
    private boolean mChecked = false;
    private OnClickCallback mClickCallback = null;
    private boolean mSettingsEditable = true;

    public interface OnClickCallback {
        void onSettingsClicked(ShortcutPreference shortcutPreference);

        void onToggleClicked(ShortcutPreference shortcutPreference);
    }

    ShortcutPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.accessibility_shortcut_secondary_action);
        setWidgetLayoutResource(C0012R$layout.preference_widget_master_switch);
        setIconSpaceReserved(true);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(16843534, typedValue, true);
        LinearLayout linearLayout = (LinearLayout) preferenceViewHolder.itemView.findViewById(C0010R$id.main_frame);
        if (linearLayout != null) {
            linearLayout.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$ShortcutPreference$HwfIFgKzs0EYzwAYg5NZOcrko */

                public final void onClick(View view) {
                    ShortcutPreference.this.lambda$onBindViewHolder$0$ShortcutPreference(view);
                }
            });
            linearLayout.setClickable(this.mSettingsEditable);
            linearLayout.setFocusable(this.mSettingsEditable);
        }
        SwitchCompat switchCompat = (SwitchCompat) preferenceViewHolder.itemView.findViewById(C0010R$id.switchWidget);
        int i = 0;
        if (switchCompat != null) {
            switchCompat.setOnTouchListener($$Lambda$ShortcutPreference$CTM9SMR4NJJ3guFmvjfJB8ZEAa4.INSTANCE);
            switchCompat.setContentDescription(getContext().getText(C0017R$string.accessibility_shortcut_settings));
            switchCompat.setChecked(this.mChecked);
            switchCompat.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$ShortcutPreference$QTeY5J7QfhyRw6VfOpZeKFiJSd0 */

                public final void onClick(View view) {
                    ShortcutPreference.this.lambda$onBindViewHolder$2$ShortcutPreference(view);
                }
            });
            switchCompat.setClickable(this.mSettingsEditable);
            switchCompat.setFocusable(this.mSettingsEditable);
            switchCompat.setBackgroundResource(this.mSettingsEditable ? typedValue.resourceId : 0);
        }
        View findViewById = preferenceViewHolder.itemView.findViewById(C0010R$id.divider);
        if (findViewById != null) {
            if (!this.mSettingsEditable) {
                i = 8;
            }
            findViewById.setVisibility(i);
        }
        preferenceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$ShortcutPreference$aJSdEhKa_jw0aOZnONmemk8TwF8 */

            public final void onClick(View view) {
                ShortcutPreference.this.lambda$onBindViewHolder$3$ShortcutPreference(view);
            }
        });
        preferenceViewHolder.itemView.setClickable(!this.mSettingsEditable);
        preferenceViewHolder.itemView.setFocusable(!this.mSettingsEditable);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$ShortcutPreference(View view) {
        callOnSettingsClicked();
    }

    static /* synthetic */ boolean lambda$onBindViewHolder$1(View view, MotionEvent motionEvent) {
        return motionEvent.getActionMasked() == 2;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$2 */
    public /* synthetic */ void lambda$onBindViewHolder$2$ShortcutPreference(View view) {
        callOnToggleClicked();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$3 */
    public /* synthetic */ void lambda$onBindViewHolder$3$ShortcutPreference(View view) {
        callOnToggleClicked();
    }

    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            notifyChanged();
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void setSettingsEditable(boolean z) {
        if (this.mSettingsEditable != z) {
            this.mSettingsEditable = z;
            notifyChanged();
        }
    }

    public boolean isSettingsEditable() {
        return this.mSettingsEditable;
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.mClickCallback = onClickCallback;
    }

    private void callOnSettingsClicked() {
        OnClickCallback onClickCallback = this.mClickCallback;
        if (onClickCallback != null) {
            onClickCallback.onSettingsClicked(this);
        }
    }

    private void callOnToggleClicked() {
        setChecked(!this.mChecked);
        OnClickCallback onClickCallback = this.mClickCallback;
        if (onClickCallback != null) {
            onClickCallback.onToggleClicked(this);
        }
    }
}
