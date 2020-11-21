package com.android.settings.wifi.slice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import com.android.settings.C0003R$array;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.AccessPoint;

public class ContextualWifiSlice extends WifiSlice {
    static final int COLLAPSED_ROW_COUNT = 0;
    static long sActiveUiSession = -1000;
    static boolean sApRowCollapsed;

    public ContextualWifiSlice(Context context) {
        super(context);
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.CONTEXTUAL_WIFI_SLICE_URI;
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        long uiSessionToken = FeatureFactory.getFactory(this.mContext).getSlicesFeatureProvider().getUiSessionToken();
        if (uiSessionToken != sActiveUiSession) {
            sActiveUiSession = uiSessionToken;
            sApRowCollapsed = hasWorkingNetwork();
        } else if (!this.mWifiManager.isWifiEnabled()) {
            sApRowCollapsed = false;
        }
        return super.getSlice();
    }

    static int getApRowCount() {
        return sApRowCollapsed ? 0 : 3;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.slice.WifiSlice
    public boolean isApRowCollapsed() {
        return sApRowCollapsed;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.slice.WifiSlice
    public ListBuilder.RowBuilder getHeaderRow(boolean z, AccessPoint accessPoint) {
        ListBuilder.RowBuilder headerRow = super.getHeaderRow(z, accessPoint);
        headerRow.setTitleItem(getHeaderIcon(z, accessPoint), 0);
        if (sApRowCollapsed) {
            headerRow.setSubtitle(getSubtitle(accessPoint));
        }
        return headerRow;
    }

    private IconCompat getHeaderIcon(boolean z, AccessPoint accessPoint) {
        Drawable drawable;
        int i;
        if (!z) {
            drawable = this.mContext.getDrawable(C0008R$drawable.ic_wifi_off);
            Context context = this.mContext;
            i = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843817));
        } else {
            drawable = this.mContext.getDrawable(Utils.getWifiIconResource(2));
            if (isNetworkConnected(accessPoint)) {
                i = Utils.getColorAccentDefaultColor(this.mContext);
            } else {
                i = Utils.getColorAttrDefaultColor(this.mContext, 16843817);
            }
        }
        drawable.setTint(i);
        return com.android.settings.Utils.createIconWithDrawable(drawable);
    }

    private boolean isNetworkConnected(AccessPoint accessPoint) {
        NetworkInfo networkInfo;
        if (accessPoint == null || (networkInfo = accessPoint.getNetworkInfo()) == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
            return false;
        }
        return true;
    }

    private CharSequence getSubtitle(AccessPoint accessPoint) {
        NetworkInfo.DetailedState detailedState;
        if (isCaptivePortal()) {
            return this.mContext.getText(this.mContext.getResources().getIdentifier("network_available_sign_in", "string", "android"));
        } else if (accessPoint == null) {
            return this.mContext.getText(C0017R$string.disconnected);
        } else {
            NetworkInfo networkInfo = accessPoint.getNetworkInfo();
            if (networkInfo == null) {
                return this.mContext.getText(C0017R$string.disconnected);
            }
            NetworkInfo.State state = networkInfo.getState();
            if (state == NetworkInfo.State.CONNECTING) {
                detailedState = NetworkInfo.DetailedState.CONNECTING;
            } else if (state == NetworkInfo.State.CONNECTED) {
                detailedState = NetworkInfo.DetailedState.CONNECTED;
            } else {
                detailedState = networkInfo.getDetailedState();
            }
            return String.format(this.mContext.getResources().getStringArray(C0003R$array.wifi_status_with_ssid)[detailedState.ordinal()], accessPoint.getTitle());
        }
    }

    private boolean hasWorkingNetwork() {
        return !TextUtils.equals(getActiveSSID(), "<unknown ssid>") && hasInternetAccess();
    }

    private String getActiveSSID() {
        if (this.mWifiManager.getWifiState() != 3) {
            return "<unknown ssid>";
        }
        return WifiInfo.sanitizeSsid(this.mWifiManager.getConnectionInfo().getSSID());
    }

    private boolean hasInternetAccess() {
        NetworkCapabilities networkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
        return networkCapabilities != null && !networkCapabilities.hasCapability(17) && !networkCapabilities.hasCapability(24) && networkCapabilities.hasCapability(16);
    }

    @Override // com.android.settings.wifi.slice.WifiSlice, com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return ContextualWifiScanWorker.class;
    }
}
