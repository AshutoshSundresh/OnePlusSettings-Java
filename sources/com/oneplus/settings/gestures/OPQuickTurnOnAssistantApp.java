package com.oneplus.settings.gestures;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPQuickTurnOnAssistantApp extends DashboardFragment implements RadioButtonPreference.OnClickListener {
    private static final Uri OPEN_WALLET_CP_URI = Uri.parse("content://finshell.wallet.quickstart.flag.provider.open/CARD_BAG_FLAG");
    private RadioButtonPreference mAskAlexa;
    private RadioButtonPreference mCamera;
    private Context mContext;
    private RadioButtonPreference mNone;
    private RadioButtonPreference mWallet;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPQuickTurnOnAssistantApp";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mContext = getActivity();
        this.mCamera = (RadioButtonPreference) findPreference("camera");
        this.mWallet = (RadioButtonPreference) findPreference("wallet");
        this.mNone = (RadioButtonPreference) findPreference("none");
        this.mAskAlexa = (RadioButtonPreference) findPreference("ask_alexa");
        this.mCamera.setOnClickListener(this);
        this.mWallet.setOnClickListener(this);
        this.mNone.setOnClickListener(this);
        this.mAskAlexa.setOnClickListener(this);
        boolean z = false;
        if (OPUtils.isO2()) {
            this.mWallet.setVisible(false);
        }
        RadioButtonPreference radioButtonPreference = this.mAskAlexa;
        if (OPUtils.isSupportAskAlexa() && OPUtils.isAppExist(this.mContext, "com.amazon.dee.app")) {
            z = true;
        }
        radioButtonPreference.setVisible(z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_quick_turn_on_assistant_app;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateQuickTurnOnAssistantAppSelectStatus(Settings.Secure.getInt(this.mContext.getContentResolver(), "op_app_double_tap_power_gesture", 0));
        updateQuickTurnOnAssistantAppEnabledStatus();
        this.mWallet.setEnabled(checkSupportWalletFlag(this.mContext));
    }

    private void updateQuickTurnOnAssistantAppSelectStatus(int i) {
        boolean z = false;
        this.mCamera.setChecked(i == 0);
        this.mWallet.setChecked(i == 1);
        RadioButtonPreference radioButtonPreference = this.mAskAlexa;
        if (i == 2) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
    }

    private void updateQuickTurnOnAssistantAppEnabledStatus() {
        boolean z = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", 0) == 1) {
            z = false;
        }
        this.mNone.setChecked(!z);
        if (!z) {
            this.mCamera.setChecked(false);
            this.mWallet.setChecked(false);
            this.mAskAlexa.setChecked(false);
        }
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        int i = 1;
        int i2 = 0;
        if ("camera".equals(key)) {
            i = 0;
        } else {
            if (!"wallet".equals(key)) {
                if ("ask_alexa".equals(key)) {
                    i = 2;
                } else {
                    "none".equals(key);
                }
            }
            i2 = i;
            i = 0;
        }
        Settings.Secure.putInt(this.mContext.getContentResolver(), "camera_double_tap_power_gesture_disabled", i);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "op_app_double_tap_power_gesture", i2);
        updateQuickTurnOnAssistantAppSelectStatus(i2);
        updateQuickTurnOnAssistantAppEnabledStatus();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0037, code lost:
        if (r8 != null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0040, code lost:
        if (0 == 0) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0042, code lost:
        r8.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0045, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean checkSupportWalletFlag(android.content.Context r9) {
        /*
            java.lang.String r0 = "HAS_ACTIVE_CARD"
            java.lang.String[] r0 = new java.lang.String[]{r0}
            r7 = 0
            r8 = 0
            android.content.ContentResolver r1 = r9.getContentResolver()     // Catch:{ Exception -> 0x003c }
            android.net.Uri r2 = com.oneplus.settings.gestures.OPQuickTurnOnAssistantApp.OPEN_WALLET_CP_URI     // Catch:{ Exception -> 0x003c }
            r4 = 0
            r5 = 0
            r6 = 0
            r3 = r0
            android.database.Cursor r8 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x003c }
            if (r8 == 0) goto L_0x0037
            int r9 = r8.getCount()     // Catch:{ Exception -> 0x003c }
            if (r9 <= 0) goto L_0x0037
            r8.moveToFirst()     // Catch:{ Exception -> 0x003c }
            r9 = r0[r7]     // Catch:{ Exception -> 0x003c }
            int r9 = r8.getColumnIndex(r9)     // Catch:{ Exception -> 0x003c }
            java.lang.String r9 = r8.getString(r9)     // Catch:{ Exception -> 0x003c }
            java.lang.String r0 = "true"
            boolean r9 = android.text.TextUtils.equals(r0, r9)     // Catch:{ Exception -> 0x003c }
            if (r8 == 0) goto L_0x0036
            r8.close()
        L_0x0036:
            return r9
        L_0x0037:
            if (r8 == 0) goto L_0x0045
            goto L_0x0042
        L_0x003a:
            r9 = move-exception
            goto L_0x0046
        L_0x003c:
            r9 = move-exception
            r9.printStackTrace()     // Catch:{ all -> 0x003a }
            if (r8 == 0) goto L_0x0045
        L_0x0042:
            r8.close()
        L_0x0045:
            return r7
        L_0x0046:
            if (r8 == 0) goto L_0x004b
            r8.close()
        L_0x004b:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.gestures.OPQuickTurnOnAssistantApp.checkSupportWalletFlag(android.content.Context):boolean");
    }
}
