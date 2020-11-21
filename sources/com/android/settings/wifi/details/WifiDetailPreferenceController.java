package com.android.settings.wifi.details;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
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
import com.android.settings.wifi.WifiDialog;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.R$string;
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
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
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

public class WifiDetailPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, WifiDialog.WifiDialogListener, LifecycleObserver, OnPause, OnResume {
    private static final boolean DEBUG = Log.isLoggable("WifiDetailsPrefCtrl", 3);
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
    private static final long TIMEOUT = Duration.ofSeconds(10).toMillis();
    static CountDownTimer mTimer;
    private AccessPoint mAccessPoint;
    private ActionButtonsPreference mButtonsPref;
    private final Clock mClock;
    private WifiManager.ActionListener mConnectListener;
    private boolean mConnected;
    private int mConnectingState;
    private final ConnectivityManager mConnectivityManager;
    Preference mDataUsageSummaryPref;
    private Preference mDnsPref;
    private EntityHeaderController mEntityHeaderController;
    private final IntentFilter mFilter;
    private final PreferenceFragmentCompat mFragment;
    private Preference mFrequencyPref;
    private Preference mGatewayPref;
    private final Handler mHandler;
    private final IconInjector mIconInjector;
    private Preference mIpAddressPref;
    private Preference mIpv6AddressPref;
    private PreferenceCategory mIpv6Category;
    private boolean mIsEphemeral;
    private boolean mIsExpired;
    private boolean mIsOutOfRange;
    private boolean mIsPasspointConfigurationR1;
    private boolean mIsReady;
    private Lifecycle mLifecycle;
    private LinkProperties mLinkProperties;
    private Preference mMacAddressPref;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private Network mNetwork;
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        /* class com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass2 */

        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            if (network.equals(WifiDetailPreferenceController.this.mNetwork) && !linkProperties.equals(WifiDetailPreferenceController.this.mLinkProperties)) {
                WifiDetailPreferenceController.this.mLinkProperties = linkProperties;
                WifiDetailPreferenceController.this.refreshEntityHeader();
                WifiDetailPreferenceController.this.refreshButtons();
                WifiDetailPreferenceController.this.refreshIpLayerInfo();
            }
        }

        private boolean hasCapabilityChanged(NetworkCapabilities networkCapabilities, int i) {
            if (WifiDetailPreferenceController.this.mNetworkCapabilities != null && WifiDetailPreferenceController.this.mNetworkCapabilities.hasCapability(i) == networkCapabilities.hasCapability(i)) {
                return WifiDetailPreferenceController.DEBUG;
            }
            return true;
        }

        private boolean hasPrivateDnsStatusChanged(NetworkCapabilities networkCapabilities) {
            if (WifiDetailPreferenceController.this.mNetworkCapabilities != null && WifiDetailPreferenceController.this.mNetworkCapabilities.isPrivateDnsBroken() == networkCapabilities.isPrivateDnsBroken()) {
                return WifiDetailPreferenceController.DEBUG;
            }
            return true;
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (network.equals(WifiDetailPreferenceController.this.mNetwork) && !networkCapabilities.equals(WifiDetailPreferenceController.this.mNetworkCapabilities)) {
                if (hasPrivateDnsStatusChanged(networkCapabilities) || hasCapabilityChanged(networkCapabilities, 16) || hasCapabilityChanged(networkCapabilities, 17) || hasCapabilityChanged(networkCapabilities, 24)) {
                    WifiDetailPreferenceController.this.mAccessPoint.update(WifiDetailPreferenceController.this.mWifiConfig, WifiDetailPreferenceController.this.mWifiInfo, WifiDetailPreferenceController.this.mNetworkInfo);
                    WifiDetailPreferenceController.this.refreshEntityHeader();
                }
                WifiDetailPreferenceController.this.mNetworkCapabilities = networkCapabilities;
                WifiDetailPreferenceController.this.refreshButtons();
                WifiDetailPreferenceController.this.refreshIpLayerInfo();
            }
        }

        public void onLost(Network network) {
            if (WifiDetailPreferenceController.this.mIsEphemeral && network.equals(WifiDetailPreferenceController.this.mNetwork)) {
                WifiDetailPreferenceController.this.exitActivity();
            }
        }
    };
    private NetworkCapabilities mNetworkCapabilities;
    private NetworkInfo mNetworkInfo;
    private final NetworkRequest mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass1 */

        /* JADX WARNING: Removed duplicated region for block: B:17:0x003b A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0040  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r4, android.content.Intent r5) {
            /*
                r3 = this;
                java.lang.String r4 = r5.getAction()
                int r5 = r4.hashCode()
                r0 = -385684331(0xffffffffe902ec95, float:-9.892349E24)
                r1 = 2
                r2 = 1
                if (r5 == r0) goto L_0x002e
                r0 = -343630553(0xffffffffeb849d27, float:-3.2064068E26)
                if (r5 == r0) goto L_0x0024
                r0 = 1625920338(0x60e99352, float:1.3464709E20)
                if (r5 == r0) goto L_0x001a
                goto L_0x0038
            L_0x001a:
                java.lang.String r5 = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0038
                r4 = 0
                goto L_0x0039
            L_0x0024:
                java.lang.String r5 = "android.net.wifi.STATE_CHANGE"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0038
                r4 = r2
                goto L_0x0039
            L_0x002e:
                java.lang.String r5 = "android.net.wifi.RSSI_CHANGED"
                boolean r4 = r4.equals(r5)
                if (r4 == 0) goto L_0x0038
                r4 = r1
                goto L_0x0039
            L_0x0038:
                r4 = -1
            L_0x0039:
                if (r4 == 0) goto L_0x0040
                if (r4 == r2) goto L_0x0043
                if (r4 == r1) goto L_0x0043
                goto L_0x0048
            L_0x0040:
                r3.updateMatchingWifiConfig()
            L_0x0043:
                com.android.settings.wifi.details.WifiDetailPreferenceController r3 = com.android.settings.wifi.details.WifiDetailPreferenceController.this
                com.android.settings.wifi.details.WifiDetailPreferenceController.access$000(r3)
            L_0x0048:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }

        private void updateMatchingWifiConfig() {
            for (WifiConfiguration wifiConfiguration : WifiDetailPreferenceController.this.mWifiManager.getPrivilegedConfiguredNetworks()) {
                if (WifiDetailPreferenceController.this.mAccessPoint.matches(wifiConfiguration)) {
                    WifiDetailPreferenceController.this.mWifiConfig = wifiConfiguration;
                    return;
                }
            }
        }
    };
    private int mRssiSignalLevel = -1;
    private Preference mRxLinkSpeedPref;
    private Preference mSecurityPref;
    private String[] mSignalStr;
    private Preference mSignalStrengthPref;
    private Preference mSsidPref;
    private Preference mSubnetPref;
    WifiDataUsageSummaryPreferenceController mSummaryHeaderController;
    private Preference mTxLinkSpeedPref;
    private WifiConfiguration mWifiConfig;
    private WifiInfo mWifiInfo;
    final WifiTracker.WifiListener mWifiListener = new WifiTracker.WifiListener() {
        /* class com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass3 */

        @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
        public void onWifiStateChanged(int i) {
            Log.d("WifiDetailsPrefCtrl", "onWifiStateChanged(" + i + ")");
            if (WifiDetailPreferenceController.this.mConnectingState == 2 && i == 3) {
                WifiDetailPreferenceController.this.updateConnectingState(4);
            } else if (WifiDetailPreferenceController.this.mConnectingState != 1 && i == 1) {
                WifiDetailPreferenceController.this.updateConnectingState(8);
            }
        }

        @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
        public void onConnectedChanged() {
            WifiDetailPreferenceController.this.refreshPage();
        }

        @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
        public void onAccessPointsChanged() {
            WifiDetailPreferenceController.this.refreshPage();
        }
    };
    private final WifiManager mWifiManager;
    private int mWifiStandard;
    private final WifiTracker mWifiTracker;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public static WifiDetailPreferenceController newInstance(AccessPoint accessPoint, ConnectivityManager connectivityManager, Context context, PreferenceFragmentCompat preferenceFragmentCompat, Handler handler, Lifecycle lifecycle, WifiManager wifiManager, MetricsFeatureProvider metricsFeatureProvider) {
        return new WifiDetailPreferenceController(accessPoint, connectivityManager, context, preferenceFragmentCompat, handler, lifecycle, wifiManager, metricsFeatureProvider, new IconInjector(context), new Clock());
    }

    WifiDetailPreferenceController(AccessPoint accessPoint, ConnectivityManager connectivityManager, Context context, PreferenceFragmentCompat preferenceFragmentCompat, Handler handler, Lifecycle lifecycle, WifiManager wifiManager, MetricsFeatureProvider metricsFeatureProvider, IconInjector iconInjector, Clock clock) {
        super(context);
        this.mAccessPoint = accessPoint;
        this.mConnectivityManager = connectivityManager;
        this.mFragment = preferenceFragmentCompat;
        this.mHandler = handler;
        this.mSignalStr = context.getResources().getStringArray(C0003R$array.wifi_signal);
        this.mWifiConfig = accessPoint.getConfig();
        this.mWifiManager = wifiManager;
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        this.mIconInjector = iconInjector;
        this.mClock = clock;
        IntentFilter intentFilter = new IntentFilter();
        this.mFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        this.mLifecycle = lifecycle;
        lifecycle.addObserver(this);
        this.mWifiTracker = WifiTrackerFactory.create(this.mFragment.getActivity(), this.mWifiListener, this.mLifecycle, true, true);
        this.mConnected = this.mAccessPoint.isActive();
        this.mIsEphemeral = this.mAccessPoint.isEphemeral();
        this.mConnectingState = 1;
        this.mConnectListener = new WifiManager.ActionListener() {
            /* class com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass4 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                WifiDetailPreferenceController.this.updateConnectingState(6);
            }
        };
        this.mIsExpired = this.mAccessPoint.isExpired();
        this.mIsPasspointConfigurationR1 = this.mAccessPoint.isPasspointConfigurationR1();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        setupEntityHeader(preferenceScreen);
        ActionButtonsPreference actionButtonsPreference = (ActionButtonsPreference) preferenceScreen.findPreference(KEY_BUTTONS_PREF);
        actionButtonsPreference.setButton1Text(C0017R$string.forget);
        actionButtonsPreference.setButton1Icon(C0008R$drawable.ic_settings_delete);
        actionButtonsPreference.setButton1OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$HDOTYXVF80U7sCZa22KqorlzriY */

            public final void onClick(View view) {
                WifiDetailPreferenceController.this.lambda$displayPreference$0$WifiDetailPreferenceController(view);
            }
        });
        actionButtonsPreference.setButton3Text(C0017R$string.wifi_connect);
        actionButtonsPreference.setButton3Icon(C0008R$drawable.ic_settings_wireless);
        actionButtonsPreference.setButton3OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$PxMNywf_HXiVAESmLubuiIo869s */

            public final void onClick(View view) {
                WifiDetailPreferenceController.this.lambda$displayPreference$1$WifiDetailPreferenceController(view);
            }
        });
        actionButtonsPreference.setButton3Enabled(true);
        actionButtonsPreference.setButton4Text(C0017R$string.share);
        actionButtonsPreference.setButton4Icon(C0008R$drawable.ic_qrcode_24dp);
        actionButtonsPreference.setButton4OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$QsxxFhKQ64dtDlyizqvsqmZBnQs */

            public final void onClick(View view) {
                WifiDetailPreferenceController.this.lambda$displayPreference$2$WifiDetailPreferenceController(view);
            }
        });
        this.mButtonsPref = actionButtonsPreference;
        updateCaptivePortalButton();
        if (isPasspointConfigurationR1Expired()) {
            this.mButtonsPref.setButton3Visible(DEBUG);
        }
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
        if (!this.mAccessPoint.getSecurityString(DEBUG).equals("SAE") || !this.mAccessPoint.getConfig().allowedKeyManagement.get(1)) {
            this.mSecurityPref.setSummary(this.mAccessPoint.getSecurityString(DEBUG));
        } else {
            this.mSecurityPref.setSummary(this.mContext.getString(C0017R$string.wifi_security_wpa_wpa2));
        }
        this.mSecurityPref.setSummary(this.mAccessPoint.getSecurityString(DEBUG));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$WifiDetailPreferenceController(View view) {
        forgetNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$WifiDetailPreferenceController(View view) {
        connectNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$2 */
    public /* synthetic */ void lambda$displayPreference$2$WifiDetailPreferenceController(View view) {
        shareNetwork();
    }

    private boolean updateCaptivePortalButton() {
        Uri captivePortalVenueInfoUrl = getCaptivePortalVenueInfoUrl();
        if (captivePortalVenueInfoUrl == null) {
            ActionButtonsPreference actionButtonsPreference = this.mButtonsPref;
            actionButtonsPreference.setButton2Text(C0017R$string.wifi_sign_in_button_text);
            actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_sign_in);
            actionButtonsPreference.setButton2OnClickListener(new View.OnClickListener() {
                /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$O2kJKwNs1e_DC3UCzRHbgb9e8dg */

                public final void onClick(View view) {
                    WifiDetailPreferenceController.this.lambda$updateCaptivePortalButton$3$WifiDetailPreferenceController(view);
                }
            });
            return canSignIntoNetwork();
        }
        ActionButtonsPreference actionButtonsPreference2 = this.mButtonsPref;
        actionButtonsPreference2.setButton2Text(C0017R$string.wifi_venue_website_button_text);
        actionButtonsPreference2.setButton2Icon(C0008R$drawable.ic_settings_sign_in);
        actionButtonsPreference2.setButton2OnClickListener(new View.OnClickListener(captivePortalVenueInfoUrl) {
            /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$H7KKdhXnJyt8cAynwhTjk_QZk0 */
            public final /* synthetic */ Uri f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                WifiDetailPreferenceController.this.lambda$updateCaptivePortalButton$4$WifiDetailPreferenceController(this.f$1, view);
            }
        });
        return this.mAccessPoint.isActive();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCaptivePortalButton$3 */
    public /* synthetic */ void lambda$updateCaptivePortalButton$3$WifiDetailPreferenceController(View view) {
        signIntoNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCaptivePortalButton$4 */
    public /* synthetic */ void lambda$updateCaptivePortalButton$4$WifiDetailPreferenceController(Uri uri, View view) {
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
            this.mSummaryHeaderController = new WifiDataUsageSummaryPreferenceController(this.mFragment.getActivity(), this.mLifecycle, this.mFragment, this.mAccessPoint.getSsid());
            return;
        }
        this.mEntityHeaderController = EntityHeaderController.newInstance(this.mFragment.getActivity(), this.mFragment, layoutPreference.findViewById(C0010R$id.entity_header));
        ((ImageView) layoutPreference.findViewById(C0010R$id.entity_header_icon)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.mEntityHeaderController.setLabel(this.mAccessPoint.getTitle());
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
        String str;
        if (usingDataUsageHeader(this.mContext)) {
            this.mSummaryHeaderController.updateState(this.mDataUsageSummaryPref);
            return;
        }
        if (isPasspointConfigurationR1Expired()) {
            str = this.mContext.getResources().getString(R$string.wifi_passpoint_expired);
        } else {
            str = this.mAccessPoint.getSettingsSummary(true);
        }
        EntityHeaderController entityHeaderController = this.mEntityHeaderController;
        entityHeaderController.setSummary(str);
        entityHeaderController.setSecondSummary(getExpiryTimeSummary());
        entityHeaderController.setRecyclerView(this.mFragment.getListView(), this.mLifecycle);
        entityHeaderController.done((Activity) this.mFragment.getActivity(), true);
    }

    private void updateNetworkInfo() {
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        this.mNetwork = currentNetwork;
        this.mLinkProperties = this.mConnectivityManager.getLinkProperties(currentNetwork);
        this.mNetworkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mNetwork);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateNetworkInfo();
        refreshPage();
        this.mContext.registerReceiver(this.mReceiver, this.mFilter);
        this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mHandler);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mNetwork = null;
        this.mLinkProperties = null;
        this.mNetworkCapabilities = null;
        this.mNetworkInfo = null;
        this.mWifiInfo = null;
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshPage() {
        if (updateAccessPoint()) {
            Log.d("WifiDetailsPrefCtrl", "Update UI!");
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
    }

    /* access modifiers changed from: package-private */
    public boolean updateAccessPoint() {
        boolean z;
        NetworkInfo networkInfo;
        boolean z2 = this.mIsOutOfRange;
        updateAccessPointFromScannedList();
        boolean isActive = this.mAccessPoint.isActive();
        boolean z3 = DEBUG;
        if (isActive) {
            updateNetworkInfo();
            this.mNetworkInfo = this.mConnectivityManager.getNetworkInfo(this.mNetwork);
            WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            this.mWifiInfo = connectionInfo;
            if (this.mNetwork == null || (networkInfo = this.mNetworkInfo) == null || connectionInfo == null) {
                this.mIsOutOfRange = z2;
                return DEBUG;
            }
            z = this.mAccessPoint.update(this.mWifiConfig, connectionInfo, networkInfo) | DEBUG;
        } else {
            z = false;
        }
        boolean z4 = z | (this.mRssiSignalLevel != this.mAccessPoint.getLevel());
        if (z2 != this.mIsOutOfRange) {
            z3 = true;
        }
        boolean z5 = z4 | z3;
        if (this.mConnected == this.mAccessPoint.isActive()) {
            return z5;
        }
        this.mConnected = this.mAccessPoint.isActive();
        updateConnectingState(this.mAccessPoint.isActive() ? 5 : 8);
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateAccessPointFromScannedList() {
        this.mIsOutOfRange = true;
        for (AccessPoint accessPoint : this.mWifiTracker.getAccessPoints()) {
            if (this.mAccessPoint.matches(accessPoint)) {
                this.mAccessPoint = accessPoint;
                this.mWifiConfig = accessPoint.getConfig();
                this.mIsOutOfRange = true ^ this.mAccessPoint.isReachable();
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void exitActivity() {
        if (DEBUG) {
            Log.d("WifiDetailsPrefCtrl", "Exiting the WifiNetworkDetailsPage");
        }
        this.mFragment.getActivity().finish();
    }

    private void refreshRssiViews() {
        int level = this.mAccessPoint.getLevel();
        int wifiStandard = this.mAccessPoint.getWifiStandard();
        boolean z = this.mAccessPoint.isVhtMax8SpatialStreamsSupported() && this.mAccessPoint.isHe8ssCapableAp();
        if (this.mIsOutOfRange) {
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
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null) {
            this.mFrequencyPref.setVisible(DEBUG);
            return;
        }
        int frequency = wifiInfo.getFrequency();
        if (frequency >= 2400 && frequency < 2500) {
            str = this.mContext.getResources().getString(C0017R$string.wifi_band_24ghz);
        } else if (frequency >= 4900 && frequency < 5900) {
            str = this.mContext.getResources().getString(C0017R$string.wifi_band_5ghz);
        } else if (frequency < 58320 || frequency >= 70200) {
            Log.e("WifiDetailsPrefCtrl", "Unexpected frequency " + frequency);
            if (this.mConnectingState == 4) {
                this.mFrequencyPref.setVisible(DEBUG);
                return;
            }
            return;
        } else {
            str = this.mContext.getResources().getString(C0017R$string.wifi_band_60ghz);
        }
        this.mFrequencyPref.setSummary(str);
        this.mFrequencyPref.setVisible(true);
    }

    private void refreshTxSpeed() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null) {
            this.mTxLinkSpeedPref.setVisible(DEBUG);
            return;
        }
        this.mTxLinkSpeedPref.setVisible(wifiInfo.getTxLinkSpeedMbps() >= 0);
        this.mTxLinkSpeedPref.setSummary(this.mContext.getString(C0017R$string.tx_link_speed, Integer.valueOf(this.mWifiInfo.getTxLinkSpeedMbps())));
    }

    private void refreshRxSpeed() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo == null) {
            this.mRxLinkSpeedPref.setVisible(DEBUG);
            return;
        }
        this.mRxLinkSpeedPref.setVisible(wifiInfo.getRxLinkSpeedMbps() >= 0);
        this.mRxLinkSpeedPref.setSummary(this.mContext.getString(C0017R$string.rx_link_speed, Integer.valueOf(this.mWifiInfo.getRxLinkSpeedMbps())));
    }

    private void refreshSsid() {
        if (this.mAccessPoint.isPasspoint() || this.mAccessPoint.isOsuProvider()) {
            this.mSsidPref.setVisible(true);
            this.mSsidPref.setSummary(this.mAccessPoint.getSsidStr());
            return;
        }
        this.mSsidPref.setVisible(DEBUG);
    }

    private void refreshMacAddress() {
        String macAddress = getMacAddress();
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

    private String getMacAddress() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            return wifiInfo.getMacAddress();
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration != null && wifiConfiguration.macRandomizationSetting == 1) {
            return wifiConfiguration.getRandomizedMacAddress().toString();
        }
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        if (factoryMacAddresses != null && factoryMacAddresses.length > 0) {
            return factoryMacAddresses[0];
        }
        Log.e("WifiDetailsPrefCtrl", "Can't get device MAC address!");
        return null;
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
        this.mButtonsPref.setButton1Text(this.mIsEphemeral ? C0017R$string.wifi_disconnect_button_text : C0017R$string.forget);
        boolean canForgetNetwork = canForgetNetwork();
        boolean updateCaptivePortalButton = updateCaptivePortalButton();
        boolean z = true;
        boolean z2 = canConnectNetwork() && !isPasspointConfigurationR1Expired();
        boolean canShareNetwork = canShareNetwork();
        this.mButtonsPref.setButton1Visible(canForgetNetwork);
        this.mButtonsPref.setButton2Visible(updateCaptivePortalButton);
        this.mButtonsPref.setButton3Visible(z2);
        this.mButtonsPref.setButton4Visible(canShareNetwork);
        ActionButtonsPreference actionButtonsPreference = this.mButtonsPref;
        if (!canForgetNetwork && !updateCaptivePortalButton && !z2 && !canShareNetwork) {
            z = false;
        }
        actionButtonsPreference.setVisible(z);
    }

    private boolean canConnectNetwork() {
        return !this.mAccessPoint.isActive();
    }

    private boolean isPasspointConfigurationR1Expired() {
        if (!this.mIsPasspointConfigurationR1 || !this.mIsExpired) {
            return DEBUG;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshIpLayerInfo() {
        if (!this.mAccessPoint.isActive() || this.mNetwork == null || this.mLinkProperties == null) {
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

    private boolean canForgetNetwork() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if ((wifiInfo == null || !wifiInfo.isEphemeral()) && !canModifyNetwork() && !this.mAccessPoint.isPasspoint() && !this.mAccessPoint.isPasspointConfig()) {
            return DEBUG;
        }
        return true;
    }

    public boolean canModifyNetwork() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration == null || WifiUtils.isNetworkLockedDown(this.mContext, wifiConfiguration)) {
            return DEBUG;
        }
        return true;
    }

    private boolean canSignIntoNetwork() {
        if (!this.mAccessPoint.isActive() || !WifiUtils.canSignIntoNetwork(this.mNetworkCapabilities)) {
            return DEBUG;
        }
        return true;
    }

    private boolean canShareNetwork() {
        if (this.mAccessPoint.getConfig() == null || !WifiDppUtils.isSupportConfiguratorQrCodeGenerator(this.mContext, this.mAccessPoint)) {
            return DEBUG;
        }
        return true;
    }

    private void forgetNetwork() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null && wifiInfo.isEphemeral()) {
            this.mWifiManager.disableEphemeralNetwork(this.mWifiInfo.getSSID());
        } else if (this.mAccessPoint.isPasspoint() || this.mAccessPoint.isPasspointConfig()) {
            showConfirmForgetDialog();
            return;
        } else {
            WifiConfiguration wifiConfiguration = this.mWifiConfig;
            if (wifiConfiguration != null) {
                this.mWifiManager.forget(wifiConfiguration.networkId, null);
            }
        }
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 137, new Pair[0]);
        this.mFragment.getActivity().finish();
    }

    /* access modifiers changed from: protected */
    public void showConfirmForgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setPositiveButton(C0017R$string.forget, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$rMhNXfX33cqsf6Z9LJGSfWrTGRo */

            public final void onClick(DialogInterface dialogInterface, int i) {
                WifiDetailPreferenceController.this.lambda$showConfirmForgetDialog$5$WifiDetailPreferenceController(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        builder.setTitle(C0017R$string.wifi_forget_dialog_title);
        builder.setMessage(C0017R$string.forget_passpoint_dialog_message);
        builder.create().show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showConfirmForgetDialog$5 */
    public /* synthetic */ void lambda$showConfirmForgetDialog$5$WifiDetailPreferenceController(DialogInterface dialogInterface, int i) {
        try {
            this.mWifiManager.removePasspointConfiguration(this.mAccessPoint.getPasspointFqdn());
        } catch (RuntimeException unused) {
            Log.e("WifiDetailsPrefCtrl", "Failed to remove Passpoint configuration for " + this.mAccessPoint.getPasspointFqdn());
        }
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 137, new Pair[0]);
        this.mFragment.getActivity().finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: launchWifiDppConfiguratorActivity */
    public void lambda$shareNetwork$6() {
        Intent configuratorQrCodeGeneratorIntentOrNull = WifiDppUtils.getConfiguratorQrCodeGeneratorIntentOrNull(this.mContext, this.mWifiManager, this.mAccessPoint);
        if (configuratorQrCodeGeneratorIntentOrNull == null) {
            Log.e("WifiDetailsPrefCtrl", "Launch Wi-Fi DPP QR code generator with a wrong Wi-Fi network!");
            return;
        }
        this.mMetricsFeatureProvider.action(0, 1710, 1595, null, Integer.MIN_VALUE);
        this.mContext.startActivity(configuratorQrCodeGeneratorIntentOrNull);
    }

    private void shareNetwork() {
        WifiDppUtils.showLockScreen(this.mContext, new Runnable() {
            /* class com.android.settings.wifi.details.$$Lambda$WifiDetailPreferenceController$GzgCxez5l_aTssbX8V6mILozME */

            public final void run() {
                WifiDetailPreferenceController.this.lambda$shareNetwork$6$WifiDetailPreferenceController();
            }
        });
    }

    private void signIntoNetwork() {
        this.mMetricsFeatureProvider.action(this.mFragment.getActivity(), 1008, new Pair[0]);
        this.mConnectivityManager.startCaptivePortalApp(this.mNetwork);
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onSubmit(WifiDialog wifiDialog) {
        if (wifiDialog.getController() != null) {
            this.mWifiManager.save(wifiDialog.getController().getConfig(), new WifiManager.ActionListener() {
                /* class com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass5 */

                public void onSuccess() {
                }

                public void onFailure(int i) {
                    if (WifiDetailPreferenceController.this.mFragment.getActivity() != null) {
                        Toast.makeText(((AbstractPreferenceController) WifiDetailPreferenceController.this).mContext, C0017R$string.wifi_failed_save_message, 0).show();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public static class IconInjector {
        private final Context mContext;

        public IconInjector(Context context) {
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
    public void connectNetwork() {
        this.mFragment.getActivity();
        if (this.mWifiConfig == null) {
            Toast.makeText(this.mContext, C0017R$string.wifi_failed_connect_message, 0).show();
            return;
        }
        this.mConnectingState = 1;
        if (this.mWifiManager.isWifiEnabled()) {
            updateConnectingState(4);
        } else {
            updateConnectingState(2);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateConnectingState(int i) {
        this.mFragment.getActivity();
        Log.d("WifiDetailsPrefCtrl", "updateConnectingState from " + this.mConnectingState + " to " + i);
        int i2 = this.mConnectingState;
        if (i2 == 1 || i2 == 2) {
            if (i == 2) {
                Log.d("WifiDetailsPrefCtrl", "Turn on Wi-Fi automatically!");
                updateConnectedButton(2);
                Toast.makeText(this.mContext, C0017R$string.wifi_turned_on_message, 0).show();
                this.mWifiManager.setWifiEnabled(true);
                startTimer();
            } else if (i == 4) {
                Log.d("WifiDetailsPrefCtrl", "connecting...");
                updateConnectedButton(4);
                if (this.mAccessPoint.isPasspoint()) {
                    this.mWifiManager.connect(this.mWifiConfig, this.mConnectListener);
                } else {
                    this.mWifiManager.connect(this.mWifiConfig.networkId, this.mConnectListener);
                }
                startTimer();
            } else if (i == 3) {
                Log.e("WifiDetailsPrefCtrl", "Wi-Fi failed to enable network!");
                stopTimer();
                Toast.makeText(this.mContext, C0017R$string.wifi_failed_connect_message, 0).show();
                updateConnectedButton(3);
                i = 1;
            }
        } else if (i2 == 4) {
            if (i == 5) {
                Log.d("WifiDetailsPrefCtrl", "connected");
                stopTimer();
                updateConnectedButton(5);
                Context context = this.mContext;
                Toast.makeText(context, context.getString(C0017R$string.wifi_connected_to_message, this.mAccessPoint.getTitle()), 0).show();
                refreshPage();
            } else {
                if (i == 7) {
                    Log.d("WifiDetailsPrefCtrl", "AP not in range");
                    stopTimer();
                    Toast.makeText(this.mContext, C0017R$string.wifi_not_in_range_message, 0).show();
                    updateConnectedButton(7);
                } else if (i == 6) {
                    Log.d("WifiDetailsPrefCtrl", "failed");
                    stopTimer();
                    Toast.makeText(this.mContext, C0017R$string.wifi_failed_connect_message, 0).show();
                    updateConnectedButton(6);
                }
                i = 1;
            }
            this.mConnectingState = i;
        } else if (i2 != 5) {
            Log.e("WifiDetailsPrefCtrl", "Invalid state : " + this.mConnectingState);
            return;
        }
        if (i == 8) {
            Log.d("WifiDetailsPrefCtrl", "disconnected");
            updateConnectedButton(8);
            refreshPage();
            this.mWifiInfo = null;
            i = 1;
        }
        this.mConnectingState = i;
    }

    private void updateConnectedButton(int i) {
        switch (i) {
            case 2:
            case 4:
                ActionButtonsPreference actionButtonsPreference = this.mButtonsPref;
                actionButtonsPreference.setButton3Text(C0017R$string.wifi_connecting);
                actionButtonsPreference.setButton3Enabled(DEBUG);
                return;
            case 3:
            case 6:
            case 7:
            case 8:
                if (isPasspointConfigurationR1Expired()) {
                    this.mButtonsPref.setButton3Visible(DEBUG);
                    return;
                }
                ActionButtonsPreference actionButtonsPreference2 = this.mButtonsPref;
                actionButtonsPreference2.setButton3Text(C0017R$string.wifi_connect);
                actionButtonsPreference2.setButton3Icon(C0008R$drawable.ic_settings_wireless);
                actionButtonsPreference2.setButton3Enabled(true);
                actionButtonsPreference2.setButton3Visible(true);
                return;
            case 5:
                ActionButtonsPreference actionButtonsPreference3 = this.mButtonsPref;
                actionButtonsPreference3.setButton3Text(C0017R$string.wifi_connect);
                actionButtonsPreference3.setButton3Icon(C0008R$drawable.ic_settings_wireless);
                actionButtonsPreference3.setButton3Enabled(true);
                actionButtonsPreference3.setButton3Visible(DEBUG);
                return;
            default:
                Log.e("WifiDetailsPrefCtrl", "Invalid connect button state : " + i);
                return;
        }
    }

    private void startTimer() {
        if (mTimer != null) {
            stopTimer();
        }
        long j = TIMEOUT;
        AnonymousClass6 r0 = new CountDownTimer(j, j + 1) {
            /* class com.android.settings.wifi.details.WifiDetailPreferenceController.AnonymousClass6 */

            public void onTick(long j) {
            }

            public void onFinish() {
                if (WifiDetailPreferenceController.this.mFragment == null || WifiDetailPreferenceController.this.mFragment.getActivity() == null) {
                    Log.d("WifiDetailsPrefCtrl", "Ignore timeout since activity not exist!");
                    return;
                }
                Log.e("WifiDetailsPrefCtrl", "Timeout for state:" + WifiDetailPreferenceController.this.mConnectingState);
                if (WifiDetailPreferenceController.this.mConnectingState == 2) {
                    WifiDetailPreferenceController.this.updateConnectingState(3);
                } else if (WifiDetailPreferenceController.this.mConnectingState == 4) {
                    WifiDetailPreferenceController.this.updateAccessPointFromScannedList();
                    if (WifiDetailPreferenceController.this.mIsOutOfRange) {
                        WifiDetailPreferenceController.this.updateConnectingState(7);
                    } else {
                        WifiDetailPreferenceController.this.updateConnectingState(6);
                    }
                }
            }
        };
        mTimer = r0;
        r0.start();
    }

    private void stopTimer() {
        CountDownTimer countDownTimer = mTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            mTimer = null;
        }
    }

    private void refreshMacTitle() {
        int i;
        if (this.mWifiConfig != null && !this.mAccessPoint.isPasspoint() && !this.mAccessPoint.isPasspointConfig()) {
            Preference preference = this.mMacAddressPref;
            if (this.mWifiConfig.macRandomizationSetting == 1) {
                i = C0017R$string.wifi_advanced_randomized_mac_address_title;
            } else {
                i = C0017R$string.wifi_advanced_device_mac_address_title;
            }
            preference.setTitle(i);
        }
    }
}
