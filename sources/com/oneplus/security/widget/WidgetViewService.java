package com.oneplus.security.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.UserHandle;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.security.BaseSharePreference;
import com.oneplus.security.network.operator.OperatorDataModelFactory;
import com.oneplus.security.network.operator.OperatorModelInterface;
import com.oneplus.security.network.operator.OperatorPackageUsageUpdater;
import com.oneplus.security.network.simcard.SimStateListener;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.network.simcard.SimcardDataModelInterface;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.network.view.DataUsageMainActivity;
import com.oneplus.security.receiver.NetworkStateUtils;
import com.oneplus.security.utils.FileSizeUtil;
import com.oneplus.security.utils.FunctionUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.security.utils.SharedPreferenceHelper;
import com.oneplus.security.utils.Utils;
import com.oneplus.security.widget.FileSystemObserver;
import com.oneplus.settings.SettingsBaseApplication;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WidgetViewService extends Service implements OperatorPackageUsageUpdater, SimStateListener, FileSystemObserver.StorageListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static String dataLeftTitle = "";
    private static final Object dataLock = new byte[0];
    private static String dataUsedTitle = "";
    public static boolean isScreenOn = true;
    public static Map<Integer, WidgetData> sWidgetDataItems = new ConcurrentHashMap();
    private static String storageAndBatteryUnits = "%";
    private boolean dataTitleIsUsed = true;
    private int mAvailableStorage = 0;
    private BatteryReceiver mBatteryReceiver;
    private ExecutorService mCachedThreadPool = null;
    private Context mContext;
    private int mCurrentDataSim;
    private WidgetData mDataUsage = null;
    private DefaultDataSimChangedReceiver mDefaultDataSimChangedReceiver;
    private FileSystemObserver mFileSystemObserver;
    private Handler mHandler;
    private OperatorModelInterface mOperatorDataModel;
    private int mPower = 0;
    private RefreshThread mRefreshThread = new RefreshThread();
    private ScreenOnOffReceiver mScreenOnReceiver;
    private SharedPreferences mSharedPreferences;
    private SimcardDataModelInterface mSimcardDataModel;
    private final StorageEventListener mStorageListener = new StorageEventListener() {
        /* class com.oneplus.security.widget.WidgetViewService.AnonymousClass1 */

        public void onStorageStateChanged(String str, String str2, String str3) {
            WidgetViewService.this.loadSystemStorage();
        }

        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            WidgetViewService.this.loadSystemStorage();
        }

        public void onVolumeRecordChanged(VolumeRecord volumeRecord) {
            WidgetViewService.this.loadSystemStorage();
        }
    };
    private StorageManager mStorageManager;
    private final ContentObserver mStorageObserver = new ContentObserver(this.mHandler) {
        /* class com.oneplus.security.widget.WidgetViewService.AnonymousClass2 */

        public void onChange(boolean z, Uri uri) {
            WidgetViewService.this.sendHandlerMessage(6, null, 5000);
        }
    };
    private Timer mTimer = new Timer();
    private long mTotal;
    private long mUsed;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context) {
        try {
            context.startServiceAsUser(new Intent(context, WidgetViewService.class), new UserHandle(-2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stoptService(Context context) {
        try {
            context.stopServiceAsUser(new Intent(context, WidgetViewService.class), new UserHandle(-2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        Log.d("WidgetViewService", "onSharedPreferenceChanged key = " + str);
        if (str.equals("key_data_usage_total_state_subid_" + OPSNSUtils.findSubIdBySlotId(this.mCurrentDataSim))) {
            notifyMonthlyRemainingDataChanged(this.mTotal, this.mUsed);
        }
    }

    static class WidgetHandler extends Handler {
        private final WeakReference<WidgetViewService> mService;

        public WidgetHandler(WidgetViewService widgetViewService) {
            this.mService = new WeakReference<>(widgetViewService);
        }

        public void handleMessage(Message message) {
            WidgetViewService widgetViewService = this.mService.get();
            if (widgetViewService == null) {
                LogUtils.d("WidgetViewService", "mService.get() is null.");
                return;
            }
            switch (message.what) {
                case 1:
                    widgetViewService.requestDataUsageUpdate();
                    return;
                case 2:
                case 3:
                case 4:
                    synchronized (WidgetViewService.dataLock) {
                        if (Utils.isCollectionEmpty(WidgetViewService.sWidgetDataItems.entrySet())) {
                            LogUtils.d("WidgetViewService", "sWidgetDataItems is empty");
                            return;
                        }
                        if (message.obj instanceof WidgetData) {
                            WidgetData widgetData = (WidgetData) message.obj;
                            WidgetViewService.sWidgetDataItems.put(Integer.valueOf(widgetData.getType()), widgetData);
                        }
                        widgetViewService.updateRemoteViews(WidgetViewService.sWidgetDataItems);
                        return;
                    }
                case 5:
                    widgetViewService.setWidgetData();
                    synchronized (WidgetViewService.dataLock) {
                        widgetViewService.updateRemoteViews(WidgetViewService.sWidgetDataItems);
                    }
                    return;
                case 6:
                    widgetViewService.loadSystemStorage();
                    return;
                default:
                    return;
            }
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        LogUtils.d("WidgetViewService", "--------onStartCommand-----------");
        sendHandlerMessage(5, null, 1000);
        requestDataUsageUpdate();
        loadSystemStorage();
        return 1;
    }

    public void onCreate() {
        LogUtils.d("WidgetViewService", "--------onCreate-----------");
        super.onCreate();
        this.mContext = this;
        this.mHandler = new WidgetHandler(this);
        SimcardDataModel instance = SimcardDataModel.getInstance(getApplicationContext());
        this.mSimcardDataModel = instance;
        instance.registerSimStateListener(this);
        this.mCurrentDataSim = this.mSimcardDataModel.getCurrentTrafficRunningSlotId();
        initOperatorDataModel();
        registerDefaultDataSimChangedReceiver();
        registerBatteryReceiver();
        StorageManager storageManager = (StorageManager) getApplicationContext().getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        storageManager.registerListener(this.mStorageListener);
        FileSizeUtil.registerStorageDBObserver(getApplicationContext(), this.mStorageObserver);
        registFileObserver();
        try {
            storageAndBatteryUnits = getResources().getString(C0017R$string.symbol_percent);
            dataLeftTitle = getResources().getString(C0017R$string.data_usage_left_title);
            dataUsedTitle = getResources().getString(C0017R$string.traffic_package_used);
        } catch (Resources.NotFoundException e) {
            LogUtils.e("WidgetViewService", "onCreate getString error:" + e.getMessage());
            e.printStackTrace();
        }
        WidgetData widgetData = new WidgetData();
        this.mDataUsage = widgetData;
        widgetData.setType(0);
        boolean z = !TrafficUsageAlarmUtils.getDataTotalState(this, this.mCurrentDataSim);
        this.dataTitleIsUsed = z;
        if (z) {
            this.mDataUsage.setTitle(dataUsedTitle);
        } else {
            this.mDataUsage.setTitle(dataLeftTitle);
        }
        this.mDataUsage.setUnits("");
        this.mDataUsage.setValue("-");
        setWidgetData();
        isScreenOn = ((PowerManager) this.mContext.getSystemService("power")).isInteractive();
        this.mTimer.schedule(this.mRefreshThread, 1000, 60000);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mScreenOnReceiver = new ScreenOnOffReceiver(this);
        getApplicationContext().registerReceiver(this.mScreenOnReceiver, intentFilter);
        SharedPreferences defaultSharedPreferences = BaseSharePreference.getDefaultSharedPreferences("traffic_usage_alert");
        this.mSharedPreferences = defaultSharedPreferences;
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (configuration != null) {
            getResources().updateConfiguration(configuration, null);
        }
        try {
            storageAndBatteryUnits = getResources().getString(C0017R$string.symbol_percent);
            dataLeftTitle = getResources().getString(C0017R$string.data_usage_left_title);
            dataUsedTitle = getResources().getString(C0017R$string.traffic_package_used);
            LogUtils.d("WidgetViewService", "storageAndBatteryUnits:" + storageAndBatteryUnits + ",dataLeftTitle:" + dataLeftTitle + ",dataUsedTitle:" + dataUsedTitle);
        } catch (Resources.NotFoundException e) {
            LogUtils.e("WidgetViewService", "onConfigurationChanged getString error:" + e.getMessage());
            e.printStackTrace();
        }
        boolean z = !TrafficUsageAlarmUtils.getDataTotalState(this, this.mCurrentDataSim);
        this.dataTitleIsUsed = z;
        WidgetData widgetData = this.mDataUsage;
        if (widgetData != null) {
            if (z) {
                widgetData.setTitle(dataUsedTitle);
            } else {
                widgetData.setTitle(dataLeftTitle);
            }
            sendHandlerMessage(5, null, 1000);
        } else {
            startService(SettingsBaseApplication.getContext());
        }
        super.onConfigurationChanged(configuration);
    }

    public void onDestroy() {
        LogUtils.d("WidgetViewService", "--------onDestroy-----------");
        getApplicationContext().unregisterReceiver(this.mScreenOnReceiver);
        unRegisterDefaultDataSimChangedReceiver();
        unRegisterBatteryReceiver();
        FileSizeUtil.unRegisterStorageDBObserver(getApplicationContext(), this.mStorageObserver);
        this.mStorageManager.unregisterListener(this.mStorageListener);
        unRegistFileObserver();
        SharedPreferenceHelper.putInt("shelf_widget_id", -1);
        this.mSimcardDataModel.removeSimStateListener(this);
        this.mOperatorDataModel.removeTrafficUsageUpdater(this);
        this.mOperatorDataModel.removeQueryResultListener(this.mCurrentDataSim);
        Map<Integer, WidgetData> map = sWidgetDataItems;
        if (map != null) {
            map.clear();
        }
        ExecutorService executorService = this.mCachedThreadPool;
        if (executorService != null) {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                LogUtils.d("WidgetViewService", e.getMessage());
            }
        }
        this.mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void setWidgetData() {
        WidgetData widgetData;
        WidgetData widgetData2 = this.mDataUsage;
        if (widgetData2 != null) {
            widgetData = new WidgetData(0, widgetData2.getValue(), this.mDataUsage.getUnits(), this.mDataUsage.getTitle());
        } else {
            widgetData = new WidgetData(0, "-", "", dataUsedTitle);
        }
        WidgetData widgetData3 = new WidgetData(1, String.valueOf(this.mAvailableStorage), storageAndBatteryUnits);
        WidgetData widgetData4 = new WidgetData(2, String.valueOf(this.mPower), storageAndBatteryUnits);
        synchronized (dataLock) {
            sWidgetDataItems.put(0, widgetData);
            sWidgetDataItems.put(1, widgetData3);
            sWidgetDataItems.put(2, widgetData4);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initOperatorDataModel() {
        this.mCurrentDataSim = this.mSimcardDataModel.getCurrentTrafficRunningSlotId();
        OperatorModelInterface operatorDataModel = OperatorDataModelFactory.getOperatorDataModel(getApplicationContext());
        this.mOperatorDataModel = operatorDataModel;
        operatorDataModel.addTrafficUsageUpdater(this);
        this.mOperatorDataModel.addQueryResultListener(this.mCurrentDataSim);
    }

    class RefreshThread extends TimerTask {
        RefreshThread() {
        }

        public void run() {
            if (WidgetViewService.isScreenOn && NetworkStateUtils.currentNetWorkIsMobileData(WidgetViewService.this.mContext)) {
                WidgetViewService.this.setWidgetData();
                WidgetViewService.this.sendHandlerMessage(1, null, 1000);
            }
        }
    }

    class ScreenOnOffReceiver extends BroadcastReceiver {
        ScreenOnOffReceiver(WidgetViewService widgetViewService) {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("WidgetViewService", "ScreenOnOffReceiver action=" + action);
            if ("android.intent.action.SCREEN_ON".equals(action)) {
                WidgetViewService.isScreenOn = true;
                WidgetViewService.startService(context);
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                WidgetViewService.isScreenOn = false;
            }
        }
    }

    public void updateRemoteViews(Map<Integer, WidgetData> map) {
        int i;
        if (map == null || map.isEmpty()) {
            LogUtils.d("WidgetViewService", "widgetDataItems is empty or null.");
            return;
        }
        try {
            ComponentName componentName = new ComponentName(SettingsBaseApplication.getContext(), SecurityWidgetProvider.class);
            AppWidgetManager instance = AppWidgetManager.getInstance(SettingsBaseApplication.getContext());
            int[] appWidgetIds = instance.getAppWidgetIds(componentName);
            if (appWidgetIds != null) {
                if (appWidgetIds.length > 0) {
                    LogUtils.d("WidgetViewService", "--------updateRemoteViews widgetDataItems=" + map + ",widgets.length:" + appWidgetIds.length);
                    int i2 = -1;
                    if (FunctionUtils.isSupportTransparent(this.mContext)) {
                        i2 = SharedPreferenceHelper.getInt("shelf_widget_id", -1);
                    }
                    int i3 = 0;
                    int i4 = 0;
                    while (i4 < appWidgetIds.length) {
                        int i5 = appWidgetIds[i4];
                        int i6 = C0012R$layout.security_widget_layout;
                        if (i5 == i2) {
                            i6 = C0012R$layout.security_widget_layout_shelf;
                        }
                        RemoteViews remoteViews = new RemoteViews(SettingsBaseApplication.getContext().getPackageName(), i6);
                        setViewClickIntent(this.mContext, remoteViews);
                        WidgetData widgetData = map.get(Integer.valueOf(i3));
                        Log.d("WidgetViewService", "dataUsage" + widgetData.getTitle());
                        WidgetData widgetData2 = map.get(1);
                        WidgetData widgetData3 = map.get(2);
                        remoteViews.setTextViewText(C0010R$id.widget_datausage_title, widgetData.getTitle());
                        remoteViews.setTextViewText(C0010R$id.widget_datausage_left_value, widgetData.getValue());
                        remoteViews.setTextViewText(C0010R$id.widget_datausage_left_units, widgetData.getUnits());
                        remoteViews.setTextViewText(C0010R$id.widget_storage_left_value, String.format("%.0f", Float.valueOf(Float.parseFloat(widgetData2.getValue()))));
                        remoteViews.setTextViewText(C0010R$id.widget_storage_left_units, widgetData2.getUnits());
                        remoteViews.setTextViewText(C0010R$id.widget_battery_left_value, String.format("%.0f", Float.valueOf(Float.parseFloat(widgetData3.getValue()))));
                        remoteViews.setTextViewText(C0010R$id.widget_battery_left_units, widgetData3.getUnits());
                        remoteViews.setTextViewText(C0010R$id.widget_action_bar, getText(C0017R$string.widget_name).toString().toUpperCase());
                        LogUtils.d("WidgetViewService", "widgetId :  " + i5);
                        LogUtils.d("WidgetViewService", "onShelfWidgetId :  " + i2);
                        if (i5 == i2) {
                            LogUtils.d("WidgetViewService", "widgetId : ======  onShelfWidgetId");
                            remoteViews.setInt(C0010R$id.widget_layout, "setBackgroundColor", this.mContext.getResources().getColor(C0006R$color.transparent));
                            remoteViews.setViewVisibility(C0010R$id.widget_action_bar, 8);
                            i = 0;
                        } else {
                            LogUtils.d("WidgetViewService", "widgetId : !!!!!!=  onShelfWidgetId");
                            i = 0;
                            remoteViews.setViewVisibility(C0010R$id.widget_action_bar, 0);
                        }
                        instance.updateAppWidget(i5, remoteViews);
                        i4++;
                        i3 = i;
                    }
                    return;
                }
            }
            LogUtils.d("WidgetViewService", "updateRemoteViews widgets is empty, service.stopSelf()");
            stopSelf();
        } catch (Exception e) {
            LogUtils.e("WidgetViewService", "updateAppWidget exception." + e.getMessage());
        }
    }

    public void requestDataUsageUpdate() {
        LogUtils.d("WidgetViewService", "--------requestDataUsageUpdate mCurrentDataSim=" + this.mCurrentDataSim);
        if (this.mSimcardDataModel.isSlotSimInserted(this.mCurrentDataSim)) {
            OperatorModelInterface operatorModelInterface = this.mOperatorDataModel;
            if (operatorModelInterface != null) {
                operatorModelInterface.requesetPkgMonthlyUsageAndTotalInByte(this.mCurrentDataSim);
                return;
            }
            return;
        }
        notifyMonthlyRemainingDataChanged(-1, -1);
    }

    public void notifyMonthlyRemainingDataChanged(long j, long j2) {
        String[] strArr;
        String[] strArr2;
        LogUtils.d("WidgetViewService", "--------notifyMonthlyRemainingDataChanged-----------total=" + j + ",used=" + j2);
        this.mUsed = j2;
        this.mTotal = j;
        WidgetData widgetData = new WidgetData();
        widgetData.setType(0);
        if (TextUtils.isEmpty(dataLeftTitle) || TextUtils.isEmpty(dataUsedTitle)) {
            try {
                dataLeftTitle = getResources().getString(C0017R$string.data_usage_left_title);
                dataUsedTitle = getResources().getString(C0017R$string.traffic_package_used);
            } catch (Resources.NotFoundException e) {
                LogUtils.e("WidgetViewService", "getString error:" + e.getMessage());
                e.printStackTrace();
            }
        }
        if (j2 == -1) {
            widgetData.setValue("-");
            widgetData.setUnits("");
            widgetData.setTitle(dataUsedTitle);
        } else {
            this.dataTitleIsUsed = !TrafficUsageAlarmUtils.getDataTotalState(this, this.mCurrentDataSim);
            try {
                strArr2 = Utils.getFormattedFileSizeAndUnitForDisplay(getApplicationContext(), j - j2, true, true);
                Context applicationContext = getApplicationContext();
                if (j2 <= 0) {
                    j2 = 0;
                }
                strArr = Utils.getFormattedFileSizeAndUnitForDisplay(applicationContext, j2, true, true);
            } catch (Exception e2) {
                LogUtils.e("WidgetViewService", "getFormattedFileSizeAndUnit error!!!");
                e2.printStackTrace();
                strArr2 = new String[]{"", ""};
                strArr = new String[]{"", ""};
            }
            if (!this.dataTitleIsUsed) {
                widgetData.setValue(strArr2[0]);
                widgetData.setUnits(strArr2[1]);
                widgetData.setTitle(dataLeftTitle);
            } else {
                widgetData.setValue(strArr[0]);
                widgetData.setUnits(strArr[1]);
                widgetData.setTitle(dataUsedTitle);
            }
        }
        WidgetData widgetData2 = this.mDataUsage;
        if (widgetData2 == null || widgetData2.getValue() == null || !this.mDataUsage.getValue().equals(widgetData.getValue()) || this.mDataUsage.getUnits() == null || !this.mDataUsage.getUnits().equals(widgetData.getUnits()) || this.mDataUsage.getTitle() == null || !this.mDataUsage.getTitle().equals(widgetData.getTitle())) {
            this.mDataUsage = widgetData;
            sendHandlerMessage(2, widgetData, 1000);
            return;
        }
        Log.d("WidgetViewService", "mDataUsage == data");
    }

    @Override // com.oneplus.security.network.operator.OperatorPackageUsageUpdater
    public void onTrafficTotalAndUsedUpdate(long j, long j2, int i) {
        LogUtils.d("WidgetViewService", "onTrafficTotalAndUsedUpdate totalByte=" + j + ",usedByte=" + j2 + ",slotId=" + i);
        if (i == this.mCurrentDataSim) {
            notifyMonthlyRemainingDataChanged(j, j2);
        }
    }

    @Override // com.oneplus.security.network.simcard.SimStateListener
    public void onSimStateChanged(String str) {
        LogUtils.d("WidgetViewService", "onSimStateChanged simState=" + str);
    }

    @Override // com.oneplus.security.network.simcard.SimStateListener
    public void onSimOperatorCodeChanged(int i, String str) {
        LogUtils.d("WidgetViewService", "onSimOperatorCodeChanged slotId=" + i + ",simValue" + str);
        initOperatorDataModel();
        requestDataUsageUpdate();
    }

    public void setViewClickIntent(Context context, RemoteViews remoteViews) {
        Intent intent = new Intent(context, DataUsageMainActivity.class);
        intent.putExtra("select_tab", this.mCurrentDataSim);
        intent.putExtra("tracker_event", 1);
        intent.setFlags(268468224);
        remoteViews.setOnClickPendingIntent(C0010R$id.widget_datausage, PendingIntent.getActivity(context, 0, intent, 134217728));
        Intent intent2 = new Intent();
        intent2.setFlags(268468224);
        if (!FunctionUtils.isH2OS()) {
            intent2.setAction("android.settings.INTERNAL_STORAGE_SETTINGS");
        } else if (Utils.issSDKAbove28()) {
            intent2.setAction("com.oneplus.filemanager.action.SMART_CLEAN");
        } else {
            intent2.setAction("com.oneplus.security.action.CLEAN_ACTIVITY");
        }
        remoteViews.setOnClickPendingIntent(C0010R$id.widget_storage, PendingIntent.getActivity(context, 1, intent2, 134217728));
        Intent intent3 = new Intent("android.intent.action.POWER_USAGE_SUMMARY");
        intent3.putExtra(":settings:show_fragment_as_subsetting", true);
        intent3.putExtra("settings:from_app", "com.oneplus.security");
        intent3.putExtra("tracker_event", 1);
        intent3.setFlags(268468224);
        remoteViews.setOnClickPendingIntent(C0010R$id.widget_battery, PendingIntent.getActivity(context, 2, intent3, 134217728));
    }

    /* access modifiers changed from: package-private */
    public class DefaultDataSimChangedReceiver extends BroadcastReceiver {
        DefaultDataSimChangedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED".equals(intent.getAction())) {
                LogUtils.d("WidgetViewService", "------DefaultDataSimChangedReceiver-----");
                WidgetViewService.this.initOperatorDataModel();
                WidgetViewService.this.requestDataUsageUpdate();
            }
        }
    }

    private void registerDefaultDataSimChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        this.mDefaultDataSimChangedReceiver = new DefaultDataSimChangedReceiver();
        getApplicationContext().registerReceiver(this.mDefaultDataSimChangedReceiver, intentFilter);
    }

    private void unRegisterDefaultDataSimChangedReceiver() {
        getApplicationContext().unregisterReceiver(this.mDefaultDataSimChangedReceiver);
    }

    private void registerBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        this.mBatteryReceiver = new BatteryReceiver();
        getApplicationContext().registerReceiver(this.mBatteryReceiver, intentFilter);
    }

    private void unRegisterBatteryReceiver() {
        getApplicationContext().unregisterReceiver(this.mBatteryReceiver);
    }

    /* access modifiers changed from: package-private */
    public class BatteryReceiver extends BroadcastReceiver {
        BatteryReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("level", 0);
                int intExtra2 = intent.getIntExtra("scale", 100);
                intent.getIntExtra("status", 1);
                int i = (intExtra * 100) / intExtra2;
                if (i != WidgetViewService.this.mPower) {
                    WidgetViewService.this.mPower = i;
                    LogUtils.d("WidgetViewService", "------BatteryReceiver------power=" + i);
                    WidgetViewService.this.sendHandlerMessage(3, new WidgetData(2, String.valueOf(i), WidgetViewService.storageAndBatteryUnits), 1000);
                }
            }
        }
    }

    public ExecutorService getCachedThreadPool() {
        if (this.mCachedThreadPool == null) {
            this.mCachedThreadPool = Executors.newCachedThreadPool();
        }
        return this.mCachedThreadPool;
    }

    private void registFileObserver() {
        if (this.mFileSystemObserver == null) {
            this.mFileSystemObserver = new FileSystemObserver(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        this.mFileSystemObserver.setStorageListener(this);
        this.mFileSystemObserver.startWatching();
    }

    private void unRegistFileObserver() {
        FileSystemObserver fileSystemObserver = this.mFileSystemObserver;
        if (fileSystemObserver != null) {
            fileSystemObserver.stopWatching();
            this.mFileSystemObserver.setStorageListener(null);
        }
    }

    public void loadSystemStorage() {
        ExecutorService cachedThreadPool = getCachedThreadPool();
        if (!cachedThreadPool.isShutdown()) {
            cachedThreadPool.execute(new Runnable() {
                /* class com.oneplus.security.widget.WidgetViewService.AnonymousClass3 */

                public void run() {
                    if (WidgetViewService.this.mStorageManager == null) {
                        LogUtils.e("WidgetViewService", "mStorageManager is null");
                        return;
                    }
                    int availableStoragePercentValue = FileSizeUtil.getAvailableStoragePercentValue(WidgetViewService.this.mStorageManager, WidgetViewService.this);
                    if (WidgetViewService.this.mAvailableStorage == availableStoragePercentValue) {
                        LogUtils.d("WidgetViewService", "availableStorage has no change,availableStorage:" + availableStoragePercentValue + "%");
                        return;
                    }
                    WidgetViewService.this.mAvailableStorage = availableStoragePercentValue;
                    WidgetData widgetData = new WidgetData(1, String.valueOf(availableStoragePercentValue), WidgetViewService.storageAndBatteryUnits);
                    LogUtils.d("WidgetViewService", "availableStorage:" + availableStoragePercentValue + "%");
                    WidgetViewService.this.sendHandlerMessage(4, widgetData, 1000);
                }
            });
        }
    }

    @Override // com.oneplus.security.widget.FileSystemObserver.StorageListener
    public void onFileChanged() {
        sendHandlerMessage(6, null, 5000);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendHandlerMessage(int i, WidgetData widgetData, long j) {
        this.mHandler.removeMessages(i);
        Message obtainMessage = this.mHandler.obtainMessage(i);
        if (widgetData != null) {
            obtainMessage.obj = widgetData;
        }
        this.mHandler.sendMessageDelayed(obtainMessage, j);
    }
}
