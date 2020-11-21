package com.android.settingslib.search;

import android.content.Context;
import android.provider.SearchIndexableResource;
import java.util.List;

public interface Indexable$SearchIndexProvider {
    List<SearchIndexableRaw> getDynamicRawDataToIndex(Context context, boolean z);

    List<String> getNonIndexableKeys(Context context);

    List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z);

    List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z);
}
