package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.appcompat.R$attr;
import androidx.appcompat.R$styleable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.TintableBackgroundView;
import androidx.core.widget.TintableCompoundButton;

public class AppCompatCheckBox extends CheckBox implements TintableCompoundButton, TintableBackgroundView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private static final int[] INDETERMINATE_STATE_SET = {R$attr.state_indeterminate};
    private static final String TAG = AppCompatCheckBox.class.getSimpleName();
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    private boolean mBroadcasting;
    private boolean mChecked;
    private final AppCompatCompoundButtonHelper mCompoundButtonHelper;
    private boolean mIndeterminate;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private OnTriStateCheckedChangeListener mOnTriStateCheckedChangeListener;
    private final AppCompatTextHelper mTextHelper;
    private boolean mThreeState;

    public interface OnTriStateCheckedChangeListener {
        void onCheckedChanged(AppCompatCheckBox appCompatCheckBox, Boolean bool);
    }

    public AppCompatCheckBox(Context context) {
        this(context, null);
    }

    public AppCompatCheckBox(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.checkboxStyle);
    }

    public AppCompatCheckBox(Context context, AttributeSet attributeSet, int i) {
        super(TintContextWrapper.wrap(context), attributeSet, i);
        ThemeUtils.checkAppCompatTheme(this, getContext());
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = new AppCompatCompoundButtonHelper(this);
        this.mCompoundButtonHelper = appCompatCompoundButtonHelper;
        appCompatCompoundButtonHelper.loadFromAttributes(attributeSet, i);
        AppCompatBackgroundHelper appCompatBackgroundHelper = new AppCompatBackgroundHelper(this);
        this.mBackgroundTintHelper = appCompatBackgroundHelper;
        appCompatBackgroundHelper.loadFromAttributes(attributeSet, i);
        AppCompatTextHelper appCompatTextHelper = new AppCompatTextHelper(this);
        this.mTextHelper = appCompatTextHelper;
        appCompatTextHelper.loadFromAttributes(attributeSet, i);
        init(context, attributeSet, i);
    }

    @Override // android.widget.CompoundButton
    public void setButtonDrawable(Drawable drawable) {
        super.setButtonDrawable(drawable);
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = this.mCompoundButtonHelper;
        if (appCompatCompoundButtonHelper != null) {
            appCompatCompoundButtonHelper.onSetButtonDrawable();
        }
    }

    @Override // android.widget.CompoundButton
    public void setButtonDrawable(int i) {
        setButtonDrawable(AppCompatResources.getDrawable(getContext(), i));
    }

    public int getCompoundPaddingLeft() {
        int compoundPaddingLeft = super.getCompoundPaddingLeft();
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = this.mCompoundButtonHelper;
        return appCompatCompoundButtonHelper != null ? appCompatCompoundButtonHelper.getCompoundPaddingLeft(compoundPaddingLeft) : compoundPaddingLeft;
    }

    @Override // androidx.core.widget.TintableCompoundButton
    public void setSupportButtonTintList(ColorStateList colorStateList) {
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = this.mCompoundButtonHelper;
        if (appCompatCompoundButtonHelper != null) {
            appCompatCompoundButtonHelper.setSupportButtonTintList(colorStateList);
        }
    }

    @Override // androidx.core.widget.TintableCompoundButton
    public ColorStateList getSupportButtonTintList() {
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = this.mCompoundButtonHelper;
        if (appCompatCompoundButtonHelper != null) {
            return appCompatCompoundButtonHelper.getSupportButtonTintList();
        }
        return null;
    }

    @Override // androidx.core.widget.TintableCompoundButton
    public void setSupportButtonTintMode(PorterDuff.Mode mode) {
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = this.mCompoundButtonHelper;
        if (appCompatCompoundButtonHelper != null) {
            appCompatCompoundButtonHelper.setSupportButtonTintMode(mode);
        }
    }

    public PorterDuff.Mode getSupportButtonTintMode() {
        AppCompatCompoundButtonHelper appCompatCompoundButtonHelper = this.mCompoundButtonHelper;
        if (appCompatCompoundButtonHelper != null) {
            return appCompatCompoundButtonHelper.getSupportButtonTintMode();
        }
        return null;
    }

    @Override // androidx.core.view.TintableBackgroundView
    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            appCompatBackgroundHelper.setSupportBackgroundTintList(colorStateList);
        }
    }

    @Override // androidx.core.view.TintableBackgroundView
    public ColorStateList getSupportBackgroundTintList() {
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            return appCompatBackgroundHelper.getSupportBackgroundTintList();
        }
        return null;
    }

    @Override // androidx.core.view.TintableBackgroundView
    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            appCompatBackgroundHelper.setSupportBackgroundTintMode(mode);
        }
    }

    @Override // androidx.core.view.TintableBackgroundView
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            return appCompatBackgroundHelper.getSupportBackgroundTintMode();
        }
        return null;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            appCompatBackgroundHelper.onSetBackgroundDrawable(drawable);
        }
    }

    public void setBackgroundResource(int i) {
        super.setBackgroundResource(i);
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            appCompatBackgroundHelper.onSetBackgroundResource(i);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        AppCompatBackgroundHelper appCompatBackgroundHelper = this.mBackgroundTintHelper;
        if (appCompatBackgroundHelper != null) {
            appCompatBackgroundHelper.applySupportBackgroundTint();
        }
        AppCompatTextHelper appCompatTextHelper = this.mTextHelper;
        if (appCompatTextHelper != null) {
            appCompatTextHelper.applyCompoundDrawablesTints();
        }
    }

    public void init(Context context, AttributeSet attributeSet, int i) {
        Boolean bool;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.AppCompatCheckBox, i, 0);
        boolean z = obtainStyledAttributes.getBoolean(R$styleable.AppCompatCheckBox_threeState, false);
        boolean z2 = obtainStyledAttributes.getBoolean(R$styleable.AppCompatCheckBox_android_checked, false);
        boolean z3 = obtainStyledAttributes.getBoolean(R$styleable.AppCompatCheckBox_indeterminate, false);
        setThreeState(z);
        if (z3) {
            if (z3) {
                bool = null;
            } else {
                bool = Boolean.valueOf(z2);
            }
            setTriStateChecked(bool);
        } else {
            setCheckedInternal(z2);
        }
        obtainStyledAttributes.recycle();
    }

    public void toggle() {
        if (!this.mThreeState) {
            setCheckedInternal(!this.mChecked);
        }
    }

    public boolean performClick() {
        if (this.mThreeState) {
            if (this.mIndeterminate) {
                setTriStateChecked(Boolean.TRUE);
            } else {
                setTriStateChecked(Boolean.valueOf(!this.mChecked));
            }
        }
        boolean performClick = super.performClick();
        if (!performClick) {
            playSoundEffect(0);
        }
        return performClick;
    }

    public void setThreeState(boolean z) {
        this.mThreeState = z;
    }

    @ViewDebug.ExportedProperty
    public boolean isIndeterminate() {
        return this.mIndeterminate;
    }

    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    public void setChecked(boolean z) {
        setChecked(Boolean.valueOf(z));
    }

    public void setChecked(Boolean bool) {
        setTriStateChecked(bool);
    }

    public void setCheckedInternal(boolean z) {
        setCheckedInternal(z, false);
    }

    public void setCheckedInternal(boolean z, boolean z2) {
        boolean z3 = this.mChecked != z;
        if (z3 || z2) {
            this.mChecked = z;
            refreshDrawableState();
            notifyViewAccessibilityStateChangedIfNeededInternal(0);
            if (z3 && !this.mBroadcasting) {
                this.mBroadcasting = true;
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckedChanged(this, this.mChecked);
                }
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener2 = this.mOnCheckedChangeWidgetListener;
                if (onCheckedChangeListener2 != null) {
                    onCheckedChangeListener2.onCheckedChanged(this, this.mChecked);
                }
                this.mBroadcasting = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setTriStateChecked(Boolean bool) {
        if (this.mIndeterminate != (bool == null) || (bool != null && bool.booleanValue() != this.mChecked)) {
            this.mIndeterminate = bool == null;
            if (bool != null) {
                setCheckedInternal(bool.booleanValue(), true);
            } else {
                refreshDrawableState();
                notifyViewAccessibilityStateChangedIfNeededInternal(0);
            }
            if (!this.mBroadcasting) {
                this.mBroadcasting = true;
                OnTriStateCheckedChangeListener onTriStateCheckedChangeListener = this.mOnTriStateCheckedChangeListener;
                if (onTriStateCheckedChangeListener != null) {
                    onTriStateCheckedChangeListener.onCheckedChanged(this, bool);
                }
                this.mBroadcasting = false;
            }
        }
    }

    private void notifyViewAccessibilityStateChangedIfNeededInternal(int i) {
        try {
            Class.forName("android.view.View").getMethod("notifyViewAccessibilityStateChangedIfNeeded", Integer.TYPE).invoke(this, Integer.valueOf(i));
        } catch (Exception e) {
            Log.e(TAG, "notifyViewAccessibilityStateChangedIfNeeded with Exception!", e);
        }
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnTriStateCheckedChangeListener(OnTriStateCheckedChangeListener onTriStateCheckedChangeListener) {
        this.mOnTriStateCheckedChangeListener = onTriStateCheckedChangeListener;
    }

    /* access modifiers changed from: package-private */
    public void setOnCheckedChangeWidgetListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeWidgetListener = onCheckedChangeListener;
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (isIndeterminate()) {
            CheckBox.mergeDrawableStates(onCreateDrawableState, INDETERMINATE_STATE_SET);
        } else if (isChecked()) {
            CheckBox.mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    /* access modifiers changed from: package-private */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            /* class androidx.appcompat.widget.AppCompatCheckBox.SavedState.AnonymousClass1 */

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
        boolean indeterminate;
        boolean threeState;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.checked = ((Boolean) parcel.readValue(null)).booleanValue();
            this.threeState = ((Boolean) parcel.readValue(null)).booleanValue();
            this.indeterminate = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeValue(Boolean.valueOf(this.checked));
            parcel.writeValue(Boolean.valueOf(this.threeState));
            parcel.writeValue(Boolean.valueOf(this.indeterminate));
        }

        public String toString() {
            return "CompoundButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + ", indeterminate=" + this.indeterminate + ", threeState=" + this.threeState + "}";
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.checked = isChecked();
        savedState.threeState = this.mThreeState;
        savedState.indeterminate = this.mIndeterminate;
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        boolean z = savedState.threeState;
        this.mThreeState = z;
        if (z) {
            setTriStateChecked(savedState.indeterminate ? null : Boolean.valueOf(savedState.checked));
        } else {
            setCheckedInternal(savedState.checked);
        }
        requestLayout();
    }
}
