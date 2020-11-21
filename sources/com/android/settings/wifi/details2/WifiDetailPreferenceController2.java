package com.android.settings.wifi.details2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.CaptivePortalData;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0003R$array;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.datausage.WifiDataUsageSummaryPreferenceController;
import com.android.settings.widget.EntityHeaderController;
import com.android.settings.wifi.WifiDialog2;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.ActionButtonsPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.android.wifitrackerlib.WifiEntry;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class WifiDetailPreferenceController2 extends AbstractPreferenceController implements PreferenceControllerMixin, WifiDialog2.WifiDialog2Listener, LifecycleObserver, OnPause, OnResume, WifiEntry.WifiEntryCallback, WifiEntry.ConnectCallback, WifiEntry.DisconnectCallback, WifiEntry.ForgetCallback, WifiEntry.SignInCallback {
    private static final boolean DEBUG = Log.isLoggable("WifiDetailsPrefCtrl2", 3);
    static final String KEY_BUTTONS_PREF = "buttons";
    static final String KEY_DATA_USAGE_HEADER = "status_header";
    static final String KEY_DNS_PREF = "dns";
    static final String KEY_FREQUENCY_PREF = "frequency";
    static final String KEY_GATEWAY_PREF = "gateway";
    static final String KEY_HEADER = "connection_header";
    static final String KEY_IPV6_ADDRESSES_PREF = "ipv6_addresses";
    static final String KEY_IPV6_CATEGORY = "ipv6_category";
    static final String KEY_IP_ADDRESS_PREF = "ip_address";
    static final String KEY_MAC_ADDRESS_PREF = "mac_address";
    static final String KEY_RX_LINK_SPEED = "rx_link_speed";
    static final String KEY_SECURITY_PREF = "security";
    static final String KEY_SIGNAL_STRENGTH_PREF = "signal_strength";
    static final String KEY_SSID_PREF = "ssid";
    static final String KEY_SUBNET_MASK_PREF = "subnet_mask";
    static final String KEY_TX_LINK_SPEED = "tx_link_speed";
    private ActionButtonsPreference mButtonsPref;
    private final Clock mClock;
    private final ConnectivityManager mConnectivityManager;
    Preference mDataUsageSummaryPref;
    private Preference mDnsPref;
    private EntityHeaderController mEntityHeaderController;
    private final PreferenceFragmentCompat mFragment;
    private Preference mFrequencyPref;
    private Preference mGatewayPref;
    private final Handler mHandler;
    private final IconInjector mIconInjector;
    private Preference mIpAddressPref;
    private Preference mIpv6AddressPref;
    private PreferenceCategory mIpv6Category;
    private boolean mIsReady;
    private Lifecycle mLifecycle;
    private LinkProperties mLinkProperties;
    private Preference mMacAddressPref;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private Network mNetwork;
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        /* class com.android.settings.wifi.details2.WifiDetailPreferenceController2.AnonymousClass1 */

        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            if (network.equals(WifiDetailPreferenceController2.this.mNetwork) && !linkProperties.equals(WifiDetailPreferenceController2.this.mLinkProperties)) {
                WifiDetailPreferenceController2.this.mLinkProperties = linkProperties;
                WifiDetailPreferenceController2.this.refreshEntityHeader();
                WifiDetailPreferenceController2.this.refreshButtons();
                WifiDetailPreferenceController2.this.refreshIpLayerInfo();
            }
        }

        private boolean hasCapabilityChanged(NetworkCapabilities networkCapabilities, int i) {
            if (WifiDetailPreferenceController2.this.mNetworkCapabilities != null && WifiDetailPreferenceController2.this.mNetworkCapabilities.hasCapability(i) == networkCapabilities.hasCapability(i)) {
                return WifiDetailPreferenceController2.DEBUG;
            }
            return true;
        }

        private boolean hasPrivateDnsStatusChanged(NetworkCapabilities networkCapabilities) {
            if (WifiDetailPreferenceController2.this.mNetworkCapabilities != null && WifiDetailPreferenceController2.this.mNetworkCapabilities.isPrivateDnsBroken() == networkCapabilities.isPrivateDnsBroken()) {
                return WifiDetailPreferenceController2.DEBUG;
            }
            return true;
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (network.equals(WifiDetailPreferenceController2.this.mNetwork) && !networkCapabilities.equals(WifiDetailPreferenceController2.this.mNetworkCapabilities)) {
                if (hasPrivateDnsStatusChanged(networkCapabilities) || hasCapabilityChanged(networkCapabilities, 16) || hasCapabilityChanged(networkCapabilities, 17) || hasCapabilityChanged(networkCapabilities, 24)) {
                    WifiDetailPreferenceController2.this.refreshEntityHeader();
                }
                WifiDetailPreferenceController2.this.mNetworkCapabilities = networkCapabilities;
                WifiDetailPreferenceController2.this.refreshButtons();
                WifiDetailPreferenceController2.this.refreshIpLayerInfo();
            }
        }

        public void onLost(Network network) {
            if (!WifiDetailPreferenceController2.this.mWifiEntry.isSaved() && network.equals(WifiDetailPreferenceController2.this.mNetwork)) {
                if (WifiDetailPreferenceController2.DEBUG) {
                    Log.d("WifiDetailsPrefCtrl2", "OnLost and exit WifiNetworkDetailsPage");
                }
                WifiDetailPreferenceController2.this.mFragment.getActivity().finish();
            }
        }
    };
    private NetworkCapabilities mNetworkCapabilities;
    private final NetworkRequest mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build();
    private int mRssiSignalLevel = -1;
    private Preference mRxLinkSpeedPref;
    private Preference mSecurityPref;
    private String[] mSignalStr;
    private Preference mSignalStrengthPref;
    private Preference mSsidPref;
    private Preference mSubnetPref;
    WifiDataUsageSummaryPreferenceController mSummaryHeaderController;
    private Preference mTxLinkSpeedPref;
    private final WifiEntry mWifiEntry;
    private WifiInfo mWifiInfo;
    private final WifiManager mWifiManager;
    private int mWifiStandard;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public static WifiDetailPreferenceController2 newInstance(WifiEntry wifiEntry, ConnectivityManager connectivityManager, Context context, PreferenceFragmentCompat preferenceFragmentCompat, Handler handler, Lifecycle lifecycle, WifiManager wifiManager, MetricsFeatureProvider metricsFeatureProvider) {
        return new WifiDetailPreferenceController2(wifiEntry, connectivityManager, context, preferenceFragmentCompat, handler, lifecycle, wifiManager, metricsFeatureProvider, new IconInjector(context), new Clock());
    }

    WifiDetailPreferenceController2(WifiEntry wifiEntry, ConnectivityManager connectivityManager, Context context, PreferenceFragmentCompat preferenceFragmentCompat, Handler handler, Lifecycle lifecycle, WifiManager wifiManager, MetricsFeatureProvider metricsFeatureProvider, IconInjector iconInjector, Clock clock) {
        super(context);
        this.mWifiEntry = wifiEntry;
        wifiEntry.setListener(this);
        this.mConnectivityManager = connectivityManager;
        this.mFragment = preferenceFragmentCompat;
        this.mHandler = handler;
        this.mSignalStr = context.getResources().getStringArray(C0003R$array.wifi_signal);
        this.mWifiManager = wifiManager;
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        this.mIconInjector = iconInjector;
        this.mClock = clock;
        this.mLifecycle = lifecycle;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        setupEntityHeader(preferenceScreen);
        ActionButtonsPreference actionButtonsPreference = (ActionButtonsPreference) preferenceScreen.findPreference(KEY_BUTTONS_PREF);
        actionButtonsPreference.setButton1Text(C0017R$string.forget);
        actionButtonsPreference.setButton1Icon(C0008R$drawable.ic_settings_delete);
        actionButtonsPreference.setButton1OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$kdg_mlegDWQhbB8i8D19h9GoZd0 */

            public final void onClick(View view) {
                WifiDetailPreferenceController2.this.lambda$displayPreference$0$WifiDetailPreferenceController2(view);
            }
        });
        actionButtonsPreference.setButton2Text(C0017R$string.wifi_sign_in_button_text);
        actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_sign_in);
        actionButtonsPreference.setButton2OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$HZ2OlGAAqvN5hnYh58TjUI9Rhb0 */

            public final void onClick(View view) {
                WifiDetailPreferenceController2.this.lambda$displayPreference$1$WifiDetailPreferenceController2(view);
            }
        });
        actionButtonsPreference.setButton3Text(getConnectDisconnectButtonTextResource());
        actionButtonsPreference.setButton3Icon(getConnectDisconnectButtonIconResource());
        actionButtonsPreference.setButton3OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$kIa0zLN67h6XNI7jQPpd1nTCE8M */

            public final void onClick(View view) {
                WifiDetailPreferenceController2.this.lambda$displayPreference$2$WifiDetailPreferenceController2(view);
            }
        });
        actionButtonsPreference.setButton4Text(C0017R$string.share);
        actionButtonsPreference.setButton4Icon(C0008R$drawable.ic_qrcode_24dp);
        actionButtonsPreference.setButton4OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$6jOWuHxYQH9ZpUj4JasGFLM_Y4Y */

            public final void onClick(View view) {
                WifiDetailPreferenceController2.this.lambda$displayPreference$3$WifiDetailPreferenceController2(view);
            }
        });
        this.mButtonsPref = actionButtonsPreference;
        updateCaptivePortalButton();
        this.mSignalStrengthPref = preferenceScreen.findPreference(KEY_SIGNAL_STRENGTH_PREF);
        this.mTxLinkSpeedPref = preferenceScreen.findPreference(KEY_TX_LINK_SPEED);
        this.mRxLinkSpeedPref = preferenceScreen.findPreference(KEY_RX_LINK_SPEED);
        this.mFrequencyPref = preferenceScreen.findPreference(KEY_FREQUENCY_PREF);
        this.mSecurityPref = preferenceScreen.findPreference(KEY_SECURITY_PREF);
        this.mSsidPref = preferenceScreen.findPreference(KEY_SSID_PREF);
        this.mMacAddressPref = preferenceScreen.findPreference(KEY_MAC_ADDRESS_PREF);
        this.mIpAddressPref = preferenceScreen.findPreference(KEY_IP_ADDRESS_PREF);
        this.mGatewayPref = preferenceScreen.findPreference(KEY_GATEWAY_PREF);
        this.mSubnetPref = preferenceScreen.findPreference(KEY_SUBNET_MASK_PREF);
        this.mDnsPref = preferenceScreen.findPreference(KEY_DNS_PREF);
        this.mIpv6Category = (PreferenceCategory) preferenceScreen.findPreference(KEY_IPV6_CATEGORY);
        this.mIpv6AddressPref = preferenceScreen.findPreference(KEY_IPV6_ADDRESSES_PREF);
        this.mSecurityPref.setSummary(this.mWifiEntry.getSecurityString(DEBUG));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$WifiDetailPreferenceController2(View view) {
        forgetNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$WifiDetailPreferenceController2(View view) {
        signIntoNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$2 */
    public /* synthetic */ void lambda$displayPreference$2$WifiDetailPreferenceController2(View view) {
        connectDisconnectNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$3 */
    public /* synthetic */ void lambda$displayPreference$3$WifiDetailPreferenceController2(View view) {
        shareNetwork();
    }

    private boolean updateCaptivePortalButton() {
        Uri captivePortalVenueInfoUrl = getCaptivePortalVenueInfoUrl();
        if (captivePortalVenueInfoUrl == null) {
            ActionButtonsPreference actionButtonsPreference = this.mButtonsPref;
            actionButtonsPreference.setButton2Text(C0017R$string.wifi_sign_in_button_text);
            actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_sign_in);
            actionButtonsPreference.setButton2OnClickListener(new View.OnClickListener() {
                /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$iy9zGEnDKtfcsj0LMpV4mF3oOHc */

                public final void onClick(View view) {
                    WifiDetailPreferenceController2.this.lambda$updateCaptivePortalButton$4$WifiDetailPreferenceController2(view);
                }
            });
            return canSignIntoNetwork();
        }
        ActionButtonsPreference actionButtonsPreference2 = this.mButtonsPref;
        actionButtonsPreference2.setButton2Text(C0017R$string.wifi_venue_website_button_text);
        actionButtonsPreference2.setButton2Icon(C0008R$drawable.ic_settings_sign_in);
        actionButtonsPreference2.setButton2OnClickListener(new View.OnClickListener(captivePortalVenueInfoUrl) {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$LadmDYir0xvFaw5Z005tEInT9Y8 */
            public final /* synthetic */ Uri f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                WifiDetailPreferenceController2.this.lambda$updateCaptivePortalButton$5$WifiDetailPreferenceController2(this.f$1, view);
            }
        });
        if (this.mWifiEntry.getConnectedState() == 2) {
            return true;
        }
        return DEBUG;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCaptivePortalButton$4 */
    public /* synthetic */ void lambda$updateCaptivePortalButton$4$WifiDetailPreferenceController2(View view) {
        signIntoNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCaptivePortalButton$5 */
    public /* synthetic */ void lambda$updateCaptivePortalButton$5$WifiDetailPreferenceController2(Uri uri, View view) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(268435456);
        intent.setData(uri);
        this.mContext.startActivity(intent);
    }

    private Uri getCaptivePortalVenueInfoUrl() {
        CaptivePortalData captivePortalData;
        LinkProperties linkProperties = this.mLinkProperties;
        if (linkProperties == null || (captivePortalData = linkProperties.getCaptivePortalData()) == null) {
            return null;
        }
        return captivePortalData.getVenueInfoUrl();
    }

    private void setupEntityHeader(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(KEY_HEADER);
        if (usingDataUsageHeader(this.mContext)) {
            layoutPreference.setVisible(DEBUG);
            Preference findPreference = preferenceScreen.findPreference(KEY_DATA_USAGE_HEADER);
            this.mDataUsageSummaryPref = findPreference;
            findPreference.setVisible(true);
            this.mSummaryHeaderController = new WifiDataUsageSummaryPreferenceController(this.mFragment.getActivity(), this.mLifecycle, this.mFragment, this.mWifiEntry.getTitle());
            return;
        }
        this.mEntityHeaderController = EntityHeaderController.newInstance(this.mFragment.getActivity(), this.mFragment, layoutPreference.findViewById(C0010R$id.entity_header));
        ((ImageView) layoutPreference.findViewById(C0010R$id.entity_header_icon)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.mEntityHeaderController.setLabel(this.mWifiEntry.getTitle());
    }

    private String getExpiryTimeSummary() {
        LinkProperties linkProperties = this.mLinkProperties;
        if (linkProperties == null || linkProperties.getCaptivePortalData() == null) {
            return null;
        }
        long expiryTimeMillis = this.mLinkProperties.getCaptivePortalData().getExpiryTimeMillis();
        if (expiryTimeMillis <= 0) {
            return null;
        }
        ZonedDateTime now = this.mClock.now();
        ZonedDateTime ofInstant = ZonedDateTime.ofInstant(Instant.ofEpochMilli(expiryTimeMillis), now.getZone());
        if (now.isAfter(ofInstant)) {
            return null;
        }
        if (now.plusDays(2).isAfter(ofInstant)) {
            Context context = this.mContext;
            return context.getString(C0017R$string.wifi_time_remaining, StringUtil.formatElapsedTime(context, (double) (Duration.between(now, ofInstant).getSeconds() * 1000), DEBUG));
        }
        return this.mContext.getString(C0017R$string.wifi_expiry_time, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(ofInstant));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshEntityHeader() {
        if (usingDataUsageHeader(this.mContext)) {
            this.mSummaryHeaderController.updateState(this.mDataUsageSummaryPref);
            return;
        }
        EntityHeaderController entityHeaderController = this.mEntityHeaderController;
        entityHeaderController.setSummary(this.mWifiEntry.getSummary());
        entityHeaderController.setSecondSummary(getExpiryTimeSummary());
        entityHeaderController.setRecyclerView(this.mFragment.getListView(), this.mLifecycle);
        entityHeaderController.done((Activity) this.mFragment.getActivity(), true);
    }

    /* access modifiers changed from: package-private */
    public void updateNetworkInfo() {
        if (this.mWifiEntry.getConnectedState() == 2) {
            Network currentNetwork = this.mWifiManager.getCurrentNetwork();
            this.mNetwork = currentNetwork;
            this.mLinkProperties = this.mConnectivityManager.getLinkProperties(currentNetwork);
            this.mNetworkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mNetwork);
            this.mConnectivityManager.getNetworkInfo(this.mNetwork);
            this.mWifiInfo = this.mWifiManager.getConnectionInfo();
            return;
        }
        this.mNetwork = null;
        this.mLinkProperties = null;
        this.mNetworkCapabilities = null;
        this.mWifiInfo = null;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateNetworkInfo();
        refreshPage();
        this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mHandler);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }

    private void refreshPage() {
        Log.d("WifiDetailsPrefCtrl2", "Update UI!");
        refreshEntityHeader();
        refreshButtons();
        refreshRssiViews();
        refreshFrequency();
        refreshTxSpeed();
        refreshRxSpeed();
        refreshIpLayerInfo();
        refreshSsid();
        refreshMacAddress();
    }

    private void refreshRssiViews() {
        int level = this.mWifiEntry.getLevel();
        int wifiStandard = this.mWifiEntry.getWifiStandard();
        boolean z = this.mWifiEntry.isVhtMax8SpatialStreamsSupported() && this.mWifiEntry.isHe8ssCapableAp();
        if (level == -1) {
            this.mSignalStrengthPref.setVisible(DEBUG);
            this.mRssiSignalLevel = -1;
        } else if (this.mRssiSignalLevel != level || this.mWifiStandard != wifiStandard || this.mIsReady != z) {
            this.mRssiSignalLevel = level;
            this.mWifiStandard = wifiStandard;
            this.mIsReady = z;
            Drawable icon = this.mIconInjector.getIcon(level, wifiStandard, z);
            EntityHeaderController entityHeaderController = this.mEntityHeaderController;
            if (entityHeaderController != null) {
                entityHeaderController.setIcon(redrawIconForHeader(icon));
                entityHeaderController.done((Activity) this.mFragment.getActivity(), true);
            }
            icon.getConstantState().newDrawable().mutate().setTintList(Utils.getColorAttr(this.mContext, 16843817));
            this.mSignalStrengthPref.setSummary(this.mSignalStr[this.mRssiSignalLevel]);
            this.mSignalStrengthPref.setVisible(true);
        }
    }

    private Drawable redrawIconForHeader(Drawable drawable) {
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.wifi_detail_page_header_image_size);
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        if ((minimumWidth == dimensionPixelSize && minimumHeight == dimensionPixelSize) || !VectorDrawable.class.isInstance(drawable)) {
            return drawable;
        }
        drawable.setTintList(null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable((Resources) null, com.android.settings.Utils.createBitmap(drawable, dimensionPixelSize, dimensionPixelSize));
        bitmapDrawable.setTintList(Utils.getColorAttr(this.mContext, 16842806));
        return bitmapDrawable;
    }

    private void refreshFrequency() {
        String str;
        WifiEntry.ConnectedInfo connectedInfo = this.mWifiEntry.getConnectedInfo();
        if (connectedInfo == null) {
            this.mFrequencyPref.setVisible(DEBUG);
            return;
        }
        int i = connectedInfo.frequencyMhz;
        if (i >= 2400 && i < 2500) {
            str = this.mContext.getResources().getString(C0017R$string.wifi_band_24ghz);
        } else if (i >= 4900 && i < 5900) {
            str = this.mContext.getResources().getString(C0017R$string.wifi_band_5ghz);
        } else if (this.mWifiEntry.getConnectedState() == 1) {
            this.mFrequencyPref.setVisible(DEBUG);
            return;
        } else {
            Log.e("WifiDetailsPrefCtrl2", "Unexpected frequency " + i);
            return;
        }
        this.mFrequencyPref.setSummary(str);
        this.mFrequencyPref.setVisible(true);
    }

    private void refreshTxSpeed() {
        if (this.mWifiInfo == null || this.mWifiEntry.getConnectedState() != 2) {
            this.mTxLinkSpeedPref.setVisible(DEBUG);
            return;
        }
        this.mTxLinkSpeedPref.setVisible(this.mWifiInfo.getTxLinkSpeedMbps() >= 0);
        this.mTxLinkSpeedPref.setSummary(this.mContext.getString(C0017R$string.tx_link_speed, Integer.valueOf(this.mWifiInfo.getTxLinkSpeedMbps())));
    }

    private void refreshRxSpeed() {
        if (this.mWifiInfo == null || this.mWifiEntry.getConnectedState() != 2) {
            this.mRxLinkSpeedPref.setVisible(DEBUG);
            return;
        }
        this.mRxLinkSpeedPref.setVisible(this.mWifiInfo.getRxLinkSpeedMbps() >= 0);
        this.mRxLinkSpeedPref.setSummary(this.mContext.getString(C0017R$string.rx_link_speed, Integer.valueOf(this.mWifiInfo.getRxLinkSpeedMbps())));
    }

    private void refreshSsid() {
        if (!this.mWifiEntry.isSubscription() || this.mWifiEntry.getSsid() == null) {
            this.mSsidPref.setVisible(DEBUG);
            return;
        }
        this.mSsidPref.setVisible(true);
        this.mSsidPref.setSummary(this.mWifiEntry.getSsid());
    }

    private void refreshMacAddress() {
        String macAddress = this.mWifiEntry.getMacAddress();
        if (macAddress == null) {
            this.mMacAddressPref.setVisible(DEBUG);
            return;
        }
        this.mMacAddressPref.setVisible(true);
        if (macAddress.equals("02:00:00:00:00:00")) {
            this.mMacAddressPref.setSummary(C0017R$string.device_info_not_available);
        } else {
            this.mMacAddressPref.setSummary(macAddress);
        }
        refreshMacTitle();
    }

    private void updatePreference(Preference preference, String str) {
        if (!TextUtils.isEmpty(str)) {
            preference.setSummary(str);
            preference.setVisible(true);
            return;
        }
        preference.setVisible(DEBUG);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshButtons() {
        boolean canForgetNetwork = canForgetNetwork();
        boolean updateCaptivePortalButton = updateCaptivePortalButton();
        boolean canConnect = this.mWifiEntry.canConnect();
        boolean z = DEBUG;
        boolean z2 = canConnect || this.mWifiEntry.canDisconnect();
        boolean canShareNetwork = canShareNetwork();
        this.mButtonsPref.setButton1Visible(canForgetNetwork);
        this.mButtonsPref.setButton2Visible(updateCaptivePortalButton);
        this.mButtonsPref.setButton3Visible(z2 || this.mWifiEntry.getConnectedState() == 1);
        this.mButtonsPref.setButton3Enabled(z2);
        this.mButtonsPref.setButton3Text(getConnectDisconnectButtonTextResource());
        this.mButtonsPref.setButton3Icon(getConnectDisconnectButtonIconResource());
        this.mButtonsPref.setButton4Visible(canShareNetwork);
        ActionButtonsPreference actionButtonsPreference = this.mButtonsPref;
        if (canForgetNetwork || updateCaptivePortalButton || z2 || canShareNetwork) {
            z = true;
        }
        actionButtonsPreference.setVisible(z);
    }

    private int getConnectDisconnectButtonTextResource() {
        int connectedState = this.mWifiEntry.getConnectedState();
        if (connectedState == 0) {
            return C0017R$string.wifi_connect;
        }
        if (connectedState == 1) {
            return C0017R$string.wifi_connecting;
        }
        if (connectedState == 2) {
            return C0017R$string.wifi_disconnect_button_text;
        }
        throw new IllegalStateException("Invalid WifiEntry connected state");
    }

    private int getConnectDisconnectButtonIconResource() {
        int connectedState = this.mWifiEntry.getConnectedState();
        if (connectedState == 0 || connectedState == 1) {
            return C0008R$drawable.ic_settings_wireless;
        }
        if (connectedState == 2) {
            return C0008R$drawable.ic_settings_close;
        }
        throw new IllegalStateException("Invalid WifiEntry connected state");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshIpLayerInfo() {
        if (this.mWifiEntry.getConnectedState() != 2 || this.mNetwork == null || this.mLinkProperties == null) {
            this.mIpAddressPref.setVisible(DEBUG);
            this.mSubnetPref.setVisible(DEBUG);
            this.mGatewayPref.setVisible(DEBUG);
            this.mDnsPref.setVisible(DEBUG);
            this.mIpv6Category.setVisible(DEBUG);
            return;
        }
        StringJoiner stringJoiner = new StringJoiner("\n");
        String str = null;
        String str2 = null;
        String str3 = null;
        for (LinkAddress linkAddress : this.mLinkProperties.getLinkAddresses()) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                str2 = linkAddress.getAddress().getHostAddress();
                str3 = ipv4PrefixLengthToSubnetMask(linkAddress.getPrefixLength());
            } else if (linkAddress.getAddress() instanceof Inet6Address) {
                stringJoiner.add(linkAddress.getAddress().getHostAddress());
            }
        }
        Iterator<RouteInfo> it = this.mLinkProperties.getRoutes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            RouteInfo next = it.next();
            if (next.isIPv4Default() && next.hasGateway()) {
                str = next.getGateway().getHostAddress();
                break;
            }
        }
        updatePreference(this.mIpAddressPref, str2);
        updatePreference(this.mSubnetPref, str3);
        updatePreference(this.mGatewayPref, str);
        updatePreference(this.mDnsPref, (String) this.mLinkProperties.getDnsServers().stream().map($$Lambda$XZAGhHrbkIDyusER4MAM6luKcT0.INSTANCE).collect(Collectors.joining("\n")));
        if (stringJoiner.length() > 0) {
            this.mIpv6AddressPref.setSummary(BidiFormatter.getInstance().unicodeWrap(stringJoiner.toString()));
            this.mIpv6Category.setVisible(true);
            return;
        }
        this.mIpv6Category.setVisible(DEBUG);
    }

    private static String ipv4PrefixLengthToSubnetMask(int i) {
        try {
            return NetworkUtils.getNetworkPart(InetAddress.getByAddress(new byte[]{-1, -1, -1, -1}), i).getHostAddress();
        } catch (UnknownHostException unused) {
            return null;
        }
    }

    public boolean canModifyNetwork() {
        if (!this.mWifiEntry.isSaved() || WifiUtils.isNetworkLockedDown(this.mContext, this.mWifiEntry.getWifiConfiguration())) {
            return DEBUG;
        }
        return true;
    }

    public boolean canForgetNetwork() {
        if (!this.mWifiEntry.canForget() || WifiUtils.isNetworkLockedDown(this.mContext, this.mWifiEntry.getWifiConfiguration())) {
            return DEBUG;
        }
        return true;
    }

    private boolean canSignIntoNetwork() {
        return this.mWifiEntry.canSignIn();
    }

    private boolean canShareNetwork() {
        return this.mWifiEntry.canShare();
    }

    private void forgetNetwork() {
        if (this.mWifiEntry.isSubscription()) {
            showConfirmForgetDialog();
            return;
        }
        this.mWifiEntry.forget(this);
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 137, new Pair[0]);
        this.mFragment.getActivity().finish();
    }

    /* access modifiers changed from: protected */
    public void showConfirmForgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setPositiveButton(C0017R$string.forget, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$c7cxTqoCnFColthDjM8ACmjVLBc */

            public final void onClick(DialogInterface dialogInterface, int i) {
                WifiDetailPreferenceController2.this.lambda$showConfirmForgetDialog$6$WifiDetailPreferenceController2(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        builder.setTitle(C0017R$string.wifi_forget_dialog_title);
        builder.setMessage(C0017R$string.forget_passpoint_dialog_message);
        builder.create().show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showConfirmForgetDialog$6 */
    public /* synthetic */ void lambda$showConfirmForgetDialog$6$WifiDetailPreferenceController2(DialogInterface dialogInterface, int i) {
        try {
            this.mWifiEntry.forget(this);
        } catch (RuntimeException e) {
            Log.e("WifiDetailsPrefCtrl2", "Failed to remove Passpoint configuration: " + e);
        }
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 137, new Pair[0]);
        this.mFragment.getActivity().finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: launchWifiDppConfiguratorActivity */
    public void lambda$shareNetwork$7() {
        Intent configuratorQrCodeGeneratorIntentOrNull = WifiDppUtils.getConfiguratorQrCodeGeneratorIntentOrNull(this.mContext, this.mWifiManager, this.mWifiEntry);
        if (configuratorQrCodeGeneratorIntentOrNull == null) {
            Log.e("WifiDetailsPrefCtrl2", "Launch Wi-Fi DPP QR code generator with a wrong Wi-Fi network!");
            return;
        }
        this.mMetricsFeatureProvider.action(0, 1710, 1595, null, Integer.MIN_VALUE);
        this.mContext.startActivity(configuratorQrCodeGeneratorIntentOrNull);
    }

    private void shareNetwork() {
        WifiDppUtils.showLockScreen(this.mContext, new Runnable() {
            /* class com.android.settings.wifi.details2.$$Lambda$WifiDetailPreferenceController2$hRlStYRtk6gRnIZ9b4fjQtDJAVs */

            public final void run() {
                WifiDetailPreferenceController2.this.lambda$shareNetwork$7$WifiDetailPreferenceController2();
            }
        });
    }

    private void signIntoNetwork() {
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 1008, new Pair[0]);
        this.mWifiEntry.signIn(this);
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        if (wifiDialog2.getController() != null) {
            this.mWifiManager.save(wifiDialog2.getController().getConfig(), new WifiManager.ActionListener() {
                /* class com.android.settings.wifi.details2.WifiDetailPreferenceController2.AnonymousClass2 */

                public void onSuccess() {
                }

                public void onFailure(int i) {
                    FragmentActivity activity = WifiDetailPreferenceController2.this.mFragment.getActivity();
                    if (activity != null) {
                        Toast.makeText(activity, C0017R$string.wifi_failed_save_message, 0).show();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public static class IconInjector {
        private final Context mContext;

        IconInjector(Context context) {
            this.mContext = context;
        }

        public Drawable getIcon(int i, int i2, boolean z) {
            return this.mContext.getDrawable(Utils.getWifiIconResource(i, i2, z)).mutate();
        }
    }

    /* access modifiers changed from: package-private */
    public static class Clock {
        Clock() {
        }

        public ZonedDateTime now() {
            return ZonedDateTime.now();
        }
    }

    private boolean usingDataUsageHeader(Context context) {
        return FeatureFlagUtils.isEnabled(context, "settings_wifi_details_datausage_header");
    }

    /* access modifiers changed from: package-private */
    public void connectDisconnectNetwork() {
        if (this.mWifiEntry.getConnectedState() == 0) {
            this.mWifiEntry.connect(this);
        } else {
            this.mWifiEntry.disconnect(this);
        }
    }

    private void refreshMacTitle() {
        int i;
        if (this.mWifiEntry.isSaved() && !this.mWifiEntry.isSubscription()) {
            Preference preference = this.mMacAddressPref;
            if (this.mWifiEntry.getPrivacy() == 1) {
                i = C0017R$string.wifi_advanced_randomized_mac_address_title;
            } else {
                i = C0017R$string.wifi_advanced_device_mac_address_title;
            }
            preference.setTitle(i);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry.WifiEntryCallback
    public void onUpdated() {
        updateNetworkInfo();
        refreshPage();
        ((WifiNetworkDetailsFragment2) this.mFragment).refreshPreferences();
    }

    @Override // com.android.wifitrackerlib.WifiEntry.ConnectCallback
    public void onConnectResult(int i) {
        if (i == 0) {
            Context context = this.mContext;
            Toast.makeText(context, context.getString(C0017R$string.wifi_connected_to_message, this.mWifiEntry.getTitle()), 0).show();
        } else if (this.mWifiEntry.getLevel() == -1) {
            Toast.makeText(this.mContext, C0017R$string.wifi_not_in_range_message, 0).show();
        } else {
            Toast.makeText(this.mContext, C0017R$string.wifi_failed_connect_message, 0).show();
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry.DisconnectCallback
    public void onDisconnectResult(int i) {
        if (i == 0) {
            FragmentActivity activity = this.mFragment.getActivity();
            if (activity != null) {
                Toast.makeText(activity, activity.getString(C0017R$string.wifi_disconnected_from, new Object[]{this.mWifiEntry.getTitle()}), 0).show();
                return;
            }
            return;
        }
        Log.e("WifiDetailsPrefCtrl2", "Disconnect Wi-Fi network failed");
    }

    @Override // com.android.wifitrackerlib.WifiEntry.ForgetCallback
    public void onForgetResult(int i) {
        if (i != 0) {
            Log.e("WifiDetailsPrefCtrl2", "Forget Wi-Fi network failed");
        }
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 137, new Pair[0]);
        this.mFragment.getActivity().finish();
    }
}
