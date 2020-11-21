package com.android.settings.panel;

import android.content.Context;
import android.os.Bundle;

public class PanelFeatureProviderImpl implements PanelFeatureProvider {
    @Override // com.android.settings.panel.PanelFeatureProvider
    public PanelContent getPanel(Context context, Bundle bundle) {
        if (context == null) {
            return null;
        }
        String string = bundle.getString("PANEL_TYPE_ARGUMENT");
        String string2 = bundle.getString("PANEL_MEDIA_PACKAGE_NAME");
        char c = 65535;
        switch (string.hashCode()) {
            case 66351017:
                if (string.equals("android.settings.panel.action.NFC")) {
                    c = 2;
                    break;
                }
                break;
            case 464243859:
                if (string.equals("android.settings.panel.action.INTERNET_CONNECTIVITY")) {
                    c = 0;
                    break;
                }
                break;
            case 648354091:
                if (string.equals("com.android.settings.panel.action.MEDIA_OUTPUT_GROUP")) {
                    c = 5;
                    break;
                }
                break;
            case 1215888444:
                if (string.equals("android.settings.panel.action.VOLUME")) {
                    c = 4;
                    break;
                }
                break;
            case 1827023883:
                if (string.equals("com.android.settings.panel.action.MEDIA_OUTPUT")) {
                    c = 1;
                    break;
                }
                break;
            case 2057152695:
                if (string.equals("android.settings.panel.action.WIFI")) {
                    c = 3;
                    break;
                }
                break;
        }
        if (c == 0) {
            return InternetConnectivityPanel.create(context);
        }
        if (c == 1) {
            return MediaOutputPanel.create(context, string2);
        }
        if (c == 2) {
            return NfcPanel.create(context);
        }
        if (c == 3) {
            return WifiPanel.create(context);
        }
        if (c == 4) {
            return VolumePanel.create(context);
        }
        if (c == 5) {
            return MediaOutputGroupPanel.create(context, string2);
        }
        throw new IllegalStateException("No matching panel for: " + string);
    }
}
