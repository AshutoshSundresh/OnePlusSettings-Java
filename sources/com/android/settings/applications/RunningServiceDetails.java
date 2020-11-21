package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.applications.RunningProcessesView;
import com.android.settings.applications.RunningState;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;

public class RunningServiceDetails extends InstrumentedFragment implements RunningState.OnRefreshUiListener {
    final ArrayList<ActiveDetail> mActiveDetails = new ArrayList<>();
    ViewGroup mAllDetails;
    ActivityManager mAm;
    StringBuilder mBuilder = new StringBuilder(128);
    boolean mHaveData;
    LayoutInflater mInflater;
    RunningState.MergedItem mMergedItem;
    int mNumProcesses;
    int mNumServices;
    String mProcessName;
    TextView mProcessesHeader;
    TextView mServicesHeader;
    boolean mShowBackground;
    ViewGroup mSnippet;
    RunningProcessesView.ActiveItem mSnippetActiveItem;
    RunningProcessesView.ViewHolder mSnippetViewHolder;
    RunningState mState;
    int mUid;
    int mUserId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 85;
    }

    /* access modifiers changed from: package-private */
    public class ActiveDetail implements View.OnClickListener {
        RunningProcessesView.ActiveItem mActiveItem;
        ComponentName mInstaller;
        PendingIntent mManageIntent;
        Button mReportButton;
        View mRootView;
        RunningState.ServiceItem mServiceItem;
        Button mStopButton;
        RunningProcessesView.ViewHolder mViewHolder;

        ActiveDetail() {
        }

        /* access modifiers changed from: package-private */
        public void stopActiveService(boolean z) {
            RunningState.ServiceItem serviceItem = this.mServiceItem;
            if (z || (serviceItem.mServiceInfo.applicationInfo.flags & 1) == 0) {
                RunningServiceDetails.this.getActivity().stopService(new Intent().setComponent(serviceItem.mRunningService.service));
                RunningServiceDetails runningServiceDetails = RunningServiceDetails.this;
                RunningState.MergedItem mergedItem = runningServiceDetails.mMergedItem;
                if (mergedItem == null) {
                    runningServiceDetails.mState.updateNow();
                    RunningServiceDetails.this.finish();
                } else if (runningServiceDetails.mShowBackground || mergedItem.mServices.size() > 1) {
                    RunningServiceDetails.this.mState.updateNow();
                } else {
                    RunningServiceDetails.this.mState.updateNow();
                    RunningServiceDetails.this.finish();
                }
            } else {
                RunningServiceDetails.this.showConfirmStopDialog(serviceItem.mRunningService.service);
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(13:2|(1:4)(1:5)|6|(1:8)(1:9)|10|(5:11|12|13|14|15)|16|17|27|28|(4:29|30|31|32)|43|45) */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ba, code lost:
            if (r7 != null) goto L_0x0097;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00df, code lost:
            r3 = e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00f6, code lost:
            r6.close();
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x00bd */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00f6  */
        /* JADX WARNING: Removed duplicated region for block: B:47:0x0132 A[SYNTHETIC, Splitter:B:47:0x0132] */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0138 A[SYNTHETIC, Splitter:B:52:0x0138] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onClick(android.view.View r13) {
            /*
            // Method dump skipped, instructions count: 407
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.RunningServiceDetails.ActiveDetail.onClick(android.view.View):void");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean findMergedItem() {
        RunningState.MergedItem mergedItem;
        int i;
        String str;
        RunningState.ProcessItem processItem;
        RunningState.ProcessItem processItem2;
        ArrayList<RunningState.MergedItem> currentBackgroundItems = this.mShowBackground ? this.mState.getCurrentBackgroundItems() : this.mState.getCurrentMergedItems();
        if (currentBackgroundItems != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= currentBackgroundItems.size()) {
                    break;
                }
                mergedItem = currentBackgroundItems.get(i2);
                if (mergedItem.mUserId == this.mUserId && (((i = this.mUid) < 0 || (processItem2 = mergedItem.mProcess) == null || processItem2.mUid == i) && ((str = this.mProcessName) == null || ((processItem = mergedItem.mProcess) != null && str.equals(processItem.mProcessName))))) {
                    break;
                }
                i2++;
            }
        }
        mergedItem = null;
        if (this.mMergedItem == mergedItem) {
            return false;
        }
        this.mMergedItem = mergedItem;
        return true;
    }

    /* access modifiers changed from: package-private */
    public void addServicesHeader() {
        if (this.mNumServices == 0) {
            TextView textView = (TextView) this.mInflater.inflate(C0012R$layout.preference_category, this.mAllDetails, false);
            this.mServicesHeader = textView;
            textView.setText(C0017R$string.runningservicedetails_services_title);
            this.mAllDetails.addView(this.mServicesHeader);
        }
        this.mNumServices++;
    }

    /* access modifiers changed from: package-private */
    public void addProcessesHeader() {
        if (this.mNumProcesses == 0) {
            TextView textView = (TextView) this.mInflater.inflate(C0012R$layout.preference_category, this.mAllDetails, false);
            this.mProcessesHeader = textView;
            textView.setText(C0017R$string.runningservicedetails_processes_title);
            this.mAllDetails.addView(this.mProcessesHeader);
        }
        this.mNumProcesses++;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r9v0, resolved type: com.android.settings.applications.RunningState$MergedItem */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: package-private */
    public void addServiceDetailsView(RunningState.ServiceItem serviceItem, RunningState.MergedItem mergedItem, boolean z, boolean z2) {
        int i;
        if (z) {
            addServicesHeader();
        } else if (mergedItem.mUserId != UserHandle.myUserId()) {
            addProcessesHeader();
        }
        RunningState.ServiceItem serviceItem2 = serviceItem != null ? serviceItem : mergedItem;
        ActiveDetail activeDetail = new ActiveDetail();
        boolean z3 = false;
        View inflate = this.mInflater.inflate(C0012R$layout.running_service_details_service, this.mAllDetails, false);
        this.mAllDetails.addView(inflate);
        activeDetail.mRootView = inflate;
        activeDetail.mServiceItem = serviceItem;
        RunningProcessesView.ViewHolder viewHolder = new RunningProcessesView.ViewHolder(inflate);
        activeDetail.mViewHolder = viewHolder;
        activeDetail.mActiveItem = viewHolder.bind(this.mState, serviceItem2, this.mBuilder);
        if (!z2) {
            inflate.findViewById(C0010R$id.service).setVisibility(8);
        }
        if (serviceItem != null) {
            ActivityManager.RunningServiceInfo runningServiceInfo = serviceItem.mRunningService;
            if (runningServiceInfo.clientLabel != 0) {
                activeDetail.mManageIntent = this.mAm.getRunningServiceControlPanel(runningServiceInfo.service);
            }
        }
        TextView textView = (TextView) inflate.findViewById(C0010R$id.comp_description);
        activeDetail.mStopButton = (Button) inflate.findViewById(C0010R$id.left_button);
        activeDetail.mReportButton = (Button) inflate.findViewById(C0010R$id.right_button);
        if (!z || mergedItem.mUserId == UserHandle.myUserId()) {
            if (serviceItem != null && serviceItem.mServiceInfo.descriptionRes != 0) {
                PackageManager packageManager = getActivity().getPackageManager();
                ServiceInfo serviceInfo = serviceItem.mServiceInfo;
                textView.setText(packageManager.getText(serviceInfo.packageName, serviceInfo.descriptionRes, serviceInfo.applicationInfo));
            } else if (mergedItem.mBackground) {
                textView.setText(C0017R$string.background_process_stop_description);
            } else if (activeDetail.mManageIntent != null) {
                try {
                    textView.setText(getActivity().getString(C0017R$string.service_manage_description, new Object[]{getActivity().getPackageManager().getResourcesForApplication(serviceItem.mRunningService.clientPackage).getString(serviceItem.mRunningService.clientLabel)}));
                } catch (PackageManager.NameNotFoundException unused) {
                }
            } else {
                FragmentActivity activity = getActivity();
                if (serviceItem != null) {
                    i = C0017R$string.service_stop_description;
                } else {
                    i = C0017R$string.heavy_weight_stop_description;
                }
                textView.setText(activity.getText(i));
            }
            activeDetail.mStopButton.setOnClickListener(activeDetail);
            activeDetail.mStopButton.setText(getActivity().getText(activeDetail.mManageIntent != null ? C0017R$string.service_manage : C0017R$string.service_stop));
            activeDetail.mReportButton.setOnClickListener(activeDetail);
            activeDetail.mReportButton.setText(17041142);
            if (Settings.Global.getInt(getActivity().getContentResolver(), "send_action_app_error", 0) == 0 || serviceItem == null) {
                activeDetail.mReportButton.setEnabled(false);
            } else {
                FragmentActivity activity2 = getActivity();
                ServiceInfo serviceInfo2 = serviceItem.mServiceInfo;
                ComponentName errorReportReceiver = ApplicationErrorReport.getErrorReportReceiver(activity2, serviceInfo2.packageName, serviceInfo2.applicationInfo.flags);
                activeDetail.mInstaller = errorReportReceiver;
                Button button = activeDetail.mReportButton;
                if (errorReportReceiver != null) {
                    z3 = true;
                }
                button.setEnabled(z3);
            }
        } else {
            textView.setVisibility(8);
            inflate.findViewById(C0010R$id.control_buttons_panel).setVisibility(8);
        }
        this.mActiveDetails.add(activeDetail);
    }

    /* access modifiers changed from: package-private */
    public void addProcessDetailsView(RunningState.ProcessItem processItem, boolean z) {
        int i;
        addProcessesHeader();
        ActiveDetail activeDetail = new ActiveDetail();
        View inflate = this.mInflater.inflate(C0012R$layout.running_service_details_process, this.mAllDetails, false);
        this.mAllDetails.addView(inflate);
        activeDetail.mRootView = inflate;
        RunningProcessesView.ViewHolder viewHolder = new RunningProcessesView.ViewHolder(inflate);
        activeDetail.mViewHolder = viewHolder;
        activeDetail.mActiveItem = viewHolder.bind(this.mState, processItem, this.mBuilder);
        TextView textView = (TextView) inflate.findViewById(C0010R$id.comp_description);
        if (processItem.mUserId != UserHandle.myUserId()) {
            textView.setVisibility(8);
        } else if (z) {
            textView.setText(C0017R$string.main_running_process_description);
        } else {
            CharSequence charSequence = null;
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = processItem.mRunningProcessInfo;
            ComponentName componentName = runningAppProcessInfo.importanceReasonComponent;
            int i2 = runningAppProcessInfo.importanceReasonCode;
            if (i2 == 1) {
                i = C0017R$string.process_provider_in_use_description;
                if (componentName != null) {
                    ProviderInfo providerInfo = getActivity().getPackageManager().getProviderInfo(runningAppProcessInfo.importanceReasonComponent, 0);
                    charSequence = RunningState.makeLabel(getActivity().getPackageManager(), providerInfo.name, providerInfo);
                }
            } else if (i2 != 2) {
                i = 0;
            } else {
                i = C0017R$string.process_service_in_use_description;
                if (componentName != null) {
                    try {
                        ServiceInfo serviceInfo = getActivity().getPackageManager().getServiceInfo(runningAppProcessInfo.importanceReasonComponent, 0);
                        charSequence = RunningState.makeLabel(getActivity().getPackageManager(), serviceInfo.name, serviceInfo);
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
            if (!(i == 0 || charSequence == null)) {
                textView.setText(getActivity().getString(i, new Object[]{charSequence}));
            }
        }
        this.mActiveDetails.add(activeDetail);
    }

    /* access modifiers changed from: package-private */
    public void addDetailsViews(RunningState.MergedItem mergedItem, boolean z, boolean z2) {
        RunningState.ProcessItem processItem;
        if (mergedItem != null) {
            boolean z3 = true;
            if (z) {
                for (int i = 0; i < mergedItem.mServices.size(); i++) {
                    addServiceDetailsView(mergedItem.mServices.get(i), mergedItem, true, true);
                }
            }
            if (!z2) {
                return;
            }
            if (mergedItem.mServices.size() <= 0) {
                if (mergedItem.mUserId == UserHandle.myUserId()) {
                    z3 = false;
                }
                addServiceDetailsView(null, mergedItem, false, z3);
                return;
            }
            int i2 = -1;
            while (i2 < mergedItem.mOtherProcesses.size()) {
                if (i2 < 0) {
                    processItem = mergedItem.mProcess;
                } else {
                    processItem = mergedItem.mOtherProcesses.get(i2);
                }
                if (processItem == null || processItem.mPid > 0) {
                    addProcessDetailsView(processItem, i2 < 0);
                }
                i2++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void addDetailViews() {
        ArrayList<RunningState.MergedItem> arrayList;
        for (int size = this.mActiveDetails.size() - 1; size >= 0; size--) {
            this.mAllDetails.removeView(this.mActiveDetails.get(size).mRootView);
        }
        this.mActiveDetails.clear();
        TextView textView = this.mServicesHeader;
        if (textView != null) {
            this.mAllDetails.removeView(textView);
            this.mServicesHeader = null;
        }
        TextView textView2 = this.mProcessesHeader;
        if (textView2 != null) {
            this.mAllDetails.removeView(textView2);
            this.mProcessesHeader = null;
        }
        this.mNumProcesses = 0;
        this.mNumServices = 0;
        RunningState.MergedItem mergedItem = this.mMergedItem;
        if (mergedItem == null) {
            return;
        }
        if (mergedItem.mUser != null) {
            if (this.mShowBackground) {
                arrayList = new ArrayList<>(this.mMergedItem.mChildren);
                Collections.sort(arrayList, this.mState.mBackgroundComparator);
            } else {
                arrayList = mergedItem.mChildren;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                addDetailsViews(arrayList.get(i), true, false);
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                addDetailsViews(arrayList.get(i2), false, true);
            }
            return;
        }
        addDetailsViews(mergedItem, true, true);
    }

    /* access modifiers changed from: package-private */
    public void refreshUi(boolean z) {
        if (findMergedItem()) {
            z = true;
        }
        if (z) {
            RunningState.MergedItem mergedItem = this.mMergedItem;
            if (mergedItem != null) {
                this.mSnippetActiveItem = this.mSnippetViewHolder.bind(this.mState, mergedItem, this.mBuilder);
            } else {
                RunningProcessesView.ActiveItem activeItem = this.mSnippetActiveItem;
                if (activeItem != null) {
                    activeItem.mHolder.size.setText("");
                    this.mSnippetActiveItem.mHolder.uptime.setText("");
                    this.mSnippetActiveItem.mHolder.description.setText(C0017R$string.no_services);
                } else {
                    finish();
                    return;
                }
            }
            addDetailViews();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void finish() {
        ThreadUtils.postOnMainThread(new Runnable() {
            /* class com.android.settings.applications.$$Lambda$RunningServiceDetails$YTkFZYBIB00Mbz3Oy26GxrtuRF0 */

            public final void run() {
                RunningServiceDetails.this.lambda$finish$0$RunningServiceDetails();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$finish$0 */
    public /* synthetic */ void lambda$finish$0$RunningServiceDetails() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        this.mUid = getArguments().getInt("uid", -1);
        this.mUserId = getArguments().getInt("user_id", 0);
        this.mProcessName = getArguments().getString("process", null);
        this.mShowBackground = getArguments().getBoolean("background", false);
        this.mAm = (ActivityManager) getActivity().getSystemService("activity");
        this.mInflater = (LayoutInflater) getActivity().getSystemService("layout_inflater");
        this.mState = RunningState.getInstance(getActivity());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.running_service_details, viewGroup, false);
        Utils.prepareCustomPreferencesList(viewGroup, inflate, inflate, false);
        this.mAllDetails = (ViewGroup) inflate.findViewById(C0010R$id.all_details);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(C0010R$id.snippet);
        this.mSnippet = viewGroup2;
        this.mSnippetViewHolder = new RunningProcessesView.ViewHolder(viewGroup2);
        ensureData();
        return inflate;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mHaveData = false;
        this.mState.pause();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        ensureData();
    }

    /* access modifiers changed from: package-private */
    public ActiveDetail activeDetailForService(ComponentName componentName) {
        ActivityManager.RunningServiceInfo runningServiceInfo;
        for (int i = 0; i < this.mActiveDetails.size(); i++) {
            ActiveDetail activeDetail = this.mActiveDetails.get(i);
            RunningState.ServiceItem serviceItem = activeDetail.mServiceItem;
            if (!(serviceItem == null || (runningServiceInfo = serviceItem.mRunningService) == null || !componentName.equals(runningServiceInfo.service))) {
                return activeDetail;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showConfirmStopDialog(ComponentName componentName) {
        MyAlertDialogFragment newConfirmStop = MyAlertDialogFragment.newConfirmStop(1, componentName);
        newConfirmStop.setTargetFragment(this, 0);
        newConfirmStop.show(getFragmentManager(), "confirmstop");
    }

    public static class MyAlertDialogFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 536;
        }

        public static MyAlertDialogFragment newConfirmStop(int i, ComponentName componentName) {
            MyAlertDialogFragment myAlertDialogFragment = new MyAlertDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            bundle.putParcelable("comp", componentName);
            myAlertDialogFragment.setArguments(bundle);
            return myAlertDialogFragment;
        }

        /* access modifiers changed from: package-private */
        public RunningServiceDetails getOwner() {
            return (RunningServiceDetails) getTargetFragment();
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            int i = getArguments().getInt("id");
            if (i == 1) {
                final ComponentName componentName = (ComponentName) getArguments().getParcelable("comp");
                if (getOwner().activeDetailForService(componentName) == null) {
                    return null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(C0017R$string.runningservicedetails_stop_dlg_title));
                builder.setMessage(getActivity().getString(C0017R$string.runningservicedetails_stop_dlg_text));
                builder.setPositiveButton(C0017R$string.dlg_ok, new DialogInterface.OnClickListener() {
                    /* class com.android.settings.applications.RunningServiceDetails.MyAlertDialogFragment.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActiveDetail activeDetailForService = MyAlertDialogFragment.this.getOwner().activeDetailForService(componentName);
                        if (activeDetailForService != null) {
                            activeDetailForService.stopActiveService(true);
                        }
                    }
                });
                builder.setNegativeButton(C0017R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
                return builder.create();
            }
            throw new IllegalArgumentException("unknown id " + i);
        }
    }

    /* access modifiers changed from: package-private */
    public void ensureData() {
        if (!this.mHaveData) {
            this.mHaveData = true;
            this.mState.resume(this);
            this.mState.waitForData();
            refreshUi(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateTimes() {
        RunningProcessesView.ActiveItem activeItem = this.mSnippetActiveItem;
        if (activeItem != null) {
            activeItem.updateTime(getActivity(), this.mBuilder);
        }
        for (int i = 0; i < this.mActiveDetails.size(); i++) {
            this.mActiveDetails.get(i).mActiveItem.updateTime(getActivity(), this.mBuilder);
        }
    }

    @Override // com.android.settings.applications.RunningState.OnRefreshUiListener
    public void onRefreshUi(int i) {
        if (getActivity() != null) {
            if (i == 0) {
                updateTimes();
            } else if (i == 1) {
                refreshUi(false);
                updateTimes();
            } else if (i == 2) {
                refreshUi(true);
                updateTimes();
            }
        }
    }
}
