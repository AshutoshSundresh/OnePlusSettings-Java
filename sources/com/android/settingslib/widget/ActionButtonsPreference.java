package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class ActionButtonsPreference extends Preference {
    private final ButtonInfo mButton1Info = new ButtonInfo();
    private final ButtonInfo mButton2Info = new ButtonInfo();
    private final ButtonInfo mButton3Info = new ButtonInfo();
    private final ButtonInfo mButton4Info = new ButtonInfo();

    public ActionButtonsPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    public ActionButtonsPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public ActionButtonsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ActionButtonsPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutResource(C0012R$layout.op_settings_action_buttons);
        setSelectable(false);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(true);
        preferenceViewHolder.setDividerAllowedBelow(true);
        this.mButton1Info.mButton = (Button) preferenceViewHolder.findViewById(C0010R$id.button1);
        this.mButton2Info.mButton = (Button) preferenceViewHolder.findViewById(C0010R$id.button2);
        this.mButton3Info.mButton = (Button) preferenceViewHolder.findViewById(C0010R$id.button3);
        this.mButton4Info.mButton = (Button) preferenceViewHolder.findViewById(C0010R$id.button4);
        this.mButton1Info.setUpButton();
        this.mButton2Info.setUpButton();
        this.mButton3Info.setUpButton();
        this.mButton4Info.setUpButton();
    }

    public ActionButtonsPreference setButton1Visible(boolean z) {
        if (z != this.mButton1Info.mIsVisible) {
            this.mButton1Info.mIsVisible = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton1Text(int i) {
        String string = getContext().getString(i);
        if (!TextUtils.equals(string, this.mButton1Info.mText)) {
            this.mButton1Info.mText = string;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton1Icon(int i) {
        if (i == 0) {
            return this;
        }
        try {
            this.mButton1Info.mIcon = getContext().getDrawable(i);
            notifyChanged();
        } catch (Resources.NotFoundException unused) {
            Log.e("ActionButtonPreference", "Resource does not exist: " + i);
        }
        return this;
    }

    public ActionButtonsPreference setButton1Enabled(boolean z) {
        if (z != this.mButton1Info.mIsEnabled) {
            this.mButton1Info.mIsEnabled = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton1OnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != this.mButton1Info.mListener) {
            this.mButton1Info.mListener = onClickListener;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton2Visible(boolean z) {
        if (z != this.mButton2Info.mIsVisible) {
            this.mButton2Info.mIsVisible = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton2Text(int i) {
        String string = getContext().getString(i);
        if (!TextUtils.equals(string, this.mButton2Info.mText)) {
            this.mButton2Info.mText = string;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton2Icon(int i) {
        if (i == 0) {
            return this;
        }
        try {
            this.mButton2Info.mIcon = getContext().getDrawable(i);
            notifyChanged();
        } catch (Resources.NotFoundException unused) {
            Log.e("ActionButtonPreference", "Resource does not exist: " + i);
        }
        return this;
    }

    public ActionButtonsPreference setButton2Enabled(boolean z) {
        if (z != this.mButton2Info.mIsEnabled) {
            this.mButton2Info.mIsEnabled = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton2OnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != this.mButton2Info.mListener) {
            this.mButton2Info.mListener = onClickListener;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton3Visible(boolean z) {
        if (z != this.mButton3Info.mIsVisible) {
            this.mButton3Info.mIsVisible = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton3Text(int i) {
        String string = getContext().getString(i);
        if (!TextUtils.equals(string, this.mButton3Info.mText)) {
            this.mButton3Info.mText = string;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton3Icon(int i) {
        if (i == 0) {
            return this;
        }
        try {
            this.mButton3Info.mIcon = getContext().getDrawable(i);
            notifyChanged();
        } catch (Resources.NotFoundException unused) {
            Log.e("ActionButtonPreference", "Resource does not exist: " + i);
        }
        return this;
    }

    public ActionButtonsPreference setButton3Enabled(boolean z) {
        if (z != this.mButton3Info.mIsEnabled) {
            this.mButton3Info.mIsEnabled = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton3OnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != this.mButton3Info.mListener) {
            this.mButton3Info.mListener = onClickListener;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton4Visible(boolean z) {
        if (z != this.mButton4Info.mIsVisible) {
            this.mButton4Info.mIsVisible = z;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton4Text(int i) {
        String string = getContext().getString(i);
        if (!TextUtils.equals(string, this.mButton4Info.mText)) {
            this.mButton4Info.mText = string;
            notifyChanged();
        }
        return this;
    }

    public ActionButtonsPreference setButton4Icon(int i) {
        if (i == 0) {
            return this;
        }
        try {
            this.mButton4Info.mIcon = getContext().getDrawable(i);
            notifyChanged();
        } catch (Resources.NotFoundException unused) {
            Log.e("ActionButtonPreference", "Resource does not exist: " + i);
        }
        return this;
    }

    public ActionButtonsPreference setButton4OnClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != this.mButton4Info.mListener) {
            this.mButton4Info.mListener = onClickListener;
            notifyChanged();
        }
        return this;
    }

    static class ButtonInfo {
        private Button mButton;
        private Drawable mIcon;
        private boolean mIsEnabled = true;
        private boolean mIsVisible = true;
        private View.OnClickListener mListener;
        private CharSequence mText;

        ButtonInfo() {
        }

        /* access modifiers changed from: package-private */
        public void setUpButton() {
            this.mButton.setText(this.mText);
            this.mButton.setOnClickListener(this.mListener);
            this.mButton.setEnabled(this.mIsEnabled);
            this.mButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, this.mIcon, (Drawable) null, (Drawable) null);
            if (shouldBeVisible()) {
                this.mButton.setVisibility(0);
            } else {
                this.mButton.setVisibility(8);
            }
        }

        private boolean shouldBeVisible() {
            return this.mIsVisible && (!TextUtils.isEmpty(this.mText) || this.mIcon != null);
        }
    }
}
