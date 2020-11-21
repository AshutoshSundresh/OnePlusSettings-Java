package com.android.settings.network;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0004R$attr;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;

public class ApnPreference extends Preference implements CompoundButton.OnCheckedChangeListener {
    private static CompoundButton mCurrentChecked;
    private static String mSelectedKey;
    private boolean mHideDetails;
    private boolean mProtectFromCheckedChange;
    private boolean mRadioEnable;
    private boolean mSelectable;
    private int mSubId;

    public ApnPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSubId = -1;
        this.mProtectFromCheckedChange = false;
        this.mSelectable = true;
        this.mHideDetails = false;
        this.mRadioEnable = true;
    }

    public ApnPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, C0004R$attr.apnPreferenceStyle);
    }

    public ApnPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.apn_radiobutton);
        if (findViewById != null && (findViewById instanceof RadioButton)) {
            RadioButton radioButton = (RadioButton) findViewById;
            if (this.mSelectable) {
                radioButton.setOnCheckedChangeListener(this);
                boolean equals = getKey().equals(mSelectedKey);
                if (equals) {
                    mCurrentChecked = radioButton;
                    mSelectedKey = getKey();
                }
                this.mProtectFromCheckedChange = true;
                radioButton.setChecked(equals);
                this.mProtectFromCheckedChange = false;
                radioButton.setVisibility(0);
                radioButton.setEnabled(this.mRadioEnable);
                return;
            }
            radioButton.setVisibility(8);
        }
    }

    public void setChecked() {
        mSelectedKey = getKey();
    }

    public static void setSelectedKey(String str) {
        mSelectedKey = str;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        Log.i("ApnPreference", "ID: " + getKey() + " :" + z);
        if (!this.mProtectFromCheckedChange) {
            if (z) {
                CompoundButton compoundButton2 = mCurrentChecked;
                if (compoundButton2 != null) {
                    compoundButton2.setChecked(false);
                }
                mCurrentChecked = compoundButton;
                String key = getKey();
                mSelectedKey = key;
                callChangeListener(key);
                return;
            }
            mCurrentChecked = null;
            mSelectedKey = null;
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        super.onClick();
        Context context = getContext();
        if (context == null) {
            return;
        }
        if (this.mHideDetails) {
            Toast.makeText(context, context.getString(C0017R$string.cannot_change_apn_toast), 1).show();
            return;
        }
        Intent intent = new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, (long) Integer.parseInt(getKey())));
        intent.putExtra("sub_id", this.mSubId);
        intent.addFlags(1);
        context.startActivity(intent);
    }

    @Override // androidx.preference.Preference
    public void setSelectable(boolean z) {
        this.mSelectable = z;
    }

    public void setRadioButtonEnable(boolean z) {
        this.mRadioEnable = z;
    }

    public void setSubId(int i) {
        this.mSubId = i;
    }

    public void setHideDetails() {
        this.mHideDetails = true;
    }
}
