package com.android.settings.development.autofill;

import android.content.Context;
import android.util.AttributeSet;

public final class AutofillMaxPartitionsPreference extends AbstractGlobalSettingsPreference {
    public AutofillMaxPartitionsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, "autofill_max_partitions_size", 10);
    }
}
