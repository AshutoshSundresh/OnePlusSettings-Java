package com.oneplus.settings.notification;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.Layout;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPEarphoneMode extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.notification.OPEarphoneMode.AnonymousClass4 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_earphone_mode;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (!OPUtils.isO2()) {
                arrayList.add("notification_ringtone");
                arrayList.add("google_tts");
            }
            if (!OPUtils.hasOnePlusDialer(context)) {
                arrayList.add("call_information_broadcast");
            }
            return arrayList;
        }
    };
    private SwitchPreference mAutoAnswerViaBluetooth;
    private SwitchPreference mAutoPlay;
    private SwitchPreference mBluetoothVolume;
    private SwitchPreference mCallInformationBroadcast;
    private Context mContext;
    private AlertDialog mDialog;
    private Preference mGoogleTTS;
    private ListPreference mNotificationRingtone;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_earphone_mode);
        this.mContext = getActivity();
        FragmentActivity activity = getActivity();
        boolean z = true;
        if (activity != null && activity.getIntent().getBooleanExtra("earmode_from_notify", false)) {
            OPUtils.sendAppTracker("ear.entrance", 0);
        }
        SwitchPreference switchPreference = (SwitchPreference) findPreference("auto_play");
        this.mAutoPlay = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mAutoPlay.setChecked(Settings.System.getIntForUser(getContentResolver(), "oem_auto_play", 0, -2) != 0);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("auto_answer_via_bluetooth");
        this.mAutoAnswerViaBluetooth = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        this.mAutoAnswerViaBluetooth.setChecked(Settings.System.getIntForUser(getContentResolver(), "auto_answer_via_bluetooth", 0, -2) != 0);
        SwitchPreference switchPreference3 = (SwitchPreference) findPreference("call_information_broadcast");
        this.mCallInformationBroadcast = switchPreference3;
        switchPreference3.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference4 = this.mCallInformationBroadcast;
        if (Settings.System.getIntForUser(getContentResolver(), "oem_call_information_broadcast", 0, -2) == 0) {
            z = false;
        }
        switchPreference4.setChecked(z);
        if (this.mCallInformationBroadcast != null && OPUtils.isGuestMode()) {
            removePreference("call_information_broadcast");
        }
        if (!OPUtils.hasOnePlusDialer(this.mContext)) {
            removePreference("call_information_broadcast");
        }
        ListPreference listPreference = (ListPreference) findPreference("notification_ringtone");
        this.mNotificationRingtone = listPreference;
        listPreference.setOnPreferenceChangeListener(this);
        if (!OPUtils.isO2() && this.mNotificationRingtone != null) {
            removePreference("notification_ringtone");
        }
        SwitchPreference switchPreference5 = (SwitchPreference) findPreference("bluetooth_volume_switch");
        this.mBluetoothVolume = switchPreference5;
        switchPreference5.setOnPreferenceChangeListener(this);
        Preference findPreference = findPreference("google_tts");
        this.mGoogleTTS = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        if (!OPUtils.isO2() || OPUtils.isGuestMode()) {
            removePreference("google_tts");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        boolean z = false;
        updateNotificationRingtoneSummary(Settings.System.getIntForUser(getContentResolver(), "oem_notification_ringtone", 0, -2));
        this.mBluetoothVolume.setChecked(!SystemProperties.getBoolean("persist.bluetooth.disableabsvol", false));
        if (this.mGoogleTTS != null) {
            if (Settings.System.getIntForUser(getContentResolver(), "oem_call_information_broadcast", 0, -2) == 1) {
                z = true;
            }
            this.mGoogleTTS.setEnabled(z);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        AlertDialog alertDialog;
        FragmentActivity activity = getActivity();
        if (!(activity == null || activity.isFinishing() || (alertDialog = this.mDialog) == null)) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
        super.onDestroy();
    }

    private void updateNotificationRingtoneSummary(int i) {
        ListPreference listPreference = this.mNotificationRingtone;
        if (listPreference != null) {
            listPreference.setValue(String.valueOf(i));
            this.mNotificationRingtone.setSummary(this.mContext.getResources().getStringArray(C0003R$array.oneplus_notification_ringtone_summary)[i]);
        }
    }

    private void confirmCallInformationBroadcast() {
        AnonymousClass1 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.notification.OPEarphoneMode.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    OPEarphoneMode.this.sendTTSCallIntent(true);
                    Settings.System.putIntForUser(OPEarphoneMode.this.getContentResolver(), "oem_call_information_broadcast", 1, -2);
                } else if (i == -2) {
                    OPEarphoneMode.this.mCallInformationBroadcast.setChecked(false);
                }
            }
        };
        AnonymousClass2 r1 = new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.notification.OPEarphoneMode.AnonymousClass2 */

            public void onDismiss(DialogInterface dialogInterface) {
                if (OPEarphoneMode.this.getActivity() != null) {
                    SwitchPreference switchPreference = OPEarphoneMode.this.mCallInformationBroadcast;
                    boolean z = false;
                    if (Settings.System.getIntForUser(OPEarphoneMode.this.getContentResolver(), "oem_call_information_broadcast", 0, -2) != 0) {
                        z = true;
                    }
                    switchPreference.setChecked(z);
                }
            }
        };
        if (this.mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(C0017R$string.oneplus_network_permission_alerts);
            builder.setMessage(C0017R$string.oneplus_network_permission_alerts_message);
            builder.setPositiveButton(17039370, r0);
            builder.setNegativeButton(17039360, r0);
            builder.setOnDismissListener(r1);
            this.mDialog = builder.create();
        }
        this.mDialog.show();
        TextView textView = (TextView) this.mDialog.findViewById(16908299);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setOnTouchListener(new View.OnTouchListener() {
            /* class com.oneplus.settings.notification.OPEarphoneMode.AnonymousClass3 */

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                TextView textView = (TextView) view;
                CharSequence text = textView.getText();
                if ((text instanceof SpannableString) && action == 1) {
                    int x = ((int) motionEvent.getX()) - textView.getTotalPaddingLeft();
                    int y = ((int) motionEvent.getY()) - textView.getTotalPaddingTop();
                    int scrollX = x + textView.getScrollX();
                    Layout layout = textView.getLayout();
                    int offsetForHorizontal = layout.getOffsetForHorizontal(layout.getLineForVertical(y + textView.getScrollY()), (float) scrollX);
                    if (((ClickableSpan[]) ((SpannableString) text).getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class)).length != 0) {
                        OPEarphoneMode.this.doClickLink();
                        OPEarphoneMode.this.mDialog.cancel();
                    }
                }
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doClickLink() {
        ActivityInfo browserApp = getBrowserApp(this.mContext);
        if (browserApp != null) {
            String str = browserApp.packageName;
            String str2 = browserApp.name;
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(this.mContext.getResources().getString(C0017R$string.oneplus_network_permission_alerts_html)));
            intent.setClassName(str, str2);
            intent.addFlags(268435456);
            this.mContext.startActivity(intent);
        }
    }

    private ActivityInfo getBrowserApp(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setDataAndType(Uri.parse("http://"), null);
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 32);
        if (queryIntentActivities.size() > 0) {
            return queryIntentActivities.get(0).activityInfo;
        }
        return null;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!"google_tts".equals(preference.getKey())) {
            return true;
        }
        try {
            Intent intent = new Intent();
            intent.setAction("android.speech.tts.engine.INSTALL_TTS_DATA");
            intent.setPackage("com.google.android.tts");
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendTTSCallIntent(boolean z) {
        try {
            Intent intent = new Intent("oneplus.intent.action.TTS_CALL");
            intent.putExtra("tts_call_value", z ? 1 : 0);
            intent.addFlags(285212672);
            getPrefContext().sendBroadcast(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v12, resolved type: androidx.preference.Preference */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v6, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r4v7, types: [int, boolean] */
    /* JADX WARNING: Unknown variable types count: 2 */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(androidx.preference.Preference r4, java.lang.Object r5) {
        /*
        // Method dump skipped, instructions count: 230
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.notification.OPEarphoneMode.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
    }
}
