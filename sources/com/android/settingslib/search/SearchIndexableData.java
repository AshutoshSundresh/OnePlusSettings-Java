package com.android.settingslib.search;

public class SearchIndexableData {
    private final Indexable$SearchIndexProvider mSearchIndexProvider;
    private final Class mTargetClass;

    public SearchIndexableData(Class cls, Indexable$SearchIndexProvider indexable$SearchIndexProvider) {
        this.mTargetClass = cls;
        this.mSearchIndexProvider = indexable$SearchIndexProvider;
    }

    public Class getTargetClass() {
        return this.mTargetClass;
    }

    public Indexable$SearchIndexProvider getSearchIndexProvider() {
        return this.mSearchIndexProvider;
    }
}
