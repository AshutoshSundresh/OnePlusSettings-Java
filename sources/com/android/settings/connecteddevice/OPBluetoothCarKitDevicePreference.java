package com.android.settings.connecteddevice;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.oneplus.settings.ui.OPCarKitButtonPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPBluetoothCarKitDevicePreference extends OPCarKitButtonPreference implements CachedBluetoothDevice.Callback {
    private static int sDimAlpha = Integer.MIN_VALUE;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final CachedBluetoothDevice mCachedDevice;
    private Context mContext;
    private final boolean mShowDevicesWithoutNames;

    public OPBluetoothCarKitDevicePreference(Context context, CachedBluetoothDevice cachedBluetoothDevice, boolean z) {
        super(context, null);
        this.mContext = context;
        getContext().getResources();
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mShowDevicesWithoutNames = z;
        if (sDimAlpha == Integer.MIN_VALUE) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16842803, typedValue, true);
            sDimAlpha = (int) (typedValue.getFloat() * 255.0f);
        }
        this.mCachedDevice = cachedBluetoothDevice;
        cachedBluetoothDevice.registerCallback(this);
        onDeviceAttributesChanged();
    }

    public CachedBluetoothDevice getBluetoothDevice() {
        return this.mCachedDevice;
    }

    public int hashCode() {
        return this.mCachedDevice.hashCode();
    }

    @Override // com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback
    public void onDeviceAttributesChanged() {
        setLeftTextTitle(this.mCachedDevice.getName());
        Pair<Drawable, String> btClassDrawableWithDescription = BluetoothUtils.getBtClassDrawableWithDescription(getContext(), this.mCachedDevice);
        Object obj = btClassDrawableWithDescription.first;
        if (obj != null) {
            setIcon((Drawable) obj);
            String str = (String) btClassDrawableWithDescription.second;
        }
        boolean z = true;
        setEnabled(!this.mCachedDevice.isBusy());
        if (!this.mShowDevicesWithoutNames && !this.mCachedDevice.hasHumanReadableName()) {
            z = false;
        }
        setVisible(z);
        notifyHierarchyChanged();
    }

    public void setButtonString(String str) {
        this.mButtonString = str;
    }

    @Override // com.oneplus.settings.ui.OPCarKitButtonPreference, androidx.preference.Preference
    public void setIcon(Drawable drawable) {
        ((OPCarKitButtonPreference) this).mIcon = drawable;
    }

    public void setButtonEnable(boolean z) {
        this.mButtonEnable = z;
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.oneplus.settings.ui.OPCarKitButtonPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mCachedDevice.getBondState() == 12) {
            if (this.mBluetoothAdapter.isCarkit(this.mCachedDevice.getDevice())) {
                setIcon(C0008R$drawable.op_ic_settings_car);
                setButtonString(this.mContext.getString(C0017R$string.oneplus_remove));
                setButtonEnable(true);
                setOnButtonClickListener(new View.OnClickListener() {
                    /* class com.android.settings.connecteddevice.OPBluetoothCarKitDevicePreference.AnonymousClass1 */

                    public void onClick(View view) {
                        OPBluetoothCarKitDevicePreference.this.mBluetoothAdapter.removeCarkit(OPBluetoothCarKitDevicePreference.this.mCachedDevice.getDevice());
                        OPBluetoothCarKitDevicePreference.this.mContext.sendBroadcast(new Intent("oneplus.action.intent.UpdateBluetoothCarkitDevice"));
                        OPUtils.sendAppTracker("blue_car_remove", OPBluetoothCarKitDevicePreference.this.mCachedDevice.getName() + "  " + OPBluetoothCarKitDevicePreference.this.mCachedDevice.getAddress());
                    }
                });
            } else {
                setButtonString(this.mContext.getString(C0017R$string.oneplus_add));
                setButtonEnable(true);
                setOnButtonClickListener(new View.OnClickListener() {
                    /* class com.android.settings.connecteddevice.OPBluetoothCarKitDevicePreference.AnonymousClass2 */

                    public void onClick(View view) {
                        OPBluetoothCarKitDevicePreference.this.mBluetoothAdapter.addCarkit(OPBluetoothCarKitDevicePreference.this.mCachedDevice.getDevice());
                        OPBluetoothCarKitDevicePreference.this.mContext.sendBroadcast(new Intent("oneplus.action.intent.UpdateBluetoothCarkitDevice"));
                        OPUtils.sendAppTracker("blue_car_add", OPBluetoothCarKitDevicePreference.this.mCachedDevice.getName() + "  " + OPBluetoothCarKitDevicePreference.this.mCachedDevice.getAddress());
                    }
                });
            }
        }
        super.onBindViewHolder(preferenceViewHolder);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof OPBluetoothCarKitDevicePreference)) {
            return false;
        }
        return this.mCachedDevice.equals(((OPBluetoothCarKitDevicePreference) obj).mCachedDevice);
    }
}
