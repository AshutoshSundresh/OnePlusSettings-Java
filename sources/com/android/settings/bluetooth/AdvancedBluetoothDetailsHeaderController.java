package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.DeviceConfig;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatteryMeterView;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.LayoutPreference;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdvancedBluetoothDetailsHeaderController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, OnDestroy, CachedBluetoothDevice.Callback {
    private static final int CASE_LOW_BATTERY_LEVEL = 19;
    private static final boolean DBG = Log.isLoggable(TAG, 3);
    private static final int LOW_BATTERY_LEVEL = 15;
    private static final String TAG = "AdvancedBtHeaderCtrl";
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private CachedBluetoothDevice mCachedDevice;
    Handler mHandler = new Handler(Looper.getMainLooper());
    final Map<String, Bitmap> mIconCache = new HashMap();
    boolean mIsRegisterCallback = DBG;
    LayoutPreference mLayoutPreference;
    final BluetoothAdapter.OnMetadataChangedListener mMetadataListener = new BluetoothAdapter.OnMetadataChangedListener() {
        /* class com.android.settings.bluetooth.AdvancedBluetoothDetailsHeaderController.AnonymousClass1 */

        public void onMetadataChanged(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            String str;
            Object[] objArr = new Object[3];
            objArr[0] = bluetoothDevice;
            objArr[1] = Integer.valueOf(i);
            if (bArr == null) {
                str = null;
            } else {
                str = new String(bArr);
            }
            objArr[2] = str;
            Log.i(AdvancedBluetoothDetailsHeaderController.TAG, String.format("Metadata updated in Device %s: %d = %s.", objArr));
            AdvancedBluetoothDetailsHeaderController.this.refresh();
        }
    };

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AdvancedBluetoothDetailsHeaderController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        boolean z = true;
        boolean z2 = DeviceConfig.getBoolean("settings_ui", "bt_advanced_header_enabled", true);
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        if (cachedBluetoothDevice == null || !BluetoothUtils.getBooleanMetaData(cachedBluetoothDevice.getDevice(), 6)) {
            z = false;
        }
        Log.d(TAG, "getAvailabilityStatus() is untethered : " + z);
        if (!z2 || !z) {
            return 2;
        }
        return 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mLayoutPreference = layoutPreference;
        layoutPreference.setVisible(isAvailable());
        refresh();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (isAvailable()) {
            this.mIsRegisterCallback = true;
            this.mCachedDevice.registerCallback(this);
            this.mBluetoothAdapter.addOnMetadataChangedListener(this.mCachedDevice.getDevice(), this.mContext.getMainExecutor(), this.mMetadataListener);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mIsRegisterCallback) {
            this.mCachedDevice.unregisterCallback(this);
            this.mBluetoothAdapter.removeOnMetadataChangedListener(this.mCachedDevice.getDevice(), this.mMetadataListener);
            this.mIsRegisterCallback = DBG;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        for (Bitmap bitmap : this.mIconCache.values()) {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.mIconCache.clear();
    }

    public void init(CachedBluetoothDevice cachedBluetoothDevice) {
        this.mCachedDevice = cachedBluetoothDevice;
    }

    /* access modifiers changed from: package-private */
    public void refresh() {
        LayoutPreference layoutPreference = this.mLayoutPreference;
        if (layoutPreference != null && this.mCachedDevice != null) {
            ((TextView) layoutPreference.findViewById(C0010R$id.entity_header_title)).setText(this.mCachedDevice.getName());
            ((TextView) this.mLayoutPreference.findViewById(C0010R$id.entity_header_summary)).setText(this.mCachedDevice.getConnectionSummary(true));
            if (!this.mCachedDevice.isConnected() || this.mCachedDevice.isBusy()) {
                updateDisconnectLayout();
                return;
            }
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(C0010R$id.layout_left), 7, 10, 13, C0017R$string.bluetooth_left_name);
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(C0010R$id.layout_middle), 9, 12, 15, C0017R$string.bluetooth_middle_name);
            updateSubLayout((LinearLayout) this.mLayoutPreference.findViewById(C0010R$id.layout_right), 8, 11, 14, C0017R$string.bluetooth_right_name);
        }
    }

    /* access modifiers changed from: package-private */
    public Drawable createBtBatteryIcon(Context context, int i, boolean z) {
        BatteryMeterView.BatteryMeterDrawable batteryMeterDrawable = new BatteryMeterView.BatteryMeterDrawable(context, context.getColor(C0006R$color.meter_background_color), context.getResources().getDimensionPixelSize(C0007R$dimen.advanced_bluetooth_battery_meter_width), context.getResources().getDimensionPixelSize(C0007R$dimen.advanced_bluetooth_battery_meter_height));
        batteryMeterDrawable.setBatteryLevel(i);
        batteryMeterDrawable.setColorFilter(new PorterDuffColorFilter(Utils.getColorAttrDefaultColor(context, 16843817), PorterDuff.Mode.SRC));
        batteryMeterDrawable.setCharging(z);
        return batteryMeterDrawable;
    }

    private void updateSubLayout(LinearLayout linearLayout, int i, int i2, int i3, int i4) {
        if (linearLayout != null) {
            BluetoothDevice device = this.mCachedDevice.getDevice();
            String stringMetaData = BluetoothUtils.getStringMetaData(device, i);
            if (stringMetaData != null) {
                updateIcon((ImageView) linearLayout.findViewById(C0010R$id.header_icon), stringMetaData);
            }
            int intMetaData = BluetoothUtils.getIntMetaData(device, i2);
            boolean booleanMetaData = BluetoothUtils.getBooleanMetaData(device, i3);
            if (DBG) {
                Log.d(TAG, "updateSubLayout() icon : " + i + ", battery : " + i2 + ", charge : " + i3 + ", batteryLevel : " + intMetaData + ", charging : " + booleanMetaData + ", iconUri : " + stringMetaData);
            }
            if (intMetaData != -1) {
                linearLayout.setVisibility(0);
                TextView textView = (TextView) linearLayout.findViewById(C0010R$id.bt_battery_summary);
                textView.setText(Utils.formatPercentage(intMetaData));
                textView.setVisibility(0);
                showBatteryIcon(linearLayout, intMetaData, booleanMetaData, i2);
            } else {
                linearLayout.setVisibility(8);
            }
            TextView textView2 = (TextView) linearLayout.findViewById(C0010R$id.header_title);
            textView2.setText(i4);
            textView2.setVisibility(0);
        }
    }

    private void showBatteryIcon(LinearLayout linearLayout, int i, boolean z, int i2) {
        boolean z2 = i <= (i2 == 12 ? 19 : 15) && !z;
        ImageView imageView = (ImageView) linearLayout.findViewById(C0010R$id.bt_battery_icon);
        if (z2) {
            imageView.setImageDrawable(this.mContext.getDrawable(C0008R$drawable.ic_battery_alert_24dp));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.advanced_bluetooth_battery_width), this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.advanced_bluetooth_battery_height));
            layoutParams.rightMargin = this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.advanced_bluetooth_battery_right_margin);
            imageView.setLayoutParams(layoutParams);
        } else {
            imageView.setImageDrawable(createBtBatteryIcon(this.mContext, i, z));
            imageView.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        }
        imageView.setVisibility(0);
    }

    private void updateDisconnectLayout() {
        this.mLayoutPreference.findViewById(C0010R$id.layout_left).setVisibility(8);
        this.mLayoutPreference.findViewById(C0010R$id.layout_right).setVisibility(8);
        LinearLayout linearLayout = (LinearLayout) this.mLayoutPreference.findViewById(C0010R$id.layout_middle);
        linearLayout.setVisibility(0);
        linearLayout.findViewById(C0010R$id.header_title).setVisibility(8);
        linearLayout.findViewById(C0010R$id.bt_battery_summary).setVisibility(8);
        linearLayout.findViewById(C0010R$id.bt_battery_icon).setVisibility(8);
        String stringMetaData = BluetoothUtils.getStringMetaData(this.mCachedDevice.getDevice(), 5);
        if (DBG) {
            Log.d(TAG, "updateDisconnectLayout() iconUri : " + stringMetaData);
        }
        if (stringMetaData != null) {
            updateIcon((ImageView) linearLayout.findViewById(C0010R$id.header_icon), stringMetaData);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateIcon(ImageView imageView, String str) {
        if (this.mIconCache.containsKey(str)) {
            imageView.setImageBitmap(this.mIconCache.get(str));
        } else {
            ThreadUtils.postOnBackgroundThread(new Runnable(str, imageView) {
                /* class com.android.settings.bluetooth.$$Lambda$AdvancedBluetoothDetailsHeaderController$4dDtXmeENFpwDvnvXACyPOoheyU */
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ImageView f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AdvancedBluetoothDetailsHeaderController.this.lambda$updateIcon$1$AdvancedBluetoothDetailsHeaderController(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateIcon$1 */
    public /* synthetic */ void lambda$updateIcon$1$AdvancedBluetoothDetailsHeaderController(String str, ImageView imageView) {
        Uri parse = Uri.parse(str);
        try {
            this.mContext.getContentResolver().takePersistableUriPermission(parse, 1);
            ThreadUtils.postOnMainThread(new Runnable(str, MediaStore.Images.Media.getBitmap(this.mContext.getContentResolver(), parse), imageView) {
                /* class com.android.settings.bluetooth.$$Lambda$AdvancedBluetoothDetailsHeaderController$gnotAXfUuRQCev17tt0MkPZ3Gl0 */
                public final /* synthetic */ String f$1;
                public final /* synthetic */ Bitmap f$2;
                public final /* synthetic */ ImageView f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    AdvancedBluetoothDetailsHeaderController.this.lambda$updateIcon$0$AdvancedBluetoothDetailsHeaderController(this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Failed to get bitmap for: " + str, e);
        } catch (SecurityException e2) {
            Log.e(TAG, "Failed to take persistable permission for: " + parse, e2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateIcon$0 */
    public /* synthetic */ void lambda$updateIcon$0$AdvancedBluetoothDetailsHeaderController(String str, Bitmap bitmap, ImageView imageView) {
        this.mIconCache.put(str, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        if (this.mCachedDevice != null) {
            refresh();
        }
    }
}
