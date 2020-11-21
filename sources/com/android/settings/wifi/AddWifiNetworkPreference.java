package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.wifi.dpp.WifiDppUtils;

public class AddWifiNetworkPreference extends Preference {
    private final Drawable mScanIconDrawable = getDrawable(C0008R$drawable.ic_scan_24dp);

    public AddWifiNetworkPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.op_preference_access_point);
        setWidgetLayoutResource(C0012R$layout.wifi_button_preference_widget);
        setTitle(C0017R$string.wifi_add_network);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageButton imageButton = (ImageButton) preferenceViewHolder.findViewById(C0010R$id.button_icon);
        imageButton.setImageDrawable(this.mScanIconDrawable);
        imageButton.setContentDescription(getContext().getString(C0017R$string.wifi_dpp_scan_qr_code));
        imageButton.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$AddWifiNetworkPreference$xLKoX30iYXFznnMnnFkVtm9yJ4 */

            public final void onClick(View view) {
                AddWifiNetworkPreference.this.lambda$onBindViewHolder$0$AddWifiNetworkPreference(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$AddWifiNetworkPreference(View view) {
        getContext().startActivity(WifiDppUtils.getEnrolleeQrCodeScannerIntent(null));
    }

    private Drawable getDrawable(int i) {
        try {
            return getContext().getDrawable(i);
        } catch (Resources.NotFoundException unused) {
            Log.e("AddWifiNetworkPreference", "Resource does not exist: " + i);
            return null;
        }
    }
}
