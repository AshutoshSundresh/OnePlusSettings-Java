package com.android.settings.wifi.slice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.constraintlayout.widget.R$styleable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.SubSettings;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.SliceBuilderUtils;
import com.android.settings.wifi.WifiDialogActivity;
import com.android.settings.wifi.WifiSettings;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.settingslib.Utils;
import com.android.settingslib.wifi.AccessPoint;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WifiSlice implements CustomSliceable {
    static final int DEFAULT_EXPANDED_ROW_COUNT = 3;
    protected final ConnectivityManager mConnectivityManager = ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class));
    protected final Context mContext;
    protected final WifiManager mWifiManager;

    /* access modifiers changed from: protected */
    public boolean isApRowCollapsed() {
        return false;
    }

    public WifiSlice(Context context) {
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.WIFI_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        boolean isWifiEnabled = isWifiEnabled();
        List list = null;
        ListBuilder listBuilder = getListBuilder(isWifiEnabled, null);
        if (!isWifiEnabled) {
            WifiScanWorker.clearClickedWifi();
            return listBuilder.build();
        }
        WifiScanWorker wifiScanWorker = (WifiScanWorker) SliceBackgroundWorker.getInstance(getUri());
        if (wifiScanWorker != null) {
            list = wifiScanWorker.getResults();
        }
        int size = list == null ? 0 : list.size();
        boolean z = size > 0 && ((AccessPoint) list.get(0)).isActive();
        handleNetworkCallback(wifiScanWorker, z);
        if (z) {
            listBuilder = getListBuilder(true, (AccessPoint) list.get(0));
        }
        if (isApRowCollapsed()) {
            return listBuilder.build();
        }
        CharSequence text = this.mContext.getText(C0017R$string.summary_placeholder);
        for (int i = 0; i < 3; i++) {
            if (i < size) {
                listBuilder.addRow(getAccessPointRow((AccessPoint) list.get(i)));
            } else if (i == size) {
                listBuilder.addRow(getLoadingRow(text));
            } else {
                ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
                rowBuilder.setTitle(text);
                rowBuilder.setSubtitle(text);
                listBuilder.addRow(rowBuilder);
            }
        }
        return listBuilder.build();
    }

    private void handleNetworkCallback(WifiScanWorker wifiScanWorker, boolean z) {
        if (wifiScanWorker != null) {
            if (z) {
                wifiScanWorker.registerNetworkCallback(this.mWifiManager.getCurrentNetwork());
            } else {
                wifiScanWorker.unregisterNetworkCallback();
            }
        }
    }

    /* access modifiers changed from: protected */
    public ListBuilder.RowBuilder getHeaderRow(boolean z, AccessPoint accessPoint) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_settings_wireless);
        String string = this.mContext.getString(C0017R$string.wifi_settings);
        SliceAction createDeeplink = SliceAction.createDeeplink(getPrimaryAction(), createWithResource, 0, string);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(string);
        rowBuilder.setPrimaryAction(createDeeplink);
        return rowBuilder;
    }

    private ListBuilder getListBuilder(boolean z, AccessPoint accessPoint) {
        SliceAction createToggle = SliceAction.createToggle(getBroadcastIntent(this.mContext), null, z);
        ListBuilder listBuilder = new ListBuilder(this.mContext, getUri(), -1);
        listBuilder.setAccentColor(-1);
        listBuilder.setKeywords(getKeywords());
        listBuilder.addRow(getHeaderRow(z, accessPoint));
        listBuilder.addAction(createToggle);
        return listBuilder;
    }

    private ListBuilder.RowBuilder getAccessPointRow(AccessPoint accessPoint) {
        boolean z = accessPoint.isActive() && isCaptivePortal();
        String title = accessPoint.getTitle();
        CharSequence accessPointSummary = getAccessPointSummary(accessPoint, z);
        IconCompat accessPointLevelIcon = getAccessPointLevelIcon(accessPoint);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitleItem(accessPointLevelIcon, 0);
        rowBuilder.setTitle(title);
        rowBuilder.setSubtitle(accessPointSummary);
        rowBuilder.setPrimaryAction(getAccessPointAction(accessPoint, z, accessPointLevelIcon, title));
        if (z) {
            rowBuilder.addEndItem(getCaptivePortalEndAction(accessPoint, title));
        } else {
            IconCompat endIcon = getEndIcon(accessPoint);
            if (endIcon != null) {
                rowBuilder.addEndItem(endIcon, 0);
            }
        }
        return rowBuilder;
    }

    private CharSequence getAccessPointSummary(AccessPoint accessPoint, boolean z) {
        if (z) {
            return this.mContext.getText(C0017R$string.wifi_tap_to_sign_in);
        }
        String settingsSummary = accessPoint.getSettingsSummary();
        return TextUtils.isEmpty(settingsSummary) ? this.mContext.getText(C0017R$string.disconnected) : settingsSummary;
    }

    private IconCompat getAccessPointLevelIcon(AccessPoint accessPoint) {
        int i;
        if (!accessPoint.isActive()) {
            i = Utils.getColorAttrDefaultColor(this.mContext, 16843817);
        } else if (accessPoint.getNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
            i = Utils.getColorAccentDefaultColor(this.mContext);
        } else {
            Context context = this.mContext;
            i = Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16843817));
        }
        Drawable drawable = this.mContext.getDrawable(Utils.getWifiIconResource(accessPoint.getLevel(), accessPoint.getWifiStandard(), accessPoint.isHe8ssCapableAp() && accessPoint.isVhtMax8SpatialStreamsSupported()));
        drawable.setTint(i);
        return com.android.settings.Utils.createIconWithDrawable(drawable);
    }

    private IconCompat getEndIcon(AccessPoint accessPoint) {
        if (accessPoint.isActive()) {
            return null;
        }
        if (accessPoint.getSecurity() != 0) {
            return IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_friction_lock_closed);
        }
        if (accessPoint.isMetered()) {
            return IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_friction_money);
        }
        return null;
    }

    private SliceAction getCaptivePortalEndAction(AccessPoint accessPoint, CharSequence charSequence) {
        return getAccessPointAction(accessPoint, false, IconCompat.createWithResource(this.mContext, C0008R$drawable.ic_settings_accent), charSequence);
    }

    private SliceAction getAccessPointAction(AccessPoint accessPoint, boolean z, IconCompat iconCompat, CharSequence charSequence) {
        int hashCode = accessPoint.hashCode();
        if (z) {
            return getBroadcastAction(hashCode, new Intent(this.mContext, ConnectToWifiHandler.class).putExtra("android.net.extra.NETWORK", this.mWifiManager.getCurrentNetwork()), iconCompat, charSequence);
        }
        Bundle bundle = new Bundle();
        accessPoint.saveWifiState(bundle);
        if (accessPoint.isActive()) {
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
            subSettingLauncher.setTitleRes(C0017R$string.pref_title_network_details);
            subSettingLauncher.setDestination(WifiNetworkDetailsFragment.class.getName());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.setSourceMetricsCategory(R$styleable.Constraint_layout_goneMarginTop);
            return getActivityAction(hashCode, subSettingLauncher.toIntent(), iconCompat, charSequence);
        } else if (WifiUtils.getConnectingType(accessPoint) != 0) {
            return getBroadcastAction(hashCode, new Intent(this.mContext, ConnectToWifiHandler.class).putExtra("access_point_state", bundle), iconCompat, charSequence);
        } else {
            return getActivityAction(hashCode, new Intent(this.mContext, WifiDialogActivity.class).putExtra("access_point_state", bundle), iconCompat, charSequence);
        }
    }

    private SliceAction getActivityAction(int i, Intent intent, IconCompat iconCompat, CharSequence charSequence) {
        return SliceAction.createDeeplink(PendingIntent.getActivity(this.mContext, i, intent, 0), iconCompat, 0, charSequence);
    }

    private SliceAction getBroadcastAction(int i, Intent intent, IconCompat iconCompat, CharSequence charSequence) {
        intent.addFlags(268435456);
        return SliceAction.create(PendingIntent.getBroadcast(this.mContext, i, intent, 134217728), iconCompat, 0, charSequence);
    }

    private ListBuilder.RowBuilder getLoadingRow(CharSequence charSequence) {
        CharSequence text = this.mContext.getText(C0017R$string.wifi_empty_list_wifi_on);
        IconCompat createIconWithDrawable = com.android.settings.Utils.createIconWithDrawable(new ColorDrawable(0));
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitleItem(createIconWithDrawable, 0);
        rowBuilder.setTitle(charSequence);
        rowBuilder.setSubtitle(text);
        return rowBuilder;
    }

    /* access modifiers changed from: protected */
    public boolean isCaptivePortal() {
        return WifiUtils.canSignIntoNetwork(this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork()));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        this.mWifiManager.setWifiEnabled(intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", this.mWifiManager.isWifiEnabled()));
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        String charSequence = this.mContext.getText(C0017R$string.wifi_settings).toString();
        return SliceBuilderUtils.buildSearchResultPageIntent(this.mContext, WifiSettings.class.getName(), "wifi", charSequence, 603).setClassName(this.mContext.getPackageName(), SubSettings.class.getName()).setData(new Uri.Builder().appendPath("wifi").build());
    }

    private boolean isWifiEnabled() {
        int wifiState = this.mWifiManager.getWifiState();
        return wifiState == 2 || wifiState == 3;
    }

    private PendingIntent getPrimaryAction() {
        return PendingIntent.getActivity(this.mContext, 0, getIntent(), 0);
    }

    private Set<String> getKeywords() {
        return (Set) Arrays.asList(TextUtils.split(this.mContext.getString(C0017R$string.keywords_wifi), ",")).stream().map($$Lambda$MGZTkxm_LWhWFo0u65o5bz97bA.INSTANCE).collect(Collectors.toSet());
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return WifiScanWorker.class;
    }
}
