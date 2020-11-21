package com.android.settings.datausage;

import android.net.NetworkTemplate;
import com.android.settingslib.NetworkPolicyEditor;

public interface DataUsageEditController {
    NetworkPolicyEditor getNetworkPolicyEditor();

    NetworkTemplate getNetworkTemplate();

    void updateDataUsage();
}
