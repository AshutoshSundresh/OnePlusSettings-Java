package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.internal.R;
import com.android.settings.R$styleable;
import com.android.settingslib.RestrictedPreference;

public class SeekBarPreference extends RestrictedPreference implements SeekBar.OnSeekBarChangeListener, View.OnKeyListener {
    private int mAccessibilityRangeInfoType;
    private boolean mContinuousUpdates;
    private int mDefaultProgress;
    private int mMax;
    private int mMin;
    private int mProgress;
    private SeekBar mSeekBar;
    private CharSequence mSeekBarContentDescription;
    private boolean mShouldBlink;
    private boolean mTrackingTouch;

    @Override // androidx.preference.Preference
    public CharSequence getSummary() {
        return null;
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mDefaultProgress = -1;
        this.mAccessibilityRangeInfoType = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ProgressBar, i, i2);
        setMax(obtainStyledAttributes.getInt(2, this.mMax));
        setMin(obtainStyledAttributes.getInt(26, this.mMin));
        obtainStyledAttributes.recycle();
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.SeekBarPreference, i, i2);
        int resourceId = obtainStyledAttributes2.getResourceId(0, 17367261);
        obtainStyledAttributes2.recycle();
        TypedArray obtainStyledAttributes3 = context.obtainStyledAttributes(attributeSet, R.styleable.Preference, i, i2);
        setSelectable(obtainStyledAttributes3.getBoolean(R$styleable.Preference_android_selectable, false));
        obtainStyledAttributes3.recycle();
        setLayoutResource(resourceId);
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.seekBarPreferenceStyle, 17957071));
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public boolean isSelectable() {
        if (isDisabledByAdmin()) {
            return true;
        }
        return super.isSelectable();
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setOnKeyListener(this);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(16909402);
        this.mSeekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(this);
        this.mSeekBar.setMax(this.mMax);
        this.mSeekBar.setMin(this.mMin);
        this.mSeekBar.setProgress(this.mProgress);
        this.mSeekBar.setEnabled(isEnabled());
        CharSequence title = getTitle();
        if (!TextUtils.isEmpty(this.mSeekBarContentDescription)) {
            this.mSeekBar.setContentDescription(this.mSeekBarContentDescription);
        } else if (!TextUtils.isEmpty(title)) {
            this.mSeekBar.setContentDescription(title);
        }
        SeekBar seekBar2 = this.mSeekBar;
        if (seekBar2 instanceof DefaultIndicatorSeekBar) {
            ((DefaultIndicatorSeekBar) seekBar2).setDefaultProgress(this.mDefaultProgress);
        }
        if (this.mShouldBlink) {
            View view = preferenceViewHolder.itemView;
            view.post(new Runnable(view) {
                /* class com.android.settings.widget.$$Lambda$SeekBarPreference$dLBfCJMqk38mmQ3tQYpIyDA0S8 */
                public final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SeekBarPreference.this.lambda$onBindViewHolder$0$SeekBarPreference(this.f$1);
                }
            });
        }
        this.mSeekBar.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            /* class com.android.settings.widget.SeekBarPreference.AnonymousClass1 */

            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                AccessibilityNodeInfo.RangeInfo rangeInfo = accessibilityNodeInfo.getRangeInfo();
                if (rangeInfo != null) {
                    accessibilityNodeInfo.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(SeekBarPreference.this.mAccessibilityRangeInfoType, rangeInfo.getMin(), rangeInfo.getMax(), rangeInfo.getCurrent()));
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$SeekBarPreference(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setHotspot((float) (view.getWidth() / 2), (float) (view.getHeight() / 2));
        }
        view.setPressed(true);
        view.setPressed(false);
        this.mShouldBlink = false;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onSetInitialValue(boolean z, Object obj) {
        int i;
        if (z) {
            i = getPersistedInt(this.mProgress);
        } else {
            i = ((Integer) obj).intValue();
        }
        setProgress(i);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Object onGetDefaultValue(TypedArray typedArray, int i) {
        return Integer.valueOf(typedArray.getInt(i, 0));
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        SeekBar seekBar;
        if (keyEvent.getAction() == 0 && (seekBar = (SeekBar) view.findViewById(16909402)) != null) {
            return seekBar.onKeyDown(i, keyEvent);
        }
        return false;
    }

    public void setMax(int i) {
        if (i != this.mMax) {
            this.mMax = i;
            notifyChanged();
        }
    }

    public void setMin(int i) {
        if (i != this.mMin) {
            this.mMin = i;
            notifyChanged();
        }
    }

    public int getMax() {
        return this.mMax;
    }

    public int getMin() {
        return this.mMin;
    }

    public void setProgress(int i) {
        setProgress(i, true);
    }

    public void setContinuousUpdates(boolean z) {
        this.mContinuousUpdates = z;
    }

    private void setProgress(int i, boolean z) {
        int i2 = this.mMax;
        if (i > i2) {
            i = i2;
        }
        int i3 = this.mMin;
        if (i < i3) {
            i = i3;
        }
        if (i != this.mProgress) {
            this.mProgress = i;
            persistInt(i);
            if (z) {
                notifyChanged();
            }
        }
    }

    public int getProgress() {
        return this.mProgress;
    }

    /* access modifiers changed from: package-private */
    public void syncProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress == this.mProgress) {
            return;
        }
        if (callChangeListener(Integer.valueOf(progress))) {
            setProgress(progress, false);
        } else {
            seekBar.setProgress(this.mProgress);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (!z) {
            return;
        }
        if (this.mContinuousUpdates || !this.mTrackingTouch) {
            syncProgress(seekBar);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mTrackingTouch = true;
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mTrackingTouch = false;
        if (seekBar.getProgress() != this.mProgress) {
            syncProgress(seekBar);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.progress = this.mProgress;
        savedState.max = this.mMax;
        savedState.min = this.mMin;
        return savedState;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mProgress = savedState.progress;
        this.mMax = savedState.max;
        this.mMin = savedState.min;
        notifyChanged();
    }

    /* access modifiers changed from: private */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class com.android.settings.widget.SeekBarPreference.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int max;
        int min;
        int progress;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.progress = parcel.readInt();
            this.max = parcel.readInt();
            this.min = parcel.readInt();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.progress);
            parcel.writeInt(this.max);
            parcel.writeInt(this.min);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }
}
