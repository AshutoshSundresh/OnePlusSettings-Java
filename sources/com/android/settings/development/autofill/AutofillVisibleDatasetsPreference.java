package com.android.settings.development.autofill;

import android.content.Context;
import android.util.AttributeSet;

public final class AutofillVisibleDatasetsPreference extends AbstractGlobalSettingsPreference {
    public AutofillVisibleDatasetsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, "autofill_max_visible_datasets", 0);
    }
}
