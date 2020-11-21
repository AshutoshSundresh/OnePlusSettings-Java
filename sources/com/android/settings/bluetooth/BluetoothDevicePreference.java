package com.android.settings.bluetooth;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public final class BluetoothDevicePreference extends GearPreference implements CachedBluetoothDevice.Callback {
    private static int sDimAlpha = Integer.MIN_VALUE;
    private String contentDescription = null;
    private final CachedBluetoothDevice mCachedDevice;
    private AlertDialog mDisconnectDialog;
    private boolean mHideSecondTarget = false;
    boolean mNeedNotifyHierarchyChanged = false;
    private final boolean mShowDevicesWithoutNames;
    private boolean mTwsAddress;
    private String mTwsBatteryInfo;
    private final UserManager mUserManager;

    public BluetoothDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z, int i) {
        super(context, null);
        getContext().getResources();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mShowDevicesWithoutNames = z;
        if (sDimAlpha == Integer.MIN_VALUE) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16842803, typedValue, true);
            sDimAlpha = (int) (typedValue.getFloat() * 255.0f);
        }
        this.mCachedDevice = cachedBluetoothDevice;
        cachedBluetoothDevice.registerCallback(this);
        System.currentTimeMillis();
        onDeviceAttributesChanged();
    }

    public void setNeedNotifyHierarchyChanged(boolean z) {
        this.mNeedNotifyHierarchyChanged = z;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settings.widget.GearPreference, com.android.settingslib.RestrictedPreference
    public boolean shouldHideSecondTarget() {
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        return cachedBluetoothDevice == null || cachedBluetoothDevice.getBondState() != 12 || this.mUserManager.hasUserRestriction("no_config_bluetooth") || this.mHideSecondTarget;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settings.widget.GearPreference, com.android.settingslib.RestrictedPreference
    public int getSecondTargetResId() {
        return C0012R$layout.preference_widget_gear;
    }

    /* access modifiers changed from: package-private */
    public CachedBluetoothDevice getCachedDevice() {
        return this.mCachedDevice;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        this.mCachedDevice.unregisterCallback(this);
        AlertDialog alertDialog = this.mDisconnectDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDisconnectDialog = null;
        }
    }

    public CachedBluetoothDevice getBluetoothDevice() {
        return this.mCachedDevice;
    }

    public void hideSecondTarget(boolean z) {
        this.mHideSecondTarget = z;
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        setTitle(this.mCachedDevice.getName());
        if (!TextUtils.isEmpty(this.mCachedDevice.getConnectionSummary()) && !TextUtils.isEmpty(this.mTwsBatteryInfo)) {
            setSummary(this.mTwsBatteryInfo);
        } else if (!this.mTwsAddress) {
            setSummary(this.mCachedDevice.getConnectionSummary());
        } else if (TextUtils.isEmpty(this.mCachedDevice.getConnectionSummary())) {
            setSummary(this.mCachedDevice.getConnectionSummary());
        }
        Pair<Drawable, String> btClassDrawableWithDescription = BluetoothUtils.getBtClassDrawableWithDescription(getContext(), this.mCachedDevice);
        Object obj = btClassDrawableWithDescription.first;
        if (obj != null) {
            ((Drawable) obj).setTint(getContext().getColor(C0006R$color.op_control_icon_color_active_default));
            setIcon((Drawable) btClassDrawableWithDescription.first);
            this.contentDescription = (String) btClassDrawableWithDescription.second;
        }
        boolean z = true;
        setEnabled(!this.mCachedDevice.isBusy());
        if (!this.mShowDevicesWithoutNames && !this.mCachedDevice.hasHumanReadableName()) {
            z = false;
        }
        setVisible(z);
        if (this.mNeedNotifyHierarchyChanged) {
            notifyHierarchyChanged();
        }
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settings.widget.GearPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ImageView imageView;
        if (findPreferenceInHierarchy("bt_checkbox") != null) {
            setDependency("bt_checkbox");
        }
        if (this.mCachedDevice.getBondState() == 12 && (imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.settings_button)) != null) {
            imageView.setOnClickListener(this);
        }
        ImageView imageView2 = (ImageView) preferenceViewHolder.findViewById(16908294);
        if (imageView2 != null) {
            imageView2.setContentDescription(this.contentDescription);
            imageView2.setImportantForAccessibility(2);
            imageView2.setElevation(getContext().getResources().getDimension(C0007R$dimen.bt_icon_elevation));
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BluetoothDevicePreference)) {
            return false;
        }
        return this.mCachedDevice.equals(((BluetoothDevicePreference) obj).mCachedDevice);
    }

    public int hashCode() {
        return this.mCachedDevice.hashCode();
    }

    @Override // androidx.preference.Preference
    public int compareTo(Preference preference) {
        if (!(preference instanceof BluetoothDevicePreference)) {
            return super.compareTo(preference);
        }
        return this.mCachedDevice.compareTo(((BluetoothDevicePreference) preference).mCachedDevice);
    }

    /* access modifiers changed from: package-private */
    public void onClicked() {
        Context context = getContext();
        int bondState = this.mCachedDevice.getBondState();
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        if (this.mCachedDevice.isConnected()) {
            metricsFeatureProvider.action(context, 868, new Pair[0]);
            askDisconnect();
        } else if (bondState == 12) {
            metricsFeatureProvider.action(context, 867, new Pair[0]);
            this.mCachedDevice.connect();
        } else if (bondState == 10) {
            metricsFeatureProvider.action(context, 866, new Pair[0]);
            if (!this.mCachedDevice.hasHumanReadableName()) {
                metricsFeatureProvider.action(context, 1096, new Pair[0]);
            }
            pair();
        }
    }

    private void askDisconnect() {
        Context context = getContext();
        String name = this.mCachedDevice.getName();
        if (TextUtils.isEmpty(name)) {
            name = context.getString(C0017R$string.bluetooth_device);
        }
        String string = context.getString(C0017R$string.bluetooth_disconnect_all_profiles, name);
        String string2 = context.getString(C0017R$string.bluetooth_disconnect_title);
        this.mDisconnectDialog = Utils.showDisconnectDialog(context, this.mDisconnectDialog, new DialogInterface.OnClickListener() {
            /* class com.android.settings.bluetooth.BluetoothDevicePreference.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                BluetoothDevicePreference.this.mCachedDevice.disconnect();
            }
        }, string2, Html.fromHtml(string));
    }

    private void pair() {
        if (!this.mCachedDevice.startPairing()) {
            Utils.showError(getContext(), this.mCachedDevice.getName(), C0017R$string.bluetooth_pairing_error_message);
        }
    }

    public void setTwsBattery(String str) {
        this.mTwsBatteryInfo = str;
    }

    public void setTwsAddress(boolean z) {
        this.mTwsAddress = z;
    }
}
