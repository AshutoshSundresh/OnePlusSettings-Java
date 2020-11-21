package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class UsbDefaultFragment extends RadioButtonPickerFragment {
    ConnectivityManager mConnectivityManager;
    long mCurrentFunctions;
    boolean mIsStartTethering = false;
    OnStartTetheringCallback mOnStartTetheringCallback = new OnStartTetheringCallback();
    long mPreviousFunctions;
    UsbBackend mUsbBackend;
    UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() {
        /* class com.android.settings.connecteddevice.usb.$$Lambda$UsbDefaultFragment$lLBUVrJXXgaOkJZIgA1Iy1UjZw */

        @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
        public final void onUsbConnectionChanged(boolean z, long j, int i, int i2) {
            UsbDefaultFragment.this.lambda$new$0$UsbDefaultFragment(z, j, i, i2);
        }
    };
    private UsbConnectionBroadcastReceiver mUsbReceiver;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1312;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$UsbDefaultFragment(boolean z, long j, int i, int i2) {
        if (this.mIsStartTethering) {
            this.mCurrentFunctions = j;
            refresh(j);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUsbBackend = new UsbBackend(context);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        getSettingsLifecycle().addObserver(this.mUsbReceiver);
        this.mCurrentFunctions = this.mUsbBackend.getDefaultUsbFunctions();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        FooterPreference.Builder builder = new FooterPreference.Builder(getActivity());
        builder.setTitle(C0017R$string.usb_default_info);
        preferenceScreen.addPreference(builder.build());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.usb_default_fragment;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<? extends CandidateInfo> getCandidates() {
        ArrayList newArrayList = Lists.newArrayList();
        for (Long l : UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            final String string = getContext().getString(UsbDetailsFunctionsController.FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue());
            final String usbFunctionsToString = UsbBackend.usbFunctionsToString(longValue);
            if (this.mUsbBackend.areFunctionsSupported(longValue)) {
                newArrayList.add(new CandidateInfo(this, true) {
                    /* class com.android.settings.connecteddevice.usb.UsbDefaultFragment.AnonymousClass1 */

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public Drawable loadIcon() {
                        return null;
                    }

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public CharSequence loadLabel() {
                        return string;
                    }

                    @Override // com.android.settingslib.widget.CandidateInfo
                    public String getKey() {
                        return usbFunctionsToString;
                    }
                });
            }
        }
        return newArrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return UsbBackend.usbFunctionsToString(this.mUsbBackend.getDefaultUsbFunctions());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(str);
        this.mPreviousFunctions = this.mUsbBackend.getCurrentFunctions();
        if (!Utils.isMonkeyRunning()) {
            if (usbFunctionsFromString == 32) {
                this.mIsStartTethering = true;
                this.mConnectivityManager.startTethering(1, true, this.mOnStartTetheringCallback);
            } else {
                this.mIsStartTethering = false;
                this.mCurrentFunctions = usbFunctionsFromString;
                this.mUsbBackend.setDefaultUsbFunctions(usbFunctionsFromString);
            }
        }
        return true;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mUsbBackend.setDefaultUsbFunctions(this.mCurrentFunctions);
    }

    final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringStarted() {
            UsbDefaultFragment.super.onTetheringStarted();
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mCurrentFunctions = 32;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(32);
        }

        public void onTetheringFailed() {
            UsbDefaultFragment.super.onTetheringFailed();
            UsbDefaultFragment usbDefaultFragment = UsbDefaultFragment.this;
            usbDefaultFragment.mUsbBackend.setDefaultUsbFunctions(usbDefaultFragment.mPreviousFunctions);
            UsbDefaultFragment.this.updateCandidates();
        }
    }

    private void refresh(long j) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (Long l : UsbDetailsFunctionsController.FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(UsbBackend.usbFunctionsToString(longValue));
            if (radioButtonPreference != null) {
                boolean areFunctionsSupported = this.mUsbBackend.areFunctionsSupported(longValue);
                radioButtonPreference.setEnabled(areFunctionsSupported);
                if (areFunctionsSupported) {
                    radioButtonPreference.setChecked(j == longValue);
                }
            }
        }
    }
}
