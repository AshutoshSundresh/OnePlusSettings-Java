package com.android.settings.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.applications.ServiceListing;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;

public class NotificationAssistantPicker extends DefaultAppPickerFragment implements ServiceListing.Callback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.notification_assistant_settings);
    private List<CandidateInfo> mCandidateInfos = new ArrayList();
    @VisibleForTesting
    protected Context mContext;
    @VisibleForTesting
    protected NotificationBackend mNotificationBackend;
    private ServiceListing mServiceListing;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 790;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mNotificationBackend = new NotificationBackend();
        ServiceListing.Builder builder = new ServiceListing.Builder(context);
        builder.setTag("NotiAssistantPicker");
        builder.setSetting("enabled_notification_assistant");
        builder.setIntentAction("android.service.notification.NotificationAssistantService");
        builder.setPermission("android.permission.BIND_NOTIFICATION_ASSISTANT_SERVICE");
        builder.setNoun("notification assistant");
        ServiceListing build = builder.build();
        this.mServiceListing = build;
        build.addCallback(this);
        this.mServiceListing.reload();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mServiceListing.removeCallback(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.notification_assistant_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<? extends CandidateInfo> getCandidates() {
        return this.mCandidateInfos;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        ComponentName allowedNotificationAssistant = this.mNotificationBackend.getAllowedNotificationAssistant();
        return allowedNotificationAssistant != null ? allowedNotificationAssistant.flattenToString() : "";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        return this.mNotificationBackend.setNotificationAssistantGranted(ComponentName.unflattenFromString(str));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public CharSequence getConfirmationMessage(CandidateInfo candidateInfo) {
        if (TextUtils.isEmpty(candidateInfo.getKey())) {
            return null;
        }
        return this.mContext.getString(C0017R$string.notification_assistant_security_warning_summary, candidateInfo.loadLabel());
    }

    @Override // com.android.settingslib.applications.ServiceListing.Callback
    public void onServicesReloaded(List<ServiceInfo> list) {
        ArrayList arrayList = new ArrayList();
        list.sort(new PackageItemInfo.DisplayNameComparator(this.mPm));
        for (ServiceInfo serviceInfo : list) {
            if (this.mContext.getPackageManager().checkPermission("android.permission.REQUEST_NOTIFICATION_ASSISTANT_SERVICE", serviceInfo.packageName) == 0) {
                arrayList.add(new DefaultAppInfo(this.mContext, this.mPm, this.mUserId, new ComponentName(serviceInfo.packageName, serviceInfo.name)));
            }
        }
        arrayList.add(new CandidateNone(this.mContext));
        this.mCandidateInfos = arrayList;
    }

    public static class CandidateNone extends CandidateInfo {
        public Context mContext;

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return "";
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        public CandidateNone(Context context) {
            super(true);
            this.mContext = context;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mContext.getString(C0017R$string.no_notification_assistant);
        }
    }
}
