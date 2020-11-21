package com.android.settings.applications;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.BidiFormatter;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.util.MemInfoReader;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.RunningState;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class RunningProcessesView extends FrameLayout implements AdapterView.OnItemClickListener, AbsListView.RecyclerListener, RunningState.OnRefreshUiListener {
    final HashMap<View, ActiveItem> mActiveItems = new HashMap<>();
    ServiceListAdapter mAdapter;
    ActivityManager mAm;
    TextView mAppsProcessPrefix;
    TextView mAppsProcessText;
    TextView mBackgroundProcessText;
    StringBuilder mBuilder = new StringBuilder(128);
    ProgressBar mColorBar;
    long mCurHighRam = -1;
    long mCurLowRam = -1;
    long mCurMedRam = -1;
    boolean mCurShowCached = false;
    long mCurTotalRam = -1;
    Runnable mDataAvail;
    TextView mForegroundProcessPrefix;
    TextView mForegroundProcessText;
    View mHeader;
    ListView mListView;
    MemInfoReader mMemInfoReader = new MemInfoReader();
    SettingsPreferenceFragment mOwner;
    RunningState mState;

    public static class ActiveItem {
        long mFirstRunTime;
        ViewHolder mHolder;
        RunningState.BaseItem mItem;
        View mRootView;
        boolean mSetBackground;

        /* access modifiers changed from: package-private */
        public void updateTime(Context context, StringBuilder sb) {
            TextView textView;
            RunningState.BaseItem baseItem = this.mItem;
            boolean z = true;
            if (baseItem instanceof RunningState.ServiceItem) {
                textView = this.mHolder.size;
            } else {
                String str = baseItem.mSizeStr;
                if (str == null) {
                    str = "";
                }
                if (!str.equals(this.mItem.mCurSizeStr)) {
                    this.mItem.mCurSizeStr = str;
                    this.mHolder.size.setText(str);
                }
                RunningState.BaseItem baseItem2 = this.mItem;
                if (baseItem2.mBackground) {
                    if (!this.mSetBackground) {
                        this.mSetBackground = true;
                        this.mHolder.uptime.setText("");
                    }
                } else if (baseItem2 instanceof RunningState.MergedItem) {
                    textView = this.mHolder.uptime;
                }
                textView = null;
            }
            if (textView != null) {
                boolean z2 = false;
                this.mSetBackground = false;
                if (this.mFirstRunTime >= 0) {
                    textView.setText(DateUtils.formatElapsedTime(sb, (SystemClock.elapsedRealtime() - this.mFirstRunTime) / 1000));
                    return;
                }
                RunningState.BaseItem baseItem3 = this.mItem;
                if (baseItem3 instanceof RunningState.MergedItem) {
                    if (((RunningState.MergedItem) baseItem3).mServices.size() <= 0) {
                        z = false;
                    }
                    z2 = z;
                }
                if (z2) {
                    textView.setText(context.getResources().getText(C0017R$string.service_restarting));
                } else {
                    textView.setText("");
                }
            }
        }
    }

    public static class ViewHolder {
        public TextView description;
        public ImageView icon;
        public TextView name;
        public View rootView;
        public TextView size;
        public TextView uptime;

        public ViewHolder(View view) {
            this.rootView = view;
            this.icon = (ImageView) view.findViewById(16908294);
            this.name = (TextView) view.findViewById(16908310);
            this.description = (TextView) view.findViewById(16908304);
            this.size = (TextView) view.findViewById(C0010R$id.widget_summary1);
            this.uptime = (TextView) view.findViewById(C0010R$id.widget_summary2);
            view.setTag(this);
        }

        public ActiveItem bind(RunningState runningState, RunningState.BaseItem baseItem, StringBuilder sb) {
            ActiveItem activeItem;
            synchronized (runningState.mLock) {
                PackageManager packageManager = this.rootView.getContext().getPackageManager();
                if (baseItem.mPackageInfo == null && (baseItem instanceof RunningState.MergedItem) && ((RunningState.MergedItem) baseItem).mProcess != null) {
                    ((RunningState.MergedItem) baseItem).mProcess.ensureLabel(packageManager);
                    baseItem.mPackageInfo = ((RunningState.MergedItem) baseItem).mProcess.mPackageInfo;
                    baseItem.mDisplayLabel = ((RunningState.MergedItem) baseItem).mProcess.mDisplayLabel;
                }
                this.name.setText(baseItem.mDisplayLabel);
                activeItem = new ActiveItem();
                activeItem.mRootView = this.rootView;
                activeItem.mItem = baseItem;
                activeItem.mHolder = this;
                activeItem.mFirstRunTime = baseItem.mActiveSince;
                if (baseItem.mBackground) {
                    this.description.setText(this.rootView.getContext().getText(C0017R$string.cached));
                } else {
                    this.description.setText(baseItem.mDescription);
                }
                baseItem.mCurSizeStr = null;
                this.icon.setImageDrawable(baseItem.loadIcon(this.rootView.getContext(), runningState));
                this.icon.setVisibility(0);
                activeItem.updateTime(this.rootView.getContext(), sb);
            }
            return activeItem;
        }
    }

    /* access modifiers changed from: package-private */
    public class ServiceListAdapter extends BaseAdapter {
        final LayoutInflater mInflater;
        final ArrayList<RunningState.MergedItem> mItems = new ArrayList<>();
        ArrayList<RunningState.MergedItem> mOrigItems;
        boolean mShowBackground;
        final RunningState mState;

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean hasStableIds() {
            return true;
        }

        ServiceListAdapter(RunningState runningState) {
            this.mState = runningState;
            this.mInflater = (LayoutInflater) RunningProcessesView.this.getContext().getSystemService("layout_inflater");
            refreshItems();
        }

        /* access modifiers changed from: package-private */
        public void setShowBackground(boolean z) {
            if (this.mShowBackground != z) {
                this.mShowBackground = z;
                this.mState.setWatchingBackgroundItems(z);
                refreshItems();
                RunningProcessesView.this.refreshUi(true);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean getShowBackground() {
            return this.mShowBackground;
        }

        /* access modifiers changed from: package-private */
        public void refreshItems() {
            ArrayList<RunningState.MergedItem> arrayList;
            if (this.mShowBackground) {
                arrayList = this.mState.getCurrentBackgroundItems();
            } else {
                arrayList = this.mState.getCurrentMergedItems();
            }
            if (this.mOrigItems != arrayList) {
                this.mOrigItems = arrayList;
                if (arrayList == null) {
                    this.mItems.clear();
                    return;
                }
                this.mItems.clear();
                this.mItems.addAll(arrayList);
                if (this.mShowBackground) {
                    Collections.sort(this.mItems, this.mState.mBackgroundComparator);
                }
            }
        }

        public int getCount() {
            return this.mItems.size();
        }

        public boolean isEmpty() {
            return this.mState.hasData() && this.mItems.size() == 0;
        }

        public Object getItem(int i) {
            return this.mItems.get(i);
        }

        public long getItemId(int i) {
            return (long) this.mItems.get(i).hashCode();
        }

        public boolean isEnabled(int i) {
            return !this.mItems.get(i).mIsProcess;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = newView(viewGroup);
            }
            bindView(view, i);
            return view;
        }

        public View newView(ViewGroup viewGroup) {
            View inflate = this.mInflater.inflate(C0012R$layout.running_processes_item, viewGroup, false);
            new ViewHolder(inflate);
            return inflate;
        }

        public void bindView(View view, int i) {
            synchronized (this.mState.mLock) {
                if (i < this.mItems.size()) {
                    RunningProcessesView.this.mActiveItems.put(view, ((ViewHolder) view.getTag()).bind(this.mState, this.mItems.get(i), RunningProcessesView.this.mBuilder));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void refreshUi(boolean z) {
        long j;
        long j2;
        if (z) {
            ServiceListAdapter serviceListAdapter = this.mAdapter;
            serviceListAdapter.refreshItems();
            serviceListAdapter.notifyDataSetChanged();
        }
        Runnable runnable = this.mDataAvail;
        if (runnable != null) {
            runnable.run();
            this.mDataAvail = null;
        }
        this.mMemInfoReader.readMemInfo();
        synchronized (this.mState.mLock) {
            if (this.mCurShowCached != this.mAdapter.mShowBackground) {
                boolean z2 = this.mAdapter.mShowBackground;
                this.mCurShowCached = z2;
                if (z2) {
                    this.mForegroundProcessPrefix.setText(getResources().getText(C0017R$string.running_processes_header_used_prefix));
                    this.mAppsProcessPrefix.setText(getResources().getText(C0017R$string.running_processes_header_cached_prefix));
                } else {
                    this.mForegroundProcessPrefix.setText(getResources().getText(C0017R$string.running_processes_header_system_prefix));
                    this.mAppsProcessPrefix.setText(getResources().getText(C0017R$string.running_processes_header_apps_prefix));
                }
            }
            long totalSize = this.mMemInfoReader.getTotalSize();
            if (this.mCurShowCached) {
                j2 = this.mMemInfoReader.getFreeSize() + this.mMemInfoReader.getCachedSize();
                j = this.mState.mBackgroundProcessMemory;
            } else {
                j2 = this.mMemInfoReader.getFreeSize() + this.mMemInfoReader.getCachedSize() + this.mState.mBackgroundProcessMemory;
                j = this.mState.mServiceProcessMemory;
            }
            long j3 = (totalSize - j) - j2;
            if (!(this.mCurTotalRam == totalSize && this.mCurHighRam == j3 && this.mCurMedRam == j && this.mCurLowRam == j2)) {
                this.mCurTotalRam = totalSize;
                this.mCurHighRam = j3;
                this.mCurMedRam = j;
                this.mCurLowRam = j2;
                BidiFormatter instance = BidiFormatter.getInstance();
                String unicodeWrap = instance.unicodeWrap(Formatter.formatShortFileSize(getContext(), j2));
                this.mBackgroundProcessText.setText(getResources().getString(C0017R$string.running_processes_header_ram, unicodeWrap));
                String unicodeWrap2 = instance.unicodeWrap(Formatter.formatShortFileSize(getContext(), j));
                this.mAppsProcessText.setText(getResources().getString(C0017R$string.running_processes_header_ram, unicodeWrap2));
                String unicodeWrap3 = instance.unicodeWrap(Formatter.formatShortFileSize(getContext(), j3));
                this.mForegroundProcessText.setText(getResources().getString(C0017R$string.running_processes_header_ram, unicodeWrap3));
                float f = (float) totalSize;
                int i = (int) ((((float) j3) / f) * 100.0f);
                this.mColorBar.setProgress(i);
                this.mColorBar.setSecondaryProgress(i + ((int) ((((float) j) / f) * 100.0f)));
            }
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        startServiceDetailsActivity((RunningState.MergedItem) ((ListView) adapterView).getAdapter().getItem(i));
    }

    private void startServiceDetailsActivity(RunningState.MergedItem mergedItem) {
        if (this.mOwner != null && mergedItem != null) {
            Bundle bundle = new Bundle();
            RunningState.ProcessItem processItem = mergedItem.mProcess;
            if (processItem != null) {
                bundle.putInt("uid", processItem.mUid);
                bundle.putString("process", mergedItem.mProcess.mProcessName);
            }
            bundle.putInt("user_id", mergedItem.mUserId);
            bundle.putBoolean("background", this.mAdapter.mShowBackground);
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
            subSettingLauncher.setDestination(RunningServiceDetails.class.getName());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.setTitleRes(C0017R$string.runningservicedetails_settings_title);
            subSettingLauncher.setSourceMetricsCategory(this.mOwner.getMetricsCategory());
            subSettingLauncher.launch();
        }
    }

    public void onMovedToScrapHeap(View view) {
        this.mActiveItems.remove(view);
    }

    public RunningProcessesView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        UserHandle.myUserId();
    }

    public void doCreate() {
        this.mAm = (ActivityManager) getContext().getSystemService("activity");
        this.mState = RunningState.getInstance(getContext());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
        layoutInflater.inflate(C0012R$layout.running_processes_view, this);
        this.mListView = (ListView) findViewById(16908298);
        View findViewById = findViewById(16908292);
        if (findViewById != null) {
            this.mListView.setEmptyView(findViewById);
        }
        this.mListView.setOnItemClickListener(this);
        this.mListView.setRecyclerListener(this);
        ServiceListAdapter serviceListAdapter = new ServiceListAdapter(this.mState);
        this.mAdapter = serviceListAdapter;
        this.mListView.setAdapter((ListAdapter) serviceListAdapter);
        View inflate = layoutInflater.inflate(C0012R$layout.running_processes_header, (ViewGroup) null);
        this.mHeader = inflate;
        this.mListView.addHeaderView(inflate, null, false);
        this.mColorBar = (ProgressBar) this.mHeader.findViewById(C0010R$id.color_bar);
        Context context = getContext();
        this.mColorBar.setProgressTintList(ColorStateList.valueOf(context.getColor(C0006R$color.running_processes_system_ram)));
        this.mColorBar.setSecondaryProgressTintList(Utils.getColorAccent(context));
        this.mColorBar.setSecondaryProgressTintMode(PorterDuff.Mode.SRC);
        this.mColorBar.setProgressBackgroundTintList(ColorStateList.valueOf(context.getColor(C0006R$color.running_processes_free_ram)));
        this.mColorBar.setProgressBackgroundTintMode(PorterDuff.Mode.SRC);
        TextView textView = (TextView) this.mHeader.findViewById(C0010R$id.freeSizePrefix);
        this.mAppsProcessPrefix = (TextView) this.mHeader.findViewById(C0010R$id.appsSizePrefix);
        this.mForegroundProcessPrefix = (TextView) this.mHeader.findViewById(C0010R$id.systemSizePrefix);
        this.mBackgroundProcessText = (TextView) this.mHeader.findViewById(C0010R$id.freeSize);
        this.mAppsProcessText = (TextView) this.mHeader.findViewById(C0010R$id.appsSize);
        this.mForegroundProcessText = (TextView) this.mHeader.findViewById(C0010R$id.systemSize);
        this.mAm.getMemoryInfo(new ActivityManager.MemoryInfo());
    }

    public void doPause() {
        this.mState.pause();
        this.mDataAvail = null;
        this.mOwner = null;
    }

    public boolean doResume(SettingsPreferenceFragment settingsPreferenceFragment, Runnable runnable) {
        this.mOwner = settingsPreferenceFragment;
        this.mState.resume(this);
        if (this.mState.hasData()) {
            refreshUi(true);
            return true;
        }
        this.mDataAvail = runnable;
        return false;
    }

    /* access modifiers changed from: package-private */
    public void updateTimes() {
        Iterator<ActiveItem> it = this.mActiveItems.values().iterator();
        while (it.hasNext()) {
            ActiveItem next = it.next();
            if (next.mRootView.getWindowToken() == null) {
                it.remove();
            } else {
                next.updateTime(getContext(), this.mBuilder);
            }
        }
    }

    @Override // com.android.settings.applications.RunningState.OnRefreshUiListener
    public void onRefreshUi(int i) {
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
