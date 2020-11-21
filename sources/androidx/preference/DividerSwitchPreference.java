package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import androidx.preference.PreferenceManager;

public class DividerSwitchPreference extends SwitchPreference implements View.OnClickListener {
    private View.OnClickListener mOnClickListener;
    private Switch mSwitch;

    public DividerSwitchPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.dividerSwitchPreferenceStyle);
    }

    public DividerSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public DividerSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOnClickListener = new View.OnClickListener() {
            /* class androidx.preference.DividerSwitchPreference.AnonymousClass1 */

            public void onClick(View view) {
                PreferenceManager.OnPreferenceTreeClickListener onPreferenceTreeClickListener;
                PreferenceManager preferenceManager = DividerSwitchPreference.this.getPreferenceManager();
                if ((preferenceManager == null || (onPreferenceTreeClickListener = preferenceManager.getOnPreferenceTreeClickListener()) == null || !onPreferenceTreeClickListener.onPreferenceTreeClick(DividerSwitchPreference.this)) && DividerSwitchPreference.this.getIntent() != null) {
                    DividerSwitchPreference.this.getContext().startActivity(DividerSwitchPreference.this.getIntent());
                }
            }
        };
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View.OnClickListener onClickListener;
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.left_layout);
        if (!(findViewById == null || (onClickListener = this.mOnClickListener) == null)) {
            findViewById.setOnClickListener(onClickListener);
        }
        View findViewById2 = preferenceViewHolder.findViewById(16908312);
        this.mSwitch = (Switch) preferenceViewHolder.findViewById(R$id.switchWidget);
        if (findViewById2 != null) {
            findViewById2.setOnClickListener(this);
        }
    }

    public void onClick(View view) {
        Switch r1 = this.mSwitch;
        if (r1 == null || r1.isEnabled()) {
            setChecked(!this.mChecked);
            if (!callChangeListener(Boolean.valueOf(this.mChecked))) {
                setChecked(!this.mChecked);
            } else {
                persistBoolean(this.mChecked);
            }
            doVibrate();
        }
    }
}
