package com.android.settings.applications.defaultapps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.autofill.AutofillServiceInfo;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.internal.content.PackageMonitor;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultAutofillPicker extends DefaultAppPickerFragment {
    static final Intent AUTOFILL_PROBE = new Intent("android.service.autofill.AutofillService");
    private DialogInterface.OnClickListener mCancelListener;
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() {
        /* class com.android.settings.applications.defaultapps.DefaultAutofillPicker.AnonymousClass1 */

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPackageAdded$0 */
        public /* synthetic */ void lambda$onPackageAdded$0$DefaultAutofillPicker$1() {
            DefaultAutofillPicker.this.update();
        }

        public void onPackageAdded(String str, int i) {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.applications.defaultapps.$$Lambda$DefaultAutofillPicker$1$FkWpTdrMINB6fYhO2TMWiQylcc */

                public final void run() {
                    DefaultAutofillPicker.AnonymousClass1.this.lambda$onPackageAdded$0$DefaultAutofillPicker$1();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPackageModified$1 */
        public /* synthetic */ void lambda$onPackageModified$1$DefaultAutofillPicker$1() {
            DefaultAutofillPicker.this.update();
        }

        public void onPackageModified(String str) {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.applications.defaultapps.$$Lambda$DefaultAutofillPicker$1$25IAggSj280QPpgEn1surevHwi4 */

                public final void run() {
                    DefaultAutofillPicker.AnonymousClass1.this.lambda$onPackageModified$1$DefaultAutofillPicker$1();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPackageRemoved$2 */
        public /* synthetic */ void lambda$onPackageRemoved$2$DefaultAutofillPicker$1() {
            DefaultAutofillPicker.this.update();
        }

        public void onPackageRemoved(String str, int i) {
            ThreadUtils.postOnMainThread(new Runnable() {
                /* class com.android.settings.applications.defaultapps.$$Lambda$DefaultAutofillPicker$1$wTLnu3hVgtYHDTidiWNsKDdM5mo */

                public final void run() {
                    DefaultAutofillPicker.AnonymousClass1.this.lambda$onPackageRemoved$2$DefaultAutofillPicker$1();
                }
            });
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 792;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean shouldShowItemNone() {
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        if (!(activity == null || activity.getIntent().getStringExtra("package_name") == null)) {
            this.mCancelListener = new DialogInterface.OnClickListener(activity) {
                /* class com.android.settings.applications.defaultapps.$$Lambda$DefaultAutofillPicker$83FPzHGzIc3oGHojfgRT8534BXQ */
                public final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DefaultAutofillPicker.lambda$onCreate$0(this.f$0, dialogInterface, i);
                }
            };
            this.mUserId = UserHandle.myUserId();
        }
        this.mSettingsPackageMonitor.register(activity, activity.getMainLooper(), false);
        update();
    }

    static /* synthetic */ void lambda$onCreate$0(Activity activity, DialogInterface dialogInterface, int i) {
        activity.setResult(0);
        activity.finish();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public DefaultAppPickerFragment.ConfirmationDialogFragment newConfirmationDialogFragment(String str, CharSequence charSequence) {
        AutofillPickerConfirmationDialogFragment autofillPickerConfirmationDialogFragment = new AutofillPickerConfirmationDialogFragment();
        autofillPickerConfirmationDialogFragment.init(this, str, charSequence);
        return autofillPickerConfirmationDialogFragment;
    }

    public static class AutofillPickerConfirmationDialogFragment extends DefaultAppPickerFragment.ConfirmationDialogFragment {
        @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
        public void onCreate(Bundle bundle) {
            setCancelListener(((DefaultAutofillPicker) getTargetFragment()).mCancelListener);
            super.onCreate(bundle);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_autofill_settings;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void update() {
        updateCandidates();
        addAddServicePreference();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mSettingsPackageMonitor.unregister();
        super.onDestroy();
    }

    private Preference newAddServicePreferenceOrNull() {
        String stringForUser = Settings.Secure.getStringForUser(getActivity().getContentResolver(), "autofill_service_search_uri", this.mUserId);
        if (TextUtils.isEmpty(stringForUser)) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringForUser));
        Context prefContext = getPrefContext();
        Preference preference = new Preference(prefContext);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(prefContext, intent) {
            /* class com.android.settings.applications.defaultapps.$$Lambda$DefaultAutofillPicker$0s8oelF3wlFcT6K3hzQ4pRJ5WEM */
            public final /* synthetic */ Context f$1;
            public final /* synthetic */ Intent f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return DefaultAutofillPicker.this.lambda$newAddServicePreferenceOrNull$1$DefaultAutofillPicker(this.f$1, this.f$2, preference);
            }
        });
        preference.setTitle(C0017R$string.print_menu_item_add_service);
        preference.setIcon(C0008R$drawable.ic_add_24dp);
        preference.setOrder(2147483646);
        preference.setPersistent(false);
        return preference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$newAddServicePreferenceOrNull$1 */
    public /* synthetic */ boolean lambda$newAddServicePreferenceOrNull$1$DefaultAutofillPicker(Context context, Intent intent, Preference preference) {
        context.startActivityAsUser(intent, UserHandle.of(this.mUserId));
        return true;
    }

    private void addAddServicePreference() {
        Preference newAddServicePreferenceOrNull = newAddServicePreferenceOrNull();
        if (newAddServicePreferenceOrNull != null) {
            getPreferenceScreen().addPreference(newAddServicePreferenceOrNull);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentServicesAsUser = this.mPm.queryIntentServicesAsUser(AUTOFILL_PROBE, 128, this.mUserId);
        Context context = getContext();
        for (ResolveInfo resolveInfo : queryIntentServicesAsUser) {
            String str = resolveInfo.serviceInfo.permission;
            if ("android.permission.BIND_AUTOFILL_SERVICE".equals(str)) {
                PackageManager packageManager = this.mPm;
                int i = this.mUserId;
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                arrayList.add(new DefaultAppInfo(context, packageManager, i, new ComponentName(serviceInfo.packageName, serviceInfo.name)));
            }
            if ("android.permission.BIND_AUTOFILL".equals(str)) {
                Log.w("DefaultAutofillPicker", "AutofillService from '" + resolveInfo.serviceInfo.packageName + "' uses unsupported permission android.permission.BIND_AUTOFILL. It works for now, but might not be supported on future releases");
                PackageManager packageManager2 = this.mPm;
                int i2 = this.mUserId;
                ServiceInfo serviceInfo2 = resolveInfo.serviceInfo;
                arrayList.add(new DefaultAppInfo(context, packageManager2, i2, new ComponentName(serviceInfo2.packageName, serviceInfo2.name)));
            }
        }
        return arrayList;
    }

    public static String getDefaultKey(Context context, int i) {
        ComponentName unflattenFromString;
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "autofill_service", i);
        if (stringForUser == null || (unflattenFromString = ComponentName.unflattenFromString(stringForUser)) == null) {
            return null;
        }
        return unflattenFromString.flattenToString();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return getDefaultKey(getContext(), this.mUserId);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public CharSequence getConfirmationMessage(CandidateInfo candidateInfo) {
        if (candidateInfo == null) {
            return null;
        }
        CharSequence loadLabel = candidateInfo.loadLabel();
        return Html.fromHtml(getContext().getString(C0017R$string.autofill_confirmation_message, loadLabel));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        String stringExtra;
        Settings.Secure.putStringForUser(getContext().getContentResolver(), "autofill_service", str, this.mUserId);
        FragmentActivity activity = getActivity();
        if (activity == null || (stringExtra = activity.getIntent().getStringExtra("package_name")) == null) {
            return true;
        }
        activity.setResult((str == null || !str.startsWith(stringExtra)) ? 0 : -1);
        activity.finish();
        return true;
    }

    static final class AutofillSettingIntentProvider {
        private final Context mContext;
        private final String mSelectedKey;
        private final int mUserId;

        public AutofillSettingIntentProvider(Context context, int i, String str) {
            this.mSelectedKey = str;
            this.mContext = context;
            this.mUserId = i;
        }

        public Intent getIntent() {
            for (ResolveInfo resolveInfo : this.mContext.getPackageManager().queryIntentServicesAsUser(DefaultAutofillPicker.AUTOFILL_PROBE, 128, this.mUserId)) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (TextUtils.equals(this.mSelectedKey, new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToString())) {
                    try {
                        String settingsActivity = new AutofillServiceInfo(this.mContext, serviceInfo).getSettingsActivity();
                        if (TextUtils.isEmpty(settingsActivity)) {
                            return null;
                        }
                        return new Intent("android.intent.action.MAIN").setComponent(new ComponentName(serviceInfo.packageName, settingsActivity));
                    } catch (SecurityException e) {
                        Log.w("DefaultAutofillPicker", "Error getting info for " + serviceInfo + ": " + e);
                    }
                }
            }
            return null;
        }
    }
}
