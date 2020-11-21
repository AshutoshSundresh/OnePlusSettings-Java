package com.android.settings.panel;

import android.content.Context;
import android.os.Bundle;

public interface PanelFeatureProvider {
    PanelContent getPanel(Context context, Bundle bundle);
}
