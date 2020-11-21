package com.oneplus.settings.chargingstations;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.chargingstations.OPChargingStationUtils;

public class OPChargingStationHeaderController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop, OPChargingStationUtils.ILocationUpdate {
    private TextView enableButton;
    private View mChargingStationDetails;
    private TextView mChargingStationDistance;
    private View mChargingStationFeatureReqView;
    private TextView mChargingStationInfoText;
    private TextView mChargingStationName;
    private TextView mChargingStationOffText;
    private View mChargingStationOffView;
    private Uri mFeatureUri;
    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.chargingstations.OPChargingStationHeaderController.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            OPChargingStationHeaderController.this.setView();
        }
    };
    private Uri mStationDistanceUri;
    private Uri mStationNameUri;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public OPChargingStationHeaderController(Context context, String str) {
        super(context, str);
        initUris();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        OPChargingStationUtils.setLocationUpdate(this);
        this.mChargingStationDetails = layoutPreference.findViewById(C0010R$id.op_charging_station_header1);
        this.mChargingStationOffView = layoutPreference.findViewById(C0010R$id.op_charging_station_header2);
        this.mChargingStationFeatureReqView = layoutPreference.findViewById(C0010R$id.op_charging_station_header3);
        this.mChargingStationDistance = (TextView) layoutPreference.findViewById(C0010R$id.op_charging_station_header_details1);
        this.mChargingStationName = (TextView) layoutPreference.findViewById(C0010R$id.op_charging_station_header_details2);
        this.mChargingStationOffText = (TextView) layoutPreference.findViewById(C0010R$id.op_charging_station_header_off_text);
        this.mChargingStationInfoText = (TextView) layoutPreference.findViewById(C0010R$id.op_feature_text);
        this.enableButton = (TextView) layoutPreference.findViewById(C0010R$id.op_enable_button);
        ((AnimationDrawable) ((ImageView) layoutPreference.findViewById(C0010R$id.op_charging_station_animation)).getBackground()).start();
        setView();
    }

    private void initUris() {
        this.mFeatureUri = Settings.System.getUriFor("op_charging_stations_feature_on");
        this.mStationNameUri = Settings.System.getUriFor("op_charging_station_beacon_name");
        this.mStationDistanceUri = Settings.System.getUriFor("op_charging_station_beacon_distance");
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mFeatureUri == null || this.mStationDistanceUri == null || this.mStationNameUri == null) {
            initUris();
        }
        this.mContext.getContentResolver().registerContentObserver(this.mFeatureUri, false, this.mObserver);
        this.mContext.getContentResolver().registerContentObserver(this.mStationNameUri, false, this.mObserver);
        this.mContext.getContentResolver().registerContentObserver(this.mStationDistanceUri, false, this.mObserver);
        setView();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setView() {
        updateHeader();
        this.enableButton.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.chargingstations.$$Lambda$OPChargingStationHeaderController$RxWddFa1TUqNf0vpzYVPYqS95po */

            public final void onClick(View view) {
                OPChargingStationHeaderController.this.lambda$setView$0$OPChargingStationHeaderController(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setView$0 */
    public /* synthetic */ void lambda$setView$0$OPChargingStationHeaderController(View view) {
        this.mContext.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    private void updateHeader() {
        if (!isOPFeatureEnabled() && !isLocationEnabled()) {
            this.mChargingStationDetails.setVisibility(8);
            this.mChargingStationOffView.setVisibility(8);
            this.mChargingStationFeatureReqView.setVisibility(0);
            this.mChargingStationInfoText.setText(this.mContext.getString(C0017R$string.op_enable_location_services));
            this.enableButton.setVisibility(0);
        } else if (isOPFeatureEnabled() && !isLocationEnabled()) {
            this.mChargingStationDetails.setVisibility(8);
            this.mChargingStationOffView.setVisibility(8);
            this.mChargingStationFeatureReqView.setVisibility(0);
            this.mChargingStationInfoText.setText(this.mContext.getString(C0017R$string.op_enable_location_services));
            this.enableButton.setVisibility(0);
        } else if (isOPFeatureEnabled() || !isLocationEnabled()) {
            updateStationDetails();
        } else {
            this.mChargingStationDetails.setVisibility(8);
            this.mChargingStationOffView.setVisibility(8);
            this.mChargingStationFeatureReqView.setVisibility(0);
            this.mChargingStationInfoText.setText(this.mContext.getString(C0017R$string.op_keep_location_services));
            this.enableButton.setVisibility(8);
        }
    }

    private void updateStationDetails() {
        String stringSystemProperty = OPChargingStationUtils.getStringSystemProperty(this.mContext, "op_charging_station_beacon_name");
        String stringGlobalProperty = OPChargingStationUtils.getStringGlobalProperty(this.mContext, "op_charging_station_beacon_distance");
        if (!TextUtils.isEmpty(stringSystemProperty)) {
            this.mChargingStationDetails.setVisibility(0);
            this.mChargingStationOffView.setVisibility(8);
            this.mChargingStationFeatureReqView.setVisibility(8);
            this.mChargingStationName.setText(stringSystemProperty);
            this.mChargingStationDistance.setText(getFormattedDistance(stringGlobalProperty));
            return;
        }
        showOffHeader(C0017R$string.op_charging_station_no_station);
    }

    private void showOffHeader(int i) {
        this.mChargingStationDetails.setVisibility(8);
        this.mChargingStationOffView.setVisibility(0);
        this.mChargingStationFeatureReqView.setVisibility(8);
        this.mChargingStationOffText.setText(i);
    }

    private String getFormattedDistance(String str) {
        if (str != null && str.length() > 0) {
            double parseDouble = Double.parseDouble(str);
            if (parseDouble >= 70.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 100);
            } else if (parseDouble >= 60.0d && parseDouble <= 69.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 70);
            } else if (parseDouble >= 50.0d && parseDouble <= 59.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 60);
            } else if (parseDouble >= 40.0d && parseDouble <= 49.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 50);
            } else if (parseDouble >= 30.0d && parseDouble <= 39.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 40);
            } else if (parseDouble >= 20.0d && parseDouble <= 29.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 30);
            } else if (parseDouble >= 10.0d && parseDouble <= 19.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 20);
            } else if (parseDouble >= 0.0d && parseDouble <= 9.0d) {
                return String.format(this.mContext.getString(C0017R$string.under_meters), 10);
            }
        }
        return String.format(this.mContext.getString(C0017R$string.under_meters), 10);
    }

    private boolean isLocationEnabled() {
        return ((LocationManager) this.mContext.getSystemService("location")).isLocationEnabled();
    }

    private boolean isOPFeatureEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "op_charging_stations_feature_on", 0) == 1;
    }

    @Override // com.oneplus.settings.chargingstations.OPChargingStationUtils.ILocationUpdate
    public void onOPLocationUpdate() {
        updateHeader();
    }
}
