package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.C0004R$attr;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class SwitchBar extends LinearLayout implements CompoundButton.OnCheckedChangeListener {
    private static final int[] XML_ATTRIBUTES = {C0004R$attr.switchBarMarginStart, C0004R$attr.switchBarMarginEnd, C0004R$attr.switchBarBackgroundColor, C0004R$attr.switchBarBackgroundActivatedColor, C0004R$attr.switchBarRestrictionIcon};
    private int mBackgroundActivatedColor;
    private int mBackgroundColor;
    private Context mContext;
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private String mLabel;
    private boolean mLoggingIntialized;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mMetricsTag;
    private String mOffText;
    private String mOnText;
    private ImageView mRestrictedIcon;
    private String mSummary;
    private final TextAppearanceSpan mSummarySpan;
    private ToggleSwitch mSwitch;
    private final List<OnSwitchChangeListener> mSwitchChangeListeners;
    private TextView mTextView;

    public interface OnSwitchChangeListener {
        void onSwitchChanged(Switch v, boolean z);
    }

    public SwitchBar(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public SwitchBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        this.mContext = context;
    }

    public SwitchBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
        this.mContext = context;
    }

    public SwitchBar(final Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSwitchChangeListeners = new ArrayList();
        this.mEnforcedAdmin = null;
        this.mContext = context;
        LayoutInflater.from(context).inflate(C0012R$layout.switch_bar, this);
        setFocusable(true);
        setClickable(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XML_ATTRIBUTES);
        this.mBackgroundColor = obtainStyledAttributes.getColor(2, 0);
        this.mBackgroundActivatedColor = obtainStyledAttributes.getColor(3, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(4);
        obtainStyledAttributes.recycle();
        this.mTextView = (TextView) findViewById(C0010R$id.switch_text);
        this.mSummarySpan = new TextAppearanceSpan(this.mContext, C0018R$style.TextAppearance_Small_SwitchBar);
        ((ViewGroup.MarginLayoutParams) this.mTextView.getLayoutParams()).setMarginStart((int) obtainStyledAttributes.getDimension(0, 0.0f));
        ToggleSwitch toggleSwitch = (ToggleSwitch) findViewById(C0010R$id.switch_widget);
        this.mSwitch = toggleSwitch;
        toggleSwitch.setSaveEnabled(false);
        this.mSwitch.setFocusable(false);
        this.mSwitch.setClickable(false);
        ((ViewGroup.MarginLayoutParams) this.mSwitch.getLayoutParams()).setMarginEnd((int) obtainStyledAttributes.getDimension(1, 0.0f));
        setBackgroundColor(this.mBackgroundColor);
        setSwitchBarText(C0017R$string.switch_on_text, C0017R$string.switch_off_text);
        addOnSwitchChangeListener(new OnSwitchChangeListener() {
            /* class com.android.settings.widget.$$Lambda$SwitchBar$xcPsCGGwUScwZOtx6bxg2zuPXc8 */

            @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                SwitchBar.this.lambda$new$0$SwitchBar(r1, z);
            }
        });
        ImageView imageView = (ImageView) findViewById(C0010R$id.restricted_icon);
        this.mRestrictedIcon = imageView;
        imageView.setImageDrawable(drawable);
        this.mRestrictedIcon.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.widget.SwitchBar.AnonymousClass1 */

            public void onClick(View view) {
                if (SwitchBar.this.mDisabledByAdmin) {
                    MetricsFeatureProvider metricsFeatureProvider = SwitchBar.this.mMetricsFeatureProvider;
                    metricsFeatureProvider.action(0, 853, 0, SwitchBar.this.mMetricsTag + "/switch_bar|restricted", 1);
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(context, SwitchBar.this.mEnforcedAdmin);
                }
            }
        });
        setVisibility(8);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$SwitchBar(Switch r1, boolean z) {
        setTextViewLabelAndBackground(z);
    }

    public boolean performClick() {
        Context context = this.mContext;
        if (context != null) {
            OPUtils.startVibratePattern(context);
        }
        return getDelegatingView().performClick();
    }

    public void setMetricsTag(String str) {
        this.mMetricsTag = str;
    }

    public void setTextViewLabelAndBackground(boolean z) {
        this.mLabel = z ? this.mOnText : this.mOffText;
        setBackgroundColor(z ? this.mBackgroundActivatedColor : this.mBackgroundColor);
        updateText();
    }

    public void setSwitchBarText(int i, int i2) {
        this.mOnText = getResources().getString(i);
        this.mOffText = getResources().getString(i2);
        setTextViewLabelAndBackground(isChecked());
    }

    public void setSwitchBarText(String str, String str2) {
        this.mOnText = str;
        this.mOffText = str2;
        setTextViewLabelAndBackground(isChecked());
    }

    public void setSummary(String str) {
        this.mSummary = str;
        updateText();
    }

    private void updateText() {
        if (TextUtils.isEmpty(this.mSummary)) {
            this.mTextView.setText(this.mLabel);
            return;
        }
        SpannableStringBuilder append = new SpannableStringBuilder(this.mLabel).append('\n');
        int length = append.length();
        append.append((CharSequence) this.mSummary);
        append.setSpan(this.mSummarySpan, length, append.length(), 0);
        this.mTextView.setText(append);
    }

    public void setChecked(boolean z) {
        setTextViewLabelAndBackground(z);
        this.mSwitch.setChecked(z);
    }

    public void setCheckedInternal(boolean z) {
        setTextViewLabelAndBackground(z);
        this.mSwitch.setCheckedInternal(z);
    }

    public boolean isChecked() {
        return this.mSwitch.isChecked();
    }

    public void setEnabled(boolean z) {
        if (!z || !this.mDisabledByAdmin) {
            super.setEnabled(z);
            this.mTextView.setEnabled(z);
            this.mSwitch.setEnabled(z);
            return;
        }
        setDisabledByAdmin(null);
    }

    /* access modifiers changed from: package-private */
    public View getDelegatingView() {
        return this.mDisabledByAdmin ? this.mRestrictedIcon : this.mSwitch;
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mEnforcedAdmin = enforcedAdmin;
        if (enforcedAdmin != null) {
            super.setEnabled(true);
            this.mDisabledByAdmin = true;
            this.mTextView.setEnabled(false);
            this.mSwitch.setEnabled(false);
            this.mSwitch.setVisibility(8);
            this.mRestrictedIcon.setVisibility(0);
            return;
        }
        this.mDisabledByAdmin = false;
        this.mSwitch.setVisibility(0);
        this.mRestrictedIcon.setVisibility(8);
        setEnabled(true);
    }

    public final ToggleSwitch getSwitch() {
        return this.mSwitch;
    }

    public void show() {
        if (!isShowing()) {
            setVisibility(0);
            this.mSwitch.setOnCheckedChangeListener(this);
        }
    }

    public void hide() {
        if (isShowing()) {
            setVisibility(8);
            this.mSwitch.setOnCheckedChangeListener(null);
        }
    }

    public boolean isShowing() {
        return getVisibility() == 0;
    }

    public void propagateChecked(boolean z) {
        int size = this.mSwitchChangeListeners.size();
        for (int i = 0; i < size; i++) {
            this.mSwitchChangeListeners.get(i).onSwitchChanged(this.mSwitch, z);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.mLoggingIntialized) {
            MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
            metricsFeatureProvider.action(0, 853, 0, this.mMetricsTag + "/switch_bar", z ? 1 : 0);
        }
        this.mLoggingIntialized = true;
        propagateChecked(z);
    }

    public void addOnSwitchChangeListener(OnSwitchChangeListener onSwitchChangeListener) {
        if (!this.mSwitchChangeListeners.contains(onSwitchChangeListener)) {
            this.mSwitchChangeListeners.add(onSwitchChangeListener);
            return;
        }
        throw new IllegalStateException("Cannot add twice the same OnSwitchChangeListener");
    }

    public void removeOnSwitchChangeListener(OnSwitchChangeListener onSwitchChangeListener) {
        if (this.mSwitchChangeListeners.contains(onSwitchChangeListener)) {
            this.mSwitchChangeListeners.remove(onSwitchChangeListener);
            return;
        }
        throw new IllegalStateException("Cannot remove OnSwitchChangeListener");
    }

    /* access modifiers changed from: package-private */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.android.settings.widget.SwitchBar.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean checked;
        boolean visible;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.checked = ((Boolean) parcel.readValue(null)).booleanValue();
            this.visible = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeValue(Boolean.valueOf(this.checked));
            parcel.writeValue(Boolean.valueOf(this.visible));
        }

        public String toString() {
            return "SwitchBar.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + " visible=" + this.visible + "}";
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.checked = this.mSwitch.isChecked();
        savedState.visible = isShowing();
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mSwitch.setCheckedInternal(savedState.checked);
        setTextViewLabelAndBackground(savedState.checked);
        setVisibility(savedState.visible ? 0 : 8);
        this.mSwitch.setOnCheckedChangeListener(savedState.visible ? this : null);
        requestLayout();
    }
}
