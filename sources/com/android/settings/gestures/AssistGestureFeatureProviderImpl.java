package com.android.settings.gestures;

import android.content.Context;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class AssistGestureFeatureProviderImpl implements AssistGestureFeatureProvider {
    @Override // com.android.settings.gestures.AssistGestureFeatureProvider
    public boolean isSensorAvailable(Context context) {
        return false;
    }

    @Override // com.android.settings.gestures.AssistGestureFeatureProvider
    public boolean isSupported(Context context) {
        return false;
    }

    @Override // com.android.settings.gestures.AssistGestureFeatureProvider
    public List<AbstractPreferenceController> getControllers(Context context, Lifecycle lifecycle) {
        return new ArrayList();
    }
}
